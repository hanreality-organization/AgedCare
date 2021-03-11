package com.punuo.sys.app.agedcare.video;

import android.graphics.ImageFormat;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.glumes.ezcamerakit.CameraKitListener;
import com.glumes.ezcamerakit.EzCamera;
import com.glumes.ezcamerakit.EzCameraKit;
import com.glumes.ezcamerakit.RequestOptions;
import com.glumes.ezcamerakit.base.AspectRatio;
import com.punuo.sip.H264Config;
import com.punuo.sip.dev.H264ConfigDev;
import com.punuo.sip.dev.event.StopVideoEvent;
import com.punuo.sip.user.SipUserManager;
import com.punuo.sip.user.request.SipSuspendMonitorRequest;
import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.R2;
import com.punuo.sys.app.agedcare.audio.VoiceEncoderThread;
import com.punuo.sys.app.agedcare.tools.H264VideoEncoder;
import com.punuo.sys.app.router.HomeRouter;
import com.punuo.sys.sdk.account.AccountManager;
import com.punuo.sys.sdk.activity.BaseActivity;
import com.punuo.sys.sdk.event.CloseOtherMediaEvent;
import com.punuo.sys.sdk.event.NetworkDisconnectedEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.punuo.sys.app.agedcare.tools.H264VideoEncoder.YUVQueue;


@Route(path = HomeRouter.ROUTER_SINGLE_MONITOR_ACTIVITY)
public class SingleMonitorActivity extends BaseActivity implements SurfaceHolder.Callback {
    private static final String TAG ="SingleMonitorActivity";
    @BindView(R2.id.h264suf)
    SurfaceView mSurfaceView;
    @BindView(R2.id.video_back)
    View videoBack;
    private final int previewFrameRate = 15;  //演示帧率
    private final int previewWidth = 640;     //水平像素
    private final int previewHeight = 480;    //垂直像素
    private EzCamera engine;
    private VoiceEncoderThread mVoiceEncoderThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_monitor_activity);
        ButterKnife.bind(this);
        EventBus.getDefault().post(new CloseOtherMediaEvent());
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        SurfaceHolder holder = mSurfaceView.getHolder();
        holder.addCallback(this);

        videoBack.setOnClickListener(v -> {
            stopMonitor();
            finish();
        });

        //开启音频采集线程
        mVoiceEncoderThread = new VoiceEncoderThread(H264ConfigDev.rtpIp, H264ConfigDev.rtpPort);
        mVoiceEncoderThread.startEncoding();

        EventBus.getDefault().register(this);
    }

    private void stopMonitor() {
        SipSuspendMonitorRequest request = new SipSuspendMonitorRequest(AccountManager.getTargetDevId());
        SipUserManager.getInstance().addRequest(request);
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

    /**
     * @see com.punuo.sip.dev.service.RecvaddrService
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(StopVideoEvent event) {
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NetworkDisconnectedEvent event) {
        stopMonitor();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mVoiceEncoderThread != null) {
            mVoiceEncoderThread.stopEncoding();
        }
        H264Config.monitorType = H264Config.IDLE;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        startPreview(holder, previewWidth, previewHeight, previewFrameRate);
        H264VideoEncoder.getInstance().initEncoder(previewWidth, previewHeight, previewFrameRate);
        H264VideoEncoder.getInstance().startEncoderThread();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        H264VideoEncoder.getInstance().close();
        if (engine != null) {
            engine.stopPreview();
        }
    }

    @Override
    public void onBackPressed() {

    }
}
