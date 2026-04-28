package com.mysite.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;

import java.util.Collections;
import java.util.List;

@Model(
    adaptables = Resource.class,
    resourceType = "mysite/components/carousel-v2",
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class CarouselV2Model {

    @org.apache.sling.models.annotations.injectorspecific.SlingObject
    private Resource resource;

    @ChildResource
    private List<CarouselV2Item> carouselItems;

    public List<CarouselV2Item> getCarouselItems() {
        if (carouselItems != null) {
            return Collections.unmodifiableList(carouselItems);
        }
        return Collections.emptyList();
    }

    
}
