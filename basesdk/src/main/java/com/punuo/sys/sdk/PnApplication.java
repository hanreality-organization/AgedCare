package com.punuo.sys.sdk;

import android.app.Application;
import android.content.Context;

import com.alibaba.android.arouter.launcher.ARouter;
import com.danikula.videocache.HttpProxyCacheServer;
import com.danikula.videocache.file.FileNameGenerator;
import com.punuo.sys.sdk.util.DeviceHelper;
import com.tencent.mmkv.MMKV;

import java.io.File;


/**
 * Created by han.chen.
 * Date on 2021/2/18.
 **/
public class PnApplication extends Application {
    private static PnApplication application;
    private HttpProxyCacheServer proxy;

    public static PnApplication getInstance() {
        if (application == null) {
            application = new PnApplication();
        }
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        if (DeviceHelper.isApkInDebug()) {
            ARouter.openLog();
            ARouter.openDebug();
        }
        ARouter.init(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MMKV.initialize(base);
    }

    public static HttpProxyCacheServer getProxy(Context context) {
        PnApplication myApplication = (PnApplication) context.getApplicationContext();
        return myApplication.proxy == null ? (myApplication.proxy = myApplication.newProxy()) : myApplication.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer
                .Builder(this)
                .maxCacheFilesCount(300)
                .cacheDirectory(new File(getExternalFilesDir("music"), "audio-cache"))
                .fileNameGenerator(new MyFileNameGenerator()).build();
    }

    public static class MyFileNameGenerator implements FileNameGenerator {
        public String generate(String url) {
            return url;
        }
    }
}
