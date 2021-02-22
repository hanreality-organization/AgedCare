package com.punuo.sys.sdk;

import android.app.Application;
import android.content.Context;

import com.alibaba.android.arouter.launcher.ARouter;
import com.punuo.sys.sdk.util.DeviceHelper;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.tencent.mmkv.MMKV;


/**
 * Created by han.chen.
 * Date on 2021/2/18.
 **/
public class PnApplication extends Application {
    private static PnApplication application;

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
        FlowManager.init(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MMKV.initialize(base);
    }
}
