package com.punuo.sys.app.agedcare.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.R2;
import com.punuo.sys.app.agedcare.adapter.ClusterAdapter;
import com.punuo.sys.app.agedcare.groupvoice.GroupInfo;
import com.punuo.sys.app.agedcare.groupvoice.GroupKeepAlive;
import com.punuo.sys.app.agedcare.groupvoice.GroupUdpThread;
import com.punuo.sys.app.agedcare.groupvoice.RtpAudio;
import com.punuo.sys.app.agedcare.service.PTTService;
import com.punuo.sys.app.agedcare.sip.BodyFactory;
import com.punuo.sys.app.agedcare.sip.SipInfo;
import com.punuo.sys.app.agedcare.sip.SipMessageFactory;
import com.punuo.sys.app.agedcare.sip.SipUser;
import com.punuo.sys.app.agedcare.tools.ActivityCollector;

import org.zoolu.sip.message.Message;

import java.io.IOException;
import java.net.SocketException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.punuo.sys.app.agedcare.sip.SipInfo.devices;
import static com.punuo.sys.app.agedcare.sip.SipInfo.qinliaouseridList;
import static com.punuo.sys.app.agedcare.sip.SipInfo.sipUser;
import static com.punuo.sys.app.agedcare.sip.SipInfo.url;


/**
 * Author chzjy
 * Date 2016/12/19.
 * 集群呼叫频道更换
 */
public class ChsChange extends HindebarActivity implements SipUser.QinliaoUpdateListener, ViewTreeObserver.OnGlobalLayoutListener {

    @BindView(R2.id.icon1)
    ImageView icon1;
    @BindView(R2.id.icon2)
    ImageView icon2;
    @BindView(R2.id.icon3)
    ImageView icon3;
    @BindView(R2.id.icon4)
    ImageView icon4;
    @BindView(R2.id.icon5)
    ImageView icon5;
    @BindView(R2.id.icon6)
    ImageView icon6;
    @BindView(R2.id.icon7)
    ImageView icon7;
    @BindView(R2.id.icon8)
    ImageView icon8;
    @BindView(R2.id.cancle)
    Button button;
    ImageView[] icons;
    int i = 0;
    FrameLayout content;
    private Context context;
    private boolean mLayoutComplete = false;
    private static final String TAG = "ChsChange";
    public static String ip = "http://101.69.255.134:8000/static/xiaoyupeihu/";

    static String getuserId;

    public static ClusterAdapter clusterAdapter;

    //    public Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            new Thread() {
//                @Override
//                public void run() {
//                    try {
//                        GroupInfo.rtpAudio.changeParticipant(SipInfo.serverIp, GroupInfo.port);
//                        GroupInfo.groupUdpThread = new GroupUdpThread(SipInfo.serverIp, GroupInfo.port);
//                        GroupInfo.groupUdpThread.startThread();
//                        GroupInfo.groupKeepAlive = new GroupKeepAlive();
//                        GroupInfo.groupKeepAlive.startThread();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } finally {
//
//                    }
//                }
//            }.start();
//        }
//    };
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case 0X222:
                    for (int i = 0; i < icons.length; i++) {
                        changeLight(icons[i], -80);
                    }
                    if (devices != null) {
                        for (i = 0; i < qinliaouseridList.size(); i++) {

                            String str = Integer.toString(i);

                            for (int j = 0; j < devices.size(); j++) {

                                Log.d("TTT", qinliaouseridList.get(i) + " " + devices.get(j).getUserid());

                                if (qinliaouseridList.get(i).equals(devices.get(j).getUserid())) {
                                    Log.d("TTTTA", str);
                                    changeLight(icons[j], 80);
                                }

                            }
                        }
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chschange);
        content = (FrameLayout) findViewById(android.R.id.content);
        content.post(new Runnable() {
            @Override
            public void run() {
                mLayoutComplete = true;
            }
        });
        content.getViewTreeObserver().addOnGlobalLayoutListener(this);
        ActivityCollector.addActivity(this);
        ButterKnife.bind(this);
        icons = new ImageView[]{icon1, icon2, icon3, icon4, icon5, icon6, icon7, icon8};
        SipInfo.sipUser.setQinliaoUpdateListener(this);
        org.zoolu.sip.message.Message online = SipMessageFactory.createNotifyRequest(
                sipUser, SipInfo.user_to, SipInfo.user_from, BodyFactory.createOnlineNotify(SipInfo.userId));
        sipUser.sendMessage(online);
        Log.e("task", "onCreate: " + getTaskId());
        showicon();


