package com.mysite.core.schedulers;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition
public @interface ScheduledContentExpiryConfig {

    @AttributeDefinition(name = "Enable Scheduler")
    boolean enable() default true;

    @AttributeDefinition(name = "Scheduler Name")
    String schedulerName() default "ScheduledContentExpiry";

    @AttributeDefinition(name = "Cron Expression")
    String cronExpression() default "*/10 * * * * ?";

    @AttributeDefinition(name = "RootPath")
    String rootPath() default "/content/mysite/us/aritclelandingpage";
}
