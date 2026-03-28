package com.mysite.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;

public interface QuoteService {


    String getResponse();
}
