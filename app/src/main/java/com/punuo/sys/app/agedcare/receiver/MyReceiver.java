package com.punuo.sys.app.agedcare.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.punuo.sys.app.agedcare.groupvoice.GroupInfo;
import com.punuo.sys.app.agedcare.groupvoice.GroupSignaling;
import com.punuo.sys.app.agedcare.sip.SipInfo;
import com.punuo.sys.sdk.util.ToastUtils;


public class MyReceiver extends BroadcastReceiver {
    public MyReceiver() {
    }

    @Override
    public synchronized void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the CallBroadcastReceiver is receiving
        // an Intent broadcast.
        KeyEvent keyEvent = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
        int keyCode = keyEvent.getKeyCode();
        System.out.println(keyCode);
        int state = keyEvent.getAction();
        Log.d("ssssssss","aaaa");
        switch (keyCode) {

            case 261:
                Log.d("ssssssss","aaaa");
                ToastUtils.showToast("PTT键按下");
                System.out.println("state = " + state);
                if (state == 0) {
                    ToastUtils.showToast("正在说话...");
                    if (GroupInfo.rtpAudio != null) {
                        System.out.println(111);
//                       GroupInfo.rtpAudio.pttChanged(true);
                        waitFor();
                        GroupSignaling groupSignaling = new GroupSignaling();
                        groupSignaling.setStart(SipInfo.devId);
                        groupSignaling.setLevel("1");
                        String start = JSON.toJSONString(groupSignaling);
                        GroupInfo.groupUdpThread.sendMsg(start.getBytes());
                    }
                } else {
                    ToastUtils.showToast("结束说话...");
                    if (GroupInfo.rtpAudio != null) {
                        System.out.println(222);
//                        GroupInfo.rtpAudio.pttChanged(false);
                        if (GroupInfo.isSpeak) {
                            GroupSignaling groupSignaling = new GroupSignaling();
                            groupSignaling.setEnd(SipInfo.devId);
                            String end = JSON.toJSONString(groupSignaling);
                            GroupInfo.groupUdpThread.sendMsg(end.getBytes());
                            waitFor();
                        }
                    }
                }
                break;
        }
    }

    private void waitFor() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
