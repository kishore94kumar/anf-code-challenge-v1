package com.anf.core.servlets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;
import javax.servlet.ServletException;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import com.google.common.collect.ImmutableMap;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith({AemContextExtension.class})
class ANFSubmitUserDetailsServletTest {

	private ANFSubmitUserDetailsServlet anfServlet = new ANFSubmitUserDetailsServlet();

	@Test
	void testDoPost(AemContext context) throws ServletException, IOException {
		MockSlingHttpServletRequest request = context.request();
        MockSlingHttpServletResponse response = context.response();

        request.setParameterMap(
        		ImmutableMap.<String,Object>builder()
        		.put("firstName", "Ramesh")
        		.put("lastName", "kumar")
        		.put("age", "28")
        		.put("country", "IN")
        		.build());

        anfServlet.doPost(request, response);

        assertEquals(200, response.getStatus());
	}


}
