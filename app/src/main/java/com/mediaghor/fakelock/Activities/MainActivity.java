package com.mediaghor.fakelock.Activities;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.kyleduo.switchbutton.SwitchButton;
import com.mediaghor.fakelock.Dialog.OverlayPermissionHelper;
import com.mediaghor.fakelock.Dialog.PermissionDialog;
import com.mediaghor.fakelock.OverlaySystem.FloatingIconManager;
import com.mediaghor.fakelock.Permissions.NotificationPermissionManager;
import com.mediaghor.fakelock.Permissions.PermissionUtils;
import com.mediaghor.fakelock.R;
import com.mediaghor.fakelock.adapter.SliderAdapter;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    LinearLayout layoutDisplayIcon,layoutPermission;
    private SwitchButton fancySwitchForDisplayIcon, fancySwitchForPermissions;
    ViewPager2 viewPager2;
    private PermissionDialog dialog;
    private NotificationPermissionManager permissionManager;
    private static final int MAX_DENIALS_BEFORE_RATIONALE = 2;
    private static final String PERMISSION_PREFS = "PermissionPrefs";
    private static final String NOTIFICATION_DENIAL_COUNT_KEY = "notification_denial_count";
    private FloatingIconManager floatingIconManager;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_ScreenOff);
        setContentView(R.layout.activity_main);

        // Initialize views
        initializeViews();


        setupLayoutSelectorViewpager();

        handleToggle();

        if (!handleToggle()) {
            layoutPermission.postDelayed(() -> {
                long now = SystemClock.uptimeMillis();
                MotionEvent downEvent = MotionEvent.obtain(
                        now, now,
                        MotionEvent.ACTION_DOWN,
                        layoutPermission.getWidth() / 2f,
                        layoutPermission.getHeight() / 2f,
                        0
                );
                MotionEvent upEvent = MotionEvent.obtain(
                        now + 100, now + 100,
                        MotionEvent.ACTION_UP,
                        layoutPermission.getWidth() / 2f,
                        layoutPermission.getHeight() / 2f,
                        0
                );
                layoutPermission.dispatchTouchEvent(downEvent);
                layoutPermission.dispatchTouchEvent(upEvent);
                downEvent.recycle();
                upEvent.recycle();
            }, 1500);  //
        }
    }



    private void initializeViews() {
        layoutPermission = findViewById(R.id.layoutPermission2);
        layoutDisplayIcon = findViewById(R.id.layoutPermission1);
        fancySwitchForDisplayIcon = findViewById(R.id.fancySwitchForDisplayIcon);
        fancySwitchForPermissions = findViewById(R.id.fancySwitchForPermissions);
        floatingIconManager = FloatingIconManager.getInstance(MainActivity.this);
        fancySwitchForDisplayIcon.setChecked(true);




        fancySwitchForPermissions.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    permissionHandler();
                    enableDisableADSB("enable");
                    // Perform action when turned ON
                } else {
                    enableDisableADSB("disable");
                }
            }
        });
        fancySwitchForDisplayIcon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    floatingIconManager.startFloatingIcon();
                    // Perform action when turned ON
                } else {
                    floatingIconManager.stopFloatingIcon();
                }
            }
        });




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.statusBarColor));
        }

        ColorStateList backColorStateList = new ColorStateList(
                new int[][] {
                        new int[] { android.R.attr.state_checked },     // Checked
                        new int[] { -android.R.attr.state_checked }     // Unchecked
                },
                new int[] {
                        ContextCompat.getColor(this, R.color.switch_on),   // Checked color
                        ContextCompat.getColor(this, R.color.switch_off)   // Unchecked color
                }
        );
        fancySwitchForDisplayIcon.setBackColor(backColorStateList);
        fancySwitchForPermissions.setBackColor(backColorStateList);

        ColorStateList thumbColorStateList = new ColorStateList(
                new int[][] {
                        new int[] { android.R.attr.state_checked },  // Checked (ON state)
                        new int[] { -android.R.attr.state_checked }   // Unchecked (OFF state)
                },
                new int[] {
                        ContextCompat.getColor(this, R.color.thumb_on_color),  // Color when ON
                        ContextCompat.getColor(this, R.color.thumb_off_color)   // Color when OFF

                }
        );
        fancySwitchForDisplayIcon.setThumbColor(thumbColorStateList);
        fancySwitchForPermissions.setThumbColor(thumbColorStateList);


    }


    private void setupLayoutSelectorViewpager(){
        viewPager2 = findViewById(R.id.viewPager);

        // Calculate dimensions for showing 3 items (1 full + 2 partial)
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int itemWidth = (int) (screenWidth * 0.60f); // Each item takes 75% of screen width
        int peekWidth = (screenWidth - itemWidth) / 2; // Space for partial items

        // Set padding to show partial items on both sides
        viewPager2.setPadding(peekWidth, 0, peekWidth, 0);
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(2); // Keep 2 pages in memory on each side

        List<Integer> imageList = Arrays.asList(
                R.drawable.lock_sample1,
                R.drawable.lock_sample1,
                R.drawable.lock_sample1,
                R.drawable.lock_sample1
        );

        SliderAdapter adapter = new SliderAdapter(imageList);
        viewPager2.setAdapter(adapter);

        // Create composite transformer for combined effects
        CompositePageTransformer compositeTransformer = new CompositePageTransformer();
        compositeTransformer.addTransformer(new MarginPageTransformer(8)); // Small margin between items
        compositeTransformer.addTransformer(new ViewPager2.PageTransformer() {
            private static final float MIN_SCALE = 0.8f;
            private static final float MIN_ALPHA = 0.5f;

            @Override
            public void transformPage(@NonNull View page, float position) {
                if (Math.abs(position) > 1) {
                    page.setAlpha(0f);
                    return;
                }

                // Scale effect
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position) / 2);
                page.setScaleX(scaleFactor);
                page.setScaleY(scaleFactor);

                // Alpha effect
                float alphaFactor = Math.max(MIN_ALPHA, 1 - Math.abs(position));
                page.setAlpha(alphaFactor);

                // Translation effect to bring items closer
                page.setTranslationX(-position * peekWidth / 2);
            }
        });

        viewPager2.setPageTransformer(compositeTransformer);

    }





    private void permissionHandler() {


        dialog = new PermissionDialog(this, new PermissionDialog.PermissionDialogListener() {
            @Override
            public void onNotificationAllowed() {
                initializePermissionManager();
                permissionManager.checkOrRequestPermission(MainActivity.this);
            }

            @Override
            public void onDisplayAllowed() {
                // Handle display permission if needed
                OverlayPermissionHelper.requestPermission(MainActivity.this, new OverlayPermissionHelper.PermissionCallback() {
                    @Override
                    public void onPermissionGranted() {
                        updateUIForGrantedPermission("display_allowed");
                        // Start your overlay service or functionality
                    }

                    @Override
                    public void onPermissionDenied() {
                        Toast.makeText(MainActivity.this, "Functionality limited without permission", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onDialogClosed() {
                // Optional: Track when user closes dialog without taking action
                trackPermissionDialogClosed();
            }
        });


        dialog.show();
        if (permissionChecker("notification")) {
            updateUIForGrantedPermission("notification_allowed");
        }
        if (permissionChecker("display_over")) {
            updateUIForGrantedPermission("display_allowed");
        }
    }

    private void initializePermissionManager() {
        if (permissionManager == null) {
            permissionManager = new NotificationPermissionManager.Builder(this)
                    .setCallback(new NotificationPermissionManager.PermissionCallback() {
                        @Override
                        public void onPermissionGranted() {
                            resetDenialCount();
                            updateUIForGrantedPermission("notification_allowed");
                        }

                        @Override
                        public void onPermissionDenied(boolean shouldShowRationale) {
                            handlePermissionDenial(shouldShowRationale);
                        }

                        @Override
                        public void onPermissionPermanentlyDenied() {
                            handlePermanentDenial();
                        }
                    })
                    .build();
        }
    }

    private void updateUIForGrantedPermission(String state) {
        runOnUiThread(() -> {
            if (dialog != null) {
                dialog.handleButtonBehaviour(state);
            }
        });
    }

    private void handlePermissionDenial(boolean shouldShowRationale) {
        incrementDenialCount();
        runOnUiThread(() -> {
            if (shouldShowRationale && getDenialCount() >= MAX_DENIALS_BEFORE_RATIONALE) {
                showRationaleDialog();
            } else {
                showToast("Please enable notifications for full functionality");
            }
        });
    }

    private void handlePermanentDenial() {
        runOnUiThread(() -> {
            showSettingsRedirectDialog();
        });
    }

    private void showRationaleDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Notifications Needed")
                .setMessage("We need notification permissions to alert you about important updates. This feature won't work without this permission.")
                .setPositiveButton("Try Again", (d, w) -> permissionManager.checkOrRequestPermission(MainActivity.this))
                .setNegativeButton("Not Now", null)
                .show();
    }

    private void showSettingsRedirectDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permission Required")
                .setMessage("You've permanently denied notifications. Please enable them in app settings.")
                .setPositiveButton("Open Settings", (d, w) -> openAppSettings())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private void incrementDenialCount() {
        SharedPreferences prefs = getSharedPreferences(PERMISSION_PREFS, MODE_PRIVATE);
        int currentCount = prefs.getInt(NOTIFICATION_DENIAL_COUNT_KEY, 0);
        prefs.edit().putInt(NOTIFICATION_DENIAL_COUNT_KEY, currentCount + 1).apply();
    }

    private int getDenialCount() {
        return getSharedPreferences(PERMISSION_PREFS, MODE_PRIVATE)
                .getInt(NOTIFICATION_DENIAL_COUNT_KEY, 0);
    }

    private void resetDenialCount() {
        getSharedPreferences(PERMISSION_PREFS, MODE_PRIVATE)
                .edit()
                .remove(NOTIFICATION_DENIAL_COUNT_KEY)
                .apply();
    }

    public boolean permissionChecker(String permissionName) {
        if ("notification".equals(permissionName)) {
            return PermissionUtils.hasNotificationPermission(this);
        } else if ("display_over".equals(permissionName)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return Settings.canDrawOverlays(this);
            } else {
                // On versions below Android 6.0, the permission is automatically granted
                return true;
            }
        }
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissionManager != null) {
            permissionManager.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        }
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void trackPermissionDialogClosed() {
        // Analytics or tracking if needed
        handleToggle();
        if(handleToggle()){
            enableDisableADSB("enable");
        }


    }


    public boolean handleToggle(){
        if (permissionChecker("notification") && permissionChecker("display_over")) {
            fancySwitchForPermissions.setChecked(true);

            return true;
        }else {
            fancySwitchForPermissions.setChecked(false);
            return false;
        }

    }


    private void LogMessage(String tag, String message){
        Log.d(tag,message);
    }



    private void enableDisableADSB(String state){
        if (state.equals("enable")){
            layoutDisplayIcon.setForeground(null);
            layoutPermission.setForeground(null);
            fancySwitchForDisplayIcon.setEnabled(true);
            fancySwitchForDisplayIcon.setClickable(true);


        } else if (state.equals("disable")) {

            fancySwitchForDisplayIcon.setChecked(false);
            fancySwitchForDisplayIcon.setEnabled(false);
            layoutDisplayIcon.setForeground(
                    new ColorDrawable(ContextCompat.getColor(MainActivity.this, R.color.disable_layout_foreground))
            );
            TypedValue typedValue = new TypedValue();
            getTheme().resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true);
            Drawable selectableDrawable = ContextCompat.getDrawable(this, typedValue.resourceId);
            layoutPermission.setForeground(selectableDrawable);



        }else {
            showToast("Undefined State 'enableDisableADSB() ");
        }
    }



    // In onActivityResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogMessage("dialog debug","Activity Result Called");
        if(requestCode == 1002){
            runOnUiThread(() -> {
                LogMessage("dialog debug","Request Code 100");

                updateUIForGrantedPermission("display_allowed");

                if(dialog== null){
                    LogMessage("dialog debug","Dialog Null");

                    permissionHandler();
                }
            });
        }
    }
}


















