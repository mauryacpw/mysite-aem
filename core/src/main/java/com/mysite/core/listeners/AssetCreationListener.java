package com.mysite.core.listeners;

import org.apache.sling.event.jobs.JobManager;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Component(
        service = EventHandler.class,
        immediate = true,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Asset Creation Event Handler",
                EventConstants.EVENT_TOPIC + "=org/apache/sling/api/resource/Resource/ADDED",
                EventConstants.EVENT_FILTER + "=(path=/content/dam/mysite/*.jpg)"
        }
)
public class AssetCreationListener implements EventHandler {

    private static final Logger log = LoggerFactory.getLogger(AssetCreationListener.class);

    @Reference
    private JobManager jobManager;

    @Override
    public void handleEvent(Event event) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        String path = (String) event.getProperty("path");
        log.info("Asset Creation Event received from path {}", path);
        if (path == null) {
            return;
        }

        if (path.contains("/jcr:content") || path.contains("/renditions")) {
            return;
        }

        log.info("Creating job for: {}", path);

        Map<String, Object> props = new HashMap<>();
        props.put("assetPath", path);

        jobManager.addJob("custom/rendition/job", props);
    }
}