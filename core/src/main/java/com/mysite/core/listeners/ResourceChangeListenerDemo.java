package com.mysite.core.listeners;

import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Component(service= ResourceChangeListener.class, immediate = true,
    property= {
            ResourceChangeListener.PATHS+"=/content/mysite",
            ResourceChangeListener.CHANGES+"="+ResourceChangeListener.CHANGE_CHANGED
    }
)
public class ResourceChangeListenerDemo implements ResourceChangeListener {

    private static final Logger log = LoggerFactory.getLogger(ResourceChangeListenerDemo.class);

    @Override
    public void onChange(List<ResourceChange> list) {
        log.info("onChange {}", list);
    }
}
