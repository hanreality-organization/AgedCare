package com.punuo.sys.app.agedcare.http;

import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by 林逸磊 on 2018/5/21.
 */

public class Okhttputils {
    static String TAG="okhttp";
    static String response1;

    public static String sendpost(String url, String userid) {
        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
        FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体
        formBody.add("userid", userid);//传递键值对参数
        final Request request = new Request.Builder()//创建Request 对象。
                .url(url)
                .post(formBody.build())//传递请求体
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("okhttp", response.toString());
                response1 = response.toString();
            }
        });
        return response1;
    }

    public static String sendget(String url, String userid) {
        Response response = null;
        try {
            OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象
            Request request = new Request.Builder()
                    .url(url+"?userid="+userid)//请求接口。如果需要传参拼接到接口后面。
                    .build();//创建Request 对象


            response = client.newCall(request).execute();//得到Response 对象
            if (response.isSuccessful()) {
                Log.d(TAG, "response.code()==" + response.code());
                Log.d(TAG, "response.message()==" + response.message());
                response1=response.body().string();
        }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response1;
    }
}
