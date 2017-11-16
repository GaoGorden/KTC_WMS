package com.example.gordenyou.ktc_wms;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;

/**
 * Created by Gordenyou on 2017/11/16.
 */

public class DownloadActivity extends Activity {
    private String DownloadUrl;
    private static final String TGA = "DownloadActivity";

    private ProgressBar progressBar;
    private TextView tv_progress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        progressBar = findViewById(R.id.progress);
        tv_progress = findViewById(R.id.tv_progress);
        Intent intent = getIntent();
        DownloadUrl = intent.getStringExtra("DownloadUrl");

        downloadApk();
    }

    private void downloadApk() {
        //首先判断SD卡是否可用，未写保护

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //设置APK的保存路径
            String path = Environment.getExternalStorageDirectory().
                    getAbsolutePath() + File.separator + "KTC_WMS.apk";
            HttpUtils utils = new HttpUtils();
            //请求下载（下载apk网络地址,apk保存路径，回调函数）
            utils.download(DownloadUrl, path, new RequestCallBack<File>() {
                //下载成功调用方法
                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    //下载的apk文件
                    File file = responseInfo.result;
                    Log.i(TGA, "下载成功");
                    //安装Apk
                    installApk(file);
                }

                //下载失败调用方法
                @Override
                public void onFailure(HttpException arg0, String arg1) {
                    Log.i(TGA, "下载失败" + arg0 + arg1);
                }

                //下载过程中调用方法
                @Override
                public void onLoading(long total, long current,
                                      boolean isUploading) {
                    Log.i(TGA, "下载中....");
                    Log.i(TGA, "total=" + total);
                    Log.i(TGA, "current=" + current);
                    super.onLoading(total, current, isUploading);
                    int now = (int) ((current * 100) / total);
                    tv_progress.setText("下载进度：" + now + "%");
                    progressBar.setProgress(now);
                }

                //下载开始调用方法
                @Override
                public void onStart() {
                    Log.i(TGA, "下载开始");
                    super.onStart();
                }


            });
        }
    }

    /**
     * 安装APK
     *
     * @return
     */
    private void installApk(File file) {
        //通过API调用安装APK的activity（隐式调用）
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        //如果用户取消安装，此时应该使用：
        //startActivityForResult(intent,0)
        //startActivity(intent);
        startActivityForResult(intent, 0);
    }
}
