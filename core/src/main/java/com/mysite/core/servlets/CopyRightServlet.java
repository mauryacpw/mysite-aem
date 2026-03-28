package com.mysite.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

@Component(service = Servlet.class)
@SlingServletPaths("/bin/copyRight")
public class CopyRightServlet extends SlingSafeMethodsServlet {

    @Override
    protected void doGet(SlingHttpServletRequest request,SlingHttpServletResponse response) throws ServletException, IOException {
        String path = request.getParameter("path");
        ResourceResolver resolver = request.getResourceResolver();
        Resource resource = resolver.getResource(path);
        if(resource!=null) {
            ValueMap values = resource.adaptTo(ValueMap.class);
            String componentText = values.get("componentText", String.class);
            String copyrightText = values.get("copyrightText", String.class);

            JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();

            jsonBuilder.add("componentText", componentText);
            jsonBuilder.add("copyrightText", copyrightText);

            response.setContentType("application/json");

            response.getWriter().write(jsonBuilder.build().toString());

        }
    }
}
