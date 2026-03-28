package com.mysite.core.schedulers;

import com.day.cq.search.QueryBuilder;
import com.mysite.core.service.NPUtilService;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Component(service = Runnable.class, immediate = true)
@Designate(ocd = ScheduledContentExpiryConfig.class)
public class ScheduledContentExpiry implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(ScheduledContentExpiry.class);
    private String rootPath;
    @Reference
    private Scheduler scheduler;

    @Reference
    private JobManager jobManager;

    @Activate
    @Modified
    protected void activate(ScheduledContentExpiryConfig config) {

        String schedulerName = config.schedulerName();
        rootPath = config.rootPath();
        if (config.enable()) {

            ScheduleOptions options = scheduler.EXPR(config.cronExpression());
            options.name(schedulerName);
            options.canRunConcurrently(false);

            scheduler.schedule(this, options);

            log.info("Expired Content Scheduler started");

        } else {
            scheduler.unschedule(schedulerName);
            log.info("Expired Content Scheduler disabled");
        }
    }


    @Override
    public void run() {
        Map<String, Object> props = new HashMap<>();
        props.put("rootPath",rootPath);
        log.info("Scheduler started in run !!");
        jobManager.addJob("mysite/ExpiryScheduler", props);
    }
}
