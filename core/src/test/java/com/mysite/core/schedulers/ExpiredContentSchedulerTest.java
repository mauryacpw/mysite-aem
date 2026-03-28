package com.mysite.core.schedulers;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.model.WorkflowModel;
import com.day.cq.replication.ReplicationStatus;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.mysite.core.service.NPUtilService;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.lang.reflect.Field;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ExpiredContentSchedulerTest {

    private ExpiredContentScheduler scheduler;

    private Scheduler slingScheduler;
    private QueryBuilder queryBuilder;
    private NPUtilService utilService;

    private ResourceResolver resolver;
    private Session session;
    private Query query;
    private SearchResult result;
    private WorkflowSession workflowSession;
    private WorkflowModel workflowModel;
    private WorkflowData workflowData;
    private Hit hit;
    private Resource resource;
    private ReplicationStatus replicationStatus;
    private ScheduleOptions options;

    @BeforeEach
    void setUp() throws Exception {
        scheduler = new ExpiredContentScheduler();

        slingScheduler = mock(Scheduler.class);
        queryBuilder = mock(QueryBuilder.class);
        utilService = mock(NPUtilService.class);

        resolver = mock(ResourceResolver.class);
        session = mock(Session.class);
        query = mock(Query.class);
        result = mock(SearchResult.class);
        workflowSession = mock(WorkflowSession.class);
        workflowModel = mock(WorkflowModel.class);
        workflowData = mock(WorkflowData.class);
        hit = mock(Hit.class);
        resource = mock(Resource.class);
        replicationStatus = mock(ReplicationStatus.class);
        options = mock(ScheduleOptions.class);

        Field f1 = ExpiredContentScheduler.class.getDeclaredField("scheduler");
        f1.setAccessible(true);
        f1.set(scheduler, slingScheduler);

        Field f2 = ExpiredContentScheduler.class.getDeclaredField("queryBuilder");
        f2.setAccessible(true);
        f2.set(scheduler, queryBuilder);

        Field f3 = ExpiredContentScheduler.class.getDeclaredField("npUtilService");
        f3.setAccessible(true);
        f3.set(scheduler, utilService);
    }

    @Test
    void testActivate_enabled() {
        ExpiredContentSchedulerConfig config = mock(ExpiredContentSchedulerConfig.class);

        when(config.enable()).thenReturn(true);
        when(config.schedulerName()).thenReturn("test");
        when(config.cronExpression()).thenReturn("0 * * * * ?");
        when(slingScheduler.EXPR(anyString())).thenReturn(options);

        scheduler.activate(config);

        verify(slingScheduler).schedule(eq(scheduler), eq(options));
    }

    @Test
    void testActivate_disabled() {
        ExpiredContentSchedulerConfig config = mock(ExpiredContentSchedulerConfig.class);

        when(config.enable()).thenReturn(false);
        when(config.schedulerName()).thenReturn("test");

        scheduler.activate(config);

        verify(slingScheduler).unschedule("test");
    }

    @Test
    void testRun_success() throws RepositoryException, WorkflowException {
        when(utilService.getResourceResolver()).thenReturn(resolver);
        when(resolver.adaptTo(Session.class)).thenReturn(session);
        when(resolver.adaptTo(WorkflowSession.class)).thenReturn(workflowSession);

        when(queryBuilder.createQuery(any(), any())).thenReturn(query);
        when(query.getResult()).thenReturn(result);
        when(result.getHits()).thenReturn(List.of(hit));

        when(hit.getPath()).thenReturn("/content/test");
        when(resolver.getResource("/content/test/jcr:content")).thenReturn(resource);

        when(resource.adaptTo(ReplicationStatus.class)).thenReturn(replicationStatus);
        when(replicationStatus.isActivated()).thenReturn(true);

        when(workflowSession.getModel(anyString())).thenReturn(workflowModel);
        when(workflowSession.newWorkflowData(anyString(), anyString())).thenReturn(workflowData);

        when(resolver.isLive()).thenReturn(true);

        scheduler.run();

        verify(workflowSession).startWorkflow(workflowModel, workflowData);
        verify(resolver).close();
    }

    @Test
    void testRun_modelNull() throws WorkflowException {
        when(utilService.getResourceResolver()).thenReturn(resolver);
        when(resolver.adaptTo(Session.class)).thenReturn(session);
        when(resolver.adaptTo(WorkflowSession.class)).thenReturn(workflowSession);

        when(queryBuilder.createQuery(any(), any())).thenReturn(query);
        when(query.getResult()).thenReturn(result);
        when(workflowSession.getModel(anyString())).thenReturn(null);

        when(resolver.isLive()).thenReturn(true);

        scheduler.run();

        verify(workflowSession, never()).startWorkflow(any(), any());
    }

    @Test
    void testRun_resourceNull() throws RepositoryException, WorkflowException {
        when(utilService.getResourceResolver()).thenReturn(resolver);
        when(resolver.adaptTo(Session.class)).thenReturn(session);
        when(resolver.adaptTo(WorkflowSession.class)).thenReturn(workflowSession);

        when(queryBuilder.createQuery(any(), any())).thenReturn(query);
        when(query.getResult()).thenReturn(result);
        when(result.getHits()).thenReturn(List.of(hit));

        when(hit.getPath()).thenReturn("/content/test");
        when(resolver.getResource(anyString())).thenReturn(null);

        when(workflowSession.getModel(anyString())).thenReturn(workflowModel);
        when(resolver.isLive()).thenReturn(true);

        scheduler.run();

        verify(workflowSession, never()).startWorkflow(any(), any());
    }

    @Test
    void testRun_notActivated() throws RepositoryException, WorkflowException {
        when(utilService.getResourceResolver()).thenReturn(resolver);
        when(resolver.adaptTo(Session.class)).thenReturn(session);
        when(resolver.adaptTo(WorkflowSession.class)).thenReturn(workflowSession);

        when(queryBuilder.createQuery(any(), any())).thenReturn(query);
        when(query.getResult()).thenReturn(result);
        when(result.getHits()).thenReturn(List.of(hit));

        when(hit.getPath()).thenReturn("/content/test");
        when(resolver.getResource(anyString())).thenReturn(resource);

        when(resource.adaptTo(ReplicationStatus.class)).thenReturn(replicationStatus);
        when(replicationStatus.isActivated()).thenReturn(false);

        when(workflowSession.getModel(anyString())).thenReturn(workflowModel);
        when(resolver.isLive()).thenReturn(true);

        scheduler.run();

        verify(workflowSession, never()).startWorkflow(any(), any());
    }

    @Test
    void testRun_statusNull() throws RepositoryException, WorkflowException {
        when(utilService.getResourceResolver()).thenReturn(resolver);
        when(resolver.adaptTo(Session.class)).thenReturn(session);
        when(resolver.adaptTo(WorkflowSession.class)).thenReturn(workflowSession);

        when(queryBuilder.createQuery(any(), any())).thenReturn(query);
        when(query.getResult()).thenReturn(result);
        when(result.getHits()).thenReturn(List.of(hit));

        when(hit.getPath()).thenReturn("/content/test");
        when(resolver.getResource(anyString())).thenReturn(resource);

        when(resource.adaptTo(ReplicationStatus.class)).thenReturn(null);

        when(workflowSession.getModel(anyString())).thenReturn(workflowModel);
        when(resolver.isLive()).thenReturn(true);

        scheduler.run();

        verify(workflowSession, never()).startWorkflow(any(), any());
    }

    @Test
    void testRun_exception() {
        when(utilService.getResourceResolver()).thenThrow(new RuntimeException());

        try {
            scheduler.run();
        } catch (Exception ignored) {
        }
    }

    @Test
    void testRun_hitException() throws RepositoryException, WorkflowException {
        when(utilService.getResourceResolver()).thenReturn(resolver);
        when(resolver.adaptTo(Session.class)).thenReturn(session);
        when(resolver.adaptTo(WorkflowSession.class)).thenReturn(workflowSession);

        when(queryBuilder.createQuery(any(), any())).thenReturn(query);
        when(query.getResult()).thenReturn(result);
        when(result.getHits()).thenReturn(List.of(hit));

        when(hit.getPath()).thenThrow(new RuntimeException());

        when(workflowSession.getModel(anyString())).thenReturn(workflowModel);
        when(resolver.isLive()).thenReturn(true);

        scheduler.run();
    }
}