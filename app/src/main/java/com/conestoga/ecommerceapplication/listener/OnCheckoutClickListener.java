package com.conestoga.ecommerceapplication.listener;

import com.conestoga.ecommerceapplication.model.CartItem;

import java.util.List;

public interface OnCheckoutClickListener {

    void onCheckoutClick(List<CartItem> cartItems);
}
