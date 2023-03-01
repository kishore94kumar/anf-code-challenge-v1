package com.anf.core.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import com.anf.core.pojo.News;

/**
 * NewsFeedModel component model
 * @author Kishore
 *
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class NewsFeedModel {

    @ValueMapValue
    @Default(values = "/var/commerce/products/anf-code-challenge/newsData")
    public String newsFeedPath;

    private List<News> newsList = new ArrayList<>();

    @SlingObject
    private ResourceResolver resourceResolver;

    @PostConstruct
    protected void init(){
        Resource resourceData = resourceResolver.getResource(newsFeedPath);
        if(resourceData != null) {
            Iterator<Resource> newsItems = resourceData.listChildren();
            while(newsItems.hasNext()) {
                Resource newsFeedItemResource = newsItems.next();
                newsList.add(getNewsItem(newsFeedItemResource));
            }
        }
    }

    /**
     * To get a news items properties and assign to News object
     * @param newsFeedItemResource
     * @return news
     */
    private News getNewsItem(Resource newsFeedItemResource) {
        ValueMap valueMap = newsFeedItemResource.getValueMap();

        News news = new News();
        news.setAuthor(valueMap.get("author", String.class));
        news.setContent(valueMap.get("content", String.class));
        news.setDescription(valueMap.get("description", String.class));
        news.setTitle(valueMap.get("title", String.class));
        news.setUrl(valueMap.get("url", String.class));
        news.setUrlImage(valueMap.get("urlImage", String.class));

        return news;
    }

    /**
     * To get a news feed path
     * @return newsFeedPath
     */
    public String getNewsFeedPath() {
        return newsFeedPath;
    }

    /**
     * To get a news list items
     * @return newsList
     */
    public List<News> getNewsList() {
        return newsList;
    }

}
