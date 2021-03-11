package com.punuo.sys.app.agedcare.video;

public class VideoInfo {

    public static NalBuffer[] nalBuffers = new NalBuffer[200];
    /**
     * 每一个新的NAL设置首包打包状态为false，即没有打包首包
     */
    public static boolean firstPktReceived = false;
    /**
     * 记录打包分片的索引
     */
    public static int pktflag = 0;
    /**
     * 若未打包到末包，则此状态一直为true
     */
    public static boolean status = true;
    /**
     * 打包分片长度
     */
    public static int DIVIDE_LENGTH = 1000;

}
