package com.lei.okhttpdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 1.拿到okHttpClient对象
 * 2.构造Request
 * 2.1.构造requestBody
 * 2.2包装requestBody
 * 3.Call --> execute
 */
public class MainActivity extends AppCompatActivity {

    OkHttpClient okHttpClient = new OkHttpClient();
    private TextView mTvResult;
    private ImageView mIvResult;
    private String mBaseUrl = "http://192.168.0.101:8080/okhttpDemo/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CookieJar cookieJar = okHttpClient.cookieJar();

        mTvResult = findViewById(R.id.tv_result);
        mIvResult = findViewById(R.id.img);
    }

    public void doPost(View view) {
       // 1.拿到OkHttpClient对象

        /*
       2.构造Request
       2.1构造requestBody
       */
        FormBody.Builder formBody = new FormBody.Builder();
        RequestBody requestBody = formBody.add("username","hello")
                                          .add("password","1234").build();

        Request.Builder builder = new Request.Builder();
        Request request = builder.url(mBaseUrl + "login").post(requestBody).build();
//3.4
        executeRequest(request);
    }

    //发送json格式字符串
    public void doPostString(View view) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain;chaset=utf-8"), "{username:hello,password:123}");
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(mBaseUrl + "postString").post(requestBody).build();
//3.4
        executeRequest(request);
    }

    public void doGet(View view) throws IOException {
        /*
        对于一个请求首先需要四部
        1.拿到OkHttpClient对象
         */
       // OkHttpClient okHttpClient = new OkHttpClient();//全局的执行者

        /*
        2.构造Request
         */
        Request.Builder builder = new Request.Builder();
        //请求
        Request request = builder
                .get()
                .url(mBaseUrl + "login?username=hello&password=1234")
                .build();


        executeRequest(request);
    }

    //封装方法
    private void executeRequest(Request request) {
        /*
        3.将Request封装为Call
        是一个执行者
         */
        Call call = okHttpClient.newCall(request);//将request传入，返回Call

        //Response response = call.execute();//执行请求

        /*
        4.执行Call
         */
        call.enqueue(new Callback() {//回调的接口
            @Override//发生错误时回调
            public void onFailure(Call call, IOException e) {
                L.e("onFailure:" + e.getMessage());
                e.printStackTrace();
            }

            @Override//执行完成
            public void onResponse(Call call, Response response) throws IOException {
                L.e("onResponse:");
                final String res = response.body().string();
                L.e(res);

                //InputStream is = response.body().byteStream();

                runOnUiThread(new Runnable() {//UI线程
                    @Override
                    public void run() {
                        mTvResult.setText(res);
                    }
                });

            }
        });//异步的方法
    }



    //发送文件
    public void doPostFile(View view) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }

        File file = new File(Environment.getExternalStorageDirectory(), "a.mp4");
        if(!file.exists()) {
            L.e(file.getAbsolutePath() + " not exist!");
            return;
        }
        //                                                            搜索mime type
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"),file);
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(mBaseUrl + "postFile").post(requestBody).build();

        executeRequest(request);
    }

    //上传文件
    public void doUpload(View view) {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }

        File file = new File(Environment.getExternalStorageDirectory(), "y.jpg");

        if(!file.exists()) {
            L.e(file.getAbsolutePath() + " not exist !");
            return;
        }

        MultipartBody.Builder multpartBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        RequestBody requestBody = multpartBody.addFormDataPart("username","hello")
                .addFormDataPart("password","1234")
                .addFormDataPart("mPhoto", "y.jpg",RequestBody.create(MediaType.parse("application/octet-stream"),file))
                .build();

        Request.Builder builder = new Request.Builder();
        Request request = builder.url(mBaseUrl + "upLoadInfo").post(requestBody).build();

        executeRequest(request);
    }

    //下载文件
    public void doDownload(View view) {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
        Request.Builder builder = new Request.Builder();
        final Request request = builder.get().url(mBaseUrl + "files/a.mp4").build();
        Call call = okHttpClient.newCall(request);//将request传入，返回Call
        call.enqueue(new Callback() {//回调的接口
            @Override//发生错误时回调
            public void onFailure(Call call, IOException e) {
                L.e("onFailure:" + e.getMessage());
                e.printStackTrace();
            }
            @Override//执行完成
            public void onResponse(Call call, Response response) throws IOException {
                L.e("onResponse:");

                final long total = response.body().contentLength();//拿到文件总长度
                long sum = 0L;

               InputStream is = response.body().byteStream();

               int len = 0;
               byte[] buf = new byte[1024];
               File file = new File(Environment.getExternalStorageDirectory(), "b.mp4");
                FileOutputStream fos = new FileOutputStream(file);
                while((len = is.read(buf)) != -1) {
                    fos.write(buf, 0, len);

                    sum += len;

                    L.e(sum + "/"+total);
                    final long finalSum = sum;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTvResult.setText(finalSum + "/" + total);
                        }
                    });
                }

                fos.flush();
                fos.close();
                is.close();

                L.e("download success");
            }
        });//异步的方法
    }
    public void doDownloadImage(View view) {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
        Request.Builder builder = new Request.Builder();
        final Request request = builder.get().url(mBaseUrl + "files/y.jpg").build();
        Call call = okHttpClient.newCall(request);//将request传入，返回Call
        call.enqueue(new Callback() {//回调的接口
            @Override//发生错误时回调
            public void onFailure(Call call, IOException e) {
                L.e("onFailure:" + e.getMessage());
                e.printStackTrace();
            }
            @Override//执行完成
            public void onResponse(Call call, Response response) throws IOException {
                L.e("onResponse:");
                InputStream is = response.body().byteStream();

                final Bitmap bitmap = BitmapFactory.decodeStream(is);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mIvResult.setImageBitmap(bitmap);
                    }
                });

                L.e("download Image");
            }
        });//异步的方法
    }
}
