package com.mediaghor.fakelock.Helper;


import android.content.Context;
import android.content.SharedPreferences;

public class SaveMyLayout {

    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_LAYOUT_ID = "SELECTED_LAYOUT_ID";

    // Save layout ID
    public static void saveSelectedLayoutId(Context context, int layoutId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_LAYOUT_ID, layoutId);
        editor.apply(); // async (better than commit)
    }

    // Fetch saved layout ID
    public static int getSavedLayoutId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_LAYOUT_ID, -1);
        // returns -1 if nothing is saved yet
    }
}
