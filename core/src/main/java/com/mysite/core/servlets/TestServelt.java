package com.mysite.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;

import javax.jcr.Node;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

@Component(service = Servlet.class)
@SlingServletPaths("/bin/test")
public class TestServelt extends SlingAllMethodsServlet {

    @Override
    protected void doGet( SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {


        ResourceResolver resolver =  request.getResourceResolver();

        JsonObjectBuilder builder = Json.createObjectBuilder();

        builder.add("AuthType", request.getAuthType());
        builder.add("path", request.getResource().getPath());
        builder.add("name", request.getResource().getName());



        response.setContentType("application/json");

        response.getWriter().write(builder.build().toString());

    }

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {

        String nodeName =request.getParameter("nodeName");
        String title =request.getParameter("title");
        String description =request.getParameter("description");







        JsonObjectBuilder builder = Json.createObjectBuilder();

        builder.add("nodeName", nodeName);
        builder.add("title", title);
        builder.add("description", description);

        response.setContentType("application/json");
        response.getWriter().write(builder.build().toString());
    }
}
