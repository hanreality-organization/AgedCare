package com.punuo.sys.app.agedcare.ui;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.punuo.sip.dev.event.StartVideoEvent;
import com.punuo.sip.dev.model.CallResponse;
import com.punuo.sip.user.SipUserManager;
import com.punuo.sip.user.request.SipCallReplyRequest;
import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.R2;
import com.punuo.sys.app.router.HomeRouter;
import com.punuo.sys.sdk.account.AccountManager;
import com.punuo.sys.sdk.activity.BaseActivity;
import com.punuo.sys.sdk.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.punuo.sys.app.agedcare.sip.SipInfo.isanswering;

@Route(path = HomeRouter.ROUTER_VIDEO_REPLY_ACTIVITY)
public class VideoReplyActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R2.id.bt_accept)
    Button bt_accept;
    @BindView(R2.id.bt_refuse)
    Button bt_refuse;
    @BindView(R2.id.name)
    TextView name;
    @BindView(R2.id.CIV_avatar)
    ImageView CIV_avatar;
    private static final String TAG = "VideoReplyActivity";
    private MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_connect);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);  //注册
        try {
            mMediaPlayer = MediaPlayer.create(this, R.raw.videowait);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @OnClick({R2.id.bt_accept, R2.id.bt_refuse,})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.bt_accept) {
            SipCallReplyRequest replyRequest = new SipCallReplyRequest("agree", AccountManager.getTargetDevId());
            SipUserManager.getInstance().addRequest(replyRequest);
            showLoadingDialog("等待建立连接中...");
        } else if (id == R.id.bt_refuse) {
            SipCallReplyRequest replyRequest = new SipCallReplyRequest("refuse", AccountManager.getTargetDevId());
            SipUserManager.getInstance().addRequest(replyRequest);
            isanswering = false;
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(StartVideoEvent event) {
        dismissLoadingDialog();
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CallResponse event) {
        if (TextUtils.equals(event.operate, "cancel")){
            ToastUtils.showToast("对方已取消");
            finish();
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
