package com.example.android.booklisting;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

public final class Utils {
    // Return a pixel int value depending of the dp value given in parameter
    public static int getPixelValue(Context context, int dimenId) {
        Resources resources = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dimenId,
                resources.getDisplayMetrics()
        );
    }

    // Shrink a string if length > 30 char
    public static String shrinkString(String string, int nbChar) {
        if (string.length() > nbChar) {
            String shrinkedString = string.substring(0, Math.min(string.length(), nbChar));
            shrinkedString = shrinkedString.trim();
            shrinkedString += "...";
            return shrinkedString;
        }
        else return string;
    }

    // Split a date from YYYY-MM-DD to YYYY
    public static String getYear(String date) {
        return date.split("-")[0];
    }
}
