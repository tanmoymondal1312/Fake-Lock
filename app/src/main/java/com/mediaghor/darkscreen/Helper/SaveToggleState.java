package com.mediaghor.darkscreen.Helper;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;

/**
 * Helper class for saving and retrieving toggle state using SharedPreferences.
 * Uses proper type handling for boolean values.
 */
public class SaveToggleState {
    private static final String PREF_NAME = "ToggleStatePrefs";
    private static final String KEY_TOGGLE_STATE = "toggle_state";

    // Private constructor to prevent instantiation
    private SaveToggleState() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Saves the toggle state to SharedPreferences.
     *
     * @param context the application context
     * @param state the boolean state to save
     */
    public static void saveToggleState(@NonNull Context context, boolean state) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_TOGGLE_STATE, state);
        editor.apply();
    }

    /**
     * Retrieves the saved toggle state from SharedPreferences.
     *
     * @param context the application context
     * @return the saved toggle state, or false if not found
     */
    public static boolean getToggleState(@NonNull Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_TOGGLE_STATE, false);
    }

    /**
     * Clears the saved toggle state from SharedPreferences.
     *
     * @param context the application context
     */
    public static void clearToggleState(@NonNull Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(KEY_TOGGLE_STATE);
        editor.apply();
    }
}