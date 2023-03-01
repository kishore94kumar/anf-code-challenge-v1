package com.anf.core.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;

@Component(service = {Servlet.class})
@SlingServletPaths(value = "/bin/queryPages")
@ServiceDescription("Servlet to fetch the first 10 pages which has anfCodeChallenge property")
public class PageQueryServlet extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = 2610051404257637265L;

	/**
	 * LOGGER
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(PageQueryServlet.class);

	/**
	 * QueryBuilder builder
	 */
	@Reference
	private transient QueryBuilder builder;

	private static final String PAGE_LIST = "pageList";

	private static final String JSON_EXCEPTION_MSG = "Error while putting pages in JSON object {}";


	/**
	 * Method to fetch a first 10 pages which has property anfCodeChallenge
	 * @param request
	 * @param response
	 */
	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) 
			throws ServletException, IOException {

		ResourceResolver resourceResolver = request.getResourceResolver();
		response.setContentType("application/json");
		String selector = request.getRequestPathInfo().getSelectorString();
		JSONObject jsonObj = new JSONObject();

		try {
			if (StringUtils.isNotBlank(selector) && selector.equalsIgnoreCase("querybuilder")) {
				response.getWriter().println(getQueryBuilderResults(jsonObj, resourceResolver));
			} else if (StringUtils.isNotBlank(selector) && selector.equalsIgnoreCase("sql2")) {
				response.getWriter().println(getSQL2Results(jsonObj, resourceResolver));
				response.setStatus(HttpServletResponse.SC_OK);
			} else {
				jsonObj.put("errorMsg", "Need to use either querybuilder or sql2 selector");
				response.getWriter().println(jsonObj.toString());
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		}  catch (JSONException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			LOGGER.error("Error while fetching page list {}", e.getMessage());
		} 
	}

	/**
	 * Query map properties
	 * @return
	 */
	private Map<String, String> getQueryMap() {
		Map <String, String> queryMap = new HashMap<>();
		queryMap.put("path", "/content/anf-code-challenge/us/en");
		queryMap.put("type", "cq:Page");
		queryMap.put("property", "jcr:content/anfCodeChallenge");
		queryMap.put("property.operation", "exists");
		queryMap.put("orderby", "@jcr:content/jcr:created");
		queryMap.put("orderby.sort", "asc");
		queryMap.put("p.limit", "10");
		return queryMap;
	}

	/**
	 * Query builder method to get the page results
	 * @param jsonObj
	 * @param resourceResolver
	 * @return
	 */
	private String getQueryBuilderResults(JSONObject jsonObj, ResourceResolver resourceResolver) {
		try {
			Map<String, String> map = getQueryMap();
			PredicateGroup predicateGroup = PredicateGroup.create(map);
			QueryBuilder queryBuilder = resourceResolver.adaptTo(QueryBuilder.class);
			Query query = queryBuilder.createQuery(predicateGroup, resourceResolver.adaptTo(Session.class));
			SearchResult result = query.getResult();

			JSONArray jsonArray = new JSONArray();
			result.getHits().forEach(hit -> {
				try {
					jsonArray.put(hit.getPath());
				} catch (RepositoryException e) {
					LOGGER.error("Error while trying to query the page paths {} ", e.getMessage());
				}
			});
			jsonObj.put(PAGE_LIST, jsonArray);
		}  catch (JSONException e) {
			LOGGER.error(JSON_EXCEPTION_MSG, e.getMessage());
		}
		return jsonObj.toString();
	}

	/**
	 * SQL2 method to get the page results
	 * @param jsonObj
	 * @param resourceResolver
	 * @return
	 * @throws RepositoryException
	 */
	private String getSQL2Results(JSONObject jsonObj, ResourceResolver resourceResolver) {
		try {
			final String sqlQuery =
					"SELECT * FROM [cq:Page] AS page "
							+ "WHERE ISDESCENDANTNODE(page ,\"/content/anf-code-challenge/us/en\") "
							+ "AND [jcr:content/anfCodeChallenge] = \"true\" "
							+ "ORDER BY page.[jcr:created] ASC";
			Iterator<Resource> pageResources = resourceResolver.findResources(sqlQuery, javax.jcr.query.Query.JCR_SQL2);
			JSONArray jsonArray = new JSONArray();
			for (int i = 0; i < 10 && pageResources.hasNext(); i++) {
				Resource jsonResource = pageResources.next();
				jsonArray.put(jsonResource.getPath());
			}
			jsonObj.put(PAGE_LIST, jsonArray);
		}  catch (JSONException e) {
			LOGGER.error(JSON_EXCEPTION_MSG, e.getMessage());
		}
		return jsonObj.toString();
	}

}
