package com.mysite.core.service;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;


@ObjectClassDefinition
public @interface QuoteServiceConfiguration {

    @AttributeDefinition
    public boolean enabled() default true;

    @AttributeDefinition
    public String quotePath() default "zenquotes.io/api/random";




}
