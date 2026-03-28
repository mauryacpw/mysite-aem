package com.mysite.core.workflow;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.metadata.MetaDataMap;

import com.day.cq.mailer.MessageGatewayService;
import com.day.cq.mailer.MessageGateway;

import org.apache.commons.mail.SimpleEmail;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.jackrabbit.api.security.user.Authorizable;

import javax.jcr.Session;
import javax.jcr.Value;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
        service = WorkflowProcess.class,
        property = {"process.label=Notify Author"}
)
public class NotifyAuthorProcess implements WorkflowProcess {

    private static final Logger log = LoggerFactory.getLogger(NotifyAuthorProcess.class);

    @Reference
    private MessageGatewayService messageGatewayService;

    @Override
    public void execute(WorkItem item, WorkflowSession session, MetaDataMap args) {

        try {

            String pagePath = item.getWorkflowData().getPayload().toString();

            ResourceResolver resolver = session.adaptTo(ResourceResolver.class);
            Session jcrSession = session.adaptTo(Session.class);

            if(resolver == null || jcrSession == null){
                log.error("Unable to obtain resolver or session");
                return;
            }

            Resource contentResource = resolver.getResource(pagePath + "/jcr:content");

            if (contentResource == null) {
                log.error("jcr:content not found for {}", pagePath);
                return;
            }

            ValueMap properties = contentResource.getValueMap();

            /* Prefer lastModifiedBy, fallback to createdBy */

            String userId = properties.get("cq:lastModifiedBy", String.class);

            if(userId == null){
                userId = properties.get("jcr:createdBy", String.class);
            }

            if(userId == null){
                log.error("No author found for {}", pagePath);
                return;
            }

            /* Fetch user */

            UserManager userManager = resolver.adaptTo(UserManager.class);

            Authorizable authorizable = userManager.getAuthorizable(userId);

            if (authorizable == null) {
                log.error("User not found {}", userId);
                return;
            }

            /* Fetch email */

            Value[] emails = authorizable.getProperty("profile/email");

            if(emails == null || emails.length == 0){
                log.error("Email not found for user {}", userId);
                return;
            }

            String email = emails[0].getString();
            log.info("Sending email to {}", email);

            /* Send Email */

            MessageGateway<SimpleEmail> gateway =
                    messageGatewayService.getGateway(SimpleEmail.class);

            if(gateway == null){
                log.error("Email gateway unavailable");
                return;
            }

            SimpleEmail emailObj = new SimpleEmail();

            emailObj.addTo(email);
            emailObj.setSubject("Page Expired Notification");
            emailObj.setMsg(
                    "Your page has expired and was unpublished.\n\n" +
                            "Page Path: " + pagePath
            );

            gateway.send(emailObj);

            log.info("Notification sent to {}", email);

        } catch (Exception e) {

            log.error("Error sending email", e);

        }
    }
}