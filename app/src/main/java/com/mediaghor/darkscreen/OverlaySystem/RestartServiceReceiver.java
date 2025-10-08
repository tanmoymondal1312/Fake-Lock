package com.mediaghor.darkscreen.OverlaySystem;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RestartServiceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, FloatingIconService.class));
    }
}