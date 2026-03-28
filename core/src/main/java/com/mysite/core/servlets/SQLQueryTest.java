package com.mysite.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;

import javax.jcr.query.Query;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Iterator;


@Component(service = Servlet.class, immediate = true)
@SlingServletPaths("/bin/sql")
public class SQLQueryTest extends SlingSafeMethodsServlet {

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {

        ResourceResolver resolver = request.getResourceResolver();

        String sql = "SELECT * FROM [cq:Component] AS s \n" +
                "where s.[componentGroup]= 'custom-components'";

        Iterator<Resource> res = resolver.findResources(sql, Query.JCR_SQL2);

        JsonObjectBuilder  builder = Json.createObjectBuilder();
        while (res.hasNext()) {
            Resource resource = res.next();
            String name = resource.getName();
            builder.add(name, Json.createObjectBuilder().add("name", name));
        }
        response.setContentType("application/json");
        response.getWriter().println(builder.build().toString());

    }
}
