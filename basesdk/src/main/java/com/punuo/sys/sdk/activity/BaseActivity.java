package com.punuo.sys.sdk.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.punuo.sys.sdk.R;
import com.punuo.sys.sdk.util.BaseHandler;
import com.punuo.sys.sdk.view.PNLoadingDialog;

public class BaseActivity extends AppCompatActivity implements BaseHandler.MessageHandler, ViewTreeObserver.OnGlobalLayoutListener {
    private PNLoadingDialog mLoadingDialog;
    protected BaseHandler mBaseHandler;

    private FrameLayout content;
    private boolean mLayoutComplete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        initLoadingDialog();
        mBaseHandler = new BaseHandler(this);
        content = findViewById(android.R.id.content);
        content.post(() -> mLayoutComplete = true);
        content.getViewTreeObserver().addOnGlobalLayoutListener(this);
        hideStatusBarNavigationBar();
    }

    public void hideStatusBarNavigationBar() {
        Window window = getWindow();
        window.addFlags( WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags( WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void onGlobalLayout() {
        if (!mLayoutComplete) {
            return;
        }
        onNavigationBarStatusChanged();
    }

    protected void onNavigationBarStatusChanged() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
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
        content.getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    @Override
    public void handleMessage(Message msg) {

    }
}
