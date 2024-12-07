package com.conestoga.ecommerceapplication.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.conestoga.ecommerceapplication.HomeActivity;
import com.conestoga.ecommerceapplication.R;
import com.conestoga.ecommerceapplication.adapter.CartAdapter;
import com.conestoga.ecommerceapplication.enums.CollectionName;
import com.conestoga.ecommerceapplication.listener.OnCheckoutClickListener;
import com.conestoga.ecommerceapplication.listener.ToolbarTitleListener;
import com.conestoga.ecommerceapplication.manager.CartManager;
import com.conestoga.ecommerceapplication.model.CartItem;
import com.conestoga.ecommerceapplication.model.Product;
import com.conestoga.ecommerceapplication.utils.FirebaseAuthUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CartFragment extends Fragment {

    private final static String TAG = "CartFragment";

    private OnCheckoutClickListener onCheckoutClickListener;

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private TextView totalPriceText;
    private Button checkoutButton;

    private List<CartItem> cartItemList;

    public CartFragment() {
    }

    public void setOnCheckoutClickListener(OnCheckoutClickListener listener) {
        this.onCheckoutClickListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cartItemList = CartManager.getInstance().getCartItems();

        loadCartItems();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        totalPriceText = view.findViewById(R.id.totalPriceText);
        checkoutButton = view.findViewById(R.id.checkoutButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        cartAdapter = new CartAdapter(cartItemList, getContext());
        HomeActivity homeActivity = (HomeActivity) requireActivity();
        cartAdapter.setOnProductClickListener(homeActivity);
        recyclerView.setAdapter(cartAdapter);

        // 设置监听器
        cartAdapter.setOnCartChangedListener(() -> {
            double totalPrice = calculateTotalPrice();
            updateCheckoutButtonState(totalPrice);
        });

        double totalPrice = calculateTotalPrice();

        updateCheckoutButtonState(totalPrice);

        checkoutButton.setOnClickListener(v -> {
            if (onCheckoutClickListener != null) {
                onCheckoutClickListener.onCheckoutClick(cartItemList);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof ToolbarTitleListener) {
            ((ToolbarTitleListener) getActivity()).updateToolbarTitle(getString(R.string.cart));
        }
    }

    private void loadCartItems() {
        DatabaseReference cartItemRef = FirebaseDatabase.getInstance().getReference(CollectionName.CART_ITEMS.getName());
        String currentUserId = FirebaseAuthUtils.getCurrentUserId();
        if (!TextUtils.isEmpty(currentUserId)) {
            cartItemRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    CartManager.getInstance().clearCart();
                    for (DataSnapshot data : snapshot.getChildren()) {
                        CartItem cartItem = data.getValue(CartItem.class);
                        CartManager.getInstance().addToCart(cartItem);
                    }
                    updateCheckoutButtonState(calculateTotalPrice());

                    cartAdapter.notifyDataSetChanged();

                    Log.i(TAG, "Successful to load cart items. currentUserId=" + currentUserId);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Failed to load cart items. currentUserId=" + currentUserId);
                }
            });
        }
    }

    public double calculateTotalPrice() {
        double totalPrice = 0;
        for (CartItem item : cartItemList) {
            Product productItem = item.getProductItem();
            totalPrice += productItem.getPrice() * item.getTotalQuantity();
        }
        totalPriceText.setText(String.format("Total: $%.2f", totalPrice));
        return totalPrice;
    }

    private void updateCheckoutButtonState(double totalPrice) {
        checkoutButton.setBackgroundResource(R.drawable.checkout_button_selector);
        checkoutButton.setEnabled(totalPrice > 0);
    }
}
