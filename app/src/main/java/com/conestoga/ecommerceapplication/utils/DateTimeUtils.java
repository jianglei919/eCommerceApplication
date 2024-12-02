package com.conestoga.ecommerceapplication.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtils {

    public static final String FORMAT_YYYY_MM_DD = "yyyy-MM-dd HH:mm:ss";

    public static String formatTimestamp(long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_YYYY_MM_DD, Locale.getDefault());
        return sdf.format(date);
    }
}
