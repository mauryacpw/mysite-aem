package com.mysite.core.models;

import java.util.Date;

public interface ProductCard {
     String getProductTitle() ;

     Date getProductExpiry();

     int getProductPrice();
     String getProductImage();

     String getProductColor();

     String[] getProductTag();
     boolean isProductExpired();
}
