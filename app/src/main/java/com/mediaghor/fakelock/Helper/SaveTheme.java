package com.mediaghor.fakelock.Helper;



import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

public class SaveTheme {

    private static final String PREF_NAME = "Theme";
    private static final String THEME_NAME = "SELECTED_THEME";

    // Save layout ID
    public static void saveThemeState(@NonNull Context context, boolean state) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(THEME_NAME, state);
        editor.apply();
    }

    // Fetch saved layout ID
    public static int getTheme(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(THEME_NAME, -1);
        // returns -1 if nothing is saved yet
    }
}
