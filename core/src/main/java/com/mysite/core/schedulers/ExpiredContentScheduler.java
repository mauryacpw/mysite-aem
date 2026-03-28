package com.mysite.core.schedulers;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.model.WorkflowModel;
import com.day.cq.replication.ReplicationStatus;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;

import com.mysite.core.service.NPUtilService;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import org.osgi.service.metatype.annotations.Designate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;

@Component(service = Runnable.class, immediate = true)
@Designate(ocd = ExpiredContentSchedulerConfig.class)
public class ExpiredContentScheduler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(ExpiredContentScheduler.class);

    @Reference
    private Scheduler scheduler;

    @Reference
    private QueryBuilder queryBuilder;

    @Reference
    private NPUtilService  npUtilService;

    @Activate
    @Modified
    protected void activate(ExpiredContentSchedulerConfig config) {

        String schedulerName = config.schedulerName();

        if (config.enable()) {

            ScheduleOptions options = scheduler.EXPR(config.cronExpression());
            options.name(schedulerName);
            options.canRunConcurrently(false);

            scheduler.schedule(this, options);

            log.info("Expired Content Scheduler started");

        } else {
            scheduler.unschedule(schedulerName);
            log.info("Expired Content Scheduler disabled");
        }
    }

    @Override
    public void run() {

        ResourceResolver resolver = npUtilService.getResourceResolver();

        try {

            Session session = resolver.adaptTo(Session.class);

            Map<String, String> map = new HashMap<>();

            map.put("path", "/content/mysite/us/aritclelandingpage");
            map.put("type", "cq:Page");

            map.put("p.limit", "-1");

            /* property must exist */
            map.put("1_property", "jcr:content/expiry-date");
            map.put("1_property.operation", "exists");
                map.put("2_daterange.property", "jcr:content/expiry-date");
            map.put("2_daterange.upperBound",
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
                            .format(new Date()));
            map.put("2_daterange.upperOperation", "<=");





            Query query = queryBuilder.createQuery(PredicateGroup.create(map), session);

            SearchResult result = query.getResult();

            WorkflowSession workflowSession = resolver.adaptTo(WorkflowSession.class);

            assert workflowSession != null;
            WorkflowModel model = workflowSession.getModel("/var/workflow/models/scheduled-content-expiry-workflow");

            if (model == null) {
                log.error("Workflow model not found");
                return;
            }

            log.info("Total pages found: {}", result.getHits().size());


            result.getHits().forEach(hit -> {

                try {

                    String pagePath = hit.getPath();

                    Resource resource = resolver.getResource(pagePath + "/jcr:content");

                    if(resource == null){
                        log.warn("jcr:content not found for {}", pagePath);
                        return;
                    }

                    ReplicationStatus status = resource.adaptTo(ReplicationStatus.class);

                    if(status == null || !status.isActivated()){
                        log.info("Page is not published. Skipping workflow for {}", pagePath);
                        return;
                    }

                    log.info("Page is published. Starting workflow for {}", pagePath);

                    WorkflowData workflowData =
                            workflowSession.newWorkflowData("JCR_PATH", pagePath);

                    workflowSession.startWorkflow(model, workflowData);

                } catch (Exception e) {

                    log.error("Error starting workflow", e);

                }

            });

        } catch (Exception e) {

            log.error("Error executing scheduler", e);

        } finally {

            if (resolver != null && resolver.isLive()) {
                resolver.close();
            }

        }
    }
}