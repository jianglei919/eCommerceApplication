package com.conestoga.ecommerceapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.conestoga.ecommerceapplication.adapter.CartAdapter;
import com.conestoga.ecommerceapplication.enums.CollectionName;
import com.conestoga.ecommerceapplication.manager.CartManager;
import com.conestoga.ecommerceapplication.model.CartItem;
import com.conestoga.ecommerceapplication.model.Product;
import com.conestoga.ecommerceapplication.utils.FirebaseAuthUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private final static String TAG = "CartActivity";

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItemList;
    private TextView totalPriceText;
    private Button checkoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.recyclerView);
        totalPriceText = findViewById(R.id.totalPriceText);
        checkoutButton = findViewById(R.id.checkoutButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartItemList = CartManager.getInstance().getCartItems();

        loadCartItems();

        cartAdapter = new CartAdapter(cartItemList, this);
        recyclerView.setAdapter(cartAdapter);

        // 设置监听器
        cartAdapter.setOnCartChangedListener(() -> {
            double totalPrice = calculateTotalPrice();
            updateCheckoutButtonState(totalPrice);
        });

        double totalPrice = calculateTotalPrice();

        updateCheckoutButtonState(totalPrice);

        checkoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
            intent.putExtra("cartItemList", new ArrayList<>(cartItemList));
            startActivity(intent);
        });
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

                    Log.e(TAG, "Successful to load cart items. currentUserId=" + currentUserId);
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