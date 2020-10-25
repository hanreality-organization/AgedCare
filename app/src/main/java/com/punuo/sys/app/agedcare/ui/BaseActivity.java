package com.punuo.sys.app.agedcare.ui;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.WindowManager;

/**
 * Author:miner
 * Date: 2017/8/10
 * Description:
 */

    public abstract class BaseActivity extends HindebarActivity {

    public CountDownTimer countDownTimer;

    //public Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置屏幕长亮
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //context = this;
        setContentView(getLayoutRes());

    }

    protected abstract int getLayoutRes();

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //有按下动作时取消定时
                if (countDownTimer != null){
                    countDownTimer.cancel();
                }
                break;
            case MotionEvent.ACTION_UP:
                //抬起时启动定时
                startAD();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }


    /**
     * 跳轉廣告
     */
    public void startAD() {
        if (countDownTimer == null) {
             long advertisingTime = 30 * 1000;//定时跳转广告时间
            countDownTimer = new CountDownTimer(advertisingTime, 10000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onFinish() {
                    //定时完成后的操作
                    //跳转到广告页面
                    startActivity(new Intent(BaseActivity.this,BaseDispatchTouchActivity.class), ActivityOptions.makeSceneTransitionAnimation(BaseActivity.this).toBundle());
                }
            };
            countDownTimer.start();
        } else {
            countDownTimer.start();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        //显示是启动定时
        startAD();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //当activity不在前台是停止定时
        if (countDownTimer != null){
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁时停止定时
        if (countDownTimer != null){
            countDownTimer.cancel();
        }
    }
}
