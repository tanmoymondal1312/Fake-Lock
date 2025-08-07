package com.mediaghor.fakelock.Permissions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

public class OverlayPermissionUtils {

    public static final int REQUEST_CODE_OVERLAY = 1002;

    // Call this method from your Activity or Fragment
    public static void requestOverlayPermission(Activity activity) {
        if (Settings.canDrawOverlays(activity)) {
            // Permission already granted
            onOverlayPermissionGranted(activity);
            return;
        }

        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            Uri uri = Uri.parse("package:" + activity.getPackageName());
            intent.setData(uri);

            // For Android 11+ (API 30+) there's a better way (auto-focus on the toggle)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }

            activity.startActivityForResult(intent, REQUEST_CODE_OVERLAY);
        } else {
            // Pre-Marshmallow: No need to request
            onOverlayPermissionGranted(activity);
        }
    }

    public static void onOverlayPermissionGranted(Context context) {
        Toast.makeText(context, "Overlay permission granted!", Toast.LENGTH_SHORT).show();
        // Continue your feature here
    }

    // Optional: Handle the result from onActivityResult
    public static void handleOverlayPermissionResult(Activity activity) {
        if (Settings.canDrawOverlays(activity)) {
            onOverlayPermissionGranted(activity);
        } else {
            Toast.makeText(activity, "Please enable 'Display over other apps' permission", Toast.LENGTH_LONG).show();
        }
    }
}
