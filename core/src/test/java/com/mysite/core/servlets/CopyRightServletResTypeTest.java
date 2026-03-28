package com.mysite.core.servlets;

import io.wcm.testing.mock.aem.junit5.AemContext;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CopyRightServletResTypeTest {

    private final AemContext context = new AemContext();
    private CopyRightServletResType servlet;

    @BeforeEach
    void setUp() {
        servlet = new CopyRightServletResType();

        context.create().resource("/content/test",
                "sling:resourceType", "mysite/components/custom-components/copy-rights",
                "componentText", "Hello",
                "copyrightText", "© 2026");
    }

    @Test
    void testDoGet_validResource() throws Exception {

        Resource resource = context.resourceResolver().getResource("/content/test");

        SlingHttpServletRequest request = mock(SlingHttpServletRequest.class);
        SlingHttpServletResponse response = mock(SlingHttpServletResponse.class);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        when(request.getResource()).thenReturn(resource);
        when(response.getWriter()).thenReturn(writer);

        servlet.doGet(request, response);

        writer.flush();
        String result = stringWriter.toString();

        assertTrue(result.contains("Hello"));
        assertTrue(result.contains("© 2026"));
        verify(response).setContentType("application/json");
    }

    @Test
    void testDoGet_nullResource() throws Exception {

        SlingHttpServletRequest request = mock(SlingHttpServletRequest.class);
        SlingHttpServletResponse response = mock(SlingHttpServletResponse.class);

        when(request.getResource()).thenReturn(null);

        servlet.doGet(request, response);

        verify(response, never()).getWriter();
    }
}