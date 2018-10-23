package com.example.will.backtasks.service;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.will.backtasks.api.AppConstant;

public class MyBackgroundTaskIntentService extends IntentService {

    private static final String TAG = "MyBackgroundTaskIntentService";

    //    必须实现无参的构造方法
    public MyBackgroundTaskIntentService() {
        super("MyBackgroundTaskIntentService");
    }

    public MyBackgroundTaskIntentService(String name) {
        super(name);
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG, "onCreate: " + "backthread Create");
    }

    //    处理后台任务,子线程中.自动开启一个新线程,若是线程中的任务处理完毕就将service销毁了,但是若任务没有处理
    //    完毕又进行了startService(),那么将会将那个任务放在已经开启的线程中处理,有队列的排序.
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        String data = intent.getDataString();

        // Do something
        try {
            Thread thread = Thread.currentThread();
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Intent localTask = new Intent(AppConstant.BROADCAST_ACTION_ONE.name());
        localTask.putExtra("status", "任务处理完毕");
        LocalBroadcastManager.getInstance(this).sendBroadcast(localTask);
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i(TAG, "onDestroy: " + "backthread destory");
    }
}
