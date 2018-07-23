package com.example.will.backtasks;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

public class MyBackgroundTaskIntentService extends IntentService {

//    必须实现无参的构造方法
    public MyBackgroundTaskIntentService() {
        super("MyBackgroundTaskIntentService");
    }

    public MyBackgroundTaskIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
}
