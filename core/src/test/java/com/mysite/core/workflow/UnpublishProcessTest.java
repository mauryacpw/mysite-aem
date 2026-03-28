package com.mysite.core.workflow;

import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.replication.Replicator;
import com.day.cq.replication.ReplicationActionType;
import io.wcm.testing.mock.aem.junit5.AemContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.jcr.Session;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnpublishProcessTest {

    private final AemContext context = new AemContext();

    private UnpublishProcess process;

    @Mock
    private WorkItem workItem;

    @Mock
    private WorkflowSession workflowSession;

    @Mock
    private WorkflowData workflowData;

    @Mock
    private MetaDataMap metaDataMap;

    @Mock
    private Replicator replicator;

    @Mock
    private Session session;

    @BeforeEach
    void setUp() throws Exception {
        process = new UnpublishProcess();
        java.lang.reflect.Field field = UnpublishProcess.class.getDeclaredField("replicator");
        field.setAccessible(true);
        field.set(process, replicator);
    }

    @Test
    void testExecute_success() throws Exception {
        String path = "/content/mysite/test";

        when(workItem.getWorkflowData()).thenReturn(workflowData);
        when(workflowData.getPayload()).thenReturn(path);
        when(workflowSession.adaptTo(Session.class)).thenReturn(session);

        process.execute(workItem, workflowSession, metaDataMap);

        verify(replicator).replicate(session, ReplicationActionType.DEACTIVATE, path);
    }

    @Test
    void testExecute_exception() {
        process.execute(workItem, workflowSession, metaDataMap);
        verifyNoInteractions(replicator);
    }
}