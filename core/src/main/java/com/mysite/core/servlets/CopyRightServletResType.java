package com.mysite.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

@Component(service = Servlet.class)
@SlingServletResourceTypes(resourceTypes = "mysite/components/custom-components/copy-rights", methods = "GET")
public class CopyRightServletResType extends SlingSafeMethodsServlet {

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {

        Resource resource = request.getResource();

        if (resource != null) {

            ValueMap values = resource.getValueMap();

            String componentText = values.get("componentText", "");
            String copyrightText = values.get("copyrightText", "");

            JsonObjectBuilder jsonBuilder = Json.createObjectBuilder().add("componentText", componentText).add("copyrightText", copyrightText);

            response.setContentType("application/json");
            response.getWriter().write(jsonBuilder.build().toString());

        }
    }
}