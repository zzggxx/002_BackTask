package com.example.loader;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * 看到这代码是不是觉得特别简洁，让 MainActivity 中继承了 AlbumCollection 中的 AlbumCallback 接口，接着 onCreate()
 * 中实例化了 AlbumCollection，然后让 AlbumCollection 开始加载数据。
 * <p>
 * 等数据加载完成后，便将包含数据的 Cursor 回调在 onAlbumLoad() 方法中，我们便可以进行 UI 的更新。
 * <p>
 * 可以看到采用 Loader 机制，可以让我们的 Activity 或 Fragment 中的代码变得相当的简洁、清晰，而且代码耦合程度也相当低。
 */

public class MainActivity extends AppCompatActivity implements AlbumCollection.AlbumCallbacks {

    private AlbumCollection mCollection;
    private AlbumAdapter mAdapter;
    private RecyclerView mRvAlbum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

//            解耦加载方式
            mCollection = new AlbumCollection();
            mCollection.onCreate(this, this);
            mCollection.loadAlbums();

        } else {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        }
    }

    @Override
    public void onAlbumLoad(Cursor cursor) {
        mRvAlbum = (RecyclerView) findViewById(R.id.main_rv_album);
        mRvAlbum.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new AlbumAdapter(cursor);
        mRvAlbum.setAdapter(mAdapter);
    }

    @Override
    public void onAlbumReset() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mCollection = new AlbumCollection();
                    mCollection.onCreate(this, this);
                    mCollection.loadAlbums();
                }
                break;
            default:
                break;
        }
    }
}

