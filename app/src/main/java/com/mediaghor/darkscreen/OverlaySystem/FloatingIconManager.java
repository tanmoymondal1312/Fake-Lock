package com.mediaghor.darkscreen.OverlaySystem;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

public class FloatingIconManager {
    private static final String TAG = "FloatingIconManager";
    private static FloatingIconManager instance;
    private final Context context;
    private boolean isRunning = false;

    private FloatingIconManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public static synchronized FloatingIconManager getInstance(Context context) {
        if (instance == null) {
            instance = new FloatingIconManager(context);
        }
        return instance;
    }

    public void startFloatingIcon() {
        if (isRunning) {
            Log.d(TAG, "Service is already running");
            return;
        }

        try {
            setupServiceChecker(context);
            FloatingIconService.startService(context);
            isRunning = true;
            Log.d(TAG, "Floating icon service started");
        } catch (Exception e) {
            Log.e(TAG, "Error starting floating icon service", e);
            isRunning = false;
        }
    }

    public void stopFloatingIcon() {
        if (!isRunning) {
            Log.d(TAG, "Service is not running");
            return;
        }

        try {
            FloatingIconService.stopService(context);
            isRunning = false;
            Log.d(TAG, "Floating icon service stopped");
        } catch (Exception e) {
            Log.e(TAG, "Error stopping floating icon service", e);
        }
    }

    private void setupServiceChecker(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, RestartServiceReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            alarmManager.setInexactRepeating(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                    AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                    pendingIntent);
        }
    }

    public boolean isIconRunning() {
        return isRunning;
    }
}