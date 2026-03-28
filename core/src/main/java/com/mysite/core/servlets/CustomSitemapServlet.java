package com.mysite.core.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import javax.jcr.Session;
import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.*;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.day.cq.search.*;
import com.day.cq.search.result.*;
import com.day.cq.wcm.api.Page;
import com.day.cq.replication.ReplicationStatus;

@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.paths=/bin/custom-sitemap",
                "sling.servlet.extensions=xml",
                "sling.servlet.methods=GET"
        }
)
public class CustomSitemapServlet extends SlingSafeMethodsServlet {

    @Reference
    private QueryBuilder queryBuilder;

    @Override
    protected void doGet(SlingHttpServletRequest request,
                         SlingHttpServletResponse response)
            throws IOException {

        response.setContentType("application/xml");
        response.setCharacterEncoding("UTF-8");

        ResourceResolver resolver = request.getResourceResolver();
        Session session = resolver.adaptTo(Session.class);

        PrintWriter writer = response.getWriter();

        writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        writer.println("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">");

        String rootPath = findSitemapRoot(resolver, session);

        if (rootPath == null) {
            writer.println("</urlset>");
            return;
        }

        Map<String, String> map = new HashMap<>();
        map.put("path", rootPath);
        map.put("type", "cq:Page");
        map.put("p.limit", "-1");

        Query query = queryBuilder.createQuery(PredicateGroup.create(map), session);
        SearchResult result = query.getResult();

        for (Hit hit : result.getHits()) {
            try {
                Resource res = hit.getResource();
                Page page = res.adaptTo(Page.class);

                if (page == null) continue;

                ReplicationStatus status = page.adaptTo(ReplicationStatus.class);
                if (status == null || !status.isActivated()) {
                    continue;
                }

                Resource content = page.getContentResource();
                if (content == null) continue;

                ValueMap props = content.getValueMap();

                String[] robots = props.get("cq:robotsTags", String[].class);

                if (robots == null || robots.length == 0) {
                    continue;
                }

                if (Arrays.asList(robots).contains("noindex")) {
                    continue;
                }

                String url = page.getPath() + ".html";

                Calendar cal = props.get("cq:lastModified", Calendar.class);
                if (cal == null) {
                    cal = props.get("jcr:created", Calendar.class);
                }

                String lastMod = cal != null ? cal.toInstant().toString() : "";

                writer.println("<url>");
                writer.println("<loc>" + url + "</loc>");
                writer.println("<lastmod>" + lastMod + "</lastmod>");
                writer.println("</url>");

            } catch (Exception e) {
            }
        }

        writer.println("</urlset>");
    }

    private String findSitemapRoot(ResourceResolver resolver, Session session) {

        Map<String, String> map = new HashMap<>();
        map.put("path", "/content");
        map.put("type", "cq:PageContent");
        map.put("1_property", "sling:sitemapRoot");
        map.put("1_property.value", "true");
        map.put("p.limit", "1");

        Query query = queryBuilder.createQuery(PredicateGroup.create(map), session);
        SearchResult result = query.getResult();

        for (Hit hit : result.getHits()) {
            try {
                return hit.getPath().replace("/jcr:content", "");
            } catch (Exception ignored) {}
        }

        return null;
    }
}