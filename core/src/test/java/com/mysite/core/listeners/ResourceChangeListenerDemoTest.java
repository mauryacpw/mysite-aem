package com.mysite.core.listeners;

import io.wcm.testing.mock.aem.junit5.AemContext;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChange.ChangeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

class ResourceChangeListenerDemoTest {

    private final AemContext context = new AemContext();
    private ResourceChangeListenerDemo listener;

    @BeforeEach
    void setUp() {
        listener = new ResourceChangeListenerDemo();
        context.registerService(ResourceChangeListenerDemo.class, listener);
    }

    @Test
    void testOnChange_withSingleChange() {
        ResourceChange change = new ResourceChange(
                ChangeType.CHANGED,
                "/content/mysite/test",
                false
        );

        List<ResourceChange> changes = Collections.singletonList(change);

        listener.onChange(changes);
    }

    @Test
    void testOnChange_withEmptyList() {
        listener.onChange(Collections.emptyList());
    }
}