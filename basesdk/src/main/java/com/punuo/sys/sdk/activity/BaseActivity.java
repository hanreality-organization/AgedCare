package com.punuo.sys.sdk.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.launcher.ARouter;
import com.punuo.sys.app.router.CompatRouter;
import com.punuo.sys.sdk.R;
import com.punuo.sys.sdk.task.ImageTask;
import com.punuo.sys.sdk.util.BaseHandler;
import com.punuo.sys.sdk.view.PNLoadingDialog;

public class BaseActivity extends AppCompatActivity implements BaseHandler.MessageHandler {
    private PNLoadingDialog mLoadingDialog;
    protected BaseHandler mBaseHandler;
    //TODO 视频的时候不能开启定时
    private CountDownTimer countDownTimer;
    private boolean needCountDown = true;

    public void setNeedCountDown(boolean needCountDown) {
        this.needCountDown = needCountDown;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        initLoadingDialog();
        mBaseHandler = new BaseHandler(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        ViewGroup contentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
        View childView = contentView.getChildAt(0);
        if (childView != null) {
            childView.setFitsSystemWindows(true);
            childView.requestApplyInsets();
        }
    }

    public BaseHandler getBaseHandler() {
        return mBaseHandler;
    }

    private void initLoadingDialog() {
        mLoadingDialog = new PNLoadingDialog(this);
        mLoadingDialog.setCancelable(true);
        mLoadingDialog.setCanceledOnTouchOutside(false);
    }

    public void showLoadingDialog() {
        if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
            mLoadingDialog.show();
        }
    }

    public void showLoadingDialog(String msg) {
        if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
            mLoadingDialog.setLoadingMsg(msg);
            showLoadingDialog();
        }
    }

    public void dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    /**
     * 复写返回键操作,返回true则不继续下发
     *
     * @return
     */
    protected boolean onPressBack() {
        return false;
    }

    @Override
    public void onBackPressed() {
        if (onPressBack()) {
            return;
        }
        try {
            super.onBackPressed();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            finish();
        }
        overridePendingTransition(R.anim.push_right_in, R.anim.right_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissLoadingDialog();
        if (countDownTimer != null && needCountDown) {
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (countDownTimer != null && needCountDown) {
            countDownTimer.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (countDownTimer != null && needCountDown) {
            countDownTimer.cancel();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //有按下动作时取消定时
                if (countDownTimer != null && needCountDown) {
                    countDownTimer.cancel();
                }
                break;
            case MotionEvent.ACTION_UP:
                //抬起时启动定时
                if (needCountDown) {
                    startScreenSaver();
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 跳轉廣告
     */
    public void startScreenSaver() {
        long advertisingTime = 40 * 1000;//定时跳转广告时间
        if (countDownTimer == null) {
            countDownTimer = new CountDownTimer(advertisingTime, 10000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    new ImageTask(imageList -> {
                        if (!imageList.isEmpty()) {
                            ARouter.getInstance().build(CompatRouter.ROUTER_SCREEN_SAVER_ACTIVITY).navigation();
                        }
                    }).execute();
                }
            };
        }
        countDownTimer.start();
    }

    @Override
    public void handleMessage(Message msg) {

    }
}
