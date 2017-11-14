package com.example.gordenyou.ktc_wms;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.WMSLib.utils.StreamUtil;
import com.WMSLib.utils.ToastUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends Activity {
    private TextView tv_version_name;

    private String TGA = "LoginActivity";
    private String DownloadUrl = "";
    private String AppDescription = "";

    private static final int UPDATE_VERSION = 100;
    private static final int ENTER_HOME = 101;
    private static final int URL_ERR = 102;
    private static final int IO_ERR = 103;
    private static final int JSON_ERR = 104;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_VERSION:
                    showUpdateDialog();
                    break;
                case URL_ERR:
                    ToastUtil.show(getBaseContext(), "URL异常");
                    break;
                case IO_ERR:
                    ToastUtil.show(getBaseContext(), "IO异常");
                    break;
                case JSON_ERR:
                    ToastUtil.show(getBaseContext(), "Json异常");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tv_version_name = findViewById(R.id.version_name);
        initData();


    }

    private void initData() {
        String version_name = getVersionName();
        tv_version_name.setText("当前版本：");
        CheckVersion();
    }

    /**
     * 检查APP的版本
     */
    private void CheckVersion() {
        new Thread() {
            @Override
            public void run() {
                //获取消息对象
                Message msg = Message.obtain();
                long startTime = System.currentTimeMillis();
                try {
                    URL url = new URL("http://192.168.0.67/Test/update.json");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(2000);
                    conn.setReadTimeout(2000);
                    conn.setRequestMethod("Get");
//                    if(conn.getResponseCode() == 200){
                    if (true) {
                        InputStream is = conn.getInputStream();
                        String json = StreamUtil.streamToString(is);
//                        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//                        StringBuilder builder = new StringBuilder();
//                        String line;
//                        while ((line = reader.readLine()) != null) {
//                            builder.append(line);
//                        }
//                        reader.close();
                        JSONObject jsonObject = new JSONObject(json);
                        DownloadUrl = jsonObject.getString("downloadUrl");
                        AppDescription = jsonObject.getString("versionDes");
                        String versionname = jsonObject.getString("versionName");
                        String versioncode = jsonObject.getString("versionCode");
                        if (getVersionCode() < Integer.valueOf(versioncode)) {
                            msg.what = UPDATE_VERSION;
                        } else {
                            msg.what = ENTER_HOME;
                        }
                    } else {
                        msg.what = IO_ERR;
                    }
                } catch (MalformedURLException e) {
                    msg.what = URL_ERR;
                    e.printStackTrace();
                } catch (IOException e) {
                    msg.what = IO_ERR;
                    e.printStackTrace();
                } catch (JSONException e) {
                    msg.what = JSON_ERR;
                    e.printStackTrace();
                } finally {
                    long endTime = System.currentTimeMillis();
                    if ((endTime - startTime) < 4000) {
                        try {
                            Thread.sleep(4000 - (endTime - startTime));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    handler.sendMessage(msg);
                }
            }
        }.start();
    }

    /**
     * 弹出提示框，选择更新
     */
    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_launcher_background);
        builder.setTitle("更新提示");
        builder.setMessage(AppDescription);
        builder.setCancelable(false);//不可取消更新
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                downloadApk();
            }
        });
//        builder.setNegativeButton("稍后更新", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                GoHome();
//            }
//        });
    }

    private void downloadApk() {
        //首先判断SD卡是否可用，未写保护
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //设置APK的保存路径
            String path = Environment.getDownloadCacheDirectory().getAbsolutePath()
                    + File.separator + "KTC_WMS.apk";
            HttpUtils utils = new HttpUtils();
            utils.download(DownloadUrl, path, new RequestCallBack<File>() {
                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    //下载的apk文件
                    File file = responseInfo.result;
                    Log.i(TGA, "下载成功！");
                    installApk(file);
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Log.i(TGA, "下载失败");
                }

                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    //这里要在状态栏显示进度！
                    super.onLoading(total, current, isUploading);
                }

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

    private int getVersionCode() {
        PackageManager pm = this.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(this.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private String getVersionName() {
        PackageManager pm = this.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(this.getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
