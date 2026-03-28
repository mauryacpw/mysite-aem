package com.mysite.core.schedulers;

import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.Replicator;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.mysite.core.service.NPUtilService;
import org.apache.sling.api.resource.*;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Component(service = JobConsumer.class, immediate = true,
        property = {
                JobConsumer.PROPERTY_TOPICS + "=mysite/ExpiryScheduler"
        }
)
public class ScheduledContentExpiryJobConsumer implements JobConsumer {

    private static final Logger log = LoggerFactory.getLogger(ScheduledContentExpiryJobConsumer.class);

    @Reference
    private NPUtilService npService;

    @Reference
    private QueryBuilder queryBuilder;

    @Reference
    private Replicator replicator;

    @Override
    public JobResult process(Job job) {

        Object rootObj = job.getProperty("rootPath");
        if (rootObj == null) {
            log.error("rootPath is missing in job");
            return JobResult.FAILED;
        }

        String rootPath = rootObj.toString();

        ResourceResolver resolver = null;

        try {
            resolver = npService.getResourceResolver();

            if (resolver == null || !resolver.isLive()) {
                log.error("Resolver is null or closed");
                return JobResult.FAILED;
            }

            Session session = resolver.adaptTo(Session.class);
            if (session == null) {
                log.error("Session is null");
                return JobResult.FAILED;
            }

            Map<String, String> map = new HashMap<>();

            map.put("path", rootPath);
            map.put("type", "cq:Page");
            map.put("p.limit", "-1");

            map.put("1_property", "jcr:content/expiry-date");
            map.put("1_property.operation", "exists");

            map.put("2_daterange.property", "jcr:content/expiry-date");
            map.put("2_daterange.upperBound",
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").format(new Date()));
            map.put("2_daterange.upperOperation", "<=");

            map.put("3_property", "jcr:content/cq:lastReplicationAction");
            map.put("3_property.value", "Activate");

            Query query = queryBuilder.createQuery(PredicateGroup.create(map), session);
            SearchResult result = query.getResult();

            long total = result.getTotalMatches();
            log.info("Total pages found: {}", total);

            StringBuilder expiryLog = new StringBuilder();
            AtomicBoolean hasUnpublished = new AtomicBoolean(false);

            DateTimeFormatter tsFormatter =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            result.getHits().forEach(hit -> {
                try {
                    String path = hit.getPath();

                    replicator.replicate(session, ReplicationActionType.DEACTIVATE, path);

                    String timestamp = java.time.LocalDateTime.now().format(tsFormatter);

                    log.info("Unpublished: {}", path);

                    expiryLog.append(timestamp)
                            .append(" | Unpublished Page: ")
                            .append(path)
                            .append("\n");

                    hasUnpublished.set(true);

                } catch (Exception e) {
                    try {
                        log.error("Error processing: {}", hit.getPath(), e);
                    } catch (RepositoryException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });

            if (hasUnpublished.get()) {
                createLogReport(resolver, expiryLog);
            }

            createLogReport(resolver, expiryLog);

            return JobResult.OK;

        } catch (Exception e) {
            log.error("Job failed", e);
            return JobResult.FAILED;

        } finally {
            if (resolver != null && resolver.isLive()) {
                resolver.close();
            }
        }
    }

    private void createLogReport(ResourceResolver resolver, StringBuilder expiryLog)
            throws PersistenceException {

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM-dd", Locale.ENGLISH);
        String fDate = today.format(formatter).toUpperCase();

        String path = "/var/audit/logs";

        Resource folder = ResourceUtil.getOrCreateResource(
                resolver, path, "sling:Folder", "sling:Folder", true);

        String fileName = fDate + ".log";

        Resource file = resolver.getResource(path + "/" + fileName);

        String newContent = expiryLog.toString();

        if (file == null) {

            Map<String, Object> fileProps = new HashMap<>();
            fileProps.put("jcr:primaryType", "nt:file");

            Resource fileNode = resolver.create(folder, fileName, fileProps);

            Map<String, Object> contentProps = new HashMap<>();
            contentProps.put("jcr:primaryType", "nt:resource");
            contentProps.put("jcr:mimeType", "text/plain");
            contentProps.put("jcr:data",
                    new java.io.ByteArrayInputStream(newContent.getBytes(StandardCharsets.UTF_8)));

            resolver.create(fileNode, "jcr:content", contentProps);

        } else {

            Resource content = file.getChild("jcr:content");

            if (content == null) {
                throw new IllegalStateException("jcr:content missing for " + file.getPath());
            }

            ModifiableValueMap mvm = content.adaptTo(ModifiableValueMap.class);

            String existingContent = "";

            try (java.io.InputStream is = content.adaptTo(java.io.InputStream.class)) {
                if (is != null) {
                    existingContent = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                }
            } catch (Exception e) {
                throw new RuntimeException("Error reading existing log", e);
            }

            String updatedContent = existingContent
                    + (existingContent.endsWith("\n") ? "" : "\n")
                    + newContent;

            mvm.put("jcr:data",
                    new java.io.ByteArrayInputStream(updatedContent.getBytes(StandardCharsets.UTF_8)));
        }

        resolver.commit();
    }
}