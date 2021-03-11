package com.punuo.sys.app.agedcare.tools;

import com.punuo.sip.dev.H264ConfigDev;

import java.net.DatagramSocket;
import java.net.SocketException;

import jlibrtp.Participant;
import jlibrtp.RTPSession;

/**
 * Created by han.chen.
 * Date on 2021/3/10.
 **/
public class RTPVideoSendSession implements PnVideoSession {
    public static final String TAG = "RTPVideoSendSession";
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
    private DatagramSocket mRtpSocket;
    private DatagramSocket mRtcpSocket;
    private RTPSession mRTPSession;
    private long Ssrc;
    private byte[] rtppkt = new byte[DIVIDE_LENGTH + 2];

    public RTPVideoSendSession(String ip, int port) {
        try {
            mRtpSocket = new DatagramSocket();
            mRtcpSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        mRTPSession = new RTPSession(mRtpSocket, mRtcpSocket);
        Participant participant = new Participant(ip, port, port + 1);
        mRTPSession.addParticipant(participant);
        Ssrc = (H264ConfigDev.magic[15] & 0x000000ff)
                | ((H264ConfigDev.magic[14] << 8) & 0x0000ff00)
                | ((H264ConfigDev.magic[13] << 16) & 0x00ff0000)
                | ((H264ConfigDev.magic[12] << 24) & 0xff000000);
        mRTPSession.setSsrc(Ssrc);
    }

    @Override
    public void payloadType(int payloadT) {
        mRTPSession.payloadType(payloadT);
    }

    @Override
    public void release() {

    }

    @Override
    public long[] sendData(byte[] buf) {
        return mRTPSession.sendData(buf);
    }

    /**
     * 生成RTP心跳保活包，即在magic之前再加上0x00 0x01 0x00 0x10
     */
    public void setRtpHeartBeatData() {
        byte[] msg = new byte[20];
        msg[0] = 0x00;
        msg[1] = 0x01;
        msg[2] = 0x00;
        msg[3] = 0x10;
        try {
            System.arraycopy(H264ConfigDev.magic, 0, msg, 4, 16);
        } catch (Exception e) {
            e.printStackTrace();
        }
        payloadType(0x7a);
        sendData(msg);
    }

    /**
     * 分片、发送方法
     */
    public void divideAndSendNal(byte[] encodeData) {
        if (encodeData.length <= 0) {
            return;
        }

        if (encodeData.length > DIVIDE_LENGTH) {
            status = true;
            firstPktReceived = false;
            pktflag = 0;

            while (status) {
                if (!firstPktReceived) {  //首包
                    sendFirstPacket(encodeData);
                } else {
                    if (encodeData.length - pktflag > DIVIDE_LENGTH) {  //中包
                        sendMiddlePacket(encodeData);
                    } else {   //末包
                        sendLastPacket(encodeData);
                    }
                } //end of 首包
            }//end of while
        } else {   //不分片包
            sendCompletePacket(encodeData);
        }
    }

    /**
     * 发送首包
     */
    public void sendFirstPacket(byte[] encodeData) {
        rtppkt[0] = (byte) (encodeData[0] & 0xe0);
        rtppkt[0] = (byte) (rtppkt[0] + 0x1c);
        rtppkt[1] = (byte) (0x80 + (encodeData[0] & 0x1f));
        payloadType(0x62);
        System.arraycopy(encodeData, 0, rtppkt, 2, DIVIDE_LENGTH);
        pktflag = pktflag + DIVIDE_LENGTH;
        firstPktReceived = true;
        //发送打包数据
        sendData(rtppkt);   //发送打包数据
    }

    /**
     * 发送中包
     */
    public void sendMiddlePacket(byte[] encodeData) {
        rtppkt[0] = (byte) (encodeData[0] & 0xe0);
        rtppkt[0] = (byte) (rtppkt[0] + 0x1c);
        rtppkt[1] = (byte) ((encodeData[0] & 0x1f));
        System.arraycopy(encodeData, pktflag, rtppkt, 2, DIVIDE_LENGTH);
        pktflag = pktflag + DIVIDE_LENGTH;
        //设置RTP包的负载类型为0x62
        payloadType(0x62);
        //发送打包数据
        sendData(rtppkt);
    }

    /**
     * 发送末包
     */
    public void sendLastPacket(byte[] encodeData) {
        byte[] rtppktLast = new byte[encodeData.length - pktflag + 2];
        rtppktLast[0] = (byte) (encodeData[0] & 0xe0);
        rtppktLast[0] = (byte) (rtppktLast[0] + 0x1c);
        rtppktLast[1] = (byte) (0x40 + (encodeData[0] & 0x1f));
        System.arraycopy(encodeData, pktflag, rtppktLast, 2, encodeData.length - pktflag);
        //设置RTP包的负载类型为0x62
        payloadType(0x62);
        //发送打包数据
        sendData(rtppktLast);   //发送打包数据  //发送打包数据
        status = false;  //打包组包结束，下一步进行解码
    }

    /**
     * 发送完整包
     */
    public void sendCompletePacket(byte[] encodeData) {
        //设置RTP包的负载类型为0x62
        payloadType(0x62);
        //发送打包数据
        sendData(encodeData);
    }

}
