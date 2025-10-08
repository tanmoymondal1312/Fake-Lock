package com.mediaghor.darkscreen.Helper;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

public class SaveTheme {

    private static final String PREF_NAME = "Theme";
    private static final String THEME_KEY = "SELECTED_THEME";

    // Save theme state (true = Dark, false = Light)
    public static void saveThemeState(@NonNull Context context, boolean isDark) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(THEME_KEY, isDark);
        editor.apply();
    }

    // Fetch saved theme state (default = Light)
    public static boolean getThemeState(@NonNull Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(THEME_KEY, false);
    }
}
