package com.conestoga.ecommerceapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.conestoga.ecommerceapplication.fragment.CartFragment;
import com.conestoga.ecommerceapplication.fragment.CheckoutFragment;
import com.conestoga.ecommerceapplication.fragment.OrderDetailFragment;
import com.conestoga.ecommerceapplication.fragment.OrderListFragment;
import com.conestoga.ecommerceapplication.fragment.ProductDetailFragment;
import com.conestoga.ecommerceapplication.fragment.ProductListFragment;
import com.conestoga.ecommerceapplication.listener.OnCheckoutClickListener;
import com.conestoga.ecommerceapplication.listener.OnOrderClickListener;
import com.conestoga.ecommerceapplication.listener.OnProductClickListener;
import com.conestoga.ecommerceapplication.listener.ToolbarTitleListener;
import com.conestoga.ecommerceapplication.model.CartItem;
import com.conestoga.ecommerceapplication.model.Order;
import com.conestoga.ecommerceapplication.model.Product;
import com.conestoga.ecommerceapplication.utils.FirebaseAuthUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class HomeActivity extends AppCompatActivity implements OnProductClickListener,
        OnOrderClickListener, OnCheckoutClickListener, ToolbarTitleListener {

    private final static String TAG = "HomeActivity";

    private TextView toolbarTitle;
    private BottomNavigationView bottomNavigationView;

    private static final int EXIT_INTERVAL = 2000; // 两次点击的间隔，单位：毫秒
    private long lastBackPressedTime; // 记录上一次点击返回键的时间

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbarTitle = findViewById(R.id.appTitle);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 初始化 Fragment 容器
        if (savedInstanceState == null) {
            loadFragment(new ProductListFragment());
            updateToolbarTitle(getString(R.string.products));
        }

        // 设置底部导航栏点击事件
        setupBottomNavigation();

        // 注册返回按钮回调
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                FragmentManager fragmentManager = getSupportFragmentManager();
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStack(); // 回退到上一个Fragment
                } else if (System.currentTimeMillis() - lastBackPressedTime < EXIT_INTERVAL) {
                    finish(); // 退出应用
                } else {
                    lastBackPressedTime = System.currentTimeMillis();
                    Toast.makeText(HomeActivity.this, "Press again to exit", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "HomeActivity onDestroy");
    }

    @Override
    public void onProductItemClick(Product product) {
        ProductDetailFragment fragment = ProductDetailFragment.newInstance(product);
        loadFragment(fragment);
    }

    @Override
    public void onOrderItemClick(Order order) {
        OrderDetailFragment fragment = OrderDetailFragment.newInstance(order);
        loadFragment(fragment);
    }

    @Override
    public void onCheckoutClick(List<CartItem> cartItems) {
        CheckoutFragment fragment = CheckoutFragment.newInstance(cartItems);
        loadFragment(fragment);
    }

    @Override
    public void updateToolbarTitle(String title) {
        if (toolbarTitle != null) {
            toolbarTitle.setText(title);
        }
    }

    private void loadFragment(Fragment fragment) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment != null && currentFragment.getClass().equals(fragment.getClass())) {
            return; // 当前 Fragment 已经显示，无需加载
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);

        // 如果不是 ProductListFragment，添加到返回栈
        if (!(fragment instanceof ProductListFragment)) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                loadFragment(new ProductListFragment());
                updateToolbarTitle(getString(R.string.products));
                return true;
            } else if (itemId == R.id.nav_cart) {
                CartFragment fragment = new CartFragment();
                fragment.setOnCheckoutClickListener(this);
                loadFragment(fragment);
                updateToolbarTitle(getString(R.string.cart));
                return true;
            } else if (itemId == R.id.nav_orders) {
                loadFragment(new OrderListFragment());
                updateToolbarTitle(getString(R.string.orders));
                return true;
            } else if (itemId == R.id.nav_logout) {
                logoutUser();
                return true;
            } else {
                return false;
            }
        });
    }

    private void logoutUser() {
        String currentUserId = FirebaseAuthUtils.getCurrentUserId();
        if (currentUserId != null) {
            Log.i(TAG, "User has been signed out. currentUserId= " + currentUserId);
        } else {
            Log.w(TAG, "No user is currently signed in.");
        }
        FirebaseAuth.getInstance().signOut(); // Firebase sign out

        // 跳转到 LoginActivity
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}