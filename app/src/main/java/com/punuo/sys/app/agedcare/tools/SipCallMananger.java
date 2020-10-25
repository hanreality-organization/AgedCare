package com.punuo.sys.app.agedcare.tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.punuo.sys.app.agedcare.sip.BodyFactory;
import com.punuo.sys.app.agedcare.sip.SipInfo;
import com.punuo.sys.app.agedcare.sip.SipMessageFactory;
import com.punuo.sys.app.agedcare.ui.VideoCallActivity;
import com.punuo.sys.app.agedcare.video.RtpVideo;
import com.punuo.sys.app.agedcare.video.SendActivePacket;
import com.punuo.sys.app.agedcare.video.VideoInfo;
import com.punuo.sys.app.agedcare.view.CustomProgressDialog;

import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;
import org.zoolu.sip.message.Message;

import java.io.IOException;
import java.net.SocketException;

import okhttp3.OkHttpClient;
import okhttp3.Request;

import static com.punuo.sys.app.agedcare.sip.SipInfo.groupid;
import static com.punuo.sys.app.agedcare.sip.SipInfo.serverIp;

/**
 * 作者：EchoJ on 2018/7/30 10:14 <br>
 * 邮箱：echojiangyq@gmail.com <br>
 * 描述：
 */
public class SipCallMananger {
    private static final String TAG = "SipCallMananger";
    static volatile SipCallMananger instance;
        SipCallMananger(){
            
        }
        
        public static final SipCallMananger getInstance() {
            if (instance == null) {
                synchronized (SipCallMananger.class) {
                    if (instance == null) {
                        instance =new SipCallMananger();
                    }
                }
            }
            return instance;
        }
        
        
        public void call(final Context mContext, final String userId, final boolean isIntiative){
  			Log.d("echo_tag", "2 - SipcallManager - 收到视频请求");
            if(mContext == null || TextUtils.isEmpty(userId))return;
            
            SipInfo.isWaitingFeedback = isIntiative;
            
            new Thread(new Runnable() {
                String devId;
                CustomProgressDialog inviting = null;
                
                @Override
                public void run() {
                    try {
                       Looper.prepare();
						Log.d("echo_tag", "3 - SipcallManager - 收到视频请求 - 发起视频请求");
                        OkHttpClient client = new OkHttpClient();
                        Request request1 = new Request.Builder()
                                .url("http://"+serverIp+":8000/xiaoyupeihu/public/index.php/devs/getUserDevId?id=" + userId +"&groupid="+groupid)
                                .build();
                        if (client.newCall(request1).execute().body().string().length()>=28) {
                            devId = client.newCall(request1).execute().body().string().substring(10, 28);
                        }
                        Log.d("1111", "run: "+devId);
                        devId = devId.substring(0, devId.length() - 4).concat("0160");//设备id后4位替换成0160
                        String devName = "pad";
                        final String devType = "2";
                        SipURL sipURL = new SipURL(devId, serverIp, SipInfo.SERVER_PORT_USER);
                        SipInfo.toDev = new NameAddress(devName, sipURL);
                        SipInfo.queryResponse = false;
                        SipInfo.inviteResponse = false;
                        
                        if(mContext instanceof Activity){
                            inviting = new CustomProgressDialog(mContext);
                            inviting.setCancelable(false);
                            inviting.setCanceledOnTouchOutside(false);
                            inviting.show();
                        }
                        
                        new Thread(){
                            @Override
                            public void run() {
                                try {
                                    Looper.prepare();
									Log.d("echo_tag", "4 - SipcallManager - 发起视频请求");
                                    Message query = SipMessageFactory.createOptionsRequest(SipInfo.sipUser, SipInfo.toDev,
                                            SipInfo.user_from, BodyFactory.createQueryBody(devType));
                                    outer:
                                    for (int i = 0; i < 3; i++) {
                                        SipInfo.sipUser.sendMessage(query);
                                        for (int j = 0; j < 20; j++) {
                                            sleep(100);
                                            if (SipInfo.queryResponse) {
                                                break outer;
                                            }
                                        }
                                        if (SipInfo.queryResponse) {
                                            break;
                                        }
                                    }
 									Log.d("echo_tag", "5 - SipcallManager - 发起视频请求");
                                    if (SipInfo.queryResponse) {
                                        Message invite = SipMessageFactory.createInviteRequest(SipInfo.sipUser,
                                                SipInfo.toDev, SipInfo.user_from, BodyFactory.createMediaBody(VideoInfo.resultion,"H.264","G.711",devType));
                                        outer2:
                                        for (int i = 0; i < 3; i++) {
                                            SipInfo.sipUser.sendMessage(invite);
                                            for (int j = 0; j < 20; j++) {
                                                sleep(100);
                                                if (SipInfo.inviteResponse) {
                                                    break outer2;
                                                }
                                            }
                                            if (SipInfo.inviteResponse) {
                                                break;
                                            }
                                        }
                                    }
                                    
                                    Log.d("echo_tag", "6 - SipcallManager - SipInfo.inviteResponse:" + SipInfo.inviteResponse);
                                    if (SipInfo.inviteResponse && isIntiative) {
                                        Log.i("echo_tag", "等待对方的视频请求");
                                        for (int j = 0; j < 200; j++) {
                                            sleep(100);
                                            if (!SipInfo.isWaitingFeedback) {
                                                break ;
                                            }
                                        }
                                    }

                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }finally {
                                    if(inviting != null){
                                        inviting.dismiss();
                                    }
                                    if (SipInfo.queryResponse && SipInfo.inviteResponse && (!isIntiative || (isIntiative && !SipInfo.isWaitingFeedback))) {
                                        Log.i("echo_tag", "视频请求成功");
                                        SipInfo.decoding = true;
                                        try {
                                            VideoInfo.rtpVideo = new RtpVideo(VideoInfo.rtpIp, VideoInfo.rtpPort);
                                            VideoInfo.sendActivePacket = new SendActivePacket();
                                            VideoInfo.sendActivePacket.startThread();
                                            
                                            Intent intent = new Intent(mContext, VideoCallActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            mContext.startActivity(intent);
                                        } catch (SocketException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        Log.i("echo_tag", "视频请求失败");
                                        Toast.makeText(mContext, "视频请求失败", Toast.LENGTH_SHORT).show();
                                    }
                                    SipInfo.isWaitingFeedback = false;
                                }
                                Looper.loop();
                            }
                        }.start();
                        Looper.loop();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            
        }
        
}
