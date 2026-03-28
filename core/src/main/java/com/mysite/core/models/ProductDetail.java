package com.mysite.core.models;

import java.util.List;

public interface ProductDetail {

    String getTitle();
    String getDescription();
    boolean isStatus();
    List<ProductCardImpl> getProductCards();
    String getCategory();

}
