package com.conestoga.ecommerceapplication.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.conestoga.ecommerceapplication.HomeActivity;
import com.conestoga.ecommerceapplication.R;
import com.conestoga.ecommerceapplication.adapter.OrderListAdapter;
import com.conestoga.ecommerceapplication.enums.CollectionName;
import com.conestoga.ecommerceapplication.listener.ToolbarTitleListener;
import com.conestoga.ecommerceapplication.model.Order;
import com.conestoga.ecommerceapplication.utils.FirebaseAuthUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderListFragment extends Fragment {

    private final static String TAG = "OrderListFragment";

    private RecyclerView orderRecyclerView;
    private LinearLayout emptyOrderLayout;
    private Button goToProductsButton;

    private OrderListAdapter orderListAdapter;

    private List<Order> orderList = new ArrayList<>();

    public OrderListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadOrders();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        orderRecyclerView = view.findViewById(R.id.orderRecyclerView);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        emptyOrderLayout = view.findViewById(R.id.emptyOrderLayout);
        goToProductsButton = view.findViewById(R.id.goToProductsButton);

        HomeActivity activity = (HomeActivity) requireActivity();
        orderListAdapter = new OrderListAdapter(activity, orderList, getContext());
        orderRecyclerView.setAdapter(orderListAdapter);

        // 添加按钮事件，跳转到产品列表
        goToProductsButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), HomeActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof ToolbarTitleListener) {
            ((ToolbarTitleListener) getActivity()).updateToolbarTitle(getString(R.string.orders));
        }
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

                Log.i(TAG, "Successful to load orders. currentUserId=" + currentUserId);
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