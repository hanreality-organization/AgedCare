package com.punuo.sys.app.agedcare.vi.activity;

import android.animation.ObjectAnimator;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.vi.bean.ViSearchSong;
import com.punuo.sys.app.agedcare.vi.bean.ViSongUrl;
import com.punuo.sys.app.agedcare.vi.bean.VoiceEvent;
import com.punuo.sys.app.agedcare.vi.http.ViAPI;
import com.punuo.sys.app.agedcare.vi.http.ViRequestUtils;
import com.punuo.sys.app.agedcare.vi.service.SpeechService;
import com.punuo.sys.app.agedcare.vi.utils.ViCommonUtils;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.MoveType;
import com.yhao.floatwindow.PermissionListener;
import com.yhao.floatwindow.ViewStateListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class VoiceUiActivity extends AppCompatActivity {
    private RelativeLayout rlContent;
    private View contentView;
    private RelativeLayout rlVoice;
    private ImageView view;
    private ObjectAnimator objectAnimator;
    private TextView tvHint, tvContent;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_voice_ui);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("VoiceUiActivity", "OnResume");
        //requestPermission();
        requestPermission();
    }

    // 请求录音权限
    private void requestPermission() {
        XXPermissions.with(this).permission(Permission.RECORD_AUDIO
                , Permission.READ_EXTERNAL_STORAGE
                , Permission.WRITE_EXTERNAL_STORAGE).request(new OnPermission() {
            @Override
            public void hasPermission(List<String> granted, boolean all) {
                if (all) {
                    if (!isServiceRunning(VoiceUiActivity.this, "SpeechService")) {
                        intent = new Intent(VoiceUiActivity.this, SpeechService.class);
                        startService(intent);
                    }
                }
            }

            @Override
            public void noPermission(List<String> denied, boolean quick) {
                if(quick) {
                    XXPermissions.gotoPermissionSettings(VoiceUiActivity.this);
                }
            }
        });
    }

    // 判断服务是否启动,context上下文对象 ，className服务的name
    private static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(30);

        if (!(serviceList.size() > 0)) {
            return false;
        }
        Log.e("OnlineService：", className);
        for (int i = 0; i < serviceList.size(); i++) {
            Log.e("serviceName：", serviceList.get(i).service.getClassName());
            if (serviceList.get(i).service.getClassName().contains(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    // 初始化页面
    public void initBaseView(View contentView){
        tvContent = (TextView) contentView.findViewById(R.id.vitv_voicecontent);
        rlContent = (RelativeLayout) contentView.findViewById(R.id.virl_content);
        rlVoice = (RelativeLayout) contentView.findViewById(R.id.virl_voice);
        tvHint = (TextView) contentView.findViewById(R.id.vitv_voicehint);
        view = (ImageView) contentView.findViewById(R.id.viiv_voiceicon);
        //startShakeByPropertyAnim(view, 1.2f, 1.2f, 1.2f, 1000);
        contentView.findViewById(R.id.viiv_voiceclose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideVoice();
            }
        });
    }

    @Subscribe
    public void getEvent(VoiceEvent event){
        try{
            switch (event.getCode()){
                case 1001:
                    showVoice();
                    break;
                case 1002://搜索音乐
                    getSongInfo(event.getContent());
                    hideVoice();
                    break;
                case 1003://显示说的什么
                    tvHint.setVisibility(View.GONE);
                    tvContent.setText(event.getContent());
                    break;
                case 2001://隐藏
                    hideVoice();
                    break;
            }
        }catch (Exception e){
            Log.e("exc",e.getMessage());
        }
    }

    // 搜索并获取歌曲信息
    public void getSongInfo(String singName) {
        ViRequestUtils.searchSong(this, singName, new Observer<ViSearchSong>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ViSearchSong searchSong) {
                Log.e("http", searchSong.toString());
                getPlayUrl(searchSong);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    // 获取播放链接
    public void getPlayUrl(ViSearchSong searchSong) {
        ViSearchSong.DataBean.SongBean.ListBean listBean = searchSong.getData().getSong().getList().get(0);

        ViRequestUtils.getSongPlayUrl(this, ViAPI.SONG_URL_DATA_LEFT
                + listBean.getSongmid()
                + ViAPI.SONG_URL_DATA_RIGHT, new Observer<ViSongUrl>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ViSongUrl songUrl) {
                ViSongUrl.Req0Bean.DataBean data = songUrl.getReq_0().getData();
                List<String> sip = data.getSip();
                String PlayUrl = sip.get(0) + data.getMidurlinfo().get(0).getPurl();
                if (!TextUtils.isEmpty(data.getMidurlinfo().get(0).getPurl())) {
                    hideVoice();
                    finish();
                    Intent intent = new Intent(VoiceUiActivity.this, PlayActivity.class);
                    intent.putExtra("url", PlayUrl);
                    intent.putExtra("searchSong", searchSong);
                    startActivity(intent);

                } else {
                    Toast.makeText(VoiceUiActivity.this, "VIP", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void showVoice() {
        View view = View.inflate(this, R.layout.activity_voice_ui, null);
        initBaseView(view);
        FloatWindow
                .with(getApplicationContext())
                .setView(view)
                .setWidth(ViCommonUtils.getScreenWidth(this))                               //设置控件宽高
                .setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT, 0.2f)
                .setMoveType(MoveType.inactive)
                .setDesktopShow(false)//桌面显示
                .setViewStateListener(new ViewStateListener() {
                    @Override
                    public void onPositionUpdate(int i, int i1) {

                    }

                    @Override
                    public void onShow() {

                    }

                    @Override
                    public void onHide() {

                    }

                    @Override
                    public void onDismiss() {

                    }

                    @Override
                    public void onMoveAnimStart() {

                    }

                    @Override
                    public void onMoveAnimEnd() {

                    }

                    @Override
                    public void onBackToDesktop() {

                    }
                })    //监听悬浮控件状态改变
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFail() {

                    }
                })  //监听权限申请结果
                .build();


        if (objectAnimator != null) {
            objectAnimator.start();
        }
        tvHint.setVisibility(View.VISIBLE);
        tvContent.setText("‘我想听菊花台’");
        FloatWindow.get().show();

    }

    public void hideVoice() {

        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
        if (tvHint != null) {
            tvHint.setVisibility(View.VISIBLE);

        }
        if (tvContent != null) {
            tvContent.setText("‘我想听菊花台’");
        }
        FloatWindow.destroy();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (intent != null) {
            stopService(intent);
        }
    }
}