package com.example.webprog26.servicenotif;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.webprog26.servicenotif.services.NotificationService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "MainActivity_TAG";

    public static final String DEFAULT_NOTIFICATION_MESSAGE = "com.example.webprog26.servicenotif.default_notification_service";
    public static final String NOTIFICATION_PAUSE = "com.example.webprog26.servicenotif.default_notification_pause";

    public static final long NOTIFICATION_PAUSE_INTERVAL = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnStartService = (Button) findViewById(R.id.btnStartService);
        Button btnStopService = (Button) findViewById(R.id.btnStopService);

        btnStartService.setOnClickListener(this);
        btnStopService.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, NotificationService.class);
        switch (view.getId())
        {
            case R.id.btnStartService:
                intent.putExtra(DEFAULT_NOTIFICATION_MESSAGE, getResources().getString(R.string.default_message));
                intent.putExtra(NOTIFICATION_PAUSE, NOTIFICATION_PAUSE_INTERVAL);
                startService(intent);
                break;
            case R.id.btnStopService:
                stopService(intent);
                break;
        }
    }
}
