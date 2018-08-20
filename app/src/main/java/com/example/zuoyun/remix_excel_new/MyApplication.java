package com.example.zuoyun.remix_excel_new;

import android.app.Application;

import com.yolanda.nohttp.Logger;
import com.yolanda.nohttp.NoHttp;

/**
 * Created by zuoyun on 2017/3/3.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        NoHttp.initialize(this);
        Logger.setDebug(true);// 开启NoHttp的调试模式, 配置后可看到请求过程、日志和错误信息。
        Logger.setTag("NoHttpLogger:");// 设置NoHttp打印Log的tag。
    }
}
