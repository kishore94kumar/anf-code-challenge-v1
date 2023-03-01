package com.anf.core.models;

import java.util.Map;
import javax.annotation.PostConstruct;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.anf.core.services.CountriesDropdownService;

/**
 * Model class for a countries dropdown
 * @author Kishore
 *
 */
@Model(adaptables = Resource.class)
public class CountriesDropdownModel {

	@ValueMapValue(injectionStrategy=InjectionStrategy.OPTIONAL)
	protected String countries;

	@OSGiService
	protected CountriesDropdownService countriesDropdownService;

	@PostConstruct
	protected void init() {
		//do nothing
	}

	/**
	 * To get a country code
	 * @return countries
	 */
	public String getCountryCode() {
		return countries;
	}

	/**
	 * To get a countries json as a key value pair. Key is country code and value is country name.
	 * @return map
	 */
	public Map<String, String> getCountriesJSON() {
		return countriesDropdownService.getCountriesJSON();
	}

}
