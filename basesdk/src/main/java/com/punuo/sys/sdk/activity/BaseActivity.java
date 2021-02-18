package com.punuo.sys.sdk.activity;

import android.os.Bundle;
import android.os.Message;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

import com.punuo.sys.sdk.R;
import com.punuo.sys.sdk.util.BaseHandler;
import com.punuo.sys.sdk.view.PNLoadingDialog;
public class BaseActivity extends AppCompatActivity implements BaseHandler.MessageHandler {
    private PNLoadingDialog mLoadingDialog;
    protected BaseHandler mBaseHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        initLoadingDialog();
        mBaseHandler = new BaseHandler(this);
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
    }

    @Override
    public void handleMessage(Message msg) {

    }
}
