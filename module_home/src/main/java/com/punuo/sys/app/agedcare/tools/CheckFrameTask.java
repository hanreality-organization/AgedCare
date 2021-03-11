package com.punuo.sys.app.agedcare.tools;

import android.os.Message;
import android.util.Log;

import com.punuo.sip.H264Config;
import com.punuo.sys.sdk.event.FrameTimeoutEvent;
import com.punuo.sys.sdk.util.BaseHandler;
import com.punuo.sys.sdk.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.TimerTask;

/**
 * Created by han.chen.
 * Date on 2021/3/11.
 **/
public class CheckFrameTask extends TimerTask implements BaseHandler.MessageHandler {
    private final BaseHandler mBaseHandler = new BaseHandler(this);
    private int count = 0;
    @Override
    public void run() {
        if (H264Config.frameReceived == H264Config.FRAME_TIMEOUT) {
            if (count >= 2) {
                mBaseHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showToast("长时间没有视频画面，页面关闭");
                    }
                });

                EventBus.getDefault().post(new FrameTimeoutEvent());
            } else {
                mBaseHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showToast("对方网络不稳定,视频画面可能延迟");
                    }
                });
                count++;
            }
        } else if (H264Config.frameReceived == H264Config.FRAME_RECEIVED) {
            H264Config.frameReceived = H264Config.FRAME_UNSET;
            count = 0;
            Log.i("CheckFrameTask", "got frame");
        } else {
            H264Config.frameReceived = H264Config.FRAME_TIMEOUT;
        }
    }

    @Override
    public void handleMessage(Message msg) {

    }
}
