package com.example.zuoyun.remix_excel_new.net;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.zuoyun.remix_excel_new.activity.MainActivity;
import com.example.zuoyun.remix_excel_new.bean.HLPics;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.Response;

/**
 * Created by zuoyun on 2018/5/23.
 */

public class HLUtil {
    private Context context;

    RequestQueue requestQueue = NoHttp.newRequestQueue();

    public HLUtil(Context mContext) {
        context = mContext;
    }

    public void get(){
        requestQueue.add(111, NoHttp.createStringRequest("http://845511250.top/zhengyuapi/JavaScan_jinjiang/hl.php"), responseListener);
    }

    OnResponseListener<String> responseListener=new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {

        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            if (what == 111) {
                if (response.responseCode() == 200) {
                    MainActivity.instance.hlPics = JSON.parseObject(response.get(), HLPics.class);
                    Toast.makeText(context, "已获取HL图片分组", Toast.LENGTH_SHORT).show();
                    Log.e("aaa", MainActivity.instance.hlPics.groups.size() + "");
                } else {
                    Toast.makeText(context, "获取HL图片分组失败：" + response.responseCode(), Toast.LENGTH_SHORT).show();
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


}
