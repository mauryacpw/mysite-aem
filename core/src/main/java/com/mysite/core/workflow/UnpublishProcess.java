package com.mysite.core.workflow;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.metadata.MetaDataMap;

import com.day.cq.replication.Replicator;
import com.day.cq.replication.ReplicationActionType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = WorkflowProcess.class,
        property = {"process.label=Unpublish Expired Page"})
public class UnpublishProcess implements WorkflowProcess {

    private static final Logger log = LoggerFactory.getLogger(UnpublishProcess.class);

    @Reference
    private Replicator replicator;

    @Override
    public void execute(WorkItem item, WorkflowSession workflowSession, MetaDataMap args) {

        try {

            String path = item.getWorkflowData().getPayload().toString();

            Session session = workflowSession.adaptTo(Session.class);

            replicator.replicate(session, ReplicationActionType.DEACTIVATE, path);

            log.info("Page unpublished {}", path);

        } catch (Exception e) {
            log.error("Unpublish failed", e);
        }
    }


}