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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AemContextExtension.class)
class ProductDetailImplTest {

    private final AemContext context = new AemContext();
    private JsonNode json;

    @BeforeEach
    void setUp() throws Exception {
        context.addModelsForClasses(ProductDetailImpl.class, ProductCardImpl.class);

        ObjectMapper mapper = new ObjectMapper();
        InputStream is = getClass().getClassLoader()
                .getResourceAsStream("product-detail-test.json");

        assertNotNull(is, "JSON file not found in test resources");
        json = mapper.readTree(is);
    }

    @Test
    void testBasicProperties_FromJson() {

        JsonNode basic = json.get("basicProperties");

        Resource resource = context.create().resource("/content/product",
                "title", basic.get("title").asText(),
                "description", basic.get("description").asText(),
                "status", basic.get("status").asBoolean(),
                "category", basic.get("category").asText(),
                "showCF", basic.get("showCF").asBoolean()
        );

        ProductDetailImpl model = resource.adaptTo(ProductDetailImpl.class);

        assertNotNull(model);
        assertEquals(basic.get("title").asText(), model.getTitle());
        assertEquals(basic.get("description").asText(), model.getDescription());
        assertEquals(basic.get("status").asBoolean(), model.isStatus());
        assertEquals(basic.get("category").asText(), model.getCategory());
        assertEquals(basic.get("showCF").asBoolean(), model.isShowCF());
        assertEquals(basic.get("exportedType").asText(), model.getExportedType());
    }

    @Test
    void testContentFragmentProducts_FromJson() {

        JsonNode cf1 = json.get("contentFragments").get("cf1");
        JsonNode cf2 = json.get("contentFragments").get("cf2");

        // Create CF resources
        context.create().resource(cf1.get("path").asText() + "/jcr:content/data/master",
                "productTitle", cf1.get("productTitle").asText());

        context.create().resource(cf2.get("path").asText() + "/jcr:content/data/master",
                "productTitle", cf2.get("productTitle").asText());

        Resource resource = context.create().resource("/content/product",
                "showCF", true,
                "contentFragments", new String[]{
                        cf1.get("path").asText(),
                        cf2.get("path").asText()
                }
        );

        ProductDetailImpl model = resource.adaptTo(ProductDetailImpl.class);

        List<ProductCardImpl> cfProducts = model.getContentFragmentProducts();

        assertNotNull(cfProducts);
        assertEquals(2, cfProducts.size());
    }

    @Test
    void testContentFragmentProducts_NullWhenShowCFFalse() {

        Resource resource = context.create().resource("/content/product",
                "showCF", false,
                "contentFragments", new String[]{"/content/dam/cf1"}
        );

        ProductDetailImpl model = resource.adaptTo(ProductDetailImpl.class);

        assertNull(model.getContentFragmentProducts());
    }

    @Test
    void testContentFragmentProducts_EmptyWhenInvalidPath() {

        Resource resource = context.create().resource("/content/product",
                "showCF", true,
                "contentFragments", new String[]{"/invalid/path"}
        );

        ProductDetailImpl model = resource.adaptTo(ProductDetailImpl.class);

        List<ProductCardImpl> cfProducts = model.getContentFragmentProducts();

        assertNotNull(cfProducts);
        assertTrue(cfProducts.isEmpty());
    }

    @Test
    void testContentFragmentsGetter() {

        String[] fragments = {"/cf1", "/cf2"};

        Resource resource = context.create().resource("/content/product",
                "contentFragments", fragments
        );

        ProductDetailImpl model = resource.adaptTo(ProductDetailImpl.class);

        assertArrayEquals(fragments, model.getContentFragments());
    }
}