package com.punuo.sys.app.agedcare.ui;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.punuo.sip.H264Config;
import com.punuo.sip.SipConfig;
import com.punuo.sip.dev.DevHeartBeatHelper;
import com.punuo.sip.dev.SipDevManager;
import com.punuo.sip.dev.event.DevLoginFailEvent;
import com.punuo.sip.dev.event.MonitorEvent;
import com.punuo.sip.dev.event.ReRegisterDevEvent;
import com.punuo.sip.dev.model.ImageShare;
import com.punuo.sip.dev.model.LoginResponseDev;
import com.punuo.sip.dev.model.OperationData;
import com.punuo.sip.dev.request.SipDevRegisterRequest;
import com.punuo.sip.user.H264ConfigUser;
import com.punuo.sip.user.SipUserManager;
import com.punuo.sip.user.UserHeartBeatHelper;
import com.punuo.sip.user.event.ReRegisterUserEvent;
import com.punuo.sip.user.event.UnauthorizedEvent;
import com.punuo.sip.user.event.UserReplaceEvent;
import com.punuo.sip.user.model.LoginResponseUser;
import com.punuo.sip.user.request.SipGetUserIdRequest;
import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.sip.SipInfo;
import com.punuo.sys.app.agedcare.video.RtpVideo;
import com.punuo.sys.app.agedcare.video.SendActivePacket;
import com.punuo.sys.app.agedcare.video.VideoInfo;
import com.punuo.sys.app.linphone.LinphoneHelper;
import com.punuo.sys.app.router.CompatRouter;
import com.punuo.sys.app.router.HomeRouter;
import com.punuo.sys.sdk.account.UserInfoManager;
import com.punuo.sys.sdk.activity.BaseActivity;
import com.punuo.sys.sdk.event.CloseOtherMediaEvent;
import com.punuo.sys.sdk.util.DeviceHelper;
import com.punuo.sys.sdk.view.LoopIndicator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

@Route(path = HomeRouter.ROUTER_HOME_ACTIVITY)
public class HomeActivity extends BaseActivity {
    private static final String TAG = "HomeActivity";
    private boolean userLoginFailed = false;
    private boolean devLoginFailed = false;
    private LoopIndicator mLoopIndicator;
    public static String apkPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //心跳包
        initHeartBeat();
        apkPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/";

