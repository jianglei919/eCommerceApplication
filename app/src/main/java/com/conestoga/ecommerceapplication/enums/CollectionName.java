package com.conestoga.ecommerceapplication.enums;

public enum CollectionName {
    PRODUCTS("products", "The collections of product"),
    CART_ITEMS("cartItems", "The collections of cart item"),
    ORDERS("orders", "The collections of user's order list"),
    ;

    private final String name;

    private final String desc;

    CollectionName(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }
}
