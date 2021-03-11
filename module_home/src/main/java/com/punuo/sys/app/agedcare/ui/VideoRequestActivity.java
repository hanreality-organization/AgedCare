package com.punuo.sys.app.agedcare.ui;


import android.media.MediaPlayer;
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
import com.punuo.sip.user.SipUserManager;
import com.punuo.sip.user.request.SipCallReplyRequest;
import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.R2;
import com.punuo.sys.app.agedcare.Util;
import com.punuo.sys.app.router.HomeRouter;
import com.punuo.sys.sdk.account.AccountManager;
import com.punuo.sys.sdk.activity.BaseActivity;
import com.punuo.sys.sdk.model.BindUser;
import com.punuo.sys.sdk.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(path = HomeRouter.ROUTER_VIDEO_REQUEST_ACTIVITY)
public class VideoRequestActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "VideoRequestActivity";
    @BindView(R2.id.bt_cancle)
    Button cancelBtn;
    @BindView(R2.id.CI_avatar)
    ImageView avatar;
    @BindView(R2.id.name)
    TextView name;

    @Autowired(name = "model")
    BindUser mBindUser;

    private MediaPlayer mMediaPlayer;

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

        try {
            mMediaPlayer = MediaPlayer.create(this, R.raw.videowait);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R2.id.bt_cancle})
    public void onClick(View view) {
        if (view.getId() == R.id.bt_cancle) {
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
            ARouter.getInstance().build(HomeRouter.ROUTER_VIDEO_CALL_ACTIVITY).navigation();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        EventBus.getDefault().unregister(this);
    }
}
