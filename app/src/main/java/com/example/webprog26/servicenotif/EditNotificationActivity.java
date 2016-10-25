package com.example.webprog26.servicenotif;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.webprog26.servicenotif.services.NotificationService;

public class EditNotificationActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText mEtMessageToService;
    private NotificationService mNotificationService;
    private boolean isServiceBound = false; //indicates bound state with NotificationService

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, NotificationService.class), mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_notification);

        mEtMessageToService = (EditText) findViewById(R.id.etMessageToService);
        Button btnSendMessageToService = (Button) findViewById(R.id.btnSendMessageToService);
        btnSendMessageToService.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btnSendMessageToService:
                if(!isMessageExists())
                {
                    makeToast(getResources().getString(R.string.message_is_empty_warning), Toast.LENGTH_SHORT);
                    return;
                }

                if(isServiceBound)
                {
                    mNotificationService.setNewNotificationMessage(mEtMessageToService.getText().toString());
                    makeToast(getResources().getString(R.string.message_sent_successfully), Toast.LENGTH_SHORT);
                    mEtMessageToService.setText("");
                }

                break;
        }
    }

    /**
     *
     * @return boolean user message has at least one symbol
     */
    private boolean isMessageExists()
    {
        return mEtMessageToService.getText().toString().length() > 0;
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            NotificationService.LocalBinder binder = (NotificationService.LocalBinder) iBinder;
            mNotificationService = binder.getService();
            mNotificationService.resetUnread();
            isServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isServiceBound = false;
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        if(isServiceBound)
        {
            unbindService(mServiceConnection);
            isServiceBound = false;
        }
    }

    /**
     * Makes Toast message with received message text and visibility time
     * @param message
     * @param toastLength
     */
    private void makeToast(String message, int toastLength)
    {
        Toast.makeText(this, message, toastLength).show();
    }
}
