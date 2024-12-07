package com.conestoga.ecommerceapplication.manager;

import com.conestoga.ecommerceapplication.model.CartItem;
import com.conestoga.ecommerceapplication.model.Product;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private List<CartItem> cartItems;

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void addToCart(CartItem item) {
        for (CartItem cartItem : cartItems) {
            Product productItem = cartItem.getProductItem();
            if (productItem.getProductId().equals(item.getProductItem().getProductId())) {
                // If the product exist, add quantity
                cartItem.setTotalQuantity(cartItem.getTotalQuantity() + item.getTotalQuantity());
                return;
            }
        }
        // If the product not exist, add new product
        cartItems.add(item);
    }

    public void removeFromCart(String productId) {
        cartItems.removeIf(item -> item.getProductItem().getProductId().equals(productId));
    }

    public void clearCart() {
        cartItems.clear();
    }

    public double getTotalPrice() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getProductItem().getPrice() * item.getProductItem().getQuantity();
        }
        return total;
    }

    public void updateItemQuantity(String productId, int quantity) {
        for (CartItem item : cartItems) {
            if (item.getProductItem().getProductId().equals(productId)) {
                item.setTotalQuantity(quantity);
                break;
            }
        }
    }

    public int getCartItemCount() {
        return cartItems.size();
    }
}
