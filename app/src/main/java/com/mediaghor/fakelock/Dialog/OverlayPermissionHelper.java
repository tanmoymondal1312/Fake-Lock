package com.mediaghor.fakelock.Dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

public class OverlayPermissionHelper {
    public static final int REQUEST_CODE_OVERLAY = 1002;
    private static PermissionCallback callback;

    public interface PermissionCallback {
        void onPermissionGranted();
        void onPermissionDenied();
    }

    public static boolean hasPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context);
        }
        return true;
    }

    public static void requestPermission(Activity activity, PermissionCallback permissionCallback) {
        callback = permissionCallback;

        if (hasPermission(activity)) {
            permissionCallback.onPermissionGranted();
            return;
        }

        showRationaleDialog(activity, () -> {
            try {
                // Try the most direct approach first
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + activity.getPackageName()));

                // Some devices need this extra flag to go directly to the toggle screen
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    intent.putExtra("android.provider.extra.APP_PACKAGE", activity.getPackageName());
                }

                activity.startActivityForResult(intent, REQUEST_CODE_OVERLAY);
            } catch (Exception e) {
                // Fallback for devices that don't support the direct intent
                openAppSettings(activity);
            }
        });
    }

    private static void openAppSettings(Activity activity) {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
            activity.startActivityForResult(intent, REQUEST_CODE_OVERLAY);
        } catch (Exception e) {
            Toast.makeText(activity, "Please enable Display over other apps in settings", Toast.LENGTH_LONG).show();
            if (callback != null) {
                callback.onPermissionDenied();
            }
        }
    }

    private static void showRationaleDialog(Activity activity, Runnable onContinue) {
        new AlertDialog.Builder(activity)
                .setTitle("Permission Needed")
                .setMessage("This feature needs to display over other apps. Please enable the permission and return to the app.")
                .setPositiveButton("Open Settings", (d, w) -> onContinue.run())
                .setNegativeButton("Cancel", (d, w) -> {
                    if (callback != null) callback.onPermissionDenied();
                })
                .setCancelable(false)
                .show();
    }

    public static void handleActivityResult(int requestCode, Activity activity) {
        if (requestCode == REQUEST_CODE_OVERLAY && callback != null) {
            if (hasPermission(activity)) {

                callback.onPermissionGranted();
            } else {
                callback.onPermissionDenied();
            }
        }
    }
}