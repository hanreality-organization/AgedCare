package com.punuo.sys.app.agedcare.videoAndPictureUpload;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.punuo.sys.app.agedcare.friendCircleMain.adapter.AppApplication;


/**
 * Created by Wxcily on 16/1/5.
 */
public abstract class DefaultBaseActivity extends BaseActivity {

    protected Context context;
    protected Activity activity;
    protected int screenWidth;
    protected int screenHeight;

    protected boolean addTask = true;

    protected void thisHome() {
        this.addTask = false;
    }

    @Override
    protected void onBefore() {
        super.onBefore();
        this.context = this;
        this.activity = this;
        screenWidth = AppApplication.getInstance().getScreenWidth();
        screenHeight = AppApplication.getInstance().getScreenHeight();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (addTask)
            ActivityManager.getInstance().addActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (addTask)
            ActivityManager.getInstance().delActivity(this);
    }

}
