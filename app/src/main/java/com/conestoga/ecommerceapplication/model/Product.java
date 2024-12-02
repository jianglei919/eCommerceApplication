package com.conestoga.ecommerceapplication.model;

import java.io.Serializable;

public class Product implements Serializable {

    private String productId;
    private String productName;
    private String productImageUrl;
    private String productImageDetailUrl;
    private double price;
    private String store;
    private String description;
    private int quantity = 1;

    public Product() {
    }

    public Product(String productId, String productName, String productImageUrl, String productImageDetailUrl, double price, String store, String description) {
        this.productId = productId;
        this.productName = productName;
        this.productImageUrl = productImageUrl;
        this.productImageDetailUrl = productImageDetailUrl;
        this.price = price;
        this.store = store;
        this.description = description;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public String getProductImageDetailUrl() {
        return productImageDetailUrl;
    }

    public void setProductImageDetailUrl(String productImageDetailUrl) {
        this.productImageDetailUrl = productImageDetailUrl;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
