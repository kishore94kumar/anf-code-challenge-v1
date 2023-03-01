package com.anf.core.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

/**
 * NewsFeedModelTest class
 * @author Kishore
 *
 */
@ExtendWith(AemContextExtension.class)
class NewsFeedModelTest {

    private final AemContext aemContext = new AemContext();
    NewsFeedModel model = new NewsFeedModel();

    private static final String TEST_VAR_PATH = "/var/commerce/products/anf-code-challenge";
    private static final String TEST_VAR_DATA_PATH = TEST_VAR_PATH + "/data/newsData";

    /**
     * Model adapter to sample json
     * @throws Exception
     */
    @BeforeEach
    public void setUp() throws Exception {
        aemContext.addModelsForClasses(NewsFeedModel.class);
        aemContext.load().json("/content/anf-code-challenge/us/en/news.json", TEST_VAR_PATH);
        model = aemContext.currentResource(TEST_VAR_PATH + "/newsList").adaptTo(NewsFeedModel.class);
    }

    /**
     * Test method for news feed path
     */
    @Test
    void testGetNewsFeedPath() {
        assertEquals(TEST_VAR_DATA_PATH, model.getNewsFeedPath());
    }

    /**
     * Test method for news list
     */
    @Test
    void testNewsList() {
       aemContext.currentResource(TEST_VAR_DATA_PATH);
       assertEquals(5, model.getNewsList().size());
    }

}