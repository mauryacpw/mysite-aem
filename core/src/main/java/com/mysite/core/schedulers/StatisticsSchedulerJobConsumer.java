package com.mysite.core.schedulers;


import com.day.cq.dam.api.AssetManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysite.core.service.NPUtilService;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.*;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Component(service = JobConsumer.class, immediate = true, property = {
        JobConsumer.PROPERTY_TOPICS + "=mysite/StatisticsScheduler"
})
public class StatisticsSchedulerJobConsumer implements JobConsumer {
    private final static String REPORT_PATH="/content/dam/mysite/JSON-Report";

    @Reference
    NPUtilService npUtilService;

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private int totalPages = 0;
    private int totalAssets = 0;
    private int totalPagesWithoutTitle = 0;
    private int totalPagesWithoutDescription = 0;

    @Override
    public JobResult process(Job job) {

        totalPages = 0;
        totalAssets = 0;
        totalPagesWithoutTitle = 0;
        totalPagesWithoutDescription = 0;
        String projectDamPath = job.getProperty("projectDamPath", String.class);
        String projectContentPath = job.getProperty("projectContentPath", String.class);

        ResourceResolver resolver = npUtilService.getResourceResolver();

        try {

            totalPages = findTotalPages(projectContentPath, resolver);
            totalAssets = findTotalAssets(projectDamPath, resolver);

            log.info("Total Pages: {}", totalPages);
            log.info("Total Assets: {}", totalAssets);
            log.info("Pages without title: {}", totalPagesWithoutTitle);
            log.info("Pages without description: {}", totalPagesWithoutDescription);

        } catch (Exception e) {
            log.error("Error generating statistics", e);
            return JobResult.FAILED;
        }
        try {
            createJsonReport(resolver);
        } catch (PersistenceException e) {
            throw new RuntimeException(e);
        }
        return JobResult.OK;
    }

    int findTotalPages(String projectContentPath, ResourceResolver resolver) {

        Resource root = resolver.getResource(projectContentPath);

        if (root == null) {
            log.error("Invalid content path {}", projectContentPath);
            return 0;
        }

        traversePages(root);

        return totalPages;
    }

    private void traversePages(Resource resource) {

        if ("cq:Page".equals(resource.getResourceType()) ||
                resource.isResourceType("cq:Page")) {

            totalPages++;

            Resource contentNode = resource.getChild("jcr:content");

            if (contentNode != null) {

                ValueMap vm = contentNode.getValueMap();

                String title = vm.get("jcr:title", String.class);
                String description = vm.get("jcr:description", String.class);

                if (title == null || title.isEmpty()) {
                    totalPagesWithoutTitle++;
                }

                if (description == null || description.isEmpty()) {
                    totalPagesWithoutDescription++;
                }
            }
        }

        for (Resource child : resource.getChildren()) {
            traversePages(child);
        }
    }

    int findTotalAssets(String projectDamPath, ResourceResolver resolver) {

        Resource root = resolver.getResource(projectDamPath);

        if (root == null) {
            log.error("Invalid DAM path {}", projectDamPath);
            return 0;
        }

        traverseAssets(root);

        return totalAssets;
    }

    private void traverseAssets(Resource resource) {

        if ("dam:Asset".equals(resource.getResourceType()) ||
                resource.isResourceType("dam:Asset")) {

            totalAssets++;
        }

        for (Resource child : resource.getChildren()) {
            traverseAssets(child);
        }
    }




    public void createJsonReport(ResourceResolver resolver) throws PersistenceException {

        try {

            Map<String,Object> report = new HashMap<>();
            report.put("Total Pages", totalPages);
            report.put("Pages without title", totalPagesWithoutTitle);
            report.put("Pages without description", totalPagesWithoutDescription);
            report.put("Total Assets", totalAssets);

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(report);
            LocalDate today = LocalDate.now();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM-dd", Locale.ENGLISH);

            String fDate = today.format(formatter).toUpperCase();

            AssetManager assetManager = resolver.adaptTo(AssetManager.class);

            String assetpath = REPORT_PATH+"/"+fDate+".json";

            String path =REPORT_PATH ;

            Resource folder = ResourceUtil.getOrCreateResource(
                    resolver, path, "sling:Folder", "sling:Folder", true);

            Resource file = resolver.getResource(path + fDate+".json");

            if (file == null) {

                Map<String, Object> fileProps = new HashMap<>();
                fileProps.put("jcr:primaryType", "nt:file");

                Resource fileNode = resolver.create(folder, fDate+".json", fileProps);

                Map<String, Object> contentProps = new HashMap<>();
                contentProps.put("jcr:primaryType", "nt:resource");
                contentProps.put("jcr:mimeType", "application/json");
                contentProps.put("jcr:data", new java.io.ByteArrayInputStream(json.getBytes()));

                resolver.create(fileNode, "jcr:content", contentProps);

            } else {

                Resource content = file.getChild("jcr:content");

                content.adaptTo(ModifiableValueMap.class)
                        .put("jcr:data", new java.io.ByteArrayInputStream(json.getBytes()));
            }

            resolver.commit();
    } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


    } }
