package com.conestoga.ecommerceapplication.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.conestoga.ecommerceapplication.R;
import com.conestoga.ecommerceapplication.constant.CommonConstant;
import com.conestoga.ecommerceapplication.enums.CollectionName;
import com.conestoga.ecommerceapplication.listener.ToolbarTitleListener;
import com.conestoga.ecommerceapplication.manager.CartManager;
import com.conestoga.ecommerceapplication.model.CartItem;
import com.conestoga.ecommerceapplication.model.Product;
import com.conestoga.ecommerceapplication.utils.FirebaseAuthUtils;
import com.conestoga.ecommerceapplication.utils.ImageUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ProductDetailFragment extends Fragment {

    private final static String TAG = "ProductDetailFragment";

    private static final String ARG_PRODUCT_DATA = "productData";

    private ImageView productImageView;
    private TextView productNameTextView;
    private TextView productPriceTextView;
    private TextView productDescriptionTextView;
    private TextView productStoreTextView;
    private EditText quantityInputEditText;
    private Button addToCartButton;

    private Product product;

    public ProductDetailFragment() {
        // Required empty public constructor
    }

    public static ProductDetailFragment newInstance(Product product) {
        ProductDetailFragment fragment = new ProductDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PRODUCT_DATA, product);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_detail, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 从 Bundle 获取数据
        if (getArguments() != null) {
            product = (Product) getArguments().getSerializable(ARG_PRODUCT_DATA);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 初始化 UI 组件
        productImageView = view.findViewById(R.id.productImage);
        productNameTextView = view.findViewById(R.id.productName);
        productPriceTextView = view.findViewById(R.id.productPrice);
        productDescriptionTextView = view.findViewById(R.id.productDescription);
        productStoreTextView = view.findViewById(R.id.store);
        quantityInputEditText = view.findViewById(R.id.quantityInput);
        addToCartButton = view.findViewById(R.id.addToCartButton);

        // 设置产品详情
        productNameTextView.setText(product.getProductName());
        productPriceTextView.setText(String.format("$%.2f", product.getPrice()));
        productDescriptionTextView.setText(product.getDescription());
        productStoreTextView.setText(product.getStore());

        // 加载图片
        ImageUtils.loadImageFromStorage(getContext(), productImageView, product.getProductImageUrl(), product.getProductName(), CommonConstant.IMAGE_DETAIL_TYPE);

        addToCartButton.setOnClickListener(v -> {
            // 点击逻辑
            String quantityStr = quantityInputEditText.getText().toString().trim();
            if (quantityStr.isEmpty() || Integer.parseInt(quantityStr) <= 0) {
                Toast.makeText(getContext(), "Please enter a valid quantity.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (Integer.parseInt(quantityStr) > 9) {
                Toast.makeText(getContext(), "The quantity has exceeded the maximum limit.", Toast.LENGTH_SHORT).show();
                return;
            }

            int quantity = Integer.parseInt(quantityStr);

            CartManager.getInstance().addToCart(
                    new CartItem(product, product.getPrice(), quantity)
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
                            Log.i(TAG, quantity + " " + product.getProductName() + " save to database. currentUserId=" + currentUserId);
                        } else {
                            Log.i(TAG, quantity + " " + product.getProductName() + " failed to save cart item to database.. currentUserId=" + currentUserId);
                        }
                    });
                }
            } else {
                Log.w(TAG, "Failed to get current user id.");
            }

            Toast.makeText(getContext(), quantity + " " + product.getProductName() + " added to cart", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof ToolbarTitleListener) {
            ((ToolbarTitleListener) getActivity()).updateToolbarTitle(getString(R.string.product_detail));
        }
    }
}
