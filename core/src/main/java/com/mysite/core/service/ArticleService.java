package com.mysite.core.service;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.*;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = ArticleService.class,immediate = true)
@Designate(ocd = ArticleServiceConfig.class)
public class ArticleService {

    Logger log = LoggerFactory.getLogger(ArticleService.class);
    @Reference
    private NPUtilService npUtilService;
    @Activate
    public void init(ArticleServiceConfig config) {
        log.info("Inside activate method");
    }

    @Deactivate
    public void deactivate(ArticleServiceConfig config) {
        log.info("Inside deactivate method");
    }
    @Modified
    public void update(ArticleServiceConfig config){
        log.info("Updated Config: {}",config.articleApiUrl());
        log.info("Updated Status: {}",config.status());
        log.info("inside modified method");

        ResourceResolver resolver = npUtilService.getResourceResolver();
        log.info("inside modified method: "+ resolver);

        Resource page = resolver.getResource("/content/mysite/home/about-us/jcr:content");

        log.info("inside modified method page: "+ page);


    }

    public String getArticles(ArticleServiceConfig config){
        return "Articles from Api";
    }
}
