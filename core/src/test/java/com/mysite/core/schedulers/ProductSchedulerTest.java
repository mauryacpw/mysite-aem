package com.mysite.core.schedulers;

import com.mysite.core.service.NPUtilService;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.apache.sling.event.jobs.JobManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.reflect.Field;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(AemContextExtension.class)
class ProductSchedulerTest {

    private Scheduler scheduler;
    private NPUtilService npUtilService;
    private JobManager jobManager;
    private ScheduleOptions scheduleOptions;

    private ProductScheduler schedulerUnderTest;

    @BeforeEach
    void setUp() throws Exception {
        scheduler = mock(Scheduler.class);
        npUtilService = mock(NPUtilService.class);
        jobManager = mock(JobManager.class);
        scheduleOptions = mock(ScheduleOptions.class);

        schedulerUnderTest = new ProductScheduler();

        setField("scheduler", scheduler);
        setField("npUtilService", npUtilService);
        setField("jobManager", jobManager);
    }

    private void setField(String name, Object value) throws Exception {
        Field field = ProductScheduler.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(schedulerUnderTest, value);
    }

    @Test
    void testActivate_enabled() {
        ProductSchedulerConfig config = mock(ProductSchedulerConfig.class);

        when(config.enable()).thenReturn(true);
        when(config.cronExpression()).thenReturn("0 0 * * * ?");
        when(config.schedulerName()).thenReturn("productScheduler");
        when(config.apiUrl()).thenReturn("http://test.api");

        when(scheduler.EXPR(anyString())).thenReturn(scheduleOptions);

        schedulerUnderTest.activate(config);

        verify(scheduleOptions).name("productScheduler");
        verify(scheduleOptions).canRunConcurrently(false);
        verify(scheduler).schedule(eq(schedulerUnderTest), eq(scheduleOptions));
    }

    @Test
    void testActivate_disabled() {
        ProductSchedulerConfig config = mock(ProductSchedulerConfig.class);

        when(config.enable()).thenReturn(false);
        when(config.schedulerName()).thenReturn("productScheduler");

        schedulerUnderTest.activate(config);

        verify(scheduler).unschedule("productScheduler");
    }

    @Test
    void testRun_addsJob() throws Exception {
        setField("apiUrl", "http://test.api");

        schedulerUnderTest.run();

        verify(jobManager).addJob(eq("mysite/productScheduler"), argThat(map ->
                "http://test.api".equals(map.get("apiUrl"))
        ));
    }
}