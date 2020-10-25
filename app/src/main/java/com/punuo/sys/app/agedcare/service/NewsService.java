package com.punuo.sys.app.agedcare.service;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import com.punuo.sys.app.agedcare.sip.BodyFactory;
import com.punuo.sys.app.agedcare.sip.SipInfo;
import com.punuo.sys.app.agedcare.sip.SipMessageFactory;
import com.punuo.sys.app.agedcare.tools.SipCallMananger;
import com.punuo.sys.app.agedcare.ui.VideoCallActivity;
import com.punuo.sys.app.agedcare.video.H264Sending;
import com.punuo.sys.app.agedcare.video.VideoInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by ch on 2016/11/14.
 */

public class NewsService extends Service {
    private String TAG = "NewsService";
    private NotificationManager notificationManager;
    private int TASK_NOTIFICATION_ID = 0x1123;
    private int MAIL_NOTIFICATION_ID = 0x1124;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "News Service 开启");
        SipInfo.notifymedia = new Handler() {
            @Override
            public void handleMessage(Message msg) {
//                Toast.makeText(NewsService.this, "收到请求", Toast.LENGTH_SHORT).show();
//                System.out.println("收到了 视频邀请");
//                SimpleDateFormat formatter = new SimpleDateFormat("HHmmss", Locale.getDefault());
//                Date curDate = new Date(System.currentTimeMillis());
//                VideoInfo.vidieoBegin = formatter.format(curDate);
//                if (isCameraCanUse()) {
//                    Intent intent = new Intent(NewsService.this, H264Sending.class);
//                    SipInfo.inviteResponse = true;
//                    SipInfo.sipDev.sendMessage(SipMessageFactory.createResponse(SipInfo.msg, 200, "OK", BodyFactory.createMediaResponseBody("MOBILE_S9")));
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                    super.handleMessage(msg);
//                }
                if (msg.what == 0x1111) {
                     /* System.out.println("收到了 视频邀请");
                    VideoInfo.rtpIp = VideoInfo.media_info_ip;
                    VideoInfo.rtpPort = VideoInfo.media_info_port;
                    VideoInfo.magic = VideoInfo.media_info_magic;

                    Intent intent = new Intent(NewsService.this, VideoCallActivity.class);
                    Log.d("echo_tag", "1 - NewsService - 收到视频请求 - SipInfo.isWaitingFeedback： " + SipInfo.isWaitingFeedback);

                    SipInfo.inviteResponse = true;
                    SipInfo.sipDev.sendMessage(SipMessageFactory.createResponse(SipInfo.msg, 200, "OK", BodyFactory.createMediaResponseBody("MOBILE_S9")));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    */


                    Log.d("echo_tag", "1 - NewsService - 收到视频请求 - SipInfo.isWaitingFeedback： " + SipInfo.isWaitingFeedback);

                    SimpleDateFormat formatter = new SimpleDateFormat("HHmmss", Locale.getDefault());
                    Date curDate = new Date(System.currentTimeMillis());
                    VideoInfo.vidieoBegin = formatter.format(curDate);
                    if (isCameraCanUse()) {
                        SipInfo.inviteResponse = true;
                        SipInfo.sipDev.sendMessage(SipMessageFactory.createResponse(SipInfo.msg, 200, "OK", BodyFactory.createMediaResponseBody("MOBILE_S9")));

                        if (SipInfo.isWaitingFeedback) {  // 主动呼叫不用再呼叫一次，直接去视频界面
                            SipInfo.isWaitingFeedback = false; // 改变这个标志，sipCallManager中会跳转到videoCall界面
                        } else {
                            Log.d("echo_tag", "2 - NewsService - 收到视频请求 - SipInfo.sipReqFromUser： " + SipInfo.sipReqFromUser);
                            if (!TextUtils.isEmpty(SipInfo.sipReqFromUser)) {
                                SipCallMananger.getInstance().call(NewsService.this, SipInfo.sipReqFromUser, false);
                            } else {
                                Toast.makeText(NewsService.this, "收到请求，获取身份信息失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                        super.handleMessage(msg);
                    }
                } else if (msg.what == 0x2222) {
                    LocalBroadcastManager.getInstance(NewsService.this).sendBroadcast(new Intent(VideoCallActivity.BROADCAST_ACTION));
                }
                else if (msg.what == 0x3333) {
                    System.out.println("收到了 视频邀请");
                    SimpleDateFormat formatter = new SimpleDateFormat("HHmmss", Locale.getDefault());
                    Date curDate = new Date(System.currentTimeMillis());
                    VideoInfo.vidieoBegin = formatter.format(curDate);
                    if (isCameraCanUse()) {
                        Intent intent = new Intent(NewsService.this, H264Sending.class);
                        SipInfo.inviteResponse = true;
                        SipInfo.sipDev.sendMessage(SipMessageFactory.createResponse(SipInfo.msg, 200, "OK", BodyFactory.createMediaResponseBody("MOBILE_S9")));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        super.handleMessage(msg);
                    }

                }
            }
        };
    }

    public static boolean isCameraCanUse() {
        boolean canUse = true;
        android.hardware.Camera mCamera = null;
        try {
            // TODO camera驱动挂掉,处理??
            mCamera = mCamera.open();
        } catch (Exception e) {
            canUse = false;
        }
        if (canUse) {
            mCamera.release();
            mCamera = null;
        }

        return canUse;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*
     * 判断服务是否启动,context上下文对象 ，className服务的name
     */
    public static boolean isServiceRunning(Context mContext, String className) {

        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(30);

        if (!(serviceList.size() > 0)) {
            return false;
        }

        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className)) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }
}
