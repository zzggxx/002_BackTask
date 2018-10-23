package com.example.asynctask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 如何下载多张图片呢?????????????????????????
 */
public class MainActivity extends Activity {

    private Button button;
    private ImageView imageView;
    private ProgressDialog progressDialog;
    private final String IMAGE_PATH = "http://t2.hddhhn.com/uploads/tu/201610/198/scx30045vxd.jpg";
    private String TAG = "MainActivity";

    //    private final String IMAGE_PATH2 = "http://ww2.sinaimg.cn/mw690/69c7e018jw1e6hd0vm3pej20fa0a674c.jpg";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button);
        imageView = (ImageView) findViewById(R.id.imageView);

        //    弹出要给ProgressDialog
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("提示信息");
        progressDialog.setMessage("正在下载中，请稍后......");
        //    设置setCancelable(false); 表示我们不能取消这个弹出框，等下载完成之后再让弹出框消失
        progressDialog.setCancelable(false);
        //    设置ProgressDialog样式为水平的样式
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MyAsyncTask().execute(IMAGE_PATH);
            }
        });
    }

    /**
     * 定义一个类，让其继承AsyncTask这个类
     * Params: 这个泛型指定的是我们传递给异步任务执行时的参数的类型
     * String类型，表示传递给异步任务的参数类型是String，通常指定的是URL路径
     * <p>
     * Progress:这个泛型指定的是我们的异步任务在执行的时候将执行的进度返回给UI线程的参数的类型
     * Integer类型，进度条的单位通常都是Integer类型
     * <p>
     * Result：这个泛型指定的异步任务执行完后返回给UI线程的结果的类型
     * Bitmap类型，表示我们下载好的图片通过流的转换成了图片.
     *
     * @author xiaoluo
     */
    public class MyAsyncTask extends AsyncTask<String, Integer, Bitmap> {

        //        开始doInBackground前需要在主线程中处理什么
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //    在onPreExecute()中我们让ProgressDialog显示出来
            progressDialog.show();
        }

        //        主要干活的地方
        @Override
        protected Bitmap doInBackground(String... params) {

            try {
                URL url = new URL(IMAGE_PATH);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    InputStream inputStream = connection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;
                }
            } catch (MalformedURLException e) {
//                url地址的错误
                e.printStackTrace();
            } catch (IOException e) {
//                IO链接错粗
                e.printStackTrace();
            }
            return null;
        }

        //        进度更新
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            //    更新ProgressDialog的进度条
            progressDialog.setProgress(values[0]);
            Log.i(TAG, "onProgressUpdate: " + values[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            //    更新我们的ImageView控件
            imageView.setImageBitmap(bitmap);
            //    使ProgressDialog框消失
            progressDialog.dismiss();
        }
    }

}
