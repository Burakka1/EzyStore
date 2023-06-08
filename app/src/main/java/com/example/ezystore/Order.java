package com.example.ezystore;

public class Order {
    private String productName, price, imageUrl, count,  email, formattedDate;
    public Order() {
    }
    public Order(String productName, String price, String imageUrl, String count, String email, String formattedDate) {
        this.productName = productName;
        this.price = price;
        this.imageUrl = imageUrl;
        this.count = count;
        this.email = email;
        this.formattedDate = formattedDate;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }
}
