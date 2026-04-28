package com.mysite.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CarouselV2Item extends CarouselItem {

    @ValueMapValue
    private String mediaType;

    @ValueMapValue
    private String videoPath;

    public String getMediaType() {
        return mediaType;
    }

    public String getVideoPath() {
        return videoPath;
    }
}
