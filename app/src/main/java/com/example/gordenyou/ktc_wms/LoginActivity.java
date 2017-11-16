package com.example.gordenyou.ktc_wms;

import android.Manifest;
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
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
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
    private int AppVersioncode;
    private String VersionName;

    private String TGA = "LoginActivity";
    private String DownloadUrl;
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

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * 权限申请
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
// Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
// We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tv_version_name = findViewById(R.id.version_name);
        verifyStoragePermissions(LoginActivity.this);
        initData();


    }

    private void initData() {
        AppVersioncode = getVersionCode();
        VersionName = getVersionName();
        tv_version_name.setText("当前版本：" + VersionName);
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
                    conn.setRequestMethod("GET");
                    if (conn.getResponseCode() == 200) {
//                    if (true) {
                        InputStream is = conn.getInputStream();
                        String json = StreamUtil.streamToString(is);
                        JSONObject jsonObject = new JSONObject(json);
                        DownloadUrl = jsonObject.getString("downloadUrl");
                        AppDescription = jsonObject.getString("versionDes");
                        String versionname = jsonObject.getString("versionName");
                        String versioncode = jsonObject.getString("versionCode");
                        if (AppVersioncode < Integer.valueOf(versioncode)) {
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
//        builder.setCancelable(false);//不可取消更新
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(LoginActivity.this, DownloadActivity.class);
                intent.putExtra("DownloadUrl", DownloadUrl);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("稍后更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();
    }

    private void showDownloadDialog() {

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
                    ProgressBar progressBar = new ProgressBar(LoginActivity.this);
                    while (isUploading) {
                        int now = (int) (current / total) * 100;
                        progressBar.setProgress(now);
                        progressBar.setVisibility(View.VISIBLE);
                    }

                    Log.i(TGA, "下载中....");
                    Log.i(TGA, "total=" + total);
                    Log.i(TGA, "current=" + current);
                    super.onLoading(total, current, isUploading);
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
