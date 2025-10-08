package com.mediaghor.darkscreen.Activities;

import static android.view.View.VISIBLE;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.mediaghor.darkscreen.Dialog.ApSettingsDialog;
import com.mediaghor.darkscreen.Helper.SaveTheme;
import com.mediaghor.darkscreen.R;
import com.vimalcvs.switchdn.DayNightSwitch;
import com.vimalcvs.switchdn.DayNightSwitchListener;

public class ThemeSettingsActivity extends AppCompatActivity {

    DayNightSwitch dayNightSwitch;
    AppCompatImageButton backFromToolbar;
    AppCompatImageView settings;
    ApSettingsDialog apSettingsDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Apply theme before setting content view
        applyTheme();

        setContentView(R.layout.activity_theme_settings);
        initialization();
        setupWindowInsets();
        setupStatusBar();
        setupDayNightSwitch();
    }

    private void initialization() {
        backFromToolbar = findViewById(R.id.back_from_toolbar);
        settings = findViewById(R.id.btn_settings);

        backFromToolbar.setVisibility(VISIBLE);
        backFromToolbar.setEnabled(true);
        backFromToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // To show the dialog
                apSettingsDialog = new ApSettingsDialog(ThemeSettingsActivity.this,"THEME_SETTINGS");


                apSettingsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        rotateSettingsIcon(true); // rotation animation on dismiss
                    }
                });

                apSettingsDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        rotateSettingsIcon(false); // rotation animation on show
                    }
                });

                apSettingsDialog.show();
            }
        });
    }

    private void applyTheme() {
        boolean isDark = SaveTheme.getThemeState(this);
        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();

            // Set status bar color
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.statusBarColor));

            // Set status bar icon color based on theme
            boolean isDark = SaveTheme.getThemeState(this);
            setStatusBarIconColor(isDark);
        }
    }

    private void setupDayNightSwitch() {
        dayNightSwitch = findViewById(R.id.dayNightSwitchBtn);
        boolean isDark = SaveTheme.getThemeState(this);
        dayNightSwitch.setIsNight(isDark);

        dayNightSwitch.setListener(new DayNightSwitchListener() {
            @Override
            public void onSwitch(boolean is_night) {
                // Save user choice
                SaveTheme.saveThemeState(ThemeSettingsActivity.this, is_night);

                // Apply theme immediately
                if (is_night) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }

                // Update status bar icons before recreating
                setStatusBarIconColor(is_night);

                recreate(); // refresh activity to apply changes
            }
        });
    }

    private void setStatusBarIconColor(boolean isDark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = getWindow().getDecorView();
            int flags = decorView.getSystemUiVisibility();

            if (isDark) {
                // DARK THEME: We want LIGHT icons (white)
                flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR; // Clear light flag
            } else {
                // LIGHT THEME: We want DARK icons (black)
                flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR; // Set light flag
            }

            decorView.setSystemUiVisibility(flags);
        }
    }
    public void rotateSettingsIcon(boolean rotateRight) {
        // Get the current rotation
        float currentRotation = settings.getRotation();

        // Determine the target rotation
        float targetRotation;
        if (rotateRight) {
            targetRotation = currentRotation + 90f; // Rotate 90 degrees clockwise
        } else {
            targetRotation = currentRotation - 90f; // Rotate 90 degrees counterclockwise
        }

        // Animate the rotation
        settings.animate()
                .rotation(targetRotation)
                .setDuration(300) // Animation duration in milliseconds
                .start();
    }
}