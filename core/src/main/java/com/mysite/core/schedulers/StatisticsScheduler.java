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
@Designate(ocd= StatisticsSchedulerConfig.class)
public class StatisticsScheduler implements Runnable {


    @Reference
    private Scheduler scheduler;
    @Reference
    private NPUtilService npUtilService;
    @Reference
    private JobManager jobManager;

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private  String projectContentPath;
    private  String projectDamPath;


    @Activate
    @Modified
    protected void activate(StatisticsSchedulerConfig cfg) {
        projectContentPath=cfg.projectContentPath();
        projectDamPath=cfg.projectDamPath();

        if(cfg.enable()){
            ScheduleOptions sOps = scheduler.EXPR(cfg.cronExpression());
            sOps.name(cfg.schedulerName());
            sOps.canRunConcurrently(false);
            scheduler.schedule(this,sOps);
            log.info("Scheduler started");
        }
        else {
            scheduler.unschedule(cfg.schedulerName());
        }
    }

    @Override
    public void run() {
        Map<String, Object> props = new HashMap<>();
        props.put("projectContentPath", projectContentPath);
        props.put("projectDamPath", projectDamPath);
        log.info("Scheduler started in run !!");
        jobManager.addJob("mysite/StatisticsScheduler", props);
    }
}

