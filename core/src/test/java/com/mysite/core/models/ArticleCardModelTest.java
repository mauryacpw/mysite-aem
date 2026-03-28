package com.mysite.core.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(AemContextExtension.class)
class ArticleCardModelTest {

    private final AemContext context = new AemContext();

    private ResourceResolver resourceResolver;
    private PageManager pageManager;
    private Page rootPage;
    private Page childPage1;
    private Page childPage2;

    @BeforeEach
    void setUp() {
        resourceResolver = context.resourceResolver();
        pageManager = mock(PageManager.class);
        rootPage = mock(Page.class);
        childPage1 = mock(Page.class);
        childPage2 = mock(Page.class);

        context.registerService(PageManager.class, pageManager);
    }

    @Test
    void test_init_success() {

        Resource resource = context.create().resource("/content/test",
                "articleRootPath", "/content/articles",
                "maxArticles", 2
        );

        // register adapter instead of mocking adaptTo
        context.registerAdapter(ResourceResolver.class, PageManager.class, pageManager);

        when(pageManager.getPage("/content/articles")).thenReturn(rootPage);

        Iterator<Page> children = Arrays.asList(childPage1, childPage2).iterator();
        when(rootPage.listChildren()).thenReturn(children);

        Resource contentRes1 = mock(Resource.class);
        Resource contentRes2 = mock(Resource.class);

        when(childPage1.getContentResource()).thenReturn(contentRes1);
        when(childPage2.getContentResource()).thenReturn(contentRes2);

        when(contentRes1.isResourceType(anyString())).thenReturn(false);
        when(contentRes2.isResourceType(anyString())).thenReturn(false);

        Resource articleRes1 = mock(Resource.class);
        Resource articleRes2 = mock(Resource.class);

        when(contentRes1.getChildren()).thenReturn(List.of(articleRes1));
        when(contentRes2.getChildren()).thenReturn(List.of(articleRes2));

        when(articleRes1.isResourceType(anyString())).thenReturn(true);
        when(articleRes2.isResourceType(anyString())).thenReturn(true);

        ArticleDetailModel article1 = mock(ArticleDetailModel.class);
        ArticleDetailModel article2 = mock(ArticleDetailModel.class);

        when(articleRes1.adaptTo(ArticleDetailModel.class)).thenReturn(article1);
        when(articleRes2.adaptTo(ArticleDetailModel.class)).thenReturn(article2);

        when(article1.getArticleDate()).thenReturn(new Date(1000));
        when(article2.getArticleDate()).thenReturn(new Date(2000));

        when(childPage1.getPath()).thenReturn("/a1");
        when(childPage2.getPath()).thenReturn("/a2");

        ArticleCardModel model = resource.adaptTo(ArticleCardModel.class);

        assertNotNull(model);
        assertEquals(2, model.getArticleDetails().size());
    }

    @Test
    void test_init_noRootPath() {
        Resource resource = context.create().resource("/content/test");

        ArticleCardModel model = resource.adaptTo(ArticleCardModel.class);

        assertTrue(model.getArticleDetails().isEmpty());
    }

    @Test
    void test_init_pageManagerNull() {

        Resource resource = context.create().resource("/content/test",
                "articleRootPath", "/content/articles"
        );

        // force adaptTo(PageManager.class) to return null
        context.registerAdapter(ResourceResolver.class, PageManager.class, (PageManager) null);

        ArticleCardModel model = resource.adaptTo(ArticleCardModel.class);

        assertTrue(model.getArticleDetails().isEmpty());
    }

    @Test
    void test_init_rootPageNull() {

        Resource resource = context.create().resource("/content/test",
                "articleRootPath", "/content/articles"
        );

        // FIX: register adapter instead of mocking adaptTo
        context.registerAdapter(ResourceResolver.class, PageManager.class, pageManager);

        when(pageManager.getPage("/content/articles")).thenReturn(null);

        ArticleCardModel model = resource.adaptTo(ArticleCardModel.class);

        assertTrue(model.getArticleDetails().isEmpty());
    }

    @Test
    void test_maxArticles_limit() {

        Resource resource = context.create().resource("/content/test",
                "articleRootPath", "/content/articles",
                "maxArticles", 1
        );

        context.registerAdapter(ResourceResolver.class, PageManager.class, pageManager);

        when(pageManager.getPage("/content/articles")).thenReturn(rootPage);

        Iterator<Page> children = Arrays.asList(childPage1, childPage2).iterator();
        when(rootPage.listChildren()).thenReturn(children);

        Resource contentRes = mock(Resource.class);
        Resource articleRes = mock(Resource.class);

        when(childPage1.getContentResource()).thenReturn(contentRes);
        when(childPage2.getContentResource()).thenReturn(contentRes);

        when(contentRes.isResourceType(anyString())).thenReturn(false);

        List<Resource> childList = Collections.singletonList(articleRes);
        when(contentRes.getChildren()).thenReturn(childList);

        when(articleRes.isResourceType(anyString())).thenReturn(true);

        ArticleDetailModel article = mock(ArticleDetailModel.class);
        when(articleRes.adaptTo(ArticleDetailModel.class)).thenReturn(article);
        when(article.getArticleDate()).thenReturn(new Date());

        when(childPage1.getPath()).thenReturn("/a1");
        when(childPage2.getPath()).thenReturn("/a2");

        ArticleCardModel model = resource.adaptTo(ArticleCardModel.class);

        assertEquals(1, model.getArticleDetails().size());
    }
}