package com.conestoga.ecommerceapplication.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.conestoga.ecommerceapplication.HomeActivity;
import com.conestoga.ecommerceapplication.R;
import com.conestoga.ecommerceapplication.adapter.ProductDetailAdapter;
import com.conestoga.ecommerceapplication.model.CartItem;
import com.conestoga.ecommerceapplication.model.Order;
import com.conestoga.ecommerceapplication.model.PaymentInfo;
import com.conestoga.ecommerceapplication.model.Product;
import com.conestoga.ecommerceapplication.utils.DateTimeUtils;
import com.google.android.gms.common.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class OrderDetailFragment extends Fragment {

    private final static String TAG = "OrderDetailFragment";

    private static final String ARG_ORDER_DATA = "orderData";

    private TextView orderIdTextView;
    private TextView orderTimeTextView;
    private TextView orderStatusTextView;
    private TextView totalPriceTextView2;
    private TextView paymentNameTextView;
    private TextView paymentAddressTextView;
    private TextView paymentEmailTextView;
    private TextView paymentPhoneTextView;
    private TextView totalPriceTextView;
    private RecyclerView productRecyclerView;

    private ProductDetailAdapter productDetailAdapter; // Adapter for product list

    private List<Product> productList = new ArrayList<>();
    private Order order;

    public OrderDetailFragment() {
        // Required empty public constructor
    }

    public static OrderDetailFragment newInstance(Order order) {
        OrderDetailFragment fragment = new OrderDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ORDER_DATA, order);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            order = (Order) getArguments().getSerializable(ARG_ORDER_DATA); // Deserialize order object
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Views
        orderIdTextView = view.findViewById(R.id.orderId);
        orderTimeTextView = view.findViewById(R.id.orderTime);
        orderStatusTextView = view.findViewById(R.id.orderStatus);
        totalPriceTextView2 = view.findViewById(R.id.totalPrice2);
        paymentNameTextView = view.findViewById(R.id.paymentName);
        paymentAddressTextView = view.findViewById(R.id.paymentAddress);
        paymentEmailTextView = view.findViewById(R.id.paymentEmail);
        paymentPhoneTextView = view.findViewById(R.id.paymentPhone);
        totalPriceTextView = view.findViewById(R.id.totalPrice);
        productRecyclerView = view.findViewById(R.id.productRecyclerView);

        if (order != null) {
            // Populate order details
            orderIdTextView.append(order.getOrderId());
            orderTimeTextView.append(DateTimeUtils.formatTimestamp(order.getOrderTime()));
            orderStatusTextView.append(order.getStatus());
            totalPriceTextView2.append(String.format("$%.2f", order.getTotalPrice()));

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
            productRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            HomeActivity activity = (HomeActivity) requireActivity();
            productDetailAdapter = new ProductDetailAdapter(activity, productList, getContext());
            productRecyclerView.setAdapter(productDetailAdapter);

            // Set total price
            totalPriceTextView.append(String.format("$%.2f", order.getTotalPrice()));
        }
    }
}