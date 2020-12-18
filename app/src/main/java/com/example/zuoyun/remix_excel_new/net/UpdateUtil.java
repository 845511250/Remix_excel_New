package com.example.zuoyun.remix_excel_new.net;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zuoyun.remix_excel_new.R;
import com.example.zuoyun.remix_excel_new.activity.MainActivity;
import com.example.zuoyun.remix_excel_new.bean.Config;
import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.download.DownloadListener;
import com.yolanda.nohttp.download.DownloadQueue;
import com.yolanda.nohttp.download.DownloadRequest;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.Response;

import java.io.File;

/**
 * Created by zuoyun on 2018/5/23.
 */

public class UpdateUtil {
    private Context context;

    RequestQueue requestQueue = NoHttp.newRequestQueue();
    DownloadQueue downloadQueue = NoHttp.newDownloadQueue();

    AlertDialog dialog_update;

    public UpdateUtil(Context mContext) {
        context = mContext;
    }

    public void checkUpdate(){
        requestQueue.add(10, NoHttp.createStringRequest(Config.checkUpdate), responseListener);
    }

    OnResponseListener<String> responseListener=new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {

        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            if (what == 10) {
                if (response.responseCode() == 200) {
                    File fileOldApk = new File(Environment.getExternalStorageDirectory() + "/apk_new.apk");
                    if (fileOldApk.exists()) {
                        fileOldApk.delete();
                    }
                    if (!response.get().split("#")[0].equals(getVersionName())) {
                        showDialogUpdate("更新", "检测到新版本V" + response.get().split("#")[0] + "\n" + response.get().split("#")[1]);
                    }
                } else {
                    Toast.makeText(context, "服务器返回码：" + response.responseCode(), Toast.LENGTH_SHORT).show();
                }
            }

        }

        @Override
        public void onFailed(int what, Response<String> response) {

        }

        @Override
        public void onFinish(int what) {

        }
    };

    // 获取版本号
    public String getVersionName() {
        try {
            PackageManager packageManager = context.getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packInfo.versionName;
        } catch (Exception e) {
            return "wrong";
        }

    }

    //更新提示dialog
    private void showDialogUpdate(String title, final String content){
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogTransBackGround);
        dialog_update = builder.create();
        dialog_update.setCancelable(false);
        dialog_update.show();
        View view_dialog = LayoutInflater.from(context).inflate(R.layout.item_dialog_confirm, null);
        dialog_update.setContentView(view_dialog);
        final TextView tv_title_update = (TextView) view_dialog.findViewById(R.id.tv_dialog_title);
        TextView tv_content = (TextView) view_dialog.findViewById(R.id.tv_dialog_content);
        final Button bt_yes = (Button) view_dialog.findViewById(R.id.bt_dialog_yes);
        Button bt_no = (Button) view_dialog.findViewById(R.id.bt_dialog_no);

        tv_title_update.setText(title);
        tv_content.setText(content);
        bt_yes.setText("确 定");
        bt_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_update.dismiss();
            }
        });
        bt_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadRequest downloadRequest = NoHttp.createDownloadRequest(Config.downloadApk, Environment.getExternalStorageDirectory().getPath(), "apk_new.apk", false, true);
                downloadQueue.add(11, downloadRequest, new DownloadListener() {
                    @Override
                    public void onDownloadError(int what, Exception exception) {
                        tv_title_update.setText("更新下载失败！" + exception.getMessage());
                        bt_yes.setClickable(true);
                    }

                    @Override
                    public void onStart(int what, boolean isResume, long rangeSize, Headers responseHeaders, long allCount) {
                        bt_yes.setClickable(false);
                    }

                    @Override
                    public void onProgress(int what, int progress, long fileCount) {
                        tv_title_update.setText(progress + "%");
                    }

                    @Override
                    public void onFinish(int what, String filePath) {
                        bt_yes.setClickable(true);
                        installApk(new File(Environment.getExternalStorageDirectory() + "/apk_new.apk"), context);
                    }

                    @Override
                    public void onCancel(int what) {

                    }
                });
            }
        });
    }

    //安装apk
    public void installApk(File file,Context context) {
        Intent intent = new Intent();
        //执行动作
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //执行的数据类型
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
        MainActivity.instance.finish();
    }


}
