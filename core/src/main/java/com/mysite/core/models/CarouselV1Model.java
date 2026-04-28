package com.mysite.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;

import java.util.Collections;
import java.util.List;

@Model(
    adaptables = Resource.class,
    resourceType = "mysite/components/carousel-v1",
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class CarouselV1Model {

    @org.apache.sling.models.annotations.injectorspecific.SlingObject
    private Resource resource;

    @ChildResource
    private List<CarouselItem> carouselItems;

    public List<CarouselItem> getCarouselItems() {
        if (carouselItems != null) {
            return Collections.unmodifiableList(carouselItems);
        }
        return Collections.emptyList();
    }

    
}
