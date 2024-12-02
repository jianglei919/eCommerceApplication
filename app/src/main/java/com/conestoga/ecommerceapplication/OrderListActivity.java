package com.conestoga.ecommerceapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.conestoga.ecommerceapplication.adapter.OrderListAdapter;
import com.conestoga.ecommerceapplication.enums.CollectionName;
import com.conestoga.ecommerceapplication.model.Order;
import com.conestoga.ecommerceapplication.utils.FirebaseAuthUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderListActivity extends AppCompatActivity {

    private final static String TAG = "OrderListActivity";

    private RecyclerView orderRecyclerView;
    private LinearLayout emptyOrderLayout;
    private Button goToProductsButton;
    private OrderListAdapter orderListAdapter;
    private List<Order> orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        orderRecyclerView = findViewById(R.id.orderRecyclerView);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        emptyOrderLayout = findViewById(R.id.emptyOrderLayout);
        goToProductsButton = findViewById(R.id.goToProductsButton);

        // 添加按钮事件，跳转到产品列表
        goToProductsButton.setOnClickListener(v -> {
            Intent intent = new Intent(OrderListActivity.this, ProductActivity.class);
            startActivity(intent);
        });

        orderList = new ArrayList<>();

        orderListAdapter = new OrderListAdapter(orderList, this);
        orderRecyclerView.setAdapter(orderListAdapter);

        loadOrders();
    }

    private void loadOrders() {
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference(CollectionName.ORDERS.getName());
        String currentUserId = FirebaseAuthUtils.getCurrentUserId();
        if (TextUtils.isEmpty(currentUserId)) {
            showEmptyOrderLayout();
            return;
        }
        orderRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();
                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    Order order = orderSnapshot.getValue(Order.class);
                    if (order != null) {
                        orderList.add(order);
                    }
                }

                if (orderList.isEmpty()) {
                    showEmptyOrderLayout();
                } else {
                    showOrderRecyclerView();
                }

                Log.e(TAG, "Successful to load orders. currentUserId=" + currentUserId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load orders. currentUserId=" + currentUserId);
                showEmptyOrderLayout();
            }
        });
    }

    private void showEmptyOrderLayout() {
        emptyOrderLayout.setVisibility(View.VISIBLE);
        orderRecyclerView.setVisibility(View.GONE);
    }

    private void showOrderRecyclerView() {
        emptyOrderLayout.setVisibility(View.GONE);
        orderRecyclerView.setVisibility(View.VISIBLE);
        orderListAdapter.notifyDataSetChanged();
    }
}