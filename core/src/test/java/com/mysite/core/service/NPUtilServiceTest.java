package com.mysite.core.service;

import io.wcm.testing.mock.aem.junit5.AemContext;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NPUtilServiceTest {

    private final AemContext context = new AemContext();

    @Mock
    private ResourceResolverFactory factory;

    @Mock
    private ResourceResolver resolver;

    private NPUtilService service;

    @BeforeEach
    void setUp() {
        context.registerService(ResourceResolverFactory.class, factory);
        service = context.registerInjectActivateService(new NPUtilService());
    }

    @Test
    void testGetResourceResolver_success() throws Exception {
        when(factory.getServiceResourceResolver(anyMap())).thenReturn(resolver);

        ResourceResolver result = service.getResourceResolver();

        assertNotNull(result);
        verify(factory).getServiceResourceResolver(anyMap());
    }

    @Test
    void testGetResourceResolver_exception() throws Exception {
        when(factory.getServiceResourceResolver(anyMap())).thenThrow(new LoginException("error"));

        assertThrows(RuntimeException.class, () -> service.getResourceResolver());

        verify(factory).getServiceResourceResolver(anyMap());
    }
}