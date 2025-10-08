package com.mediaghor.darkscreen;



import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;
import com.mediaghor.darkscreen.Helper.SaveTheme;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        boolean isDark = SaveTheme.getThemeState(this);
        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
