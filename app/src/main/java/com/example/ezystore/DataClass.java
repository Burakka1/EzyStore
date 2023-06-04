package com.example.ezystore;


public class DataClass {
    private String imageURL, productName, description, price, ticket;


    public DataClass() {

    }


    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
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

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public DataClass(String imageURL, String productName, String description, String price, String ticket) {
        this.imageURL = imageURL;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.ticket = ticket;
    }


}
