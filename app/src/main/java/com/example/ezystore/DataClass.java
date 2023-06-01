package com.example.ezystore;

import android.view.contentcapture.DataRemovalRequest;

public class DataClass {
    private String imageURL, productName, description;

    public DataClass(){

    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DataClass(String imageURL, String productName, String description) {
        this.imageURL = imageURL;
        this.productName = productName;
        this.description = description;
    }


}
