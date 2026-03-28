package com.mysite.core.schedulers;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysite.core.service.NPUtilService;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.event.jobs.Job;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(AemContextExtension.class)
class ProductSchedulerJobConsumerTest {

    private NPUtilService npUtilService;
    private ProductSchedulerJobConsumer consumer;

    @BeforeEach
    void setUp() throws Exception {
        npUtilService = mock(NPUtilService.class);
        consumer = new ProductSchedulerJobConsumer();

        setField("npUtilService", npUtilService);
    }

    private void setField(String name, Object value) throws Exception {
        Field field = ProductSchedulerJobConsumer.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(consumer, value);
    }

    @Test
    void testProcess() {
        Job job = mock(Job.class);
        when(job.getProperty("apiUrl", String.class)).thenReturn("http://test.api");

        ProductSchedulerJobConsumer spy = spy(consumer);
        doNothing().when(spy).getResponse(anyString());

        spy.process(job);

        verify(spy).getResponse("http://test.api");
    }

    @Test
    void testGetResponse_createPage() throws Exception {

        String json = "[{\"id\":1,\"title\":\"Test Title\",\"description\":\"Test Desc\"}]";

        HttpClient client = mock(HttpClient.class);
        HttpResponse<String> response = mock(HttpResponse.class);

        when(response.body()).thenReturn(json);
        when(client.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(response);

        setField("client", client);

        ResourceResolver resolver = mock(ResourceResolver.class);
        PageManager pageManager = mock(PageManager.class);
        Page page = mock(Page.class);
        Resource contentResource = mock(Resource.class);
        ModifiableValueMap mvm = mock(ModifiableValueMap.class);

        when(npUtilService.getResourceResolver()).thenReturn(resolver);
        when(resolver.adaptTo(PageManager.class)).thenReturn(pageManager);

        when(resolver.getResource(anyString())).thenReturn(null);
        when(pageManager.create(anyString(), anyString(), anyString(), anyString())).thenReturn(page);

        when(page.getContentResource()).thenReturn(contentResource);
        when(contentResource.adaptTo(ModifiableValueMap.class)).thenReturn(mvm);

        consumer.getResponse("http://test.api");

        verify(pageManager).create(anyString(), anyString(), anyString(), anyString());
        verify(mvm).put(eq("jcr:description"), eq("Test Desc"));
        verify(resolver).commit();
    }


}