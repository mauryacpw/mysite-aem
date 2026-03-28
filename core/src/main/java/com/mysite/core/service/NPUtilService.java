package com.mysite.core.service;


import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Component(service = NPUtilService.class, immediate = true)
public class NPUtilService {

    private static final Logger log = LoggerFactory.getLogger(NPUtilService.class);
    @Reference
    private ResourceResolverFactory factory;

    public ResourceResolver getResourceResolver()  {
        Map<String , Object> map= new HashMap<>();
        map.put(ResourceResolverFactory.SUBSERVICE,"npsubservice");

        ResourceResolver resolver=null;

        try {
            resolver= factory.getServiceResourceResolver(map);
        } catch (LoginException e) {
            throw new RuntimeException(e);
        }

        return  resolver;
    }



}