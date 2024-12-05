package com.conestoga.ecommerceapplication.utils;

public class ValidateUtils {

    public static final String POSTAL_CODE_REGEX = "^[A-Za-z]\\d[A-Za-z][ -]?\\d[A-Za-z]\\d$";

    public static final String CARD_NUMBER_REGEX = "^\\d{13,19}$";

    public static boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        if (password.length() < 8) {
            return false;
        }

        if (!password.matches(".*\\d.*")) {
            return false;
        }

        if (!password.matches(".*[A-Z].*")) {
            return false;
        }

        if (!password.matches(".*[a-z].*")) {
            return false;
        }

        if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            return false;
        }

        return true;
    }

    public static boolean isValidPostalCode(String postalCode) {
        return postalCode.matches(POSTAL_CODE_REGEX);
    }

    public static boolean isValidCardNumber(String cardNumber) {
        return cardNumber.matches(CARD_NUMBER_REGEX);
    }

    public static boolean isNumeric(String number) {
        if (number == null || number.isEmpty()) {
            return false;
        }
        for (char c : number.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }
}
