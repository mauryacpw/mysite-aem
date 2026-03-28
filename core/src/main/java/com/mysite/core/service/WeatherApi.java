package com.mysite.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import org.osgi.service.component.annotations.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Component(service = WeatherApi.class, immediate = true)
public class WeatherApi {

    private static final Logger LOG = LoggerFactory.getLogger(WeatherApi.class);

    private static final String BASE_URL =
            "https://api.open-meteo.com/v1/forecast";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Map<String, String>> getWeatherData(String latitude, String longitude) {

        List<Map<String, String>> resultList = new ArrayList<>();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            String apiUrl = BASE_URL +
                    "?latitude=" + latitude +
                    "&longitude=" + longitude +
                    "&current_weather=true";

            HttpGet request = new HttpGet(apiUrl);

            try (CloseableHttpResponse response = httpClient.execute(request)) {

                String jsonResponse = EntityUtils.toString(response.getEntity());

                JsonNode rootNode = objectMapper.readTree(jsonResponse);
                JsonNode currentWeatherNode = rootNode.get("current_weather");

                if (currentWeatherNode != null) {

                    Map<String, String> weatherMap = new HashMap<>();

                    Iterator<Map.Entry<String, JsonNode>> fields = currentWeatherNode.fields();

                    while (fields.hasNext()) {
                        Map.Entry<String, JsonNode> entry = fields.next();
                        weatherMap.put(entry.getKey(), entry.getValue().asText());
                    }

                    resultList.add(weatherMap);
                }
            }

        } catch (Exception e) {
            LOG.error("Error fetching weather data", e);
        }

        return resultList;
    }
}