package com.mysite.core.servlets;
import static org.junit.jupiter.api.Assertions.*;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(AemContextExtension.class)
class QuoteServletTest {

    private final AemContext context = new AemContext();

    private QuoteServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = context.registerInjectActivateService(QuoteServlet.class);
    }

    @Test
    void testDoGet() throws Exception {
        servlet.doGet(context.request(), context.response());

        assertEquals("text/html", context.response().getContentType());
        assertTrue(context.response().getOutputAsString().contains("Today's Quote"));
    }
}