package com.mysite.core.schedulers;

import org.apache.sling.models.annotations.Default;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition
public @interface StatisticsSchedulerConfig {
    @AttributeDefinition(name = "SchedulerName")
    String schedulerName() default "StatisticsJobScheduler";
    @AttributeDefinition(name = "Cron EXPR")
    String cronExpression() default "0 0 0 ? * MON";
    @AttributeDefinition(name = "Enable/Disable Scheduler")
    boolean enable() default true;
    @AttributeDefinition(name = "Project Content Path")
    String projectContentPath();
    @AttributeDefinition(name = "Project Dam Path")
    String projectDamPath();

}
