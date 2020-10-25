package com.punuo.sys.app.agedcare.ui;


import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.app.AlertDialog;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;

import android.provider.MediaStore;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.service.NewsService;
import com.punuo.sys.app.agedcare.service.PTTService;
import com.punuo.sys.app.agedcare.sip.BodyFactory;
import com.punuo.sys.app.agedcare.sip.SipDev;
import com.punuo.sys.app.agedcare.sip.SipInfo;
import com.punuo.sys.app.agedcare.sip.SipMessageFactory;
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



public class MenuActivity extends FragmentActivity implements ViewTreeObserver.OnGlobalLayoutListener, SipDev.ScreenproUpdateListener{
    public CountDownTimer countDownTimer;


    private LinearLayout ll_item;//灰点所在的线性布局
    private ImageView blue_iv;//小蓝点
    int position;//当前界面数（从0开始）
    private int pointWidth;//小灰点的距离
//    private List<ImageView> list;//存放图片控件的List集合
//    List<Device> devices;
    Bitmap bitmap;
    public static String apkPath;

    FrameLayout content;
    private boolean mLayoutComplete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//设置App不显示菜单
        //注意这两句代码的顺序，上面一句写在下面一句后面会报错
        setContentView(R.layout.activity_menu);
       setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


//        apkPath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/AgedCare/picture/";
        apkPath=Environment.getExternalStorageDirectory().getAbsolutePath()+"/Pictures/";
        Log.d("apkTH",  apkPath);
        try {
            SipInfo.sipDev.setScreenproUpdateListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        hideBottomUIMenu();
        content = (FrameLayout) findViewById(android.R.id.content);
        content.post(new Runnable() {
            @Override
            public void run() {
                mLayoutComplete = true;

            }
        });
        content.getViewTreeObserver().addOnGlobalLayoutListener(this);

        //list = new ArrayList<ImageView>();
        ll_item = (LinearLayout) findViewById(R.id.ll_item);
        blue_iv = (ImageView) findViewById(R.id.blue_iv);
        //将图片的引用转化为图片控件存在List的集合中
        for(int i=0;i<2;i++){
            //ViewPager viewPager=new ViewPager(this);
            //ImageView imageView = new ImageView(this);
            //imageView.setImageResource(images[i]);//将相应的图片设置到IamageView
            //imageView.setScaleType(ScaleType.FIT_XY);//设置图片的拉伸方式为充满
           // list.add(imageView);
            //绘制小灰点儿，有几个界面就绘制几个
            ImageView points = new ImageView(this);
            points.setImageResource(R.drawable.grey_point);//通过shape文件绘制好灰点
            //给第一个以外的小灰点儿设置左边距，保证三个灰点水平居中
            LinearLayout.LayoutParams lllp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);//拿到灰点所处的线性布局一样的形状（一些距离属性）
            if(i>0)
                lllp.leftMargin = 30;//设置左外边距，像素
            points.setLayoutParams(lllp);//把设置好左外边距的形状设置给灰点
            ll_item.addView(points);//将灰点加入线性布局
        }
        //为了完成蓝点在界面滑动时的动画效果，必须获取到灰点的边距，通过动态的给蓝点设置边距来完成动画效果
        //由于在执行onCreate方法时，界面还没有绘制完成，无法获取pointWidth，设定小蓝点绘制完成的事件监听，当小蓝点绘制完成再获取
        blue_iv.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //获取小灰点圆心间的距离，第1个灰点和第二个灰点的距离
                pointWidth = ll_item.getChildAt(1).getLeft()-ll_item.getChildAt(0).getLeft();
            }
        });
        List<Fragment> fragments=new ArrayList<>();
        fragments.add(new MemberFragment());
        fragments.add(new MenuFragment());
        //fragments.add(new Fragment3());
        FragAdapter adapter = new FragAdapter(getSupportFragmentManager(), fragments);

        //设定适配器
        ViewPager vp = (ViewPager)findViewById(R.id.viewpager);
        vp.setAdapter(adapter);
        //VPAdapter vpAdapter = new VPAdapter();//创建适配器
        //viewPager.setAdapter(vpAdapter);//ViewPager加载适配器
        //为ViewPager设定监听器，界面是滑动时让蓝点也跟着动
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
                width = (int) (arg1*pointWidth+arg0*pointWidth);//1个界面就要一个小灰点的距离，再加上滑动过的百分比距离就是当前蓝点的位置
                LayoutParams rllp= (LayoutParams) blue_iv.getLayoutParams();//拿到蓝点所在布局的形状
                rllp.leftMargin=width;//设置蓝点的左外边距
                blue_iv.setLayoutParams(rllp);//将设置好的形状设置给蓝点
                //开始体验按钮只能出现在最后一页，并且在滑动的过程中保持消失，这样效果更好，不信可以把后面的判断删去，在最后一页回移的时候，按钮先会跟着移动，然后突然就不见了
                /*
                if(position==images.length-1&&arg1==0)
                    btn.setVisibility(View.VISIBLE);
                else
                    btn.setVisibility(View.INVISIBLE);
                    */
            }
            //状态改变时调用：arg0=0还没滑动,arg0=1正在滑动,arg0=2滑动完毕
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        init();
    }

    public void screenUpdate()
    {

//        new Task().execute(shareurl);
        bitmap=GetImageInputStream(shareurl);
        Log.d("picture111",shareurl);
//        SavaImage(bitmap, apkPath);
     saveImageToGallery(this,bitmap);
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
                stopService(new Intent(MenuActivity.this, NewsService.class));
                //关闭PTT监听服务
                stopService(new Intent(MenuActivity.this, PTTService.class));
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
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                return false;
            }
        });
    }
    public Bitmap GetImageInputStream(String imageurl){
        URL url;
        HttpURLConnection connection;
        Bitmap bitmap=null;
        try {
            url = new URL(imageurl);
            connection=(HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(6000); //超时设置
            connection.setDoInput(true);
            connection.setUseCaches(false); //设置不使用缓存
            InputStream inputStream=connection.getInputStream();
            bitmap=BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public  void saveImageToGallery(Context context, Bitmap bmp) {
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
        Log.d("lujin",apkPath+fileName);
        getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + "=?", new String[]{apkPath+fileName});//删除系统缩略图
      file.delete();
    }
    /**
     * 异步线程下载图片
     *
     */
//    class Task extends AsyncTask<String, Integer, Void>{
//
//        protected Void doInBackground(String... params) {
//            bitmap=GetImageInputStream((String)params[0]);
//            return bitmap.;
//        }
//
//        protected void onPostExecute(Void result) {
//            super.onPostExecute(result);
//            Message message=new Message();
//            message.what=0x123;
////            handler.sendMessage(message);
//        }
//
//    }



//    class VPAdapter extends PagerAdapter{
        //返回ViewPager中总页数
//        @Override
//        public int getCount() {
 //             return 2; //images.length;
 //       }
        //判断视图是否由对象生成
 //       @Override
  //      public boolean isViewFromObject(View view, Object object) {
  //          return view==object;
 //       }
 //       @Override
        /**
         * 返回将哪一个对象放在当前ViewPager中
         * container：每一页的父容器
         * position：当前页（从0开始）
         */
  //      public Object instantiateItem(ViewGroup container, int position) {
            //浪费资源，每次滑到新的页都会创建新的的ImageView，我们选择先把ImageView控件存在List集合中，再按需要获取
//          ImageView imageView = new ImageView(MenuActivity.this);
//          imageView.setImageResource(images[position]);
  //          ImageView imageView = list.get(position);
 //           container.addView(imageView);
  //          return imageView;
  //      }
  //      @Override
        /**
         * 从ViewPager中移除View对象
         */
  //      public void destroyItem(ViewGroup container, int position, Object object) {
  //          container.removeView((View) object);
   //     }
 //   }

        public class FragAdapter extends FragmentPagerAdapter {

            private List<Fragment> mFragments;

            public FragAdapter(FragmentManager fm, List<Fragment> fragments) {
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
      long advertisingTime = 40 * 1000;//定时跳转广告时间
        if (countDownTimer == null) {
            countDownTimer = new CountDownTimer(advertisingTime, 10000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onFinish() {
                    //定时完成后的操作
                    //跳转到广告页面
                    startActivity(new Intent(MenuActivity.this,BaseDispatchTouchActivity.class), ActivityOptions.makeSceneTransitionAnimation(MenuActivity.this).toBundle());
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
        if (countDownTimer != null){
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
        stopService(new Intent(MenuActivity.this, NewsService.class));
        if (countDownTimer != null){
            countDownTimer.cancel();
        }

        content.getViewTreeObserver().removeOnGlobalLayoutListener(this);
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
