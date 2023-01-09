package com.anf.core.listeners;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.PageEvent;
import com.day.cq.wcm.api.PageModification;

@Component(service = EventHandler.class,
		immediate = true,
		property = {
				EventConstants.EVENT_TOPIC + "=" + PageEvent.EVENT_TOPIC
		})
public class PageEventHandler implements EventHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(PageEventHandler.class);

	@Reference
	private ResourceResolverFactory resolverFactory;

	/**
	 * Listens to a new page creation and add a pageCreated property
	 * @param event
	 */
	@Override
	public void handleEvent(Event event) {
		Iterator<PageModification> pageInfo = PageEvent.fromEvent(event).getModifications();
		while (pageInfo.hasNext()) {
			PageModification pageModification = pageInfo.next();

			if ( (pageModification.getType().equals(PageModification.ModificationType.CREATED)) &&
					(pageModification.getPath().contains("/content/anf-code-challenge/us/en")) ) {
				setPageProperty(pageModification.getPath());
			}
		}
	}

	/**
	 * To set a pageCreated property
	 * @param path
	 */
	private void setPageProperty(String path) {
		Map<String, Object> param = new HashMap<>();
		param.put(ResourceResolverFactory.SUBSERVICE, "anfEventWriteService");

		try {
			ResourceResolver resourceResolver = resolverFactory.getServiceResourceResolver(param);
			Resource pageResource = resourceResolver.getResource(path + "/" + JcrConstants.JCR_CONTENT);
			if(pageResource != null) {
				ModifiableValueMap map = pageResource.adaptTo(ModifiableValueMap.class);
				map.put("pageCreated", Boolean.TRUE);
				resourceResolver.commit();
			}
		} catch (PersistenceException | LoginException e) {
			LOGGER.error("Error while adding page property on creation {}", path);
		}

	}

}
