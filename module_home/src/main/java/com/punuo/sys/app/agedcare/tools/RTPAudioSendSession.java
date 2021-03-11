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
public class RTPAudioSendSession {

    private DatagramSocket mRtpSocket;
    private DatagramSocket mRtcpSocket;
    private RTPSession mRTPSession;
    private long Ssrc;

    public RTPAudioSendSession(String ip, int port) {
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

    public void payloadType(int payloadT) {
        mRTPSession.payloadType(payloadT);
    }

    public long[] sendData(byte[] buf) {
        return mRTPSession.sendData(buf);
    }
}
