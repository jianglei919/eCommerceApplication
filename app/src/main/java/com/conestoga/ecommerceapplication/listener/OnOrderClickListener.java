package com.conestoga.ecommerceapplication.listener;

import com.conestoga.ecommerceapplication.model.Order;

public interface OnOrderClickListener {
    void onOrderItemClick(Order order);
}
