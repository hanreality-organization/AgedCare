package com.punuo.sys.app.agedcare.tools;

import android.util.Log;

import java.net.DatagramSocket;
import java.net.SocketException;

import jlibrtp.Participant;
import jlibrtp.RTPAppIntf;
import jlibrtp.RTPSession;

/**
 * Created by han.chen.
 * Date on 2021/3/11.
 **/
public class RTPVideoReceiveSession implements PnVideoSession {
    public static final String TAG = "RTPVideoReceiveSession";
    private DatagramSocket mRtpSocket;
    private RTPSession mRTPSession;
    private Participant mParticipant;
    private String networkAddress;
    private int remoteRtpPort;

    public RTPVideoReceiveSession(String networkAddress, int remoteRtpPort, RTPAppIntf rtpApp) {
        this.networkAddress = networkAddress;
        this.remoteRtpPort = remoteRtpPort;
        try {
            mRtpSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        mRTPSession = new RTPSession(mRtpSocket, null);
        mRTPSession.RTPSessionRegister(rtpApp, null, null);
        mParticipant = new Participant(networkAddress, remoteRtpPort, remoteRtpPort + 1);
        mRTPSession.addParticipant(mParticipant);
        mRTPSession.naivePktReception(false);
        mRTPSession.frameReconstruction(false);
    }


    @Override
    public long[] sendData(byte[] data) {
        Log.i(TAG, "keep alive sent to [socket://" + networkAddress + ":" + remoteRtpPort + "]");
        return mRTPSession.sendData(data);

    }

    @Override
    public void payloadType(int payloadT) {
        mRTPSession.payloadType(payloadT);
    }

    @Override
    public void release() {
        mRTPSession.removeParticipant(mParticipant);
        mRTPSession.endSession();
    }
}
