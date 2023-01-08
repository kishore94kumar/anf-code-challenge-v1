package com.anf.core.services;

import java.util.Map;

/**
 * CountriesDropdownService to get a countries dropdown list
 * @author Kishore
 *
 */
public interface CountriesDropdownService {

	/**
	 * Method to get a countries from a content dam node. 
	 * It will return as key value pair. 
	 * Key - country code , value - country name
	 * @return map
	 */
	Map<String,String> getCountriesJSON();
}
