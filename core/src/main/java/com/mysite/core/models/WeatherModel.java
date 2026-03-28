package com.mysite.core.models;

import com.mysite.core.service.WeatherApi;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class WeatherModel {

    @OSGiService
    private WeatherApi weatherApi;

    @ValueMapValue
    private String latitude;

    @ValueMapValue
    private String longitude;

    private List<Map<String, String>> currentWeatherList;

    @PostConstruct
    protected void init() {

        currentWeatherList = new ArrayList<>();

        if (weatherApi != null && latitude != null && longitude != null) {
            currentWeatherList = weatherApi.getWeatherData(latitude, longitude);
        }
    }

    public List<Map<String, String>> getCurrentWeatherList() {
        return currentWeatherList;
    }
}