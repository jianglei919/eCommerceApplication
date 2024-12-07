package com.conestoga.ecommerceapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.conestoga.ecommerceapplication.utils.FirebaseAuthUtils;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private final static String TAG = "LoginActivity";

    private EditText emailField;
    private EditText passwordField;
    private Button btnLogin;
    private TextView signUpPrompt;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(v -> loginUser());

        signUpPrompt = findViewById(R.id.signUpPrompt);
        signUpPrompt.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        // 获取从注册页面传递过来的邮箱
        String email = getIntent().getStringExtra("email");
        if (!TextUtils.isEmpty(email)) {
            emailField.setText(email);
        }
    }

    private void loginUser() {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.i(TAG, "Login successful! currentUserId=" + FirebaseAuthUtils.getCurrentUserId());
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.e(TAG, "Login failed: " + Objects.requireNonNull(task.getException()).getMessage());
                        Toast.makeText(this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}