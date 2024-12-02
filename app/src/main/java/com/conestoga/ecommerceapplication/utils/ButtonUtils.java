package com.conestoga.ecommerceapplication.utils;

import android.widget.Button;

import com.conestoga.ecommerceapplication.R;

public class ButtonUtils {

    public static void disabledButton(Button button) {
        button.setEnabled(false);
        button.setBackgroundResource(R.drawable.button_disabled);
    }

    public static void displayButton(Button button) {
        button.setEnabled(true);
        button.setBackgroundResource(R.drawable.rounded_button);
    }
}
