package com.mysite.core.workflow;

import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.replication.ReplicationStatus;
import io.wcm.testing.mock.aem.junit5.AemContext;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidationProcessTest {

    private final AemContext context = new AemContext();

    private ValidationProcess process;

    @Mock
    private WorkItem workItem;

    @Mock
    private WorkflowSession workflowSession;

    @Mock
    private WorkflowData workflowData;

    @Mock
    private MetaDataMap metaDataMap;

    @Mock
    private ResourceResolver resolver;

    @Mock
    private Resource resource;

    @Mock
    private ReplicationStatus replicationStatus;

    @BeforeEach
    void setUp() {
        process = new ValidationProcess();
    }

    @Test
    void testExecute_published() {
        String path = "/content/mysite/test";

        when(workItem.getWorkflowData()).thenReturn(workflowData);
        when(workflowData.getPayload()).thenReturn(path);
        when(workflowSession.adaptTo(ResourceResolver.class)).thenReturn(resolver);
        when(resolver.getResource(path + "/jcr:content")).thenReturn(resource);
        when(resource.adaptTo(ReplicationStatus.class)).thenReturn(replicationStatus);
        when(replicationStatus.isActivated()).thenReturn(true);

        process.execute(workItem, workflowSession, metaDataMap);

        verify(replicationStatus).isActivated();
    }

    @Test
    void testExecute_notPublished() {
        String path = "/content/mysite/test";

        when(workItem.getWorkflowData()).thenReturn(workflowData);
        when(workflowData.getPayload()).thenReturn(path);
        when(workflowSession.adaptTo(ResourceResolver.class)).thenReturn(resolver);
        when(resolver.getResource(path + "/jcr:content")).thenReturn(resource);
        when(resource.adaptTo(ReplicationStatus.class)).thenReturn(replicationStatus);
        when(replicationStatus.isActivated()).thenReturn(false);

        process.execute(workItem, workflowSession, metaDataMap);

        verify(replicationStatus).isActivated();
    }

    @Test
    void testExecute_noResource() {
        String path = "/content/mysite/test";

        when(workItem.getWorkflowData()).thenReturn(workflowData);
        when(workflowData.getPayload()).thenReturn(path);
        when(workflowSession.adaptTo(ResourceResolver.class)).thenReturn(resolver);
        when(resolver.getResource(path + "/jcr:content")).thenReturn(null);

        process.execute(workItem, workflowSession, metaDataMap);

        verifyNoInteractions(replicationStatus);
    }

    @Test
    void testExecute_exception() {
        when(workItem.getWorkflowData()).thenReturn(workflowData);
        when(workflowData.getPayload()).thenReturn("/content/mysite/test");
        when(workflowSession.adaptTo(ResourceResolver.class)).thenThrow(new RuntimeException());

        process.execute(workItem, workflowSession, metaDataMap);

        verifyNoInteractions(replicationStatus);
    }
}