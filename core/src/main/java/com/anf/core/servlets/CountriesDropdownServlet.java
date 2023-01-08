package com.anf.core.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.iterators.TransformIterator;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.anf.core.services.CountriesDropdownService;
import com.day.cq.commons.jcr.JcrConstants;

/**
 * Servlet to fetch a country dropdown details from country dam node
 * @author Kishore
 */
@Component(service = { Servlet.class })
@SlingServletResourceTypes(
        resourceTypes="/apps/anf-code-challenge/components/formsoptions/countries",
        methods=HttpConstants.METHOD_GET)
@ServiceDescription("Countries Dropdown Datasource for Countries Component")
public class CountriesDropdownServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;
    private static final String TEXT = "text";
    private static final String VALUE = "value";
    private Map<String, String> countriesMap = new LinkedHashMap<>();

    @Reference
    private transient CountriesDropdownService countriesDropdownService;

    /**
     * To fetch a country dropdown and assign the valuemap as text value pair for dynamic dropdown
     * @param - req
     * @param - resp
     */
    @Override
    protected void doGet(final SlingHttpServletRequest req,
            final SlingHttpServletResponse resp) throws ServletException, IOException {

        ResourceResolver resolver = req.getResourceResolver();
        countriesMap = countriesDropdownService.getCountriesJSON();

        @SuppressWarnings({"unchecked", "rawtypes"})
        DataSource ds = new SimpleDataSource(new TransformIterator<>(countriesMap.keySet().iterator(), (Transformer) o -> {
            String countryName = (String) o;
            ValueMap vm = new ValueMapDecorator(new HashMap<>());
            vm.put(TEXT, countriesMap.get(countryName));
            vm.put(VALUE, countryName);
            return new ValueMapResource(resolver, new ResourceMetadata(), JcrConstants.NT_UNSTRUCTURED, vm);
        }));
        req.setAttribute(DataSource.class.getName(), ds);
    }

}
