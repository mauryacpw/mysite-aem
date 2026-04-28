package com.mysite.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CarouselItem {

    @ValueMapValue
    private String imagePath;

    @ValueMapValue
    private String title;

    @ValueMapValue
    private String buttonText;

    @ValueMapValue
    private String buttonLink;

    public String getImagePath() {
        return imagePath;
    }

    public String getTitle() {
        return title;
    }

    public String getButtonText() {
        return buttonText;
    }

    public String getButtonLink() {
        return buttonLink;
    }
}
