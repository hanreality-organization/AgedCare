package com.punuo.sys.app.agedcare.video;

import android.util.Log;

/**
 * Author chzjy
 * Date 2016/12/19.
 */
public class SendActivePacket extends Thread {
    private boolean running = false;
    private byte[] msg = new byte[20];

    public SendActivePacket() {
        msg[0] = 0x00;
        msg[1] = 0x01;
        msg[2] = 0x00;
        msg[3] = 0x10;
        System.arraycopy(VideoInfoUser.magic, 0, msg, 4, 16);
    }

    @Override
    public void run() {
        while (running) {
            try {
                Log.d("SendActivePacket", "run: "+ Thread.currentThread().getId());
               VideoInfo.rtpVideo.sendActivePacket(msg);
               sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void startThread(){
        running = true;
        super.start();
    }

    public void stopThread(){
        running = false;
    }
}
