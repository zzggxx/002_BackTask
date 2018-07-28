package com.example.loader.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

/**
 * AsyncTaskLoader与其它类型的Loader稍有不同，AsyncTaskLoader必须要在onStartLoading中执行forceLoad方法，
 * 否则不会生效，所以，官网上建议AsyncTaskLoader使用上面的代码模板进行创建
 */

public class MyBackGroundLoader extends AsyncTaskLoader<String> {

    public MyBackGroundLoader(Context context) {
        super(context);
        onContentChanged();
    }


    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (takeContentChanged()) {
            forceLoad();
        }
    }

    @Override
    public String loadInBackground() {
        return "status";
    }
}
