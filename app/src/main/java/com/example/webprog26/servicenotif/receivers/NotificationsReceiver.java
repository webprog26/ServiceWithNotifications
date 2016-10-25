package com.example.webprog26.servicenotif.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.webprog26.servicenotif.MainActivity;
import com.example.webprog26.servicenotif.R;
import com.example.webprog26.servicenotif.services.NotificationService;

public class NotificationsReceiver extends BroadcastReceiver {
    public NotificationsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent notificationsIntent = new Intent(context, NotificationService.class);
        notificationsIntent.putExtra(MainActivity.DEFAULT_NOTIFICATION_MESSAGE, context.getResources().getString(R.string.default_message));
        context.startService(notificationsIntent);
    }
}
