package com.mysite.core.schedulers;

import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.AttributeDefinition;

@ObjectClassDefinition(name = "Expired Content Scheduler Configuration")
public @interface ExpiredContentSchedulerConfig {

    @AttributeDefinition(name = "Enable Scheduler")
    boolean enable() default true;

    @AttributeDefinition(name = "Scheduler Name")
    String schedulerName() default "ExpiredContentScheduler";

    @AttributeDefinition(name = "Cron Expression")
    String cronExpression() default "*/10 * * * * ?";
}