        Thread groupVoice = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GroupInfo.rtpAudio = new RtpAudio(SipInfo.serverIp, GroupInfo.port);
                    GroupInfo.groupUdpThread = new GroupUdpThread(SipInfo.serverIp, GroupInfo.port);
                    GroupInfo.groupUdpThread.startThread();
                    GroupInfo.groupKeepAlive = new GroupKeepAlive();
                    GroupInfo.groupKeepAlive.startThread();
                    Intent PTTIntent = new Intent(ChsChange.this, PTTService.class);
                    startService(PTTIntent);
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, "groupVoice");
        groupVoice.start();
        try {
            groupVoice.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        MyToast.show(context, "正在说话...", Toast.LENGTH_LONG);

//        MyToast.show(context, "正在说话...", Toast.LENGTH_LONG);
//        if (GroupInfo.rtpAudio != null) {
//            System.out.println(111);
////                       GroupInfo.rtpAudio.pttChanged(true);
//            waitFor();
//            GroupSignaling groupSignaling = new GroupSignaling();
//            groupSignaling.setStart(SipInfo.devId);
//            groupSignaling.setLevel("1");
//            String start = JSON.toJSONString(groupSignaling);
//            GroupInfo.groupUdpThread.sendMsg(start.getBytes());
//        }
        Intent intent = new Intent("android.intent.action.GLOBAL_BUTTON");
        KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, 261);
        intent.putExtra("android.intent.extra.KEY_EVENT", keyEvent);
        sendBroadcast(intent);
        Log.d(TAG, "aaaa");

    }

    protected void showicon() {
        if (devices != null) {
            for (i = 0; i < devices.size(); i++) {
                final int a = i;
                Glide.with(this).load(url[a])
                        .apply(new RequestOptions().override(150, 150))
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                changeLight(icons[a], -80);
                                return false;
                            }
                        })
                        .into(icons[a]);
            }
        }
    }

    private void waitFor() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
        if (!(GroupInfo.wakeLock == null)) {
            GroupInfo.wakeLock.release();
        }
        GroupInfo.rtpAudio.removeParticipant();
        GroupInfo.groupUdpThread.stopThread();
        GroupInfo.groupKeepAlive.stopThread();
        SipInfo.userLogined = false;
        SipInfo.devLogined = false;
        //停止PPT监听服务
        stopService(new Intent(this, PTTService.class));
        content.getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    @OnClick({R2.id.cancle})
    public void onClick(View view) {
        if (view.getId() == R.id.cancle) {
            Message offline = SipMessageFactory.createNotifyRequest(
                    sipUser, SipInfo.user_to, SipInfo.user_from, BodyFactory.createOfflineNotify(SipInfo.userId));
            sipUser.sendMessage(offline);
            finish();
        }
    }

    public void onGlobalLayout() {

        if (!mLayoutComplete)
            return;
        onNavigationBarStatusChanged();
    }

    public void onNavigationBarStatusChanged() {
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

    @Override
    public void iconUpdate() {

        handler.sendEmptyMessage(0X222);
//        Log.d("TTTT",Thread.currentThread().toString());
//        for (i = 0; i < devices.size(); i++) {
//            final int a = i;
//            if (qinliaouserid.equals(devices.get(a).getUserid())) {
//                changeLight(icons[a], 40);
//            } else {
//                changeLight(icons[a], -40);
//            }
//        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)

    //改变图片的亮度方法 0--原样 >0---调亮 <0---调暗
    public void changeLight(ImageView imageView, int brightness) {
        ColorMatrix cMatrix = new ColorMatrix();
        cMatrix.set(new float[]{1, 0, 0, 0, brightness, 0, 1, 0, 0, brightness, // 改变亮度
                0, 0, 1, 0, brightness, 0, 0, 0, 1, 0});
        imageView.setColorFilter(new ColorMatrixColorFilter(cMatrix));
    }
}

//        public Bitmap handleImage(Bitmap bm, int saturation, int hue, int lum) {
//            Bitmap bmp = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(bmp);
//            Paint paint = new Paint();
//            paint.setAntiAlias(true);
//            ColorMatrix mLightnessMatrix = new ColorMatrix();
//            ColorMatrix mSaturationMatrix = new ColorMatrix();
//            ColorMatrix mHueMatrix = new ColorMatrix();
//            ColorMatrix mAllMatrix = new ColorMatrix();
//            float mSaturationValue = saturation * 1.0F / 127;
//            float mHueValue = hue * 1.0F / 127;
//            float mLumValue = (lum - 127) * 1.0F / 127 * 180;
//            mHueMatrix.reset();
//            mHueMatrix.setScale(mHueValue, mHueValue, mHueValue, 1);
//
//            mSaturationMatrix.reset();
//            mSaturationMatrix.setSaturation(mSaturationValue);
//            mLightnessMatrix.reset();
//
//            mLightnessMatrix.setRotate(0, mLumValue);
//            mLightnessMatrix.setRotate(1, mLumValue);
//            mLightnessMatrix.setRotate(2, mLumValue);
//
//            mAllMatrix.reset();
//            mAllMatrix.postConcat(mHueMatrix);
//            mAllMatrix.postConcat(mSaturationMatrix);
//            mAllMatrix.postConcat(mLightnessMatrix);
//
//            paint.setColorFilter(new ColorMatrixColorFilter(mAllMatrix));
//            canvas.drawBitmap(bm, 0, 0, paint);
//            return bmp;
//        }

/**
 * 暖意特效
 *
 * @param bmp     原图片
 * @param centerX 光源横坐标
 * @param centerY 光源纵坐标
 * @return 暖意特效图片
 */
//        public Bitmap warmthFilter(Bitmap bmp, int centerX, int centerY) {
//            final int width = bmp.getWidth();
//            final int height = bmp.getHeight();
//            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
//
//            int pixR = 0;
//            int pixG = 0;
//            int pixB = 0;
//
//            int pixColor = 0;
//
//            int newR = 0;
//            int newG = 0;
//            int newB = 0;
//            int radius = Math.min(centerX, centerY);
//
//            final float strength = 150F; // 光照强度 100~150
//            int[] pixels = new int[width * height];
//            bmp.getPixels(pixels, 0, width, 0, 0, width, height);
//            int pos = 0;
//            for (int i = 1, length = height - 1; i < length; i++) {
//                for (int k = 1, len = width - 1; k < len; k++) {
//                    pos = i * width + k;
//                    pixColor = pixels[pos];
//
//                    pixR = Color.red(pixColor);
//                    pixG = Color.green(pixColor);
//                    pixB = Color.blue(pixColor);
//
//                    newR = pixR;
//                    newG = pixG;
//                    newB = pixB;
//
//                    // 计算当前点到光照中心的距离，平面座标系中求两点之间的距离
//                    int distance = (int) (Math.pow((centerY - i), 2) + Math.pow(centerX - k, 2));
//                    if (distance < radius * radius) {
//                        // 按照距离大小计算增加的光照值
//                        int result = (int) (strength * (1.0 - Math.sqrt(distance) / radius));
//                        newR = pixR + result;
//                        newG = pixG + result;
//                        newB = pixB + result;
//                    }
//
//                    newR = Math.min(255, Math.max(0, newR));
//                    newG = Math.min(255, Math.max(0, newG));
//                    newB = Math.min(255, Math.max(0, newB));
//
//                    pixels[pos] = Color.argb(255, newR, newG, newB);
//                }
//            }
//
//            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
//            return bitmap;
//        }


