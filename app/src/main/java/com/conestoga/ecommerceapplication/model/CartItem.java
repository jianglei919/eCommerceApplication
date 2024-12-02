package com.conestoga.ecommerceapplication.model;

import java.io.Serializable;

public class CartItem implements Serializable {
    private Product productItem;
    private double totalPrice;
    private int totalQuantity;

    public CartItem() {
    }

    public CartItem(Product productItem, double totalPrice, int totalQuantity) {
        this.productItem = productItem;
        this.totalPrice = totalPrice;
        this.totalQuantity = totalQuantity;
    }

    public Product getProductItem() {
        return productItem;
    }

    public void setProductItem(Product productItem) {
        this.productItem = productItem;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }
}
