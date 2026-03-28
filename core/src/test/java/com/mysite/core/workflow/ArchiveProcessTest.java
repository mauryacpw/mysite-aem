package com.mysite.core.workflow;

import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import io.wcm.testing.mock.aem.junit5.AemContext;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArchiveProcessTest {

    private final AemContext context = new AemContext();

    private ArchiveProcess archiveProcess;

    @Mock
    private WorkItem workItem;

    @Mock
    private WorkflowSession workflowSession;

    @Mock
    private WorkflowData workflowData;

    @Mock
    private MetaDataMap metaDataMap;

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private PageManager pageManager;

    @Mock
    private Page page;

    @Mock
    private Resource archiveResource;

    @BeforeEach
    void setUp() {
        archiveProcess = new ArchiveProcess();
    }

    @Test
    void testExecute_archiveExists() throws Exception {
        String pagePath = "/content/mysite/test";

        when(workItem.getWorkflowData()).thenReturn(workflowData);
        when(workflowData.getPayload()).thenReturn(pagePath);
        when(workflowSession.adaptTo(ResourceResolver.class)).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        when(pageManager.getPage(pagePath)).thenReturn(page);
        when(resourceResolver.getResource("/content/mysite/archive")).thenReturn(archiveResource);
        when(page.getName()).thenReturn("test");

        archiveProcess.execute(workItem, workflowSession, metaDataMap);

        verify(pageManager).move(eq(page), eq("/content/mysite/archive/test"), isNull(), eq(false), eq(false), isNull());
    }

    @Test
    void testExecute_archiveCreated() throws Exception {
        String pagePath = "/content/mysite/test";

        Page archivePage = mock(Page.class);

        when(workItem.getWorkflowData()).thenReturn(workflowData);
        when(workflowData.getPayload()).thenReturn(pagePath);
        when(workflowSession.adaptTo(ResourceResolver.class)).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        when(pageManager.getPage(pagePath)).thenReturn(page);
        when(resourceResolver.getResource("/content/mysite/archive")).thenReturn(null);
        when(page.getName()).thenReturn("test");

        when(page.getTemplate()).thenReturn(mock(com.day.cq.wcm.api.Template.class));
        when(page.getTemplate().getPath()).thenReturn("/conf/template");

        when(pageManager.create(eq("/content/mysite"), eq("archive"), anyString(), eq("Archive")))
                .thenReturn(archivePage);

        archiveProcess.execute(workItem, workflowSession, metaDataMap);

        verify(pageManager).create(eq("/content/mysite"), eq("archive"), anyString(), eq("Archive"));
        verify(pageManager).move(eq(page), eq("/content/mysite/archive/test"), isNull(), eq(false), eq(false), isNull());
    }

    @Test
    void testExecute_pageNotFound() throws WCMException {
        String pagePath = "/content/mysite/missing";

        when(workItem.getWorkflowData()).thenReturn(workflowData);
        when(workflowData.getPayload()).thenReturn(pagePath);
        when(workflowSession.adaptTo(ResourceResolver.class)).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        when(pageManager.getPage(pagePath)).thenReturn(null);

        archiveProcess.execute(workItem, workflowSession, metaDataMap);

        verify(pageManager, never()).move(any(Page.class), anyString(), any(), anyBoolean(), anyBoolean(), any());
    }

    @Test
    void testExecute_exception() {
        archiveProcess.execute(workItem, workflowSession, metaDataMap);
        verifyNoInteractions(pageManager);
    }
}