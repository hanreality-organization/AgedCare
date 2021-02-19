package com.punuo.sys.app.agedcare.ui;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.R2;
import com.punuo.sys.app.agedcare.sip.SipInfo;
import com.punuo.sys.app.agedcare.sip.SipMessageFactory;
import com.punuo.sys.app.agedcare.sip.SipUser;
import com.punuo.sys.app.agedcare.tools.H264decoder;
import com.punuo.sys.app.agedcare.video.H264SendingManager;
import com.punuo.sys.app.agedcare.video.VideoInfo;
import com.punuo.sys.sdk.event.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.zoolu.sip.message.Message;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.punuo.sys.app.agedcare.sip.SipInfo.isanswering;
import static com.punuo.sys.app.agedcare.sip.SipInfo.toDev;

/**
 * 视频聊天
 */
public class VideoCallActivity extends HindebarActivity implements SipUser.StopMonitor {
    public static final String TAG = "VideoCallActivity";
    private SurfaceHolder shBack;
    private int getNum = 0;
    private boolean changescreen=false;
    BufferedOutputStream outputStream;
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
//    UdpReceiveThread udpReceiveThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);
        hideNavigationBar();
        ButterKnife.bind(this);
        SipInfo.sipUser.setMonitor(this);
        shBack = svBack.getHolder();
        EventBus.getDefault().register(this);
        //shFront.setFormat(PixelFormat.TRANSPARENT);
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

        File f = new File(Environment.getExternalStorageDirectory(), "DCIM/video_decoded2.264");
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
//        udpReceiveThread.start();
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
                    new Handler(VideoCallActivity.this.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(VideoCallActivity.this, "未收到消息!", Toast.LENGTH_SHORT).show();

                        }
                    });
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
        EventBus.getDefault().post(new MessageEvent("视频开始"));
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
//                                  outputStream.write(nal);
                                  Log.i("AvcDecoder", "outputStream initialized");
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
        Message bye = SipMessageFactory.createByeRequest(SipInfo.sipUser, toDev, SipInfo.user_from);
        //创建结束视频请求
        SipInfo.sipUser.sendMessage(bye);
        isanswering = false;
        Log.e(TAG,"videoclose");
        finish();
    }

    @Override
    public void stopVideo() {
        closeVideo();
    }
    //实在去不掉用这个
    public void hideNavigationBar() {
        int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN; // hide status bar

        if (android.os.Build.VERSION.SDK_INT >= 19) {
            uiFlags |= View.SYSTEM_UI_FLAG_IMMERSIVE;//0x00001000; // SYSTEM_UI_FLAG_IMMERSIVE_STICKY: hide
        } else {
            uiFlags |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
        }

        try {
            getWindow().getDecorView().setSystemUiVisibility(uiFlags);
        } catch (Exception e) {
            // TODO: handle exception
        }

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        switch (event.getMessage())
        {
            case "callstart":
                Log.e(TAG,"等待通话");
                closeVideo();
                break;
            default:
                break;
        }

    }
//    public class UdpReceiveThread extends Thread {
//        private final String TAG = "UdpReceiveThread";
//
//        @Override
//        public void run() {
//            while (isAlive() ) {
//                try {
//                    sleep(1000);
//                    DatagramSocket socket = new DatagramSocket(VideoInfo.rtpPort); //建立 socket，其中 8888 为端口号
//                    byte data[] = new byte[1024];
//                    DatagramPacket packet = new DatagramPacket(data, data.length);
//                    socket.receive(packet);
//                    String result = new String(packet.getData(), packet.getOffset(), packet.getLength()); //packet 转换
//                    Log.e(TAG, "UDP result: " + result);
//                    socket.close();
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    break; //当 catch 到错误时，跳出循环
//                }
//            }
//        }
//    }

}
