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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(AemContextExtension.class)
class StatisticsSchedulerTest {

    private Scheduler scheduler;
    private NPUtilService npUtilService;
    private JobManager jobManager;
    private ScheduleOptions scheduleOptions;

    private StatisticsScheduler schedulerUnderTest;

    @BeforeEach
    void setUp() throws Exception {
        scheduler = mock(Scheduler.class);
        npUtilService = mock(NPUtilService.class);
        jobManager = mock(JobManager.class);
        scheduleOptions = mock(ScheduleOptions.class);

        schedulerUnderTest = new StatisticsScheduler();

        setField("scheduler", scheduler);
        setField("npUtilService", npUtilService);
        setField("jobManager", jobManager);
    }

    private void setField(String name, Object value) throws Exception {
        Field field = StatisticsScheduler.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(schedulerUnderTest, value);
    }

    @Test
    void testActivate_enabled() {
        StatisticsSchedulerConfig config = mock(StatisticsSchedulerConfig.class);

        when(config.enable()).thenReturn(true);
        when(config.cronExpression()).thenReturn("0 0 * * * ?");
        when(config.schedulerName()).thenReturn("statsScheduler");
        when(config.projectContentPath()).thenReturn("/content/test");
        when(config.projectDamPath()).thenReturn("/content/dam/test");

        when(scheduler.EXPR(anyString())).thenReturn(scheduleOptions);

        schedulerUnderTest.activate(config);

        verify(scheduleOptions).name("statsScheduler");
        verify(scheduleOptions).canRunConcurrently(false);
        verify(scheduler).schedule(eq(schedulerUnderTest), eq(scheduleOptions));
    }

    @Test
    void testActivate_disabled() {
        StatisticsSchedulerConfig config = mock(StatisticsSchedulerConfig.class);

        when(config.enable()).thenReturn(false);
        when(config.schedulerName()).thenReturn("statsScheduler");

        schedulerUnderTest.activate(config);

        verify(scheduler).unschedule("statsScheduler");
    }

    @Test
    void testRun_addsJob() throws Exception {
        setField("projectContentPath", "/content/test");
        setField("projectDamPath", "/content/dam/test");

        schedulerUnderTest.run();

        verify(jobManager).addJob(eq("mysite/StatisticsScheduler"), argThat(map ->
                "/content/test".equals(map.get("projectContentPath")) &&
                        "/content/dam/test".equals(map.get("projectDamPath"))
        ));
    }
}