package com.example.loader.ui;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.loader.R;
import com.example.loader.loader.MyBackGroundLoader;

public class Main2Activity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }

    public void initLoader(View view) {
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<String> onCreateLoader(int i, Bundle bundle) {
        return new MyBackGroundLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String s) {

    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}
