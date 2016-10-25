package com.example.webprog26.servicenotif.services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.webprog26.servicenotif.R;

/**
 * Created by webprog26 on 25.10.2016.
 */

public class HideNotificationService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher);
        Notification notification;
        if (Build.VERSION.SDK_INT < 16)
        {
            notification = builder.getNotification();
        } else {
            notification = builder.build();
        }
        startForeground(777, notification);
        stopForeground(true);
        }
}
