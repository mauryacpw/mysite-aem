package com.mysite.core.schedulers;

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


@Component(immediate = true, service = Runnable.class)
@Designate(ocd= ProductSchedulerConfig.class)
public class ProductScheduler implements Runnable {

   private String apiUrl;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Reference
    private Scheduler scheduler;
    @Reference
    private NPUtilService npUtilService;
    @Reference
    private JobManager jobManager;


    @Activate
    @Modified
    protected void activate(ProductSchedulerConfig config) {
        this.apiUrl=config.apiUrl();
        if(config.enable()){
            ScheduleOptions sOps = scheduler.EXPR(config.cronExpression());
            sOps.name(config.schedulerName());
            sOps.canRunConcurrently(false);
            scheduler.schedule(this,sOps);
            log.info("Scheduler started");
        }
        else {
            scheduler.unschedule(config.schedulerName());
        }
    }

    @Override
    public void run() {
        Map<String, Object> props = new HashMap<>();
        props.put("apiUrl", apiUrl);
        log.info("apiurl at createjob "+apiUrl);
        jobManager.addJob("mysite/productScheduler", props);
    }
}

