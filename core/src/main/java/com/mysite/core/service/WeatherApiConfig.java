package com.mysite.core.service;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition
public @interface WeatherApiConfig {
    @AttributeDefinition
    String apiUrl() default "https://api.open-meteo.com/v1/forecast";
    String latitude() default "16.5";
    String longitude() default "80.6";
    boolean current_weather() default true;
}
