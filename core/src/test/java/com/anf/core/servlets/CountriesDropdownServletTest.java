package com.anf.core.servlets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import com.anf.core.services.impl.CountriesDropdownServiceImpl;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

/**
 * Test class for country dropdown servlet
 * @author Kishore
 *
 */
@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class CountriesDropdownServletTest {

	@InjectMocks
	private CountriesDropdownServlet countriesDropdownServlet = new CountriesDropdownServlet();

	@Mock
	private CountriesDropdownServiceImpl countriesDropdownService;

	/**
	 * Test method a country dropdown doget method
	 * @param context
	 * @throws ServletException
	 * @throws IOException
	 */
	@Test
	void testDoGet(AemContext context) throws ServletException, IOException {
		MockSlingHttpServletRequest request = context.request();
        MockSlingHttpServletResponse response = context.response();

        Map<String, String> countriesMap = new HashMap<>();
        countriesMap.put("INDIA", "IN");
        countriesMap.put("Pakistan", "PK");
        countriesMap.put("United States of America", "US");

        Mockito.when(countriesDropdownService.getCountriesJSON()).thenReturn(countriesMap);

        countriesDropdownServlet.doGet(request, response);

        assertEquals(3, countriesDropdownService.getCountriesJSON().size());

	}

}
