package com.mysite.core.service;

import io.wcm.testing.mock.aem.junit5.AemContext;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    private final AemContext context = new AemContext();

    private ArticleService articleService;

    @Mock
    private NPUtilService npUtilService;

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private Resource resource;

    @Mock
    private ArticleServiceConfig config;

    @BeforeEach
    void setUp() {
        articleService = new ArticleService();
        context.registerService(NPUtilService.class, npUtilService);
        context.registerInjectActivateService(articleService);
    }

    @Test
    void testActivate() {
        articleService.init(config);
    }

    @Test
    void testDeactivate() {
        articleService.deactivate(config);
    }

    @Test
    void testModified() {
        when(npUtilService.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.getResource("/content/mysite/home/about-us/jcr:content")).thenReturn(resource);
        when(config.articleApiUrl()).thenReturn("http://test-api");
        when(config.status()).thenReturn(Boolean.valueOf("active"));

        articleService.update(config);

        verify(npUtilService).getResourceResolver();
        verify(resourceResolver).getResource("/content/mysite/home/about-us/jcr:content");
    }

    @Test
    void testGetArticles() {
        String result = articleService.getArticles(config);
        assertEquals("Articles from Api", result);
    }
}