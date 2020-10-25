package com.punuo.sys.app.agedcare.vi.http;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ViRetrofitUtils {
    private static ViRetrofitUtils mInstance;

    // 单线程
    public static ViRetrofitUtils getInstance() {
        if (mInstance == null) {
            synchronized (ViRetrofitUtils.class) {
                if (mInstance == null) {
                    mInstance = new ViRetrofitUtils();
                }
            }
        }
        return mInstance;
    }

    // Init
    public Retrofit getViRetrofit(String songUrl) {
        HttpLoggingInterceptor viLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.e("RetrofitLog", "retrofitBack = " + message);
            }
        });
        viLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // 初始化HTTP
        OkHttpClient viClient = new OkHttpClient().newBuilder()
                .readTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(viLoggingInterceptor)
                .retryOnConnectionFailure(true)
                .build();
        // 初始化Retrofit
        return new Retrofit.Builder()
                .client(viClient)
                .baseUrl(songUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static ViRetrofitService getViAPINService() {
        return getInstance().getViRetrofit(ViAPI.FIDDLER_BASE_QQ_URL).create(ViRetrofitService.class);
    }

    public static ViRetrofitService getSongUrlServer(){
        return getInstance().getViRetrofit(ViAPI.FIDDLER_BASE_SONG_URL).create(ViRetrofitService.class);
    }

    public static ViRetrofitService getSonglrc() {
        return getInstance().getViRetrofit(ViAPI.FIDDLER_BASE_QQ_URL).create(ViRetrofitService.class);
    }
}