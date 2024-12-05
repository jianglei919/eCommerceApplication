package com.conestoga.ecommerceapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ThankYouActivity extends AppCompatActivity {

    private Button backToProductsButton;
    private Button viewOrdersButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you);

        backToProductsButton = findViewById(R.id.backToProductsButton);
        viewOrdersButton = findViewById(R.id.viewOrdersButton);

        // 返回到产品页面
        backToProductsButton.setOnClickListener(v -> {
            Intent intent = new Intent(ThankYouActivity.this, ProductActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // 查看我的订单
        viewOrdersButton.setOnClickListener(v -> {
            Intent intent = new Intent(ThankYouActivity.this, OrderListActivity.class);
            startActivity(intent);
            finish();
        });
    }
}