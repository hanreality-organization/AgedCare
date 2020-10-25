package com.punuo.sys.app.agedcare.friendCircleMain.adapter;

import android.app.Application;


import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.punuo.sys.app.agedcare.videoAndPictureUpload.DeviceInfoUtils;

import okhttp3.OkHttpClient;


/**
 * 描述：
 * 作者：HMY
 * 时间：2016/5/13
 */
public class AppApplication extends Application {
    private int screenWidth;
    private int screenHeight;
    private OkHttpClient client;
    //Application单例
    private static AppApplication instance;

    public static AppApplication getInstance() {
        if(instance==null) {
            instance=new AppApplication();
        }
        return instance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        initImageLoader();

        instance = this;
         //client =OkHttpUtils.getInstance().getOkHttpClient();
        screenWidth = DeviceInfoUtils.getScreenWidth(this);//获取屏幕宽度
        screenHeight = DeviceInfoUtils.getScreenHeight(this);//获取屏幕高度
    }

    private void initImageLoader() {
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(configuration);

    }
    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }
}
