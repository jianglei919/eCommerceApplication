package com.conestoga.ecommerceapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.conestoga.ecommerceapplication.enums.CollectionName;
import com.conestoga.ecommerceapplication.enums.OrderStatusType;
import com.conestoga.ecommerceapplication.manager.CartManager;
import com.conestoga.ecommerceapplication.model.CartItem;
import com.conestoga.ecommerceapplication.model.Order;
import com.conestoga.ecommerceapplication.model.PaymentInfo;
import com.conestoga.ecommerceapplication.utils.FirebaseAuthUtils;
import com.conestoga.ecommerceapplication.utils.ValidateUtils;
import com.google.android.gms.common.util.NumberUtils;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class CheckoutActivity extends AppCompatActivity {

    private final static String TAG = "CheckoutActivity";

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText addressEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private EditText unitNumberEditText;
    private EditText cityEditText;
    private EditText stateEditText;
    private EditText postalCodeEditText;
    private EditText cardNumberEditText;
    private EditText cvvEditText;
    private Spinner paymentOptionsSpinner;
    private Button submitOrderButton;

    private List<CartItem> cartItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        firstNameEditText = findViewById(R.id.firstName);
        lastNameEditText = findViewById(R.id.lastName);
        addressEditText = findViewById(R.id.address);
        unitNumberEditText = findViewById(R.id.unitNumber);
        cityEditText = findViewById(R.id.city);
        stateEditText = findViewById(R.id.state);
        postalCodeEditText = findViewById(R.id.postalCode);
        phoneEditText = findViewById(R.id.phone);
        emailEditText = findViewById(R.id.email);
        cardNumberEditText = findViewById(R.id.cardNumber);
        cvvEditText = findViewById(R.id.cvv);
        paymentOptionsSpinner = findViewById(R.id.paymentOptions);
        submitOrderButton = findViewById(R.id.submitOrderButton);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.payment_methods, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paymentOptionsSpinner.setAdapter(adapter);

        cartItemList = (List<CartItem>) getIntent().getSerializableExtra("cartItems");

        submitOrderButton.setOnClickListener(v -> {
            if (validateFields()) {
                processOrder();
            }
        });
    }

    private boolean validateFields() {
        if (TextUtils.isEmpty(firstNameEditText.getText())) {
            firstNameEditText.setError("First name is required");
            return false;
        }
        if (TextUtils.isEmpty(lastNameEditText.getText())) {
            lastNameEditText.setError("Last name is required");
            return false;
        }
        if (TextUtils.isEmpty(addressEditText.getText())) {
            addressEditText.setError("Address is required");
            return false;
        }
        if (TextUtils.isEmpty(cityEditText.getText())) {
            cityEditText.setError("City is required");
            return false;
        }
        if (TextUtils.isEmpty(stateEditText.getText())) {
            stateEditText.setError("State is required");
            return false;
        }
        String postalCode = postalCodeEditText.getText().toString().trim();
        if (TextUtils.isEmpty(postalCode)) {
            postalCodeEditText.setError("Postal code is required");
            return false;
        }
        if (!ValidateUtils.isValidPostalCode(postalCode)) {
            postalCodeEditText.setError("Invalid postal code. Please enter a valid postal code.");
            return false;
        }
        if (TextUtils.isEmpty(phoneEditText.getText())) {
            phoneEditText.setError("Phone number is required");
            return false;
        }
        String email = emailEditText.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required");
            return false;
        }
        if (!isValidEmail(email)) {
            emailEditText.setError("Invalid email address. Please enter a valid email.");
            return false;
        }
        String cardNumber = cardNumberEditText.getText().toString().trim();
        if (TextUtils.isEmpty(cardNumber)) {
            cardNumberEditText.setError("Card number is required");
            return false;
        }
        if (!ValidateUtils.isValidCardNumber(cardNumber)) {
            cardNumberEditText.setError("Invalid card number. Please enter 13-19 digit card number.");
            return false;
        }

        String cvv = cvvEditText.getText().toString();
        if (TextUtils.isEmpty(cvv)) {
            cvvEditText.setError("CVV is required");
            return false;
        }
        if (!ValidateUtils.isNumeric(cvv)) {
            cvvEditText.setError("CVV must be 3 digit numbers");
            return false;
        }
        return true;
    }

    private void processOrder() {
        String userId = FirebaseAuthUtils.getCurrentUserId();
        String loginEmail = FirebaseAuthUtils.getCurrentUserLoginEmail();

        if (TextUtils.isEmpty(userId) && TextUtils.isEmpty(loginEmail)) {
            Toast.makeText(this, "Failed to get current user. Try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        PaymentInfo paymentInfo = new PaymentInfo(firstNameEditText.getText().toString().trim(),
                lastNameEditText.getText().toString().trim(),
                addressEditText.getText().toString().trim(),
                unitNumberEditText.getText().toString().trim(),
                cityEditText.getText().toString().trim(),
                stateEditText.getText().toString().trim(),
                postalCodeEditText.getText().toString().trim(),
                phoneEditText.getText().toString().trim(),
                emailEditText.getText().toString().trim(),
                paymentOptionsSpinner.getSelectedItem().toString(),
                cardNumberEditText.getText().toString().trim(),
                cvvEditText.getText().toString().trim());


        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference(CollectionName.ORDERS.getName());

        String orderId = ordersRef.push().getKey();
        if (orderId != null) {
            Order order = new Order();
            order.setOrderId(orderId);
            order.setLoginEmail(loginEmail);
            order.setOrderTime(System.currentTimeMillis());
            order.setStatus(OrderStatusType.PENDING.getType());
            order.setTotalPrice(CartManager.getInstance().getTotalPrice());

            cartItemList = CartManager.getInstance().getCartItems();

            order.setPaymentInfo(paymentInfo);
            order.setProductItemList(cartItemList);

            String currentUserId = FirebaseAuthUtils.getCurrentUserId();
            if (!TextUtils.isEmpty(currentUserId)) {
                ordersRef.child(currentUserId).child(orderId).setValue(order).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT).show();

                        // Clear the cart and navigate to ThankYouActivity
                        CartManager.getInstance().clearCart();

                        DatabaseReference cartItemRef = FirebaseDatabase.getInstance().getReference(CollectionName.CART_ITEMS.getName());
                        cartItemRef.child(currentUserId).removeValue().addOnCompleteListener(task1 -> {
                            if (task.isSuccessful()) {
                                Log.i(TAG, "Successful to clear all cart item. currentUserId=" + currentUserId);
                            } else {
                                Log.i(TAG, "Failed to clear all cart item. currentUserId=" + currentUserId);
                            }
                        });

                        Intent intent = new Intent(CheckoutActivity.this, ThankYouActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to place order. Try again.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Log.w(TAG, "Failed to get current user id. currentId=" + currentUserId);
            }
        }
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}