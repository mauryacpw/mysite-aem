package com.mysite.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component(immediate = true, service = QuoteService.class)
@Designate(ocd = QuoteServiceConfiguration.class)
public class QuoteServiceImpl implements QuoteService {
    Logger log = LoggerFactory.getLogger(QuoteServiceConfiguration.class);
    private final HttpClient client = HttpClient.newHttpClient();
    private QuoteServiceConfiguration config;




    @Activate
    @Modified
    public void activate(QuoteServiceConfiguration config ) {
        this.config = config;
        log.info("Quote service modified");
        getResponse();
    }

    public String getResponse(){
        final String url = "https://"+ config.quotePath().trim();
        String jsonResponse = null;


        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try{

            if(config.enabled()){
                HttpResponse<String> response = client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

                log.info(url);

                int statusCode = response.statusCode();
                log.info("Status Code: {}", statusCode);

                final String responseBody = response.body();

                  jsonResponse = responseBody;


            }

        }
        catch(Exception e){
            log.error("Exception/Error: {} ",e.getMessage());
        }

        return jsonResponse;
    }

}
