package com.mysite.core.workflow;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.exec.WorkItem;

import com.adobe.granite.workflow.metadata.MetaDataMap;

import com.day.cq.replication.ReplicationStatus;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.Resource;

import org.osgi.service.component.annotations.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = WorkflowProcess.class,
        property = {"process.label=Validate Published Page"})
public class ValidationProcess implements WorkflowProcess {

    private static final Logger log = LoggerFactory.getLogger(ValidationProcess.class);

    @Override
    public void execute(WorkItem item, WorkflowSession session, MetaDataMap args) {

        String path = item.getWorkflowData().getPayload().toString();

        try {

            ResourceResolver resolver = session.adaptTo(ResourceResolver.class);

            Resource resource = resolver.getResource(path + "/jcr:content");

            if (resource == null) {
                log.warn("jcr:content not found for {}", path);
                return;
            }

            ReplicationStatus status = resource.adaptTo(ReplicationStatus.class);

            if (status != null && status.isActivated()) {
                log.info("Validation success: Page is published {}", path);
            } else {
                log.info("Validation check: Page is not published {}", path);
            }

        } catch (Exception e) {

            log.error("Validation step error", e);

        }
    }


}