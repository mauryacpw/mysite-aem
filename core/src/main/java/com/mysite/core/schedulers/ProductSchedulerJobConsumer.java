package com.mysite.core.schedulers;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysite.core.service.NPUtilService;

import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component(
        service = JobConsumer.class,
        property = {
                JobConsumer.PROPERTY_TOPICS + "=mysite/productScheduler"
        }
)
public class ProductSchedulerJobConsumer implements JobConsumer {

    private static final Logger log = LoggerFactory.getLogger(ProductSchedulerJobConsumer.class);

    private final HttpClient client = HttpClient.newHttpClient();

    private static final String PARENT_PATH = "/content/mysite/us/aritclelandingpage";
    private static final String TEMPLATE_PATH = "/conf/mysite/settings/wcm/templates/artical-template";

    @Reference
    private NPUtilService npUtilService;

    @Override
    public JobResult process(Job job) {

        String apiUrl = job.getProperty("apiUrl", String.class);
        log.info("API URL = {}", apiUrl);

        getResponse(apiUrl);

        return JobResult.OK;
    }

    public void getResponse(String apiUrl) {

        try {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            String responseBody = response.body();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootArray = mapper.readTree(responseBody);

            ResourceResolver resolver = npUtilService.getResourceResolver();
            PageManager pageManager = resolver.adaptTo(PageManager.class);

            for (JsonNode node : rootArray) {

                String pageName = String.valueOf(node.get("id").asInt());
                String pageTitle = node.get("title").asText();
                String pageBody = node.get("description").asText();

                String pagePath = PARENT_PATH + "/" + pageName;

                log.info("pageName = {}", pageName);
                log.info("pageTitle = {}", pageTitle);
                log.info("pageBody = {}", pageBody);
                log.info("pagePath = {}", pagePath);

                if (resolver.getResource(pagePath) == null) {

                    assert pageManager != null;
                    Page page = pageManager.create(
                            PARENT_PATH,
                            pageName,
                            TEMPLATE_PATH,
                            pageTitle
                    );

                    log.debug("Page created: {}", page.getPath());

                    Resource contentResource = page.getContentResource();
                    ModifiableValueMap properties = contentResource.adaptTo(ModifiableValueMap.class);

                    assert properties != null;
                    properties.put("jcr:description", pageBody);

                } else {

                    log.debug("Page already exists: {}", pagePath);
                }
            }

            resolver.commit();

        } catch (Exception e) {

            log.error("Error creating pages {}", e.getMessage(), e);
        }
    }
}