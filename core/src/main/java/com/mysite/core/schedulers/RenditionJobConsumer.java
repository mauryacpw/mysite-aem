package com.mysite.core.schedulers;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.day.image.Layer;
import com.mysite.core.service.NPUtilService;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;


@Component(service = JobConsumer.class,
        property = {
                JobConsumer.PROPERTY_TOPICS + "=custom/rendition/job"
        })
public class RenditionJobConsumer implements JobConsumer {

    Logger log = LoggerFactory.getLogger(RenditionJobConsumer.class);

    @Reference
    private NPUtilService npUtilService;


    @Override
    public JobResult process(Job job) {

        String assetPath = (String) job.getProperty("assetPath");

        try (ResourceResolver resolver = npUtilService.getResourceResolver()) {

            Resource assetResource = resolver.getResource(assetPath);

            if (assetResource == null) {
                return JobResult.FAILED;
            }

            return processRendition(assetResource, resolver);

        } catch (Exception e) {
            log.error("Error processing job", e);
            return JobResult.FAILED;
        }
    }
    private JobResult processRendition(Resource assetResource, ResourceResolver resolver) throws Exception {

        Asset asset = assetResource.adaptTo(Asset.class);

        if (asset == null) {
            log.info("Asset Resource is null");
            return JobResult.CANCEL;
        }

        String mimeType = asset.getMimeType();
        if (mimeType == null || !mimeType.startsWith("image/")) {
            log.info("Not an image");
            return JobResult.CANCEL;
        }

        String state = assetResource.getChild("jcr:content")
                .getValueMap()
                .get("dam:assetState", String.class);


        if (!"processed".equalsIgnoreCase(state)) {
            log.info("Asset not fully processed yet → retry");
            Thread.sleep(5000);
        }

        if (asset.getRendition("custom.100x100.jpg") != null) {
            log.info("Rendition already exists");
            return JobResult.OK;
        }

        Rendition original = asset.getOriginal();

        try (InputStream is = original.getStream();
             InputStream processedStream = transformImage(is)) {

            asset.addRendition("custom.100x100.jpg", processedStream, "image/jpg");
        }

        resolver.commit();

        log.info("Rendition Process Completed");

        return JobResult.OK;
    }
    private InputStream transformImage(InputStream originalStream) throws Exception {

        Layer layer = new Layer(originalStream);
        layer.resize(100, 100);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        layer.write("jpg", 0.8, baos);

        log.info("Rendition Image Completed");

        return new ByteArrayInputStream(baos.toByteArray());
    }
}