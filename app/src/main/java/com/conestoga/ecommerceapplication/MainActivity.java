package com.conestoga.ecommerceapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private boolean isNavigated = false; // 标志变量，防止重复跳转

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView logo = findViewById(R.id.logo);

        new Handler().postDelayed(this::navigateToLogin, 3000);

        logo.setOnClickListener(v -> navigateToLogin());
    }

    private void navigateToLogin() {
        if (!isNavigated) {
            isNavigated = true;
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}