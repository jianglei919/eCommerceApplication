package com.conestoga.ecommerceapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private Handler handler = new Handler(); // 避免重复创建Handler
    private Runnable navigationTask; // 跳转任务

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView logo = findViewById(R.id.logo);

        // 初始化跳转任务
        navigationTask = this::navigateToNextScreen;

        // 延迟跳转
        handler.postDelayed(navigationTask, 3000);

        // 点击Logo立即跳转
        logo.setOnClickListener(v -> {
            handler.removeCallbacks(navigationTask); // 移除延迟任务
            navigateToNextScreen();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 确保在Activity销毁时移除Handler的任务，避免内存泄漏
        handler.removeCallbacks(navigationTask);
    }

    private void navigateToNextScreen() {
        // 检查用户登录状态
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Log.i("MainActivity", "User already logged in, navigating to HomeActivity.");
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
        } else {
            Log.i("MainActivity", "No user logged in, navigating to LoginActivity.");
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
        finish(); // 确保MainActivity不会保留在返回栈中
    }
}