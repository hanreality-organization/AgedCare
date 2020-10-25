package com.punuo.sys.app.agedcare.ui;


import android.content.Intent;
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
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import static android.R.attr.defaultValue;
import static com.punuo.sys.app.agedcare.sip.SipInfo.devName;
import static com.punuo.sys.app.agedcare.sip.SipInfo.devices;
import static com.punuo.sys.app.agedcare.sip.SipInfo.isanswering;
import static com.punuo.sys.app.agedcare.sip.SipInfo.netuserdevid;


public class VedioRequest extends HindebarActivity implements View.OnClickListener {

    @Bind(R.id.bt_cancle)

    Button bt_cancle;
    @Bind(R.id.CI_avatar)
    CircleImageView CI_avatar;
    @Bind(R.id.name)
    TextView name;
    String TAG="VedioRequest";
    SipURL sipURL = new SipURL(netuserdevid, SipInfo.serverIp, SipInfo.SERVER_PORT_USER);
    private SoundPool soundPool;
    private int streamId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vedio_request);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        int iconorder = intent.getIntExtra("iconorder", defaultValue);
        CI_avatar.setImageBitmap(devices.get(iconorder).getBitmap());
        name.setText(devices.get(iconorder).getNickname());
        EventBus.getDefault().register(this);
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
        EventBus.getDefault().post(new MessageEvent("等待通话"));
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                /**
//                 *要执行的操作
//                 */
//                SipInfo.toDev = new NameAddress(devName, sipURL);
//                org.zoolu.sip.message.Message request = SipMessageFactory.createNotifyRequest(SipInfo.sipUser, SipInfo.toDev,
//                        SipInfo.user_from, BodyFactory.createCallReply("cancel"));
//                SipInfo.sipUser.sendMessage(request);
//                finish();
//            }
//        }, 10000);
    }

    @OnClick({R.id.bt_cancle})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_cancle:
                isanswering=false;
                SipInfo.toDev = new NameAddress(devName, sipURL);
                org.zoolu.sip.message.Message request = SipMessageFactory.createNotifyRequest(SipInfo.sipUser, SipInfo.toDev,
                        SipInfo.user_from, BodyFactory.createCallReply("cancel"));
                SipInfo.sipUser.sendMessage(request);
                finish();
                break;
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if(event.getMessage().equals("取消")) {
            Log.i(TAG, "111message is " + event.getMessage());
            // 更新界面
            Toast.makeText(this,"对方已拒绝",Toast.LENGTH_SHORT).show();
            finish();

        }else if(event.getMessage().equals("视频开始"))
        {
            finish();
        }else if (event.getMessage().equals("请求失败"))
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
//    private void showToast(String info){
//        if (toast==null) {
//            toast = Toast.makeText(this, info,Toast.LENGTH_SHORT);
//            LinearLayout layout = (LinearLayout) toast.getView();
//            layout.setBackgroundColor(Color.parseColor("#FFFFFF"));
//            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
//            v.setTextColor(Color.BLACK);
//            v.setTextSize(25);
//        }else {
//            toast.setText(info);
//        }
//        toast.show();
//    }
}
