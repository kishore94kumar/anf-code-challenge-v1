package com.anf.core.servlets;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.day.cq.commons.jcr.JcrConstants;

/**
 * Servlet to persist the user details on /var/anf-code-challenge node
 * @author - Kishore
 */
@Component(service = { Servlet.class })
@SlingServletResourceTypes(
		resourceTypes="sling/servlet/default",
		methods=HttpConstants.METHOD_POST,
		selectors="submitUserDetails",
		extensions="json")
@ServiceDescription("Persist the user details on /var/anf-code-challenge node")
public class ANFSubmitUserDetailsServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(ANFSubmitUserDetailsServlet.class);
	private static final String FIRST_NAME = "firstName";
	private static final String LAST_NAME = "lastName";
	private static final String AGE = "age";
	private static final String COUNTRY = "country";
	private static final String RESULT = "result";
	private static final String SUCCESS = "success";
	private static final String FAILURE = "failure";
	private static final String VAR_USER_DETAILS = "/var/anf-code-challenge";
	private static final String EXCEPTION_MSG = "Excepting while trying to persist user details {}";

	/**
	 * To persist the user details and send the response
	 * @param req
	 * @param resp
	 */
	@Override
	protected void doPost(final SlingHttpServletRequest req,
			final SlingHttpServletResponse resp) throws ServletException, IOException {
		try {
			resp.setContentType("application/json");
			JSONObject jsonObject = new JSONObject();
			if(persistUserDetails(req)) {
				jsonObject.put(RESULT, SUCCESS);
				resp.getWriter().println(jsonObject.toString());
				resp.setStatus(HttpServletResponse.SC_OK);
			} else {
				jsonObject.put(RESULT, FAILURE);
				resp.getWriter().println(jsonObject.toString());
				resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		} catch(JSONException ex) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			LOGGER.error(EXCEPTION_MSG , ex.getMessage());
		}
	}

	/**
	 * To persist the user details to var/anfuserdetails node
	 * @param req
	 * @return
	 */
	private boolean persistUserDetails(SlingHttpServletRequest req) {
		try {
			String firstName = StringUtils.isNotBlank(req.getParameter(FIRST_NAME)) 
					? req.getParameter(FIRST_NAME) : StringUtils.EMPTY;
			String lastName = StringUtils.isNotBlank(req.getParameter(LAST_NAME)) 
					? req.getParameter(LAST_NAME) : StringUtils.EMPTY;
			String age = StringUtils.isNotBlank(req.getParameter(AGE))
					? req.getParameter(AGE) : StringUtils.EMPTY;
			String countryCode = StringUtils.isNotBlank(req.getParameter(COUNTRY)) 
					? req.getParameter(COUNTRY) : StringUtils.EMPTY;

			ResourceResolver resourceResolver = req.getResourceResolver();
			Resource userDetailsVarRootNode = ResourceUtil.getOrCreateResource(resourceResolver, 
					VAR_USER_DETAILS, JcrConstants.NT_UNSTRUCTURED, null, Boolean.TRUE);

			Map<String, Object> userInputValues = new HashMap<>();
			userInputValues.put(FIRST_NAME, firstName);
			userInputValues.put(LAST_NAME, lastName);
			userInputValues.put(AGE, age);
			userInputValues.put(COUNTRY, countryCode);
			userInputValues.put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_UNSTRUCTURED);

			resourceResolver.create(userDetailsVarRootNode, "userdetail_" + new Date().getTime(), userInputValues);
			resourceResolver.commit();

			return Boolean.TRUE;
		} catch (PersistenceException pe) {
			LOGGER.error(EXCEPTION_MSG, pe.getMessage());
		}
		return Boolean.FALSE;
	}

}