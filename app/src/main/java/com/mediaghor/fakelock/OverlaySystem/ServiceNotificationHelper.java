package com.mediaghor.fakelock.OverlaySystem;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.mediaghor.fakelock.R;

public class ServiceNotificationHelper {
    private static final String CHANNEL_ID = "floating_icon_channel";
    private static final int NOTIFICATION_ID = 101;

    public static Notification createNotification(Context context) {
        createNotificationChannel(context);

        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Click On Toggle")
                .setContentText("Click the toggle and see a fake lock screen")
                .setSmallIcon(R.drawable.lock) // Your icon here
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setOngoing(true)
                .setAutoCancel(false)
                .build();
    }

    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Floating Icon Channel",
                    NotificationManager.IMPORTANCE_MIN);
            channel.setDescription("Channel for floating icon service");

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    public static int getNotificationId() {
        return NOTIFICATION_ID;
    }
}