        UserInfoManager.getInstance().refreshUserInfo();
        //将图片的引用转化为图片控件存在List的集合中
        mLoopIndicator = findViewById(R.id.indicator);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new MemberFragment());
        fragments.add(new MenuFragment());
        mLoopIndicator.setData(fragments.size());
        mLoopIndicator.select(0);
        FragAdapter adapter = new FragAdapter(getSupportFragmentManager(), fragments);
        ViewPager vp = findViewById(R.id.viewpager);
        vp.setAdapter(adapter);
        vp.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mLoopIndicator.select(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        init();
        EventBus.getDefault().register(this);
    }

    private void initHeartBeat() {
        if (!mBaseHandler.hasMessages(UserHeartBeatHelper.MSG_HEART_BEAR_VALUE)) {
            mBaseHandler.sendEmptyMessageDelayed(UserHeartBeatHelper.MSG_HEART_BEAR_VALUE, UserHeartBeatHelper.DELAY);
        }
        if (!mBaseHandler.hasMessages(DevHeartBeatHelper.MSG_HEART_BEAR_VALUE)) {
            mBaseHandler.sendEmptyMessageDelayed(DevHeartBeatHelper.MSG_HEART_BEAR_VALUE, DevHeartBeatHelper.DELAY);
        }
    }

    private void startLinphone() {
        Log.i(TAG, "startLinphone: ");
        LinphoneHelper.getInstance().setDebug(DeviceHelper.isApkInDebug());
        LinphoneHelper.getInstance().startVoip(this);
        mBaseHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(UserInfoManager.getUserInfo().ipNumber)) {
                    LinphoneHelper.getInstance().register(UserInfoManager.getUserInfo().ipNumber, "123456", SipConfig.getServerIp() + ":5000");
                }
            }
        }, 500);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ImageShare imageShare) {
        Glide.with(this).asBitmap().load(imageShare.imageUrl)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        saveImageToGallery(HomeActivity.this, resource);
                        return false;
                    }
                }).submit();
    }

    private void init() {
        startLinphone();
    }

    public void saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File appDir = new File(apkPath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(file.getAbsolutePath())));
        getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + "=?", new String[]{apkPath + fileName});//删除系统缩略图
        file.delete();
    }

    public static class FragAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments;

        public FragAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            mFragments = fragments;
        }

        @Override
        public Fragment getItem(int arg0) {
            return mFragments.get(arg0);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //停止语音电话服务
        LinphoneHelper.getInstance().unRegister();
        LinphoneHelper.getInstance().stopVoip(this);
        mBaseHandler.removeMessages(UserHeartBeatHelper.MSG_HEART_BEAR_VALUE);
        mBaseHandler.removeMessages(DevHeartBeatHelper.MSG_HEART_BEAR_VALUE);
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startScreenSaver();
    }


    public void onBackPressed() {
        //屏蔽返回键
    }


    private void registerUser() {
        SipGetUserIdRequest registerRequest = new SipGetUserIdRequest();
        SipUserManager.getInstance().addRequest(registerRequest);
    }

    private void registerDev() {
        SipDevRegisterRequest registerRequest = new SipDevRegisterRequest();
        SipDevManager.getInstance().addRequest(registerRequest);
    }


    /**
     * 设备Sip服务重新注册事件
     *
     * @param event event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReRegisterDevEvent event) {
        if (devLoginFailed) {
            devLoginFailed = false;
            return;
        }
        mBaseHandler.removeMessages(DevHeartBeatHelper.MSG_HEART_BEAR_VALUE);
        registerDev();
    }

    /**
     * 设备Sip服务注册失败事件
     *
     * @param event event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DevLoginFailEvent event) {
        mBaseHandler.removeMessages(DevHeartBeatHelper.MSG_HEART_BEAR_VALUE);
        devLoginFailed = true;
    }

    /**
     * 用户Sip服务重新注册事件
     *
     * @param event event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReRegisterUserEvent event) {
        if (userLoginFailed) {
            userLoginFailed = false;
            return;
        }
        mBaseHandler.removeMessages(UserHeartBeatHelper.MSG_HEART_BEAR_VALUE);
        registerUser();
    }

    /**
     * 用户Sip服务注册失败事件
     *
     * @param event event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UnauthorizedEvent event) {
        mBaseHandler.removeMessages(UserHeartBeatHelper.MSG_HEART_BEAR_VALUE);
        userLoginFailed = true;
    }

    /**
     * 用户Sip服务注册成功事件
     *
     * @param event event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LoginResponseUser event) {
        //sip登陆注册成功 开启心跳保活
        if (!mBaseHandler.hasMessages(UserHeartBeatHelper.MSG_HEART_BEAR_VALUE)) {
            mBaseHandler.sendEmptyMessageDelayed(UserHeartBeatHelper.MSG_HEART_BEAR_VALUE, UserHeartBeatHelper.DELAY);
        }
    }

    /**
     * 设备Sip服务注册成功事件
     *
     * @param event event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LoginResponseDev event) {
        //sip登陆注册成功 开启心跳保活
        if (!mBaseHandler.hasMessages(DevHeartBeatHelper.MSG_HEART_BEAR_VALUE)) {
            mBaseHandler.sendEmptyMessageDelayed(DevHeartBeatHelper.MSG_HEART_BEAR_VALUE, DevHeartBeatHelper.DELAY);
        }
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case UserHeartBeatHelper.MSG_HEART_BEAR_VALUE:
                UserHeartBeatHelper.heartBeat();
                break;
            case DevHeartBeatHelper.MSG_HEART_BEAR_VALUE:
                DevHeartBeatHelper.heartBeat();
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UserReplaceEvent event) {
        AlertDialog loginReplace = new AlertDialog.Builder(getApplicationContext())
                .setTitle("账号异地登录")
                .setMessage("请重新登录")
                .setPositiveButton("确定", null)
                .create();
        loginReplace.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        loginReplace.setCancelable(false);
        loginReplace.setCanceledOnTouchOutside(false);
        loginReplace.show();
        UserInfoManager.clearUserData();
        ARouter.getInstance().build(CompatRouter.ROUTER_LOGIN_ACTIVITY).navigation();
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NetworkInfo info) {
        registerUser();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OperationData operationData) {
        EventBus.getDefault().post(new CloseOtherMediaEvent());
        ARouter.getInstance().build(HomeRouter.ROUTER_VIDEO_REPLY_ACTIVITY).navigation();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MonitorEvent event) {
        switch (event.monitorType) {
            case H264Config.SINGLE_MONITOR:
                ARouter.getInstance().build(HomeRouter.ROUTER_SINGLE_MONITOR_ACTIVITY).navigation();
                break;
            case H264Config.DOUBLE_MONITOR_NEGATIVE:
                new Thread(() -> {
                    SipInfo.decoding = true;
                    try {
                        VideoInfo.rtpVideo = new RtpVideo(H264ConfigUser.rtpIp, H264ConfigUser.rtpPort);
                        VideoInfo.sendActivePacket = new SendActivePacket();
                        VideoInfo.sendActivePacket.startThread();
                        ARouter.getInstance().build(HomeRouter.ROUTER_VIDEO_CALL_ACTIVITY).navigation();
                    } catch (SocketException e) {
                        e.printStackTrace();
                    }
                }).start();
                break;
        }
    }

}
