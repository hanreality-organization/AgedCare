package com.punuo.sys.app.agedcare.audio;

import com.punuo.sys.app.agedcare.tools.RTPAudioSendSession;

/**
 * Created by han.chen.
 * Date on 2021/3/11.
 * 音频采集线程
 **/
public class VoiceEncoderThread extends Thread {
    private static final int SAMPLE_LENGTH = 160;

    private RTPAudioSendSession mRTPAudioSendSession;
    private boolean running = false;

    public VoiceEncoderThread(String ip, int port) {
        mRTPAudioSendSession = new RTPAudioSendSession(ip, port);
        mRTPAudioSendSession.payloadType(0x45);
    }

    @Override
    public void run() {
        AudioRecordManager.getInstance().startRecording();
        short[] audioData = new short[SAMPLE_LENGTH];
        byte[] encodeData = new byte[SAMPLE_LENGTH];
        int numRead;
        while (running) {
            numRead = AudioRecordManager.getInstance().read(audioData, 0, SAMPLE_LENGTH);
            if (numRead <= 0) continue;
            calc2(audioData, 0, numRead);
            //进行pcmu编码
            G711.linear2ulaw(audioData, 0, encodeData, numRead);
            mRTPAudioSendSession.sendData(encodeData);
        }
        AudioRecordManager.getInstance().stopRecording();
        AudioRecordManager.getInstance().release();
    }

    public void startEncoding() {
        running = true;
        start();
    }

    public void stopEncoding() {
        running = false;
    }

    void calc2(short[] lin, int off, int len) {
        int i, j;
        for (i = 0; i < len; i++) {
            j = lin[i + off];
            lin[i + off] = (short) (j >> 1);
        }
    }
}
