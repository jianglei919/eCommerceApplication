package com.conestoga.ecommerceapplication.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseAuthUtils {

    static FirebaseUser CURRENT_USER = FirebaseAuth.getInstance().getCurrentUser();

    public static String getCurrentUserId() {
        if (CURRENT_USER != null) {
            return CURRENT_USER.getUid();
        }
        return null;
    }

    public static String getCurrentUserLoginEmail() {
        if (CURRENT_USER != null) {
            return CURRENT_USER.getEmail();
        }
        return null;
    }
}
