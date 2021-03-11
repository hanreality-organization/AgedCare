package com.punuo.sip;

/**
 * Created by han.chen.
 * Date on 2021/2/1.
 **/
public class H264Config {
    public final static int SINGLE_MONITOR = 1; //单向
    public final static int DOUBLE_MONITOR_POSITIVE = 2; //主动双向
    public final static int DOUBLE_MONITOR_NEGATIVE = 3; //被动双向
    public final static int IDLE = 4; // 闲置

    public static int monitorType = 0;

    public static int frameReceived = 0;

    public final static int FRAME_RECEIVED = 1; //单向
    public final static int FRAME_TIMEOUT = 2; //主动双向
    public final static int FRAME_UNSET = 0; //被动双向
}
