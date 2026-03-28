package com.mysite.core.service;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.Session;

@Component(service = TestService.class,immediate = true)
public class TestService {

    @Reference
    private NPUtilService npUtilService;

    @Activate
    @Modified
    protected void activate()
    {
        ResourceResolver resourceResolver = npUtilService.getResourceResolver();
        Session session = resourceResolver.adaptTo(Session.class);
        assert session != null;
        Resource resource;

    }
}
