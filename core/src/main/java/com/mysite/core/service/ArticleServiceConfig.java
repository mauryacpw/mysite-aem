package com.mysite.core.service;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition
public @interface ArticleServiceConfig {

    @AttributeDefinition(name = "Article RestAPI")
    public String articleApiUrl() default "/path/v1/article";

    @AttributeDefinition(name="status")
    public boolean status() default true;
}
