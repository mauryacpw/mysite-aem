package com.mysite.core.schedulers;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.mysite.core.service.NPUtilService;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Iterator;

@Component(immediate = true, service = Runnable.class)
@Designate(ocd=ArticleExpirySchedulerConfig.class)
public class ArticleExpiryScheduler implements Runnable {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Reference
    private Scheduler scheduler;
    @Reference
    private NPUtilService npUtilService;

    @Activate
    @Modified
    protected void activate(ArticleExpirySchedulerConfig cfg) {
        if(cfg.enable()){
            ScheduleOptions sOps = scheduler.EXPR(cfg.cronExpression());
            sOps.name(cfg.schedulerName());
            sOps.canRunConcurrently(false);

            scheduler.schedule(this,sOps);
        }
        else {
            scheduler.unschedule(cfg.schedulerName());
        }
    }

    @Override
    public void run() {

        ResourceResolver resolver = npUtilService.getResourceResolver();
        PageManager pageManager = resolver.adaptTo(PageManager.class);
        Page articlepage = pageManager.getPage("/content/mysite/us/articlerootpage");
        if(articlepage != null){
            Iterator<Page> iterator = articlepage.listChildren();

            while(iterator.hasNext()){
                Page page = iterator.next();
                ValueMap vm = page.getProperties();

                Date date = vm.get("cq:lastModified",Date.class);

            }
        }
    }
}
