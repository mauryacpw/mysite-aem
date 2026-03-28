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

class TestServeltTest {

    private final AemContext context = new AemContext();
    private TestServelt servlet;

    @BeforeEach
    void setUp() {
        servlet = new TestServelt();

        context.create().resource("/content/testNode");
    }

    @Test
    void testDoGet() throws Exception {

        Resource resource = context.resourceResolver().getResource("/content/testNode");

        SlingHttpServletRequest request = mock(SlingHttpServletRequest.class);
        SlingHttpServletResponse response = mock(SlingHttpServletResponse.class);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        when(request.getResourceResolver()).thenReturn(context.resourceResolver());
        when(request.getResource()).thenReturn(resource);
        when(request.getAuthType()).thenReturn("BASIC");
        when(response.getWriter()).thenReturn(writer);

        servlet.doGet(request, response);

        writer.flush();
        String result = stringWriter.toString();

        assertTrue(result.contains("BASIC"));
        assertTrue(result.contains("/content/testNode"));
        assertTrue(result.contains("testNode"));
        verify(response).setContentType("application/json");
    }

    @Test
    void testDoPost() throws Exception {

        SlingHttpServletRequest request = mock(SlingHttpServletRequest.class);
        SlingHttpServletResponse response = mock(SlingHttpServletResponse.class);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        when(request.getParameter("nodeName")).thenReturn("node1");
        when(request.getParameter("title")).thenReturn("Test Title");
        when(request.getParameter("description")).thenReturn("Test Description");
        when(response.getWriter()).thenReturn(writer);

        servlet.doPost(request, response);

        writer.flush();
        String result = stringWriter.toString();

        assertTrue(result.contains("node1"));
        assertTrue(result.contains("Test Title"));
        assertTrue(result.contains("Test Description"));
        verify(response).setContentType("application/json");
    }
}