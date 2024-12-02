package com.conestoga.ecommerceapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.conestoga.ecommerceapplication.constant.CommonConstant;
import com.conestoga.ecommerceapplication.enums.CollectionName;
import com.conestoga.ecommerceapplication.manager.CartManager;
import com.conestoga.ecommerceapplication.model.CartItem;
import com.conestoga.ecommerceapplication.model.Product;
import com.conestoga.ecommerceapplication.utils.FirebaseAuthUtils;
import com.conestoga.ecommerceapplication.utils.ImageUtils;
import com.google.android.material.badge.ExperimentalBadgeUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ProductDetailActivity extends AppCompatActivity {

    private final static String TAG = "ProductDetailActivity";

    private ImageView productImageView;
    private TextView productNameTextView;
    private TextView productPriceTextView;
    private TextView productDescriptionTextView;
    private TextView productStoreTextView;
    private EditText quantityInputEditText;
    private Button addToCartButton;
    private FloatingActionButton fabGoToCart;

    private String productId;
    private String productName;
    private String productImageUrl;
    private String productImageDetailUrl;
    private String productDescription;
    private String store;
    private double productPrice;

    @ExperimentalBadgeUtils
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // init ui component
        productImageView = findViewById(R.id.productImage);
        productNameTextView = findViewById(R.id.productName);
        productPriceTextView = findViewById(R.id.productPrice);
        productDescriptionTextView = findViewById(R.id.productDescription);
        productStoreTextView = findViewById(R.id.store);
        quantityInputEditText = findViewById(R.id.quantityInput);
        addToCartButton = findViewById(R.id.addToCartButton);
        fabGoToCart = findViewById(R.id.fabGoToCart);

        // get intent data
        Intent intent = getIntent();
        productId = intent.getStringExtra("productId");
        productName = intent.getStringExtra("productName");
        productImageUrl = intent.getStringExtra("productImageUrl");
        productImageDetailUrl = intent.getStringExtra("productImageDetailUrl");
        productPrice = intent.getDoubleExtra("price", 0);
        store = intent.getStringExtra("store");
        productDescription = intent.getStringExtra("description");

        // set product detail
        productNameTextView.setText(productName);
        productPriceTextView.setText(String.format("$%.2f", productPrice));
        productDescriptionTextView.setText(productDescription);
        productStoreTextView.setText(store);

        // loading product images
        ImageUtils.loadImageFromStorage(this, productImageView, productImageDetailUrl, productName, CommonConstant.IMAGE_DETAIL_TYPE);

        addToCartButton.setOnClickListener(v -> {
            String quantityStr = quantityInputEditText.getText().toString().trim();
            if (quantityStr.isEmpty() || Integer.parseInt(quantityStr) <= 0) {
                Toast.makeText(this, "Please enter a valid quantity.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (Integer.parseInt(quantityStr) > 9) {
                Toast.makeText(this, "The quantity has exceeded the maximum limit.", Toast.LENGTH_SHORT).show();
                return;
            }

            int quantity = Integer.parseInt(quantityStr);

            Product productItem = new Product(productId, productName, productImageUrl,
                    productImageDetailUrl, productPrice, store, productDescription);

            CartManager.getInstance().addToCart(
                    new CartItem(productItem, productItem.getPrice(), quantity)
            );

            // 把购物车中的数据存到数据库中, 用userId作主键
            List<CartItem> cartItems = CartManager.getInstance().getCartItems();
            String currentUserId = FirebaseAuthUtils.getCurrentUserId();
            if (!TextUtils.isEmpty(currentUserId)) {
                DatabaseReference cartItemRef = FirebaseDatabase.getInstance()
                        .getReference(CollectionName.CART_ITEMS.getName())
                        .child(currentUserId);
                for (CartItem cartItem : cartItems) {
                    cartItemRef.child(cartItem.getProductItem().getProductId()).setValue(cartItem).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.i(TAG, quantity + " " + productName + " save to database. currentUserId=" + currentUserId);
                        } else {
                            Log.i(TAG, quantity + " " + productName + " failed to save cart item to database.. currentUserId=" + currentUserId);
                        }
                    });
                }
            } else {
                Log.w(TAG, "Failed to get current user id." );
            }

            Toast.makeText(this, quantity + " " + productName + " added to cart", Toast.LENGTH_SHORT).show();
        });

        fabGoToCart.setOnClickListener(v -> {
            v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction(() -> {
                v.animate().scaleX(1f).scaleY(1f).setDuration(100);
                Intent cartIntent = new Intent(ProductDetailActivity.this, CartActivity.class);
                startActivity(cartIntent);
            });
        });
    }

}