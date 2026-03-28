package com.mysite.core.schedulers;

import com.day.cq.dam.api.AssetManager;
import com.mysite.core.service.NPUtilService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import org.apache.sling.api.resource.*;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class StatisticsSchedulerJobConsumerTest {

    private final AemContext context = new AemContext();
    private StatisticsSchedulerJobConsumer consumer;
    private NPUtilService npUtilService;

    @BeforeEach
    void setUp() {
        consumer = new StatisticsSchedulerJobConsumer();
        npUtilService = mock(NPUtilService.class);
        context.registerService(NPUtilService.class, npUtilService);
        context.registerAdapter(ResourceResolver.class, AssetManager.class, mock(AssetManager.class));
        context.registerInjectActivateService(consumer);
        when(npUtilService.getResourceResolver()).thenReturn(context.resourceResolver());
    }

    @Test
    void testProcessSuccess() {
        context.create().resource("/content/site/page1",
                "sling:resourceType", "cq:Page");
        context.create().resource("/content/site/page1/jcr:content",
                "jcr:title", "title",
                "jcr:description", "desc");

        context.create().resource("/content/dam/test/asset1",
                "sling:resourceType", "dam:Asset");

        Job job = mock(Job.class);
        when(job.getProperty("projectDamPath", String.class)).thenReturn("/content/dam/test");
        when(job.getProperty("projectContentPath", String.class)).thenReturn("/content/site");

        JobConsumer.JobResult result = consumer.process(job);
        assertEquals(JobConsumer.JobResult.OK, result);
    }



    @Test
    void testFindTotalPagesInvalidPath() {
        int result = consumer.findTotalPages("/invalid", context.resourceResolver());
        assertEquals(0, result);
    }

    @Test
    void testFindTotalPagesAllBranches() {
        context.create().resource("/content/site/page1",
                "sling:resourceType", "cq:Page");
        context.create().resource("/content/site/page1/jcr:content",
                "jcr:title", "",
                "jcr:description", "");

        context.create().resource("/content/site/page2",
                "sling:resourceType", "cq:Page");
        context.create().resource("/content/site/page2/jcr:content",
                "jcr:title", "title",
                "jcr:description", "desc");

        int result = consumer.findTotalPages("/content/site", context.resourceResolver());
        assertEquals(2, result);
    }

    @Test
    void testFindTotalAssetsInvalidPath() {
        int result = consumer.findTotalAssets("/invalid", context.resourceResolver());
        assertEquals(0, result);
    }

    @Test
    void testFindTotalAssetsAllBranches() {
        context.create().resource("/content/dam/test/asset1",
                "sling:resourceType", "dam:Asset");
        context.create().resource("/content/dam/test/asset2",
                "sling:resourceType", "dam:Asset");

        int result = consumer.findTotalAssets("/content/dam/test", context.resourceResolver());
        assertEquals(2, result);
    }

    @Test
    void testCreateJsonReportCreateAndUpdate() throws Exception {
        try (ResourceResolver rr = context.resourceResolver()) {

            StatisticsSchedulerJobConsumer svc = new StatisticsSchedulerJobConsumer();
            NPUtilService util = mock(NPUtilService.class);
            svc.npUtilService = util;
            when(util.getResourceResolver()).thenReturn(rr);

            context.create().resource("/content");
            context.create().resource("/content/dam");
            context.create().resource("/content/dam/mysite");

            svc.createJsonReport(rr);

            Resource folder = rr.getResource("/content/dam/mysite/JSON-Report");
            for (Resource child : folder.getChildren()) {
                rr.delete(child);
            }
            rr.commit();

            svc.createJsonReport(rr);
        }
    }
}