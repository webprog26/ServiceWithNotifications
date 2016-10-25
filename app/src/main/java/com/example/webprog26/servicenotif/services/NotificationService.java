package com.example.webprog26.servicenotif.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.webprog26.servicenotif.MainActivity;

/**
 * Created by webprog26 on 25.10.2016.
 */

public class NotificationService extends Service {

    private static final String TAG = "NotificationService_TAG";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand()");
        Log.i(TAG, "Received intent: " + intent.getStringExtra(MainActivity.DEFAULT_NOTIFICATION_MESSAGE));
        return START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
    }
}
