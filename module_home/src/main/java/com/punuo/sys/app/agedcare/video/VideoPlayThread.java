package com.punuo.sys.app.agedcare.video;

import android.view.Surface;

import com.punuo.sys.app.agedcare.tools.H264VideoDecoder;


/**
 * Created by han.chen.
 * Date on 2021/2/24.
 **/
public class VideoPlayThread extends Thread {
    public VideoPlayThread(Surface surface) {
        H264VideoDecoder.getInstance().initDecoder(surface);
    }

    private boolean running = true;
    private int number = 0;
    @Override
    public void run() {
        while (running) {
            byte[] nal = VideoInfo.nalBuffers[number].getReadableNalBuf();
            if (nal != null) {
                try {
                    H264VideoDecoder.getInstance().onFrame(nal, 0, nal.length);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            VideoInfo.nalBuffers[number].readLock();
            VideoInfo.nalBuffers[number].cleanNalBuf();
            number = ++number % 200;
        }
    }

    public void startThread() {
        running = true;
        start();
    }

    public void stopThread() {
        running = false;
        H264VideoDecoder.getInstance().stopCodec();
    }

}
