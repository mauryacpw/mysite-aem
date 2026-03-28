package com.mysite.core.listeners;

import com.mysite.core.service.NPUtilService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

import org.apache.sling.api.resource.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
@Component(
        service = EventHandler.class,
        immediate = true,
        property = {
                EventConstants.EVENT_TOPIC + "=org/apache/sling/api/resource/Resource/ADDED",
                EventConstants.EVENT_FILTER+"=(path=/content/dam/mysite/*.pdf)"
        }
)
public class PdfUploadEventHandler implements EventHandler {

    private static final Logger log = LoggerFactory.getLogger(PdfUploadEventHandler.class);

    @Reference
    private NPUtilService npUtilService;

    @Override
    public void handleEvent(Event event) {

        log.info("Inside handleEvent method");

        String path = (String) event.getProperty("path");

        if (path != null && path.startsWith("/content/dam") && path.endsWith(".pdf")) {

            log.info("PDF Uploaded: {}", path);

            try {
                ResourceResolver resolver = npUtilService.getResourceResolver();
                log.info("Inside resolver: "+ resolver);
                Resource metadata = resolver.getResource(path + "/jcr:content/metadata");
                log.info("pdf path:"+ path+"/jcr:content/metadata");
                log.info("Inside metadata: "+ metadata);
                if (metadata != null) {

                    log.info("Metadata resource found: {}", metadata.getPath());

                    ModifiableValueMap map = metadata.adaptTo(ModifiableValueMap.class);

                    if (map != null) {
                        map.put("uniqueId", UUID.randomUUID().toString());
                    }

                    resolver.commit();
                    resolver.close();
                }

            } catch (Exception e) {
                log.error("Error updating metadata", e);
            }
        }
    }
}