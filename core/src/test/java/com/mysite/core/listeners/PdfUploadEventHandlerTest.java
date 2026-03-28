package com.mysite.core.listeners;

import com.mysite.core.service.NPUtilService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import org.apache.sling.api.resource.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.osgi.service.event.Event;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

class PdfUploadEventHandlerTest {

    private final AemContext context = new AemContext();

    private PdfUploadEventHandler handler;
    private NPUtilService npUtilService;

    @BeforeEach
    void setUp() {
        handler = new PdfUploadEventHandler();
        npUtilService = mock(NPUtilService.class);

        context.registerService(NPUtilService.class, npUtilService);

        context.create().resource("/content/dam/mysite/test.pdf/jcr:content/metadata");

        ResourceResolver resolver = context.resourceResolver();
        when(npUtilService.getResourceResolver()).thenReturn(resolver);

        // inject mock service
        try {
            java.lang.reflect.Field field = PdfUploadEventHandler.class.getDeclaredField("npUtilService");
            field.setAccessible(true);
            field.set(handler, npUtilService);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testHandleEvent_validPdf() throws Exception {

        Map<String, Object> props = new HashMap<>();
        props.put("path", "/content/dam/mysite/test.pdf");

        Event event = new Event("org/apache/sling/api/resource/Resource/ADDED", props);

        handler.handleEvent(event);

        Resource metadata = context.resourceResolver()
                .getResource("/content/dam/mysite/test.pdf/jcr:content/metadata");

        ModifiableValueMap map = metadata.adaptTo(ModifiableValueMap.class);

        assert map != null;
        assert map.get("uniqueId") != null;
    }

    @Test
    void testHandleEvent_invalidPath() {
        Map<String, Object> props = new HashMap<>();
        props.put("path", "/content/dam/mysite/test.txt");

        Event event = new Event("org/apache/sling/api/resource/Resource/ADDED", props);

        handler.handleEvent(event);
    }

    @Test
    void testHandleEvent_nullPath() {
        Map<String, Object> props = new HashMap<>();

        Event event = new Event("org/apache/sling/api/resource/Resource/ADDED", props);

        handler.handleEvent(event);
    }
}