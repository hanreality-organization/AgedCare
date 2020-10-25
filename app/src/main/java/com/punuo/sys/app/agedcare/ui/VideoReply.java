package com.punuo.sys.app.agedcare.ui;

import android.content.pm.ActivityInfo;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.sip.BodyFactory;
import com.punuo.sys.app.agedcare.sip.SipInfo;
import com.punuo.sys.app.agedcare.sip.SipMessageFactory;
import com.punuo.sys.app.agedcare.view.CircleImageView;
import com.punuo.sys.app.agedcare.view.CustomProgressDialog;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import static com.punuo.sys.app.agedcare.sip.SipInfo.devName;
import static com.punuo.sys.app.agedcare.sip.SipInfo.devices;
import static com.punuo.sys.app.agedcare.sip.SipInfo.isanswering;
import static com.punuo.sys.app.agedcare.sip.SipInfo.userdevid;
import static com.punuo.sys.app.agedcare.sip.SipInfo.videouserId;

public class VideoReply extends HindebarActivity implements View.OnClickListener {
    @Bind(R.id.bt_accept)
    Button bt_accept;
    @Bind(R.id.bt_refuse)
    Button bt_refuse;
    @Bind(R.id.name)
    TextView name;
    @Bind(R.id.CIV_avatar)
    CircleImageView CIV_avatar;
    private SoundPool soundPool;
    private int streamId;
    SipURL sipURL = new SipURL(userdevid, SipInfo.serverIp, SipInfo.SERVER_PORT_USER);
    String TAG="VideoReply";
    private CustomProgressDialog logining;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_connet);
        ButterKnife.bind(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        EventBus.getDefault().register(this);  //注册
        EventBus.getDefault().post(new MessageEvent("等待通话"));
        for (int i=0;i<devices.size();i++)
        {
            if(videouserId.equals(devices.get(i).getUserid())) {
                CIV_avatar.setImageBitmap(devices.get(i).getBitmap());
                name.setText(devices.get(i).getNickname());
            }
            Log.d(TAG,videouserId);
            Log.d(TAG,devices.get(i).getUserid());
        }
        soundPool=new SoundPool(10, android.media.AudioManager.STREAM_MUSIC,5);
        final int sourceid=soundPool.load(this,R.raw.videowait,1);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {

            public void onLoadComplete(
                    SoundPool soundPool,
                    int sampleId, int status) {
                // TODO Auto-generated method stub
                Log.d("xx", "11");
                streamId=soundPool.play(sourceid, 1, 1, 0,
                        4, 1);
            }
        });
//        TimerTask task = new TimerTask() {
//            @Override
//            public void run() {
//                /**
//                 *要执行的操作
//                 */
//                SipInfo.toDev = new NameAddress(devName, sipURL);
//                org.zoolu.sip.message.Message request2 = SipMessageFactory.createNotifyRequest(SipInfo.sipUser, SipInfo.toDev,
//                        SipInfo.user_from, BodyFactory.createCallReply("refuse"));
//                SipInfo.sipUser.sendMessage(request2);
//
//                finish();
//            }
//        };
//        Timer timer = new Timer();
//        timer.schedule(task, 8000);
    }

    @OnClick({R.id.bt_accept, R.id.bt_refuse,})
                public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.bt_accept:
                        SipInfo.toDev = new NameAddress(devName, sipURL);
                        org.zoolu.sip.message.Message request1 = SipMessageFactory.createNotifyRequest(SipInfo.sipUser, SipInfo.toDev,
                                SipInfo.user_from, BodyFactory.createCallReply("agree"));
                        SipInfo.sipUser.sendMessage(request1);
                       logining= new CustomProgressDialog(this);
                        logining.setTitle("视频加载中...");
                        logining.show();

                        break;
                    case R.id.bt_refuse:
                SipInfo.toDev = new NameAddress(devName, sipURL);
                org.zoolu.sip.message.Message request2 = SipMessageFactory.createNotifyRequest(SipInfo.sipUser, SipInfo.toDev,
                        SipInfo.user_from, BodyFactory.createCallReply("refuse"));
                SipInfo.sipUser.sendMessage(request2);
                  isanswering=false;
                finish();

        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if(event.getMessage().equals("取消")) {
            Log.i(TAG, "111message is " + event.getMessage());
            // 更新界面
            finish();
            Toast.makeText(this,"对方已取消",Toast.LENGTH_SHORT).show();
        }else if (event.getMessage().equals("视频开始"))
        {

            finish();
        }
    }
    public void stopSound(int id) {
        soundPool.stop(id);
    }
    @Override
    protected void onDestroy() {
        stopSound(streamId);
        super.onDestroy();
        // 注销订阅者
        EventBus.getDefault().unregister(this);
    }

}
