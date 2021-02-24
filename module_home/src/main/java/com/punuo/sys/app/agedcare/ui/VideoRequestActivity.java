package com.punuo.sys.app.agedcare.ui;


import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.punuo.sip.H264Config;
import com.punuo.sip.dev.event.MonitorEvent;
import com.punuo.sip.dev.event.StartVideoEvent;
import com.punuo.sip.dev.model.CallResponse;
import com.punuo.sip.user.H264ConfigUser;
import com.punuo.sip.user.SipUserManager;
import com.punuo.sip.user.request.SipCallReplyRequest;
import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.R2;
import com.punuo.sys.app.agedcare.Util;
import com.punuo.sys.app.agedcare.sip.SipInfo;
import com.punuo.sys.app.agedcare.video.RtpVideo;
import com.punuo.sys.app.agedcare.video.SendActivePacket;
import com.punuo.sys.app.agedcare.video.VideoInfo;
import com.punuo.sys.app.router.HomeRouter;
import com.punuo.sys.sdk.account.AccountManager;
import com.punuo.sys.sdk.activity.BaseActivity;
import com.punuo.sys.sdk.model.BindUser;
import com.punuo.sys.sdk.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.SocketException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.punuo.sys.app.agedcare.sip.SipInfo.isanswering;

@Route(path = HomeRouter.ROUTER_VIDEO_REQUEST_ACTIVITY)
public class VideoRequestActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "VideoRequestActivity";
    @BindView(R2.id.bt_cancle)
    Button cancelBtn;
    @BindView(R2.id.CI_avatar)
    ImageView avatar;
    @BindView(R2.id.name)
    TextView name;

    private SoundPool soundPool;
    private int streamId;

    @Autowired(name = "model")
    BindUser mBindUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_request);
        ButterKnife.bind(this);
        ARouter.getInstance().inject(this);
        EventBus.getDefault().register(this);
        Glide.with(this).load(Util.getImageUrl(mBindUser.getId(), mBindUser.getAvatar()))
                .into(avatar);
        name.setText(mBindUser.getNickname());
        soundPool = new SoundPool.Builder()
                .setMaxStreams(10)
                .setAudioAttributes(new AudioAttributes.Builder().setLegacyStreamType(AudioManager.STREAM_MUSIC).build())
                .build();
        final int sourceId = soundPool.load(this, R.raw.videowait, 1);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {

            public void onLoadComplete(
                    SoundPool soundPool,
                    int sampleId, int status) {
                streamId = soundPool.play(sourceId, 1, 1, 0, 4, 1);
            }
        });
    }

    @OnClick({R2.id.bt_cancle})
    public void onClick(View view) {
        if (view.getId() == R.id.bt_cancle) {
            isanswering = false;
            SipCallReplyRequest sipCallReplyRequest = new SipCallReplyRequest("cancel", AccountManager.getTargetDevId());
            SipUserManager.getInstance().addRequest(sipCallReplyRequest);
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(StartVideoEvent event) {
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CallResponse event) {
        if (TextUtils.equals(event.operate, "refuse")){
            ToastUtils.showToast("对方已拒绝");
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MonitorEvent event) {
        if (event.monitorType == H264Config.DOUBLE_MONITOR_POSITIVE) {
            new Thread(() -> {
                SipInfo.decoding = true;
                try {
                    VideoInfo.rtpVideo = new RtpVideo(H264ConfigUser.rtpIp, H264ConfigUser.rtpPort);
                    VideoInfo.sendActivePacket = new SendActivePacket();
                    VideoInfo.sendActivePacket.startThread();
                    ARouter.getInstance().build(HomeRouter.ROUTER_VIDEO_CALL_ACTIVITY).navigation();
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public void stopSound(int id) {
        soundPool.stop(id);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopSound(streamId);
        EventBus.getDefault().unregister(this);
    }
}
