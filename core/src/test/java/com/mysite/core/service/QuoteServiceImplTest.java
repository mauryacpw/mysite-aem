package com.mysite.core.service;

import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuoteServiceImplTest {

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse httpResponse;

    @Mock
    private QuoteServiceConfiguration config;

    @InjectMocks
    private QuoteServiceImpl quoteService;

    @BeforeEach
    void setUp() throws Exception {

        Field field = QuoteServiceImpl.class.getDeclaredField("client");
        field.setAccessible(true);
        field.set(quoteService, httpClient);
    }

    private String readJson(String path) throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream(path);
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }

    @Test
    void testGetResponseSuccess() throws Exception {

        String json = readJson("serviceTest.json");

        when(config.enabled()).thenReturn(true);
        when(config.quotePath()).thenReturn("api.example.com/quotes");

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);

        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(json);

        quoteService.activate(config);

        String response = quoteService.getResponse();

        assertNotNull(response);
        assertTrue(response.contains("Test Quote"));
    }

    @Test
    void testServiceDisabled() {

        when(config.enabled()).thenReturn(false);
        when(config.quotePath()).thenReturn("api.example.com/quotes");

        quoteService.activate(config);

        String response = quoteService.getResponse();

        assertNull(response);
    }

    @Test
    void testHttpExceptionHandled() throws Exception {

        when(config.enabled()).thenReturn(true);
        when(config.quotePath()).thenReturn("api.example.com/quotes");

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new RuntimeException("HTTP failure"));

        quoteService.activate(config);

        String response = quoteService.getResponse();

        assertNull(response);
    }
}