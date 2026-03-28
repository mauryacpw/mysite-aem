package com.mysite.core.servlets;

import io.wcm.testing.mock.aem.junit5.AemContext;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CopyRightServletTest {

    private final AemContext context = new AemContext();
    private CopyRightServlet servlet;

    @BeforeEach
    void setUp() {
        servlet = new CopyRightServlet();

        context.create().resource("/content/test",
                "componentText", "Hello",
                "copyrightText", "© 2026");
    }

    @Test
    void testDoGet_validResource() throws Exception {

        SlingHttpServletRequest request = mock(SlingHttpServletRequest.class);
        SlingHttpServletResponse response = mock(SlingHttpServletResponse.class);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        when(request.getParameter("path")).thenReturn("/content/test");
        when(request.getResourceResolver()).thenReturn(context.resourceResolver());
        when(response.getWriter()).thenReturn(writer);

        servlet.doGet(request, response);

        writer.flush();
        String result = stringWriter.toString();

        assertTrue(result.contains("Hello"));
        assertTrue(result.contains("© 2026"));
        verify(response).setContentType("application/json");
    }

    @Test
    void testDoGet_resourceNotFound() throws Exception {

        SlingHttpServletRequest request = mock(SlingHttpServletRequest.class);
        SlingHttpServletResponse response = mock(SlingHttpServletResponse.class);

        when(request.getParameter("path")).thenReturn("/content/invalid");
        when(request.getResourceResolver()).thenReturn(context.resourceResolver());

        servlet.doGet(request, response);

        verify(response, never()).getWriter();
    }
}