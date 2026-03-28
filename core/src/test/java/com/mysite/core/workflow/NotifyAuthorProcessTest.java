package com.mysite.core.workflow;

import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import org.apache.commons.mail.SimpleEmail;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.jcr.Session;
import javax.jcr.Value;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotifyAuthorProcessTest {

    private final AemContext context = new AemContext();

    private NotifyAuthorProcess process;

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
    private Session jcrSession;

    @Mock
    private Resource contentResource;

    @Mock
    private ValueMap valueMap;

    @Mock
    private UserManager userManager;

    @Mock
    private Authorizable authorizable;

    @Mock
    private Value value;

    @Mock
    private MessageGatewayService gatewayService;

    @Mock
    private MessageGateway<SimpleEmail> gateway;

    @BeforeEach
    void setUp() throws Exception {
        process = new NotifyAuthorProcess();
        java.lang.reflect.Field field = NotifyAuthorProcess.class.getDeclaredField("messageGatewayService");
        field.setAccessible(true);
        field.set(process, gatewayService);
    }

    @Test
    void testExecute_success() throws Exception {
        String pagePath = "/content/mysite/test";

        when(workItem.getWorkflowData()).thenReturn(workflowData);
        when(workflowData.getPayload()).thenReturn(pagePath);

        when(workflowSession.adaptTo(ResourceResolver.class)).thenReturn(resolver);
        when(workflowSession.adaptTo(Session.class)).thenReturn(jcrSession);

        when(resolver.getResource(pagePath + "/jcr:content")).thenReturn(contentResource);
        when(contentResource.getValueMap()).thenReturn(valueMap);
        when(valueMap.get("cq:lastModifiedBy", String.class)).thenReturn("user1");

        when(resolver.adaptTo(UserManager.class)).thenReturn(userManager);
        when(userManager.getAuthorizable("user1")).thenReturn(authorizable);

        when(authorizable.getProperty("profile/email")).thenReturn(new Value[]{value});
        when(value.getString()).thenReturn("test@mail.com");

        when(gatewayService.getGateway(SimpleEmail.class)).thenReturn(gateway);

        process.execute(workItem, workflowSession, metaDataMap);

        verify(gateway).send(any(SimpleEmail.class));
    }

    @Test
    void testExecute_fallbackToCreatedBy() throws Exception {
        String pagePath = "/content/mysite/test";

        when(workItem.getWorkflowData()).thenReturn(workflowData);
        when(workflowData.getPayload()).thenReturn(pagePath);

        when(workflowSession.adaptTo(ResourceResolver.class)).thenReturn(resolver);
        when(workflowSession.adaptTo(Session.class)).thenReturn(jcrSession);

        when(resolver.getResource(pagePath + "/jcr:content")).thenReturn(contentResource);
        when(contentResource.getValueMap()).thenReturn(valueMap);
        when(valueMap.get("cq:lastModifiedBy", String.class)).thenReturn(null);
        when(valueMap.get("jcr:createdBy", String.class)).thenReturn("user1");

        when(resolver.adaptTo(UserManager.class)).thenReturn(userManager);
        when(userManager.getAuthorizable("user1")).thenReturn(authorizable);

        when(authorizable.getProperty("profile/email")).thenReturn(new Value[]{value});
        when(value.getString()).thenReturn("test@mail.com");

        when(gatewayService.getGateway(SimpleEmail.class)).thenReturn(gateway);

        process.execute(workItem, workflowSession, metaDataMap);

        verify(gateway).send(any(SimpleEmail.class));
    }

    @Test
    void testExecute_noResolverOrSession() {
        process.execute(workItem, workflowSession, metaDataMap);
        verifyNoInteractions(gatewayService);
    }

    @Test
    void testExecute_noContentResource() {
        String pagePath = "/content/mysite/test";

        when(workItem.getWorkflowData()).thenReturn(workflowData);
        when(workflowData.getPayload()).thenReturn(pagePath);

        when(workflowSession.adaptTo(ResourceResolver.class)).thenReturn(resolver);
        when(workflowSession.adaptTo(Session.class)).thenReturn(jcrSession);

        when(resolver.getResource(pagePath + "/jcr:content")).thenReturn(null);

        process.execute(workItem, workflowSession, metaDataMap);

        verifyNoInteractions(gatewayService);
    }

    @Test
    void testExecute_noUser() throws Exception {
        String pagePath = "/content/mysite/test";

        when(workItem.getWorkflowData()).thenReturn(workflowData);
        when(workflowData.getPayload()).thenReturn(pagePath);

        when(workflowSession.adaptTo(ResourceResolver.class)).thenReturn(resolver);
        when(workflowSession.adaptTo(Session.class)).thenReturn(jcrSession);

        when(resolver.getResource(pagePath + "/jcr:content")).thenReturn(contentResource);
        when(contentResource.getValueMap()).thenReturn(valueMap);
        when(valueMap.get("cq:lastModifiedBy", String.class)).thenReturn(null);
        when(valueMap.get("jcr:createdBy", String.class)).thenReturn(null);

        process.execute(workItem, workflowSession, metaDataMap);

        verifyNoInteractions(gatewayService);
    }

    @Test
    void testExecute_userNotFound() throws Exception {
        String pagePath = "/content/mysite/test";

        when(workItem.getWorkflowData()).thenReturn(workflowData);
        when(workflowData.getPayload()).thenReturn(pagePath);

        when(workflowSession.adaptTo(ResourceResolver.class)).thenReturn(resolver);
        when(workflowSession.adaptTo(Session.class)).thenReturn(jcrSession);

        when(resolver.getResource(pagePath + "/jcr:content")).thenReturn(contentResource);
        when(contentResource.getValueMap()).thenReturn(valueMap);
        when(valueMap.get("cq:lastModifiedBy", String.class)).thenReturn("user1");

        when(resolver.adaptTo(UserManager.class)).thenReturn(userManager);
        when(userManager.getAuthorizable("user1")).thenReturn(null);

        process.execute(workItem, workflowSession, metaDataMap);

        verifyNoInteractions(gatewayService);
    }

    @Test
    void testExecute_noEmail() throws Exception {
        String pagePath = "/content/mysite/test";

        when(workItem.getWorkflowData()).thenReturn(workflowData);
        when(workflowData.getPayload()).thenReturn(pagePath);

        when(workflowSession.adaptTo(ResourceResolver.class)).thenReturn(resolver);
        when(workflowSession.adaptTo(Session.class)).thenReturn(jcrSession);

        when(resolver.getResource(pagePath + "/jcr:content")).thenReturn(contentResource);
        when(contentResource.getValueMap()).thenReturn(valueMap);
        when(valueMap.get("cq:lastModifiedBy", String.class)).thenReturn("user1");

        when(resolver.adaptTo(UserManager.class)).thenReturn(userManager);
        when(userManager.getAuthorizable("user1")).thenReturn(authorizable);

        when(authorizable.getProperty("profile/email")).thenReturn(null);

        process.execute(workItem, workflowSession, metaDataMap);

        verifyNoInteractions(gatewayService);
    }

    @Test
    void testExecute_noGateway() throws Exception {
        String pagePath = "/content/mysite/test";

        when(workItem.getWorkflowData()).thenReturn(workflowData);
        when(workflowData.getPayload()).thenReturn(pagePath);

        when(workflowSession.adaptTo(ResourceResolver.class)).thenReturn(resolver);
        when(workflowSession.adaptTo(Session.class)).thenReturn(jcrSession);

        when(resolver.getResource(pagePath + "/jcr:content")).thenReturn(contentResource);
        when(contentResource.getValueMap()).thenReturn(valueMap);
        when(valueMap.get("cq:lastModifiedBy", String.class)).thenReturn("user1");

        when(resolver.adaptTo(UserManager.class)).thenReturn(userManager);
        when(userManager.getAuthorizable("user1")).thenReturn(authorizable);

        when(authorizable.getProperty("profile/email")).thenReturn(new Value[]{value});
        when(value.getString()).thenReturn("test@mail.com");

        when(gatewayService.getGateway(SimpleEmail.class)).thenReturn(null);

        process.execute(workItem, workflowSession, metaDataMap);

        verify(gatewayService).getGateway(SimpleEmail.class);
        verifyNoMoreInteractions(gatewayService);
    }
}