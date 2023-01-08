package com.anf.core.services.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.anf.core.services.CountriesDropdownService;

/**
 * To get a countries map from a dam node
 * @author Kishore
 *
 */
@Component(immediate = true, service = CountriesDropdownService.class)
public class CountriesDropdownServiceImpl implements CountriesDropdownService {

	private static final Logger LOGGER = LoggerFactory.getLogger(CountriesDropdownServiceImpl.class);

	private static final String COUNTRIES_JSON_DAM_PATH = "/content/dam/anf-code-challenge/exercise-1/countries.json";
	private static final String COUNTRIES = "countries";
	private static final String CODE = "code";
	private static final String NAME = "name";

	/**
	 * resolverFactory object
	 */
	@Reference
	private ResourceResolverFactory resolverFactory;

	/**
	 * Method to get a countries list from a anf dam node
	 * @return countriesMap
	 */
	@Override
	public Map<String,String> getCountriesJSON() {
		Map<String, String> countriesMap = new LinkedHashMap<>();
		try {
			Map<String, Object> param = new HashMap<>();
			param.put(ResourceResolverFactory.SUBSERVICE, "anfService");
			ResourceResolver resolver = resolverFactory.getServiceResourceResolver(param);

			Resource countriesJSONResource = resolver.getResource(COUNTRIES_JSON_DAM_PATH);
			if(countriesJSONResource !=null) {
				InputStream content = countriesJSONResource.adaptTo(InputStream.class);
				StringBuilder sb = new StringBuilder();
				String line;
				BufferedReader br = new BufferedReader(new InputStreamReader(content, StandardCharsets.UTF_8));

				while ((line = br.readLine()) != null) {
					sb.append(line);
				}

				JSONObject jsonObj = new JSONObject(sb.toString()); 
				JSONArray countriesArr = jsonObj.getJSONArray(COUNTRIES);      
				for (int i = 0; i < countriesArr.length(); i++) {
					JSONObject countryJSON = (JSONObject) countriesArr.get(i);
					countriesMap.put(countryJSON.get(CODE).toString(), countryJSON.get(NAME).toString());
				}
			}
		} catch(JSONException | LoginException | IOException ex) {
			LOGGER.error("Exception while fetching countries json {}", ex.getMessage());
		}
		return countriesMap;
	}

}
