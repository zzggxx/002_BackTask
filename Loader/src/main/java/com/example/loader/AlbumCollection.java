package com.example.loader;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import java.lang.ref.WeakReference;

/**
 * @author Haoz
 * @date 2017/11/26.
 */

public class AlbumCollection implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 1;
    private WeakReference<Context> mContext;
    private LoaderManager mLoaderManager;
    private AlbumCallbacks mCallbacks;

    /*----------------------以下是回调接口---------------------------------------------------------*/

    /**
     * loaderManager检查是否有Loader已经存在,没有就调用此方法去创建.
     *
     * @param id
     * @param args
     * @return
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Context context = mContext.get();
        if (context == null) {
            return null;
        }

        return AlbumLoader.newInstance(context);
    }

    /**
     * 加载完毕数据,此时需要清除数据等操作.
     * @param loader
     * @param data
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Context context = mContext.get();
        if (context == null) {
            return;
        }

        mCallbacks.onAlbumLoad(data);
    }

    /**
     * 若是更新的筛选条件的话,使用此方法重新加载Loader.
     * @param loader
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Context context = mContext.get();
        if (context == null) {
            return;
        }
        mCallbacks.onAlbumReset();
    }

    /*--------------------自定义方法------------------------------------------------------------*/
    public void onCreate(FragmentActivity activity, AlbumCallbacks callbacks) {
//        包装成弱引用以防内存泄露
        mContext = new WeakReference<Context>(activity);
//        activity或者是fragment中,也就说在显示的视图中存在,一个LoaderManager可以管理一个或者多个Loader.
        mLoaderManager = activity.getSupportLoaderManager();
//        写回调使用
        mCallbacks = callbacks;
    }

    /*------------------初始化加载器-------------------*/
    public void loadAlbums() {
//        Loader<Cursor> cursorLoader = mLoaderManager.initLoader(LOADER_ID, null, this);
        mLoaderManager.initLoader(LOADER_ID, null, this);
    }

    /*------------------埋点方法------------------------------*/
    public interface AlbumCallbacks {

        void onAlbumLoad(Cursor cursor);

        void onAlbumReset();
    }
}




















