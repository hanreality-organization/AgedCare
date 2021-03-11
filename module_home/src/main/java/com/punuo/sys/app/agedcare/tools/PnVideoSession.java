package com.punuo.sys.app.agedcare.tools;

/**
 * Created by han.chen.
 * Date on 2021/3/11.
 **/
public interface PnVideoSession {

    long[] sendData(byte[] data);

    void payloadType(int payloadT);

    void release();
}
