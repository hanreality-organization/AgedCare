package com.punuo.sys.app.agedcare.ui;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.service.NewsService;
import com.punuo.sys.app.agedcare.service.PTTService;
import com.punuo.sys.app.agedcare.sip.BodyFactory;
import com.punuo.sys.app.agedcare.sip.SipInfo;
import com.punuo.sys.app.agedcare.sip.SipMessageFactory;
import com.punuo.sys.app.router.CompatRouter;
import com.punuo.sys.app.router.HomeRouter;
import com.punuo.sys.sdk.account.UserInfoManager;
import com.punuo.sys.sdk.activity.BaseActivity;
import com.punuo.sys.sdk.task.ImageTask;

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
import static com.punuo.sys.app.agedcare.sip.SipInfo.sipUser;

@Route(path = HomeRouter.ROUTER_HOME_ACTIVITY)
public class HomeActivity extends BaseActivity implements ViewTreeObserver.OnGlobalLayoutListener {
    public CountDownTimer countDownTimer;
    private LinearLayout ll_item;//灰点所在的线性布局
    private ImageView blue_iv;//小蓝点
    int position;//当前界面数（从0开始）
    private int pointWidth;//小灰点的距离
    private Bitmap bitmap;
    public static String apkPath;

    private boolean mLayoutComplete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        apkPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/";

        UserInfoManager.getInstance().refreshUserInfo();
        ll_item = findViewById(R.id.ll_item);
        blue_iv = findViewById(R.id.blue_iv);
        //将图片的引用转化为图片控件存在List的集合中
        for (int i = 0; i < 2; i++) {
            ImageView points = new ImageView(this);
            points.setImageResource(R.drawable.grey_point);
            LinearLayout.LayoutParams lllp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            if (i > 0) {
                lllp.leftMargin = 30;
            }
            points.setLayoutParams(lllp);
            ll_item.addView(points);
        }
        blue_iv.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //获取小灰点圆心间的距离，第1个灰点和第二个灰点的距离
                pointWidth = ll_item.getChildAt(1).getLeft() - ll_item.getChildAt(0).getLeft();
            }
        });
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new MemberFragment());
        fragments.add(new MenuFragment());
        FragAdapter adapter = new FragAdapter(getSupportFragmentManager(), fragments);
        ViewPager vp = findViewById(R.id.viewpager);
        vp.setAdapter(adapter);
        vp.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            //当前选中第几个界面
            public void onPageSelected(int arg0) {
                position = arg0;
            }

            /**
             * 界面滑动时回调此方法
             * arg0:当前界面数
             * arg1:界面滑动过的百分数（0.0-1.0）
             * arg2:当前界面偏移的像素位置
             */
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                int width;//小蓝点当前滑动距离
                width = (int) (arg1 * pointWidth + arg0 * pointWidth);//1个界面就要一个小灰点的距离，再加上滑动过的百分比距离就是当前蓝点的位置
                LayoutParams rllp = (LayoutParams) blue_iv.getLayoutParams();//拿到蓝点所在布局的形状
                rllp.leftMargin = width;//设置蓝点的左外边距
                blue_iv.setLayoutParams(rllp);//将设置好的形状设置给蓝点
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        init();
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
        SipInfo.loginReplace = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(android.os.Message msg) {
                sipUser.sendMessage(SipMessageFactory.createNotifyRequest(sipUser, SipInfo.user_to,
                        SipInfo.user_from, BodyFactory.createLogoutBody()));
                SipInfo.sipDev.sendMessage(SipMessageFactory.createNotifyRequest(SipInfo.sipDev, SipInfo.dev_to,
                        SipInfo.dev_from, BodyFactory.createLogoutBody()));
                //关闭监听服务
                stopService(new Intent(HomeActivity.this, NewsService.class));
                //关闭PTT监听服务
                stopService(new Intent(HomeActivity.this, PTTService.class));
                //关闭用户心跳
                SipInfo.keepUserAlive.stopThread();
                //关闭设备心跳
                SipInfo.keepDevAlive.stopThread();
                //重置登录状态
                SipInfo.userLogined = false;
                SipInfo.devLogined = false;
                AlertDialog loginReplace = new AlertDialog.Builder(getApplicationContext())
                        .setTitle("账号异地登录")
                        .setMessage("请重新登录")
                        .setPositiveButton("确定", null)
                        .create();
                loginReplace.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                loginReplace.show();
                loginReplace.setCancelable(false);
                loginReplace.setCanceledOnTouchOutside(false);
                ARouter.getInstance().build(CompatRouter.ROUTER_LOGIN_ACTIVITY).navigation();
                return false;
            }
        });
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

    public class FragAdapter extends FragmentPagerAdapter {

        private List<Fragment> mFragments;

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
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //有按下动作时取消定时
                if (countDownTimer != null) {
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
            countDownTimer.start();
        } else {
            countDownTimer.start();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
//        当activity不在前台是停止定时
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    public void onGlobalLayout() {

        if (!mLayoutComplete)
            return;
        onNavigationBarStatusChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SipInfo.loginReplace = null;
        SipInfo.keepUserAlive.stopThread();
        SipInfo.keepDevAlive.stopThread();
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
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    protected void onNavigationBarStatusChanged() {
        // 子类重写该方法，实现自己的逻辑即可。
        hideBottomUIMenu();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startAD();
    }


    public void onBackPressed() {
        //屏蔽返回键
    }

    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY /*| View.SYSTEM_UI_FLAG_FULLSCREEN*/;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }


}
