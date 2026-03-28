package com.mysite.core.schedulers;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition
public @interface ArticleExpirySchedulerConfig {
    @AttributeDefinition(name="Cron Expression")
    String cronExpression() default "*/5 * * * * *";

    @AttributeDefinition(name = "Scheduler Name")
    String schedulerName() default "TestScheduler";

    @AttributeDefinition(name = "Enable/Disable Schedular")
    boolean enable() default false;

}
