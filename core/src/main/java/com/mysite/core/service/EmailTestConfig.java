package com.mysite.core.service;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
        name = "Email Test Configuration"
)
public @interface EmailTestConfig {

    @AttributeDefinition(
            name = "Send Test Email"
    )
    boolean sendEmail() default false;

}