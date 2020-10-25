package com.punuo.sys.app.agedcare.ui;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.tools.NetUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.punuo.sys.app.agedcare.sip.SipInfo.format;


public class EntertainmentActivity extends FragmentActivity implements ViewTreeObserver.OnGlobalLayoutListener{
    List<Fragment> fragments=new ArrayList<>();
    int position;//当前界面数（从0开始）
//    private int pointWidth;//小灰点的距离
    FrameLayout content;
    private Button back;
    private TextView tv_time;
    private TextView net;
    private static final int msgKey1 = 1;
    private boolean mLayoutComplete = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//设置App不显示菜单
        //注意这两句代码的顺序，上面一句写在下面一句后面会报错
        setContentView(R.layout.activity_entertainment);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        content = (FrameLayout) findViewById(android.R.id.content);
        content.post(new Runnable() {
            @Override
            public void run() {
                mLayoutComplete = true;

            }
        });
        content.getViewTreeObserver().addOnGlobalLayoutListener(this);

        for(int i=0;i<2;i++){

            ImageView points = new ImageView(this);
            points.setImageResource(R.drawable.grey_point);//通过shape文件绘制好灰点
            //给第一个以外的小灰点儿设置左边距，保证三个灰点水平居中
            LinearLayout.LayoutParams lllp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);//拿到灰点所处的线性布局一样的形状（一些距离属性）
            if(i>0)
                lllp.leftMargin = 30;//设置左外边距，像素
            points.setLayoutParams(lllp);//把设置好左外边距的形状设置给灰点
        }


        fragments.add(new MuiscPlay());
        fragments.add(new ShortMovieFragment());
        //fragments.add(new Fragment3());
        EntertainmentActivity.FragAdapter adapter = new FragAdapter(getSupportFragmentManager(), fragments);

        //设定适配器
        ViewPager vp = (ViewPager)findViewById(R.id.viewpager);
        vp.setAdapter(adapter);

        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            //当前选中第几个界面
            public void onPageSelected(int arg0) {
                position = arg0;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
//

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        tv_time = (TextView) findViewById(R.id.time);
        net=(TextView)findViewById(R.id.net);
        back=(Button)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("title","back");
               finish();

            }
        });
        new TimeThread().start();
        if(NetUtils.getNetWorkState(getApplicationContext())==NetUtils.NETWORK_MOBILE)
        {
            net.setText(R.string.fourG);
        }else if (NetUtils.getNetWorkState(getApplicationContext())==NetUtils.NETWORK_WIFI){
            net.setText(R.string.wifi);
        }else if (NetUtils.getNetWorkState(getApplicationContext())==NetUtils.NETWORK_NONE){
            net.setText("无网络");
        }else {
            net.setText("有线网");
        }
    }
    public class FragAdapter extends FragmentPagerAdapter {

        private List<Fragment> mFragments;

        FragAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);

            mFragments=fragments;
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
    public void onGlobalLayout() {

        if (!mLayoutComplete)
            return;
        onNavigationBarStatusChanged();
    }
    protected void onNavigationBarStatusChanged() {
        // 子类重写该方法，实现自己的逻辑即可。
        hideBottomUIMenu();
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

    public class TimeThread extends Thread{
        @Override
        public void run() {
            super.run();
            do{
                try {
                    Thread.sleep(100);
                    Message msg = new Message();
                    msg.what = msgKey1;
                    mHandler.sendMessage(msg);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }while (true);
        }
    }
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what){
                case msgKey1:
                    long time = System.currentTimeMillis();
                    Date date = new Date(time);
                    format = new SimpleDateFormat("aa hh:mm");
                    tv_time.setText(format.format(date));
                    break;
                default:
                    break;
            }
            return false;
        }
    });
    public boolean isWifiAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)this.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected() && networkInfo
                .getType() == ConnectivityManager.TYPE_WIFI);
    }

    public boolean is4GAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            TelephonyManager telephonyManager = (TelephonyManager)
                    this.getSystemService(
                            Context.TELEPHONY_SERVICE);
            int networkType = telephonyManager.getNetworkType();
            /** Current network is LTE */
            if (networkType == 13) {
                /**此时的网络是4G的*/
                return true;
            }
        }
        return false;
    }
    /**
     * 判断以太网网络是否可用

     */
    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ethNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
//        NetworkInfo wifiNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);


        if (ethNetInfo != null && ethNetInfo.isConnected()) {
            return true;
        } else  {
            return false;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        content.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        fragments.clear();

    }
}
