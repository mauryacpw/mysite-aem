package com.mysite.core.models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AemContextExtension.class)
class ArticleDetailModelTest {

    private final AemContext context = new AemContext();

    private Resource resource;
    private ArticleDetailModel model;
    private JsonNode json;

    @BeforeEach
    void setUp() throws Exception {
        context.addModelsForClasses(ArticleDetailModel.class);

        // Load JSON
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = getClass().getClassLoader()
                .getResourceAsStream("article-detail-test.json");

        assertNotNull(is, "JSON file not found");
        json = mapper.readTree(is);

        JsonNode article = json.get("article");

        // Date setup
        Calendar cal = Calendar.getInstance();
        Date testDate = cal.getTime();

        context.create().resource("/content/article",
                "articleImage", article.get("image").asText(),
                "articleTitle", article.get("title").asText(),
                "articleDescription", article.get("description").asText(),
                "articleDate", testDate
        );

        resource = context.resourceResolver().getResource("/content/article");
        model = resource.adaptTo(ArticleDetailModel.class);
    }

    @Test
    void testModelInitialization() {
        assertNotNull(model);
    }

    @Test
    void testGetters_FromJson() {

        JsonNode article = json.get("article");

        assertEquals(article.get("image").asText(), model.getArticleImage());
        assertEquals(article.get("title").asText(), model.getArticleTitle());
        assertEquals(article.get("description").asText(), model.getArticleDescription());
        assertNotNull(model.getArticleDate());
    }

    @Test
    void testSetArticlePath_FromJson() {

        JsonNode pathTest = json.get("pathTest");

        model.setArticlePath(pathTest.get("inputPath").asText());

        assertEquals(
                pathTest.get("expectedPath").asText(),
                model.getArticlePath()
        );
    }

    @Test
    void testNullValues() {

        Resource res = context.create().resource("/content/empty");
        ArticleDetailModel emptyModel = res.adaptTo(ArticleDetailModel.class);

        assertNotNull(emptyModel);
        assertNull(emptyModel.getArticleImage());
        assertNull(emptyModel.getArticleTitle());
        assertNull(emptyModel.getArticleDescription());
        assertNull(emptyModel.getArticleDate());
    }
}