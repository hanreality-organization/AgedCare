package com.punuo.sys.app.agedcare.ui;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.transition.Fade;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.LoopPagerAdapter;
import com.jude.rollviewpager.hintview.ColorPointHintView;
import com.punuo.sys.app.agedcare.R;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import static com.punuo.sys.app.agedcare.sip.SipInfo.pictureList;
import static com.punuo.sys.app.agedcare.ui.MenuActivity.apkPath;



public class BaseDispatchTouchActivity extends HindebarActivity {
    private RollPagerView mRollViewPager;
    private ArrayList<String> images=new ArrayList<String>();//图片地址
    private TestNormalAdapter testNormalAdapter;
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window window = getWindow();
        //全屏
        WindowManager.LayoutParams winParams = window.getAttributes();
        winParams.flags = winParams.flags | WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //屏幕长亮
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_dispatch_touch);

        getWindow().setEnterTransition(new Fade().setDuration(3000));
        getWindow().setExitTransition(new Fade().setDuration(2000));
//      getImagePathFromSD();
//        getlocalurl();

        EventBus.getDefault().register(this);

        mRollViewPager = (RollPagerView) findViewById(R.id.roll_view_pager);
        //设置播放时间间隔
        mRollViewPager.setPlayDelay(5000);
        //设置透明度
        mRollViewPager.setAnimationDurtion(500);
        //设置适配器
        testNormalAdapter=new TestNormalAdapter(BaseDispatchTouchActivity.this,mRollViewPager);
        mRollViewPager.setAdapter(testNormalAdapter);
        startGetImageThread();
        //设置指示器（顺序依次）
        //自定义指示器图片
        //设置圆点指示器颜色
        //设置文字指示器
        //隐藏指示器
        //mRollViewPager.setHintView(new IconHintView(this, R.drawable.point_focus, R.drawable.point_normal));
        mRollViewPager.setHintView(new ColorPointHintView(this, Color.YELLOW,Color.WHITE));
        //mRollViewPager.setHintView(new TextHintView(this));
        mRollViewPager.setHintView(null);
//       adapter.addAll(getImagePathFromSD());

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if(event.getMessage().equals("视频开始")) {
            // 更新界面
            finish();
        }else if (event.getMessage().equals("等待通话"))
        {
            finish();
        }
    }
    private class TestNormalAdapter extends LoopPagerAdapter {



        private  Context mContext;
       TestNormalAdapter(Context mContext,RollPagerView viewPager) {
            super(viewPager);

            this.mContext = mContext;
        }


//        public TestNormalAdapter(RollPagerView viewPager) {
//
//            super(viewPager);
//        }
        @Override
        public View getView(ViewGroup container, int position) {
          ImageView view = new ImageView(container.getContext());
//             view.setImageResource(image[position]);
               view.setScaleType(ImageView.ScaleType.CENTER_CROP);
                view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                Glide.with(mContext).load(images.get(position)).into(view);
//               Log.d("xiangce11", localurl[position].toString());


                return view;

        }
        @Override
        public int getRealCount() {
           return images.size();
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            //按下退出屏保
            case MotionEvent.ACTION_DOWN:
                finish();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }


    public void getlocalurl()
    {
        File file=new File(apkPath);
        if (file.exists() && file.isDirectory()) {
            if(file.list().length > 0) {
                //Not empty, do something here.

                for (int i=0;i<getImagePathFromSD().size();i++) {
//                    final int a=i;

                    pictureList.add("file://"+getImagePathFromSD().get(i));
                    Log.d("xiangce","file://"+getImagePathFromSD().get(i));
                    Log.d("xiangce111",pictureList.get(i));
                }
            }
        }

    }
    public  List<String> getImagePathFromSD() {
        // 图片列表
        List<String> imagePathList = new ArrayList<String>();
        // 得到sd卡内image文件夹的路径   File.separator(/)
//        String filePath = Environment.getExternalStorageDirectory().toString() + File.separator
//                + "image";
        // 得到该路径文件夹下所有的文件
        File fileAll = new File(apkPath);
        if(!fileAll.exists()){
            fileAll.mkdir();
        }
        File[] files = fileAll.listFiles();
        // 将所有的文件存入ArrayList中,并过滤所有图片格式的文件
        if (files!=null) {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (checkIsImageFile(file.getPath())) {
                    imagePathList.add(file.getPath());
                }
            }
        }
        // 返回得到的图片列表
        return imagePathList;
    }

    /**
     * 检查扩展名，得到图片格式的文件

     */
    @SuppressLint("DefaultLocale")
    private  boolean checkIsImageFile(String fName) {
        boolean isImageFile ;
        // 获取扩展名
        String FileEnd = fName.substring(fName.lastIndexOf(".") + 1,
                fName.length()).toLowerCase();
        if (FileEnd.equals("jpg") || FileEnd.equals("png") || FileEnd.equals("gif")
                || FileEnd.equals("jpeg")|| FileEnd.equals("bmp") ) {
            isImageFile = true;
        } else {
            isImageFile = false;
        }
        return isImageFile;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        images.clear();
        EventBus.getDefault().unregister(this);
    }
    public void startGetImageThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver contentResolver = getContentResolver();
                //获取jpeg和png格式的文件，并且按照时间进行倒序
                Cursor cursor = contentResolver.query(uri, null, MediaStore.Images.Media.MIME_TYPE + "=\"image/jpeg\" or " +
                        MediaStore.Images.Media.MIME_TYPE + "=\"image/png\"", null, MediaStore.Images.Media.DATE_MODIFIED + " desc");
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        String realPath=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        String path = "file://" +cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        Log.d("pingbao",path);
                        images.add(path);
                        testNormalAdapter.notifyDataSetChanged();
                    }

                    cursor.close();
                }

            }

        }).start();

    }
}
