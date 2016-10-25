package com.example.webprog26.servicenotif.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.example.webprog26.servicenotif.EditNotificationActivity;
import com.example.webprog26.servicenotif.MainActivity;
import com.example.webprog26.servicenotif.R;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by webprog26 on 25.10.2016.
 */

public class NotificationService extends Service {

    private static final String TAG = "NotificationService_TAG";

    private static final String GROUP_TEA = "group_tea"; //Notifications group title
    private static final int NOTIFICATION_ID = 101; //Notification id. Always the same to keep only one notification at the time

    private ArrayList<String> mNotificationsTitlesList;//ArrayList to store messages
    private NotificationsThread mNotificationsThread;//Separate Thread to send notifications
    private boolean flagIsThreadRunning = false;//indicates Servise stopped so the Thread should be stopped too
    private final IBinder mIBinder = new LocalBinder();//Redefined IBinder to bind with NotificationService from EditNotificationActivity
    private boolean hasUnread = false; //indicates unread notifications

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationsTitlesList = new ArrayList<>();
        Log.i(TAG, "onCreate()");
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher);
        Notification notification;
        if(Build.VERSION.SDK_INT < 16)
        {
            notification = builder.getNotification();
        } else {
            notification = builder.build();
        }
        startForeground(777, notification);

        Intent hideIntent = new Intent(this, HideNotificationService.class);
        startService(hideIntent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand()");

        String message;
        long notificationPauseInterval = MainActivity.NOTIFICATION_PAUSE_INTERVAL;
        if(intent == null)
        {
            message = getResources().getString(R.string.default_message);
        } else {
            message = intent.getStringExtra(MainActivity.DEFAULT_NOTIFICATION_MESSAGE);
        }
        Log.i(TAG, "Received intent: " + message);
        flagIsThreadRunning = true;
        mNotificationsThread = new NotificationsThread(notificationPauseInterval, message);
        mNotificationsThread.start();
        return START_REDELIVER_INTENT;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mIBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
        if(mNotificationsThread != null)
        {
            flagIsThreadRunning = false;
            mNotificationsThread.interrupt();
            mNotificationsThread = null;
        }
    }

    /**
     * Sends notification with given message
     * @param message
     */
    private void sendNotification(String message)
    {
        mNotificationsTitlesList.add(message);

        Intent resultIntent = new Intent(this, EditNotificationActivity.class);

        String warningString = getResources().getString(R.string.stop_this_ugliness);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(EditNotificationActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setTicker(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getResources().getString(R.string.new_notifications, mNotificationsTitlesList.size()))
                .setContentIntent(resultPendingIntent)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setGroup(GROUP_TEA)
                .setGroupSummary(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true);


        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        if(mNotificationsTitlesList.size() > 1) {
            hasUnread = true;
            for (int i = (mNotificationsTitlesList.size() - 1); i >= 0; i--) {
                inboxStyle.addLine(mNotificationsTitlesList.get(i));
            }
        }

        if(hasUnread)
        {
            builder.setContentText(warningString);
            inboxStyle.setBigContentTitle(warningString);
        } else {
            builder.setContentText(message);
        }


        builder.setNumber(mNotificationsTitlesList.size());


        builder.setStyle(inboxStyle);


        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, builder.build());
    }

    /**
     * Calls NotificationThread's setMessage with received String as a parameter
     * @param newNotificationMessage
     */
   public void setNewNotificationMessage(String newNotificationMessage)
   {
       mNotificationsThread.setMessage(newNotificationMessage);
   }

   private class NotificationsThread extends Thread{

        private long timePause;
        private String message;

        public NotificationsThread(long timePause, String message) {
            this.timePause = timePause;
            this.message = message;
        }

        @Override
        public void run() {
            super.run();
            while(flagIsThreadRunning)
            {
                try{
                    TimeUnit.SECONDS.sleep(timePause);
                    Log.i(TAG, "message is " + message);
                    sendNotification(message);
                } catch (InterruptedException ie){
                    ie.printStackTrace();
                }
            }
        }

       /**
        * Sets received message as notification message
        * @param message
        */
       public void setMessage(String message)
       {
           this.message = message;
       }
    }

    public class LocalBinder extends Binder {

        /**
         * Makes NotificationService makes accessible in binding
         * @return NotificationService
         */
        public NotificationService getService()
        {
            return NotificationService.this;
        }
    }

    /**
     * Resets hasUnread variable to false if bound with activity
     */
    public void resetUnread()
    {
        this.hasUnread = false;
        mNotificationsTitlesList.clear();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.i(TAG, "onTaskRemoved()");
    }
}
