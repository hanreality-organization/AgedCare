package com.punuo.sys.app.agedcare.sip;

import org.zoolu.sip.message.Message;

/**
 * Author chzjy
 * Date 2016/12/19.
 */

public class KeepAlive extends Thread {
    private boolean running = false;
    private int type; //0代表用户,1代表设备

    @Override
    public void run() {
        while (running) {
            try {
                if (type==0) {
                    Message heartbeat = SipMessageFactory.createRegisterRequest(
                            SipInfo.sipUser, SipInfo.user_to, SipInfo.user_from,
                            BodyFactory.createHeartbeatBody());
                    SipInfo.sipUser.sendMessage(heartbeat);
                    //延时20s,等于是20s发送一次
                    sleep(20000);
                }else if(type==1){
                    Message heartbeat_dev = SipMessageFactory.createRegisterRequest(
                            SipInfo.sipDev, SipInfo.dev_to, SipInfo.dev_from,
                            BodyFactory.createHeartbeatBody());
                    SipInfo.sipDev.sendMessage(heartbeat_dev);
                    sleep(20000);
                }
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

    public void setType(int type) {
        this.type = type;
    }
}
