package com.mysite.core.schedulers;

import com.day.cq.replication.ReplicationAction;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;
import com.mysite.core.service.NPUtilService;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;

@Component(service = Runnable.class, immediate = true,
property = {
//        "scheduler.expression= */2 * * ? * *"
})

public class TestScheduler implements Runnable{

    Logger log = LoggerFactory.getLogger(this.getClass());
    @Reference
    Replicator replicator;
    @Reference
    NPUtilService npUtilService;

    @Override
    public void run() {
        log.info("TestScheduler started");
        ResourceResolver resourceResolver = npUtilService.getResourceResolver();
        Session session = resourceResolver.adaptTo(Session.class);
        ReplicationActionType replicationAction = ReplicationActionType.ACTIVATE;
        try {
            replicator.replicate(session,replicationAction,"/content/mysite/home/about-us");
        } catch (ReplicationException e) {
            throw new RuntimeException(e);
        }
    }
}
