package com.example.will.backtasks.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.will.backtasks.service.MyBackgroundTaskIntentService;
import com.example.will.backtasks.R;
import com.example.will.backtasks.api.AppConstant;

public class MainActivity extends AppCompatActivity {

    private int i;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(MainActivity.this,
                    intent.getStringExtra("status") + ++i, Toast.LENGTH_SHORT).show();
        }
    };
    private LocalBroadcastManager mLocalBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter intentFilter = new IntentFilter(AppConstant.BROADCAST_ACTION_ONE.name());
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        mLocalBroadcastManager.registerReceiver(mReceiver, intentFilter);

        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent backgroundTask = new Intent(MainActivity.this, MyBackgroundTaskIntentService.class);
//                开启service,启动处理任务的onHandleIntent方法.
                startService(backgroundTask);

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocalBroadcastManager.unregisterReceiver(mReceiver);
    }
}
