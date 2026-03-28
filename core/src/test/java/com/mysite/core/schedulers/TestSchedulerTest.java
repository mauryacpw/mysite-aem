package com.mysite.core.schedulers;

import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;
import com.mysite.core.service.NPUtilService;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.jcr.Session;
import java.lang.reflect.Field;

import static org.mockito.Mockito.*;

@ExtendWith(AemContextExtension.class)
class TestSchedulerTest {

    private Replicator replicator;
    private NPUtilService npUtilService;
    private TestScheduler schedulerUnderTest;

    @BeforeEach
    void setUp() throws Exception {
        replicator = mock(Replicator.class);
        npUtilService = mock(NPUtilService.class);

        schedulerUnderTest = new TestScheduler();

        setField("replicator", replicator);
        setField("npUtilService", npUtilService);
    }

    private void setField(String name, Object value) throws Exception {
        Field field = TestScheduler.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(schedulerUnderTest, value);
    }

    @Test
    void testRun_success() throws Exception {

        ResourceResolver resolver = mock(ResourceResolver.class);
        Session session = mock(Session.class);

        when(npUtilService.getResourceResolver()).thenReturn(resolver);
        when(resolver.adaptTo(Session.class)).thenReturn(session);

        schedulerUnderTest.run();

        verify(replicator).replicate(
                eq(session),
                eq(ReplicationActionType.ACTIVATE),
                eq("/content/mysite/home/about-us")
        );
    }

    @Test
    void testRun_replicationException() throws Exception {

        ResourceResolver resolver = mock(ResourceResolver.class);
        Session session = mock(Session.class);

        when(npUtilService.getResourceResolver()).thenReturn(resolver);
        when(resolver.adaptTo(Session.class)).thenReturn(session);

        doThrow(new ReplicationException("error"))
                .when(replicator)
                .replicate(any(), any(), anyString());

        try {
            schedulerUnderTest.run();
        } catch (RuntimeException e) {
            assert e.getCause() instanceof ReplicationException;
        }

        verify(replicator).replicate(any(), any(), anyString());
    }
}