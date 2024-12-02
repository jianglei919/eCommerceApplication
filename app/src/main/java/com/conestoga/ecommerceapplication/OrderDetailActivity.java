package com.conestoga.ecommerceapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.conestoga.ecommerceapplication.adapter.ProductAdapter;
import com.conestoga.ecommerceapplication.model.CartItem;
import com.conestoga.ecommerceapplication.model.Order;
import com.conestoga.ecommerceapplication.model.PaymentInfo;
import com.conestoga.ecommerceapplication.model.Product;
import com.conestoga.ecommerceapplication.utils.DateTimeUtils;
import com.google.android.gms.common.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public class OrderDetailActivity extends AppCompatActivity {

    private TextView orderIdTextView;
    private TextView orderTimeTextView;
    private TextView orderStatusTextView;
    private TextView paymentNameTextView;
    private TextView paymentAddressTextView;
    private TextView paymentEmailTextView;
    private TextView paymentPhoneTextView;
    private TextView totalPriceTextView;
    private RecyclerView productRecyclerView;
    private ProductAdapter productAdapter; // Adapter for product list

    private List<Product> productList = new ArrayList<>(); // Assume a ProductItem model

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        // Initialize Views
        orderIdTextView = findViewById(R.id.orderId);
        orderTimeTextView = findViewById(R.id.orderTime);
        orderStatusTextView = findViewById(R.id.orderStatus);
        paymentNameTextView = findViewById(R.id.paymentName);
        paymentAddressTextView = findViewById(R.id.paymentAddress);
        paymentEmailTextView = findViewById(R.id.paymentEmail);
        paymentPhoneTextView = findViewById(R.id.paymentPhone);
        totalPriceTextView = findViewById(R.id.totalPrice);
        productRecyclerView = findViewById(R.id.productRecyclerView);

        // Sample data passed via Intent or fetched from Firebase
        Intent intent = getIntent();
        Order order = (Order) intent.getSerializableExtra("orderData"); // Deserialize order object

        if (order != null) {
            // Populate order details
            orderIdTextView.append(order.getOrderId());
            orderTimeTextView.append(DateTimeUtils.formatTimestamp(order.getOrderTime()));
            orderStatusTextView.append(order.getStatus());

            // Populate payment info
            PaymentInfo paymentInfo = order.getPaymentInfo();
            if (paymentInfo != null) {
                paymentNameTextView.append(paymentInfo.getFirstName() + " " + paymentInfo.getLastName());
                paymentAddressTextView.append(paymentInfo.getAddress() + ", " + paymentInfo.getCity() + ", " + paymentInfo.getState() + " " + paymentInfo.getPostalCode());
                paymentEmailTextView.append(paymentInfo.getEmail());
                paymentPhoneTextView.append(paymentInfo.getPhone());
            }

            // Populate product list
            List<CartItem> cartItemList = order.getProductItemList();
            if (!CollectionUtils.isEmpty(cartItemList)) {
                productList = cartItemList.stream()
                        .map(CartItem::getProductItem)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            }
            productRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            productAdapter = new ProductAdapter(productList, this);
            productRecyclerView.setAdapter(productAdapter);

            // Set total price
            totalPriceTextView.append(String.format("$%.2f", order.getTotalPrice()));
        }
    }

}