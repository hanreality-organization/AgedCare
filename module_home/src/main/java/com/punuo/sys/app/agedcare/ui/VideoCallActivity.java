package com.punuo.sys.app.agedcare.ui;


import android.graphics.ImageFormat;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.glumes.ezcamerakit.CameraKitListener;
import com.glumes.ezcamerakit.EzCamera;
import com.glumes.ezcamerakit.EzCameraKit;
import com.glumes.ezcamerakit.RequestOptions;
import com.glumes.ezcamerakit.base.AspectRatio;
import com.punuo.sip.H264Config;
import com.punuo.sip.dev.H264ConfigDev;
import com.punuo.sip.dev.event.StartVideoEvent;
import com.punuo.sip.dev.event.StopVideoEvent;
import com.punuo.sip.user.H264ConfigUser;
import com.punuo.sip.user.SipUserManager;
import com.punuo.sip.user.request.SipByeRequest;
import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.R2;
import com.punuo.sys.app.agedcare.audio.AudioRecordManager;
import com.punuo.sys.app.agedcare.audio.VoiceEncoderThread;
import com.punuo.sys.app.agedcare.tools.CheckFrameTask;
import com.punuo.sys.app.agedcare.tools.H264VideoEncoder;
import com.punuo.sys.app.agedcare.video.RTPVideoReceiveImp;
import com.punuo.sys.app.agedcare.video.VideoPlayThread;
import com.punuo.sys.app.router.HomeRouter;
import com.punuo.sys.sdk.account.AccountManager;
import com.punuo.sys.sdk.event.FrameTimeoutEvent;
import com.punuo.sys.sdk.util.CommonUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.punuo.sys.app.agedcare.tools.H264VideoEncoder.YUVQueue;

/**
 * 视频聊天
 */
@Route(path = HomeRouter.ROUTER_VIDEO_CALL_ACTIVITY)
public class VideoCallActivity extends AppCompatActivity {
    public static final String TAG = "VideoCallActivity";
    private final Timer mTimer = new Timer();
    @BindView(R2.id.sv_back)
    SurfaceView mSurfaceViewBack;
    @BindView(R2.id.sv_front)
    SurfaceView mSurfaceViewFront;
    @BindView(R2.id.video_back)
    Button mVideoBack;
    private EzCamera engine;
    private VoiceEncoderThread mVoiceEncoderThread;
    private VideoPlayThread mVideoPlayThread;
    private RTPVideoReceiveImp mRTPVideoReceiveImp;
    private final int previewFrameRate = 15;  //演示帧率
    private final int previewWidth = 640;     //水平像素
    private final int previewHeight = 480;    //垂直像素
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //监听视频流
        mRTPVideoReceiveImp = new RTPVideoReceiveImp(H264ConfigUser.rtpIp, H264ConfigUser.rtpPort);
        mSurfaceViewBack.getLayoutParams().height = CommonUtil.getHeight();
        mSurfaceViewBack.getLayoutParams().width = CommonUtil.getHeight() * H264ConfigUser.VIDEO_WIDTH / H264ConfigUser.VIDEO_HEIGHT;
        mSurfaceViewBack.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mVideoPlayThread = new VideoPlayThread(holder.getSurface());
                mVideoPlayThread.startThread();
                EventBus.getDefault().post(new StartVideoEvent());
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (mVideoPlayThread != null) {
                    mVideoPlayThread.stopThread();
                }
            }
        });
        mSurfaceViewFront.setZOrderOnTop(true);
        mSurfaceViewFront.setZOrderMediaOverlay(true);
        mSurfaceViewFront.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                startPreview(holder, H264ConfigDev.VIDEO_WIDTH, H264ConfigDev.VIDEO_HEIGHT, H264ConfigDev.FRAME_RATE);
                H264VideoEncoder.getInstance().initEncoder(H264ConfigDev.VIDEO_WIDTH, H264ConfigDev.VIDEO_HEIGHT, H264ConfigDev.FRAME_RATE);
                H264VideoEncoder.getInstance().startEncoderThread();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                H264VideoEncoder.getInstance().close();
                if (engine != null) {
                    engine.stopPreview();
                }
            }
        });
        mTimer.schedule(new CheckFrameTask(), 0, 10000);
        mVideoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeVideo();
            }
        });
        //开启音频采集线程
        mVoiceEncoderThread = new VoiceEncoderThread(H264ConfigDev.rtpIp, H264ConfigDev.rtpPort);
        mVoiceEncoderThread.startEncoding();
    }

    private void startPreview(SurfaceHolder holder, int previewWidth, int previewHeight, int previewFrameRate) {
        RequestOptions requestOptions = RequestOptions
                .openBackCamera()
                .setAspectRatio(AspectRatio.of(4, 3))
                .setFrameRate(previewFrameRate)
                .size(previewWidth, previewHeight)
                .setPixelFormat(ImageFormat.YV12)
                .setListener(cameraKitListener)
                .autoFocus(true);
        engine = EzCameraKit.with(holder)
                .apply(requestOptions)
                .open();
        engine.startPreview();
    }

    private final CameraKitListener cameraKitListener = new CameraKitListener() {
        @Override
        public void onPictureTaken(byte[] data) {

        }

        @Override
        public void onCameraOpened() {

        }

        @Override
        public void onCameraClosed() {

        }

        @Override
        public void onCameraPreview() {

        }

        @Override
        public void onPreviewCallback(byte[] data) {
            putYUVData(data, data.length);
        }
    };

    private void putYUVData(byte[] data, int length) {
        if (YUVQueue.size() >= 10) {
            YUVQueue.poll();
        }
        YUVQueue.add(data);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
        H264Config.frameReceived = H264Config.FRAME_UNSET;
        if (mVoiceEncoderThread != null) {
            mVoiceEncoderThread.stopEncoding();
        }
        if (mVideoPlayThread != null) {
            mVideoPlayThread.stopThread();
        }
        if (mRTPVideoReceiveImp != null) {
            mRTPVideoReceiveImp.release();
        }
        AudioRecordManager.getInstance().stop();
        H264Config.monitorType = H264Config.IDLE;
        EventBus.getDefault().unregister(this);
    }
    @Override
    public void onBackPressed() {

    }

    private void closeVideo() {
        SipByeRequest byeRequest = new SipByeRequest(AccountManager.getTargetDevId());
        SipUserManager.getInstance().addRequest(byeRequest);
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(StopVideoEvent event) {
        closeVideo();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FrameTimeoutEvent event) {
        closeVideo();
    }

}
