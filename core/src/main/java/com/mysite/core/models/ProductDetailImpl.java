package com.mysite.core.models;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Model(adaptables = {Resource.class},
        adapters = {ProductDetail.class, ComponentExporter.class},
        resourceType = "mysite/components/custom-components/product-details-sling-model",
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
        extensions = ExporterConstants.SLING_MODEL_EXTENSION
)

public class ProductDetailImpl implements ProductDetail, ComponentExporter {

    @SlingObject
    private ResourceResolver resolver;

    @ValueMapValue
    private String title;
    @ValueMapValue
    private String description;

    @ValueMapValue
    @JsonIgnore
    private boolean status;

    @ValueMapValue
    private String category;

    @ValueMapValue
    private boolean showCF;

    @ValueMapValue
    private String[] contentFragments;

    @ChildResource
    private List<ProductCardImpl> productCards;

    private List<ProductCardImpl> contentFragmentProducts;


    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }


    public boolean isStatus() {
        return status;
    }


    public List<ProductCardImpl> getContentFragmentProducts() {
        return contentFragmentProducts;
    }

    public List<ProductCardImpl> getProductCards() {
        return productCards;
    }

    public String getCategory() {
        return category;
    }


    public boolean isShowCF() {
        return showCF;
    }

    public String[] getContentFragments() {
        return contentFragments;
    }


    @Override
    public String getExportedType() {
        return "mysite/components/custom-components/product-details-sling-model";
    }

    @PostConstruct
    public void init() {


        if (showCF && contentFragments != null) {

            contentFragmentProducts = new ArrayList<>();


            for (String path : contentFragments) {
                Resource cfresource = resolver.getResource(path + "/jcr:content/data/master");
                if (cfresource != null) {
                    ProductCardImpl productCard = cfresource.adaptTo(ProductCardImpl.class);
                    if (productCard != null) {
                        contentFragmentProducts.add(productCard);
                    }

                }
            }

        }


    }


}
