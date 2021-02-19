package com.punuo.sys.app.compat.login;


import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.punuo.sip.dev.SipDevManager;
import com.punuo.sip.dev.model.LoginResponseDev;
import com.punuo.sip.dev.request.SipDevRegisterRequest;
import com.punuo.sip.user.SipUserManager;
import com.punuo.sip.user.model.LoginResponseUser;
import com.punuo.sip.user.request.SipGetUserIdRequest;
import com.punuo.sys.app.compat.R;
import com.punuo.sys.app.compat.R2;
import com.punuo.sys.app.compat.process.ProcessTasks;
import com.punuo.sys.app.router.CompatRouter;
import com.punuo.sys.app.router.HomeRouter;
import com.punuo.sys.sdk.account.AccountManager;
import com.punuo.sys.sdk.activity.BaseActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

@Route(path = CompatRouter.ROUTER_LOGIN_ACTIVITY)
public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";
    @BindView(R2.id.setting)
    Button setting;
    @BindView(R2.id.login)
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ProcessTasks.commonLaunchTasks(getApplication());
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        AccountManager.loadProperties();
        setting.setOnClickListener(v -> {
            Intent mIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
            startActivity(mIntent);
        });
        login.setOnClickListener(v -> {
            registerUser();
        });
        EventBus.getDefault().register(this);
    }

    private void registerUser() {
        SipGetUserIdRequest registerRequest = new SipGetUserIdRequest();
        SipUserManager.getInstance().addRequest(registerRequest);
    }

    private void registerDev() {
        SipDevRegisterRequest registerRequest = new SipDevRegisterRequest();
        SipDevManager.getInstance().addRequest(registerRequest);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LoginResponseUser event) {
        registerDev();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LoginResponseDev event) {
        dismissLoadingDialog();
        ARouter.getInstance().build(HomeRouter.ROUTER_HOME_ACTIVITY).navigation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissLoadingDialog();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        //屏蔽返回键
    }
}
