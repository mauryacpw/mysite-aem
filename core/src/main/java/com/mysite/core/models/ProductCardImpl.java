    package com.mysite.core.models;

    import com.fasterxml.jackson.annotation.JsonIgnore;
    import org.apache.sling.models.annotations.DefaultInjectionStrategy;
    import org.apache.sling.models.annotations.Exporter;
    import org.apache.sling.models.annotations.Model;
    import org.apache.sling.models.annotations.injectorspecific.Self;
    import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

    import org.apache.sling.api.resource.Resource;

    import javax.annotation.PostConstruct;
    import java.util.Date;

    @Model(adaptables = {Resource.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
    public class ProductCardImpl implements ProductCard {

        @ValueMapValue
        private String productTitle;
        @ValueMapValue
        @JsonIgnore
        private Date productExpiry;
        @ValueMapValue
        private int productPrice;
        @ValueMapValue
        private String productImage;

        @ValueMapValue
        private String productColor;
        @ValueMapValue
        private String[] productTag;
        @ValueMapValue
        @JsonIgnore
        private boolean productExpired;

        public void setProductTitle(String productTitle) {
            this.productTitle = productTitle;
        }

        public void setProductExpiry(Date productExpiry) {
            this.productExpiry = productExpiry;
        }

        public void setProductPrice(int productPrice) {
            this.productPrice = productPrice;
        }

        public void setProductImage(String productImage) {
            this.productImage = productImage;
        }

        public void setProductTag(String[] productTag) {
            this.productTag = productTag;
        }

        public void setProductColor(String productColor) {
            this.productColor = productColor;
        }

        public void setProductExpired(boolean productExpired) {
            this.productExpired = productExpired;
        }


        public String getProductTitle() {
            return productTitle;
        }

        public Date getProductExpiry() {
            return productExpiry;
        }

        public int getProductPrice() {
            return productPrice;
        }

        public String getProductImage() {
            return productImage;
        }

        public String getProductColor() {
            return productColor;
        }

        public String[] getProductTag() {
            return productTag;
        }

        public boolean isProductExpired() {
            return productExpired;
        }


        @PostConstruct
        public void init(){
            Date today = new Date();

            if(productExpiry!= null && productExpiry.compareTo(today)<0){
                productExpired = true;
            }
        }
    }
