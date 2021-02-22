package com.punuo.sys.app.agedcare.ui;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.WindowManager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.punuo.sip.dev.DevHeartBeatHelper;
import com.punuo.sip.dev.SipDevManager;
import com.punuo.sip.dev.event.DevLoginFailEvent;
import com.punuo.sip.dev.event.ReRegisterDevEvent;
import com.punuo.sip.dev.model.LoginResponseDev;
import com.punuo.sip.dev.model.OperationData;
import com.punuo.sip.dev.request.SipDevRegisterRequest;
import com.punuo.sip.user.SipUserManager;
import com.punuo.sip.user.UserHeartBeatHelper;
import com.punuo.sip.user.event.ReRegisterUserEvent;
import com.punuo.sip.user.event.UnauthorizedEvent;
import com.punuo.sip.user.event.UserReplaceEvent;
import com.punuo.sip.user.model.LoginResponseUser;
import com.punuo.sip.user.request.SipGetUserIdRequest;
import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.service.NewsService;
import com.punuo.sys.app.router.CompatRouter;
import com.punuo.sys.app.router.HomeRouter;
import com.punuo.sys.sdk.account.UserInfoManager;
import com.punuo.sys.sdk.activity.BaseActivity;
import com.punuo.sys.sdk.view.LoopIndicator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.punuo.sys.app.agedcare.sip.SipInfo.shareurl;

@Route(path = HomeRouter.ROUTER_HOME_ACTIVITY)
public class HomeActivity extends BaseActivity {
    private boolean userLoginFailed = false;
    private boolean devLoginFailed = false;
    private LoopIndicator mLoopIndicator;
    private Bitmap bitmap;
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

    public void screenUpdate() {

//        new Task().execute(shareurl);
        bitmap = GetImageInputStream(shareurl);
        Log.d("picture111", shareurl);
//        SavaImage(bitmap, apkPath);
        saveImageToGallery(this, bitmap);
    }

    private void init() {

        startService(new Intent(this, NewsService.class));
//        SipInfo.loginReplace = new Handler(new Handler.Callback() {
//            @Override
//            public boolean handleMessage(android.os.Message msg) {
//                sipUser.sendMessage(SipMessageFactory.createNotifyRequest(sipUser, SipInfo.user_to,
//                        SipInfo.user_from, BodyFactory.createLogoutBody()));
//                SipInfo.sipDev.sendMessage(SipMessageFactory.createNotifyRequest(SipInfo.sipDev, SipInfo.dev_to,
//                        SipInfo.dev_from, BodyFactory.createLogoutBody()));
//                //关闭监听服务
//                stopService(new Intent(HomeActivity.this, NewsService.class));
//                //关闭PTT监听服务
//                stopService(new Intent(HomeActivity.this, PTTService.class));
//                //关闭用户心跳
//                SipInfo.keepUserAlive.stopThread();
//                //关闭设备心跳
//                SipInfo.keepDevAlive.stopThread();
//                //重置登录状态
//                SipInfo.userLogined = false;
//                SipInfo.devLogined = false;
//                AlertDialog loginReplace = new AlertDialog.Builder(getApplicationContext())
//                        .setTitle("账号异地登录")
//                        .setMessage("请重新登录")
//                        .setPositiveButton("确定", null)
//                        .create();
//                loginReplace.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//                loginReplace.show();
//                loginReplace.setCancelable(false);
//                loginReplace.setCanceledOnTouchOutside(false);
//                ARouter.getInstance().build(CompatRouter.ROUTER_LOGIN_ACTIVITY).navigation();
//                return false;
//            }
//        });
    }

    public Bitmap GetImageInputStream(String imageurl) {
        URL url;
        HttpURLConnection connection;
        Bitmap bitmap = null;
        try {
            url = new URL(imageurl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(6000); //超时设置
            connection.setDoInput(true);
            connection.setUseCaches(false); //设置不使用缓存
            InputStream inputStream = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
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
        Log.d("lujin", apkPath + fileName);
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
//        SipInfo.loginReplace = null;
//        SipInfo.keepUserAlive.stopThread();
//        SipInfo.keepDevAlive.stopThread();
//        if(!(GroupInfo.wakeLock==null)){
//            GroupInfo.wakeLock.release();}
//        GroupInfo.rtpAudio.removeParticipant();
//        GroupInfo.groupUdpThread.stopThread();
//        GroupInfo.groupKeepAlive.stopThread();
//        SipInfo.userLogined = false;
//        SipInfo.devLogined = false;
//        //停止PPT监听服务
//        stopService(new Intent(this, PTTService.class));
        //关闭监听服务
        stopService(new Intent(HomeActivity.this, NewsService.class));
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
     * @param event event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DevLoginFailEvent event) {
        mBaseHandler.removeMessages(DevHeartBeatHelper.MSG_HEART_BEAR_VALUE);
        devLoginFailed = true;
    }

    /**
     * 用户Sip服务重新注册事件
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
     * @param event event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UnauthorizedEvent event) {
        mBaseHandler.removeMessages(UserHeartBeatHelper.MSG_HEART_BEAR_VALUE);
        userLoginFailed = true;
    }

    /**
     * 用户Sip服务注册成功事件
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

    }

}
