package com.mysite.core.workflow;

import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.metadata.MetaDataMap;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.Resource;

import org.osgi.service.component.annotations.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
        service = WorkflowProcess.class,
        property = {"process.label=Archive Expired Page"}
)
public class ArchiveProcess implements WorkflowProcess {

    private static final Logger log = LoggerFactory.getLogger(ArchiveProcess.class);

    private static final String ARCHIVE_ROOT = "/content/mysite/archive";

    @Override
    public void execute(WorkItem item, WorkflowSession session, MetaDataMap args) {

        ResourceResolver resolver = null;

        try {

            String pagePath = item.getWorkflowData().getPayload().toString();

            resolver = session.adaptTo(ResourceResolver.class);

            assert resolver != null;
            PageManager pageManager = resolver.adaptTo(PageManager.class);

            assert pageManager != null;
            Page page = pageManager.getPage(pagePath);

            if (page == null) {
                log.error("Page not found: {}", pagePath);
                return;
            }

            /* ----------------------------------------------------
               Ensure archive folder exists
               ---------------------------------------------------- */

            Resource archiveResource = resolver.getResource(ARCHIVE_ROOT);

            if (archiveResource == null) {

                Page archivePage = pageManager.create(
                        "/content/mysite",
                        "archive",
                        page.getTemplate().getPath(),
                        "Archive"
                );

                log.info("Archive folder created at {}", archivePage.getPath());
            }

            /* ----------------------------------------------------
               Move page with original name
               ---------------------------------------------------- */

            String destinationPath = ARCHIVE_ROOT + "/" + page.getName();

            pageManager.move(
                    page,
                    destinationPath,
                    null,
                    false,
                    false,
                    null
            );

            log.info("Page archived successfully: {}", destinationPath);

        } catch (Exception e) {

            log.error("Archive step failed", e);

        }
    }
}