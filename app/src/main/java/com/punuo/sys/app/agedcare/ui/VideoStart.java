package com.punuo.sys.app.agedcare.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.DialogPreference;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.sip.BodyFactory;
import com.punuo.sys.app.agedcare.sip.SipInfo;
import com.punuo.sys.app.agedcare.sip.SipMessageFactory;
import com.punuo.sys.app.agedcare.tools.Loading_view;
import com.punuo.sys.app.agedcare.tools.SipCallMananger;
import com.punuo.sys.app.agedcare.tools.SipVideoMananger;
import com.punuo.sys.app.agedcare.video.RtpVideo;
import com.punuo.sys.app.agedcare.video.SendActivePacket;
import com.punuo.sys.app.agedcare.video.VideoInfo;

import org.greenrobot.eventbus.EventBus;
import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;
import org.zoolu.sip.message.Message;

import java.net.SocketException;



/**
 * Created by maojianhui on 2018/7/11.
 */

public class VideoStart extends Activity {

    private Loading_view inviting;
    private Handler handlervideo = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videostart);
        hideStatusBarNavigationBar();

//        String devName = "pad";
//        final String devType = "2";
////        SipURL sipURL = new SipURL(netuserdevid, SipInfo.serverIp, SipInfo.SERVER_PORT_USER);
////        SipInfo.toDev = new NameAddress(devName, sipURL);
//        SipInfo.queryResponse = false;
//        SipInfo.inviteResponse = false;
//       inviting = new Loading_view(VideoStart.this,R.style.CustomDialog);
//       inviting.setCancelable(false);
//       inviting.setCanceledOnTouchOutside(false);
//        inviting.show();
//        new Thread() {
//            @Override
//            public void run() {
//                try {
//                    Log.d("1111", "run: "+SipInfo.toDev);
//                    Message query = SipMessageFactory.createOptionsRequest(SipInfo.sipUser, SipInfo.toDev,
//                            SipInfo.user_from, BodyFactory.createQueryBody("2"));
//                    outer:
//                    for (int i = 0; i < 3; i++) {
//                        SipInfo.sipUser.sendMessage(query);
//                        for (int j = 0; j < 20; j++) {
//                            sleep(100);
//                            if (SipInfo.queryResponse) {
//                                break outer;
//                            }
//                        }
//                        if (SipInfo.queryResponse) {
//                            break;
//                        }
//                    }
//                    if (SipInfo.queryResponse) {
//                        Message invite = SipMessageFactory.createInviteRequest(SipInfo.sipUser,
//                                SipInfo.toDev, SipInfo.user_from, BodyFactory.createMediaBody(VideoInfo.resultion, "H.264", "G.711", "2"));
//                        outer2:
//                        for (int i = 0; i < 3; i++) {
//                            SipInfo.sipUser.sendMessage(invite);
//                            for (int j = 0; j < 20; j++) {
//                                sleep(100);
//                                if (SipInfo.inviteResponse) {
//                                    break outer2;
//                                }
//                            }
//                            if (SipInfo.inviteResponse) {
//                                break;
//                            }
//                        }
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } finally {
//                   inviting.dismiss();
//                    if (SipInfo.queryResponse && SipInfo.inviteResponse) {
//                        Log.i("DevAdapter", "视频请求成功");
//                        SipInfo.decoding = true;
//                        try {
//                            VideoInfo.rtpVideo = new RtpVideo(VideoInfo.rtpIp, VideoInfo.rtpPort);
//                            VideoInfo.sendActivePacket = new SendActivePacket();
//                            VideoInfo.sendActivePacket.startThread();
//                            startActivity(new Intent(VideoStart.this, VideoPlay.class));
//                            EventBus.getDefault().post(new MessageEvent("通话开始"));
//                            finish();
//                        } catch (SocketException e) {
//                            e.printStackTrace();
//                        }
//                    } else {
//                        Log.i("DevAdapter", "视频请求失败");
//                        handlervideo.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                new AlertDialog.Builder(VideoStart.this)
//                                        .setTitle("视频请求失败！")
//                                        .setMessage("请重新尝试")
//                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialogInterface, int i) {
//                                                EventBus.getDefault().post(new MessageEvent("请求失败"));
//                                                finish();
//                                            }
//                                        }
//                            ).show();
//                            }
//                        });
//
//                        }
//                    }
//
//            }
//        }.start();
        SipVideoMananger.getInstance().call(this, SipInfo.netuserdevid, true);
//        EventBus.getDefault().post(new MessageEvent("通话开始"));

        Log.e("VideoStart","videostart");
        finish();
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
    }
    public void hideStatusBarNavigationBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags( WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags( WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        }
    }

}
