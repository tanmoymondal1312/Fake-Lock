package com.mediaghor.fakelock.Permissions;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
public class NotificationPermissionManager {
    public interface PermissionCallback {
        void onPermissionGranted();
        void onPermissionDenied(boolean shouldShowRationale);
        void onPermissionPermanentlyDenied();
    }

    private final Context context;
    private final PermissionCallback callback;
    private static final String PREFS_NAME = "NotificationPermissionPrefs";
    private static final String KEY_REJECT_COUNT = "reject_count";
    private static final int MAX_REJECTS_BEFORE_RATIONALE = 2;

    private NotificationPermissionManager(Builder builder) {
        this.context = builder.context;
        this.callback = builder.callback;
    }

    public static class Builder {
        private final Context context;
        private PermissionCallback callback;

        public Builder(Context context) {
            this.context = context.getApplicationContext();
        }

        public Builder setCallback(PermissionCallback callback) {
            this.callback = callback;
            return this;
        }

        public NotificationPermissionManager build() {
            if (callback == null) {
                throw new IllegalStateException("PermissionCallback must be set");
            }
            return new NotificationPermissionManager(this);
        }
    }

    public void checkOrRequestPermission(Activity activity) {
        if (PermissionUtils.hasNotificationPermission(activity)) {
            callback.onPermissionGranted();
        } else {
            requestPermission(activity);
        }
    }

    public void onRequestPermissionsResult(Activity activity, int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionUtils.NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (PermissionUtils.isPermissionGranted(grantResults)) {
                handlePermissionGranted();
            } else {
                handlePermissionDenied(activity);
            }
        }
    }

    private void handlePermissionGranted() {
        resetRejectCount();
        callback.onPermissionGranted();
    }

    private void handlePermissionDenied(Activity activity) {
        incrementRejectCount();

        if (!PermissionUtils.shouldShowRequestPermissionRationale(activity)) {
            callback.onPermissionPermanentlyDenied();
        } else {
            boolean shouldShowRationale = getRejectCount() >= MAX_REJECTS_BEFORE_RATIONALE;
            callback.onPermissionDenied(shouldShowRationale);
        }
    }

    private void requestPermission(Activity activity) {
        PermissionUtils.requestNotificationPermission(activity);
    }

    private void incrementRejectCount() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int count = prefs.getInt(KEY_REJECT_COUNT, 0);
        prefs.edit().putInt(KEY_REJECT_COUNT, count + 1).apply();
    }

    private int getRejectCount() {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getInt(KEY_REJECT_COUNT, 0);
    }

    private void resetRejectCount() {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .remove(KEY_REJECT_COUNT)
                .apply();
    }
}