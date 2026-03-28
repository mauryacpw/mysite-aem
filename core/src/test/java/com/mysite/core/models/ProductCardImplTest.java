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
class ProductCardImplTest {

    private final AemContext context = new AemContext();

    private Resource resource;
    private ProductCardImpl model;
    private JsonNode json;

    @BeforeEach
    void setUp() throws Exception {
        context.addModelsForClasses(ProductCardImpl.class);

        // Load JSON
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = getClass().getClassLoader()
                .getResourceAsStream("product-card-test.json");

        assertNotNull(is, "JSON file not found");
        json = mapper.readTree(is);

        JsonNode product = json.get("product");

        // Create past date
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Date pastDate = cal.getTime();

        context.create().resource("/content/product",
                "productTitle", product.get("title").asText(),
                "productPrice", product.get("price").asInt(),
                "productImage", product.get("image").asText(),
                "productColor", product.get("color").asText(),
                "productTag", new String[]{
                        product.get("tags").get(0).asText(),
                        product.get("tags").get(1).asText()
                },
                "productExpiry", pastDate
        );

        resource = context.resourceResolver().getResource("/content/product");
        model = resource.adaptTo(ProductCardImpl.class);
    }

    @Test
    void testModelInitialization_NotNull() {
        assertNotNull(model);
    }

    @Test
    void testGetters_FromJson() {

        JsonNode product = json.get("product");

        assertEquals(product.get("title").asText(), model.getProductTitle());
        assertEquals(product.get("price").asInt(), model.getProductPrice());
        assertEquals(product.get("image").asText(), model.getProductImage());
        assertEquals(product.get("color").asText(), model.getProductColor());

        assertArrayEquals(
                new String[]{
                        product.get("tags").get(0).asText(),
                        product.get("tags").get(1).asText()
                },
                model.getProductTag()
        );

        assertNotNull(model.getProductExpiry());
    }

    @Test
    void testProductExpired_WhenPastDate() {
        assertTrue(model.isProductExpired());
    }

    @Test
    void testProductNotExpired_WhenFutureDate_FromJson() {

        JsonNode future = json.get("futureProduct");

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, future.get("daysToAdd").asInt());
        Date futureDate = cal.getTime();

        context.create().resource("/content/product2",
                "productTitle", future.get("title").asText(),
                "productExpiry", futureDate
        );

        Resource res = context.resourceResolver().getResource("/content/product2");
        ProductCardImpl futureModel = res.adaptTo(ProductCardImpl.class);

        assertNotNull(futureModel);
        assertFalse(futureModel.isProductExpired());
    }

    @Test
    void testSetters_FromJson() {

        JsonNode setter = json.get("setter");

        ProductCardImpl testModel = new ProductCardImpl();
        Date date = new Date();

        testModel.setProductTitle(setter.get("title").asText());
        testModel.setProductPrice(setter.get("price").asInt());
        testModel.setProductImage(setter.get("image").asText());
        testModel.setProductColor(setter.get("color").asText());
        testModel.setProductTag(new String[]{
                setter.get("tags").get(0).asText(),
                setter.get("tags").get(1).asText()
        });
        testModel.setProductExpiry(date);
        testModel.setProductExpired(true);

        assertEquals(setter.get("title").asText(), testModel.getProductTitle());
        assertEquals(setter.get("price").asInt(), testModel.getProductPrice());
        assertEquals(setter.get("image").asText(), testModel.getProductImage());
        assertEquals(setter.get("color").asText(), testModel.getProductColor());

        assertArrayEquals(
                new String[]{
                        setter.get("tags").get(0).asText(),
                        setter.get("tags").get(1).asText()
                },
                testModel.getProductTag()
        );

        assertEquals(date, testModel.getProductExpiry());
        assertTrue(testModel.isProductExpired());
    }
}