package com.punuo.sip.thread;

import com.punuo.sip.dev.message.SipDevMessageFactory;
import com.punuo.sip.user.message.SipUserMessageFactory;

/**
 * Created by han.chen.
 * Date on 2019-08-12.
 **/
public class SipInitThread extends Thread {

    @Override
    public void run() {
        super.run();
        SipUserMessageFactory.init();
        SipDevMessageFactory.init();
    }
}
