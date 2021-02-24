package com.punuo.sys.app.agedcare.ui;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.punuo.sip.dev.event.StartVideoEvent;
import com.punuo.sip.dev.event.StopVideoEvent;
import com.punuo.sip.user.SipUserManager;
import com.punuo.sip.user.request.SipByeRequest;
import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.R2;
import com.punuo.sys.app.agedcare.sip.SipInfo;
import com.punuo.sys.app.agedcare.tools.H264decoder;
import com.punuo.sys.app.agedcare.video.H264SendingManager;
import com.punuo.sys.app.agedcare.video.VideoInfo;
import com.punuo.sys.app.router.HomeRouter;
import com.punuo.sys.sdk.account.AccountManager;
import com.punuo.sys.sdk.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.punuo.sys.app.agedcare.sip.SipInfo.isanswering;

/**
 * 视频聊天
 */
@Route(path = HomeRouter.ROUTER_VIDEO_CALL_ACTIVITY)
public class VideoCallActivity extends AppCompatActivity {
    public static final String TAG = "VideoCallActivity";
    private SurfaceHolder shBack;
    private int getNum = 0;
    Timer timer = new Timer();
    private H264decoder h264decoder;
    AlertDialog dialog;
    @BindView(R2.id.sv_back)
    SurfaceView svBack;
    @BindView(R2.id.sv_front)
    SurfaceView svFront;
    @BindView(R2.id.video_back)
    Button video_back;
    int time = 0;
    H264SendingManager sendingManager;
    public static final String BROADCAST_ACTION = "BROADCAST_ACTION";
    IntentFilter imIntentFilter;
    LocalBroadcastManager mManager;
    BroadcastReceiver mReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);
        ButterKnife.bind(this);
        shBack = svBack.getHolder();
        EventBus.getDefault().register(this);
        svFront.setZOrderOnTop(true);
        svFront.setZOrderMediaOverlay(true);
        sendingManager = new H264SendingManager(svFront);
        sendingManager.init();
        h264decoder = new H264decoder();
        playVideo();
        timer.schedule(task, 0, 5000);

        imIntentFilter = new IntentFilter();

        imIntentFilter.addAction(BROADCAST_ACTION);
        mManager = LocalBroadcastManager.getInstance(VideoCallActivity.this);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                VideoCallActivity.this.finish();
            }
        };
        video_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeVideo();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        mManager.registerReceiver(mReceiver, imIntentFilter);
    }


    @Override
    protected void onPause() {
        super.onPause();
        mManager.unregisterReceiver(mReceiver);
    }

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            if (VideoInfo.isrec == 0) {
                if (time == 6) {
                    closeVideo();
                    time = 0;
                } else {
                    ToastUtils.showToast("未收到消息!");
                    time++;
                }
            } else if (VideoInfo.isrec == 2) {
                VideoInfo.isrec = 0;
                time = 0;
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
        if (dialog != null) {
            dialog.dismiss();
        }
        VideoInfo.isrec = 1;
        SipInfo.decoding = false;
        VideoInfo.rtpVideo.removeParticipant();
        VideoInfo.sendActivePacket.stopThread();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        VideoInfo.rtpVideo.endSession();
        VideoInfo.track.stop();
        EventBus.getDefault().unregister(this);
        sendingManager.deInit();
        System.gc();//系统垃圾回收
        isanswering = false;
    }

    private void playVideo() {
        new Thread(Video).start();
        EventBus.getDefault().post(new StartVideoEvent());
    }

    Runnable Video = new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Surface surface = shBack.getSurface();
            System.out.println(surface);

            if (surface != null) {
                h264decoder.initDecoder(surface);
                while (SipInfo.decoding) {
                    if (SipInfo.isNetworkConnected) {
                        byte[] nal = VideoInfo.nalBuffers[getNum].getReadableNalBuf();
                        if (nal != null) {
                            Log.i(TAG, "nalLen:" + nal.length);
                            try {
                                //硬解码
                                h264decoder.onFrame(nal, 0, nal.length);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        VideoInfo.nalBuffers[getNum].readLock();
                        VideoInfo.nalBuffers[getNum].cleanNalBuf();
                        getNum++;
                        if (getNum == 200) {
                            getNum = 0;
                        }
                    }
                }
            }
        }
    };

    @Override
    public void onBackPressed() {
        dialog = new AlertDialog.Builder(this)
                .setTitle("是否结束聊天?")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        closeVideo();
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
    }

    private void closeVideo() {
        SipByeRequest byeRequest = new SipByeRequest(AccountManager.getTargetDevId());
        SipUserManager.getInstance().addRequest(byeRequest);
        isanswering = false;
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(StopVideoEvent event) {
        VideoInfo.endView = true;
        closeVideo();
    }
}
