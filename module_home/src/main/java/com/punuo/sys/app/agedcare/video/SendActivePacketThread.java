package com.punuo.sys.app.agedcare.video;

import com.punuo.sys.app.agedcare.tools.PnVideoSession;

public class SendActivePacketThread extends Thread {
    private boolean running = false;
    private final byte[] msg = new byte[20];
    private PnVideoSession mVideoSession;

    public SendActivePacketThread(PnVideoSession session, byte[] magic) {
        mVideoSession = session;
        msg[0] = 0x00;
        msg[1] = 0x01;
        msg[2] = 0x00;
        msg[3] = 0x10;
        System.arraycopy(magic, 0, msg, 4, 16);
    }

    @Override
    public void run() {
        while (running) {
            try {
                if (mVideoSession != null) {
                    mVideoSession.payloadType(0x7a);
                    mVideoSession.sendData(msg);
                }
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void startThread() {
        running = true;
        super.start();
    }

    public void stopThread() {
        running = false;
        mVideoSession = null;
    }
}
