package com.mysite.core.schedulers;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.mysite.core.service.NPUtilService;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Iterator;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(AemContextExtension.class)
class ArticleExpirySchedulerTest {

    private Scheduler scheduler;
    private NPUtilService npUtilService;
    private ScheduleOptions scheduleOptions;

    private ArticleExpiryScheduler schedulerUnderTest;

    @BeforeEach
    void setUp() throws Exception {
        scheduler = mock(Scheduler.class);
        npUtilService = mock(NPUtilService.class);
        scheduleOptions = mock(ScheduleOptions.class);

        schedulerUnderTest = new ArticleExpiryScheduler();

        setPrivateField(schedulerUnderTest, "scheduler", scheduler);
        setPrivateField(schedulerUnderTest, "npUtilService", npUtilService);
    }

    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void testActivate_whenEnabled() {
        ArticleExpirySchedulerConfig config = mock(ArticleExpirySchedulerConfig.class);

        when(config.enable()).thenReturn(true);
        when(config.cronExpression()).thenReturn("0 0 12 * * ?");
        when(config.schedulerName()).thenReturn("testScheduler");

        when(scheduler.EXPR(anyString())).thenReturn(scheduleOptions);

        schedulerUnderTest.activate(config);

        verify(scheduleOptions).name("testScheduler");
        verify(scheduleOptions).canRunConcurrently(false);
        verify(scheduler).schedule(eq(schedulerUnderTest), eq(scheduleOptions));
    }

    @Test
    void testActivate_whenDisabled() {
        ArticleExpirySchedulerConfig config = mock(ArticleExpirySchedulerConfig.class);

        when(config.enable()).thenReturn(false);
        when(config.schedulerName()).thenReturn("testScheduler");

        schedulerUnderTest.activate(config);

        verify(scheduler).unschedule("testScheduler");
    }

    @Test
    void testRun_withPages() {
        ResourceResolver resolver = mock(ResourceResolver.class);
        PageManager pageManager = mock(PageManager.class);
        Page rootPage = mock(Page.class);
        Page childPage = mock(Page.class);
        ValueMap valueMap = mock(ValueMap.class);

        when(npUtilService.getResourceResolver()).thenReturn(resolver);
        when(resolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        when(pageManager.getPage("/content/mysite/us/articlerootpage")).thenReturn(rootPage);

        Iterator<Page> iterator = mock(Iterator.class);
        when(rootPage.listChildren()).thenReturn(iterator);

        when(iterator.hasNext()).thenReturn(true, false);
        when(iterator.next()).thenReturn(childPage);

        when(childPage.getProperties()).thenReturn(valueMap);
        when(valueMap.get("cq:lastModified", Date.class)).thenReturn(new Date());

        schedulerUnderTest.run();

        verify(npUtilService).getResourceResolver();
        verify(pageManager).getPage("/content/mysite/us/articlerootpage");
        verify(rootPage).listChildren();
        verify(childPage).getProperties();
    }

    @Test
    void testRun_whenRootPageNull() {
        ResourceResolver resolver = mock(ResourceResolver.class);
        PageManager pageManager = mock(PageManager.class);

        when(npUtilService.getResourceResolver()).thenReturn(resolver);
        when(resolver.adaptTo(PageManager.class)).thenReturn(pageManager);
        when(pageManager.getPage("/content/mysite/us/articlerootpage")).thenReturn(null);

        schedulerUnderTest.run();

        verify(pageManager).getPage("/content/mysite/us/articlerootpage");
        verifyNoMoreInteractions(pageManager);
    }
}