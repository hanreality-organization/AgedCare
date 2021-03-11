package com.punuo.sys.app.agedcare.video;

import android.os.StrictMode;
import android.util.Log;

import com.punuo.sip.H264Config;
import com.punuo.sip.user.H264ConfigUser;
import com.punuo.sys.app.agedcare.audio.AudioRecordManager;
import com.punuo.sys.app.agedcare.audio.G711;
import com.punuo.sys.app.agedcare.tools.RTPVideoReceiveSession;

import jlibrtp.DataFrame;
import jlibrtp.Participant;
import jlibrtp.RTPAppIntf;



/**
 * Author chzjy
 * Date 2016/12/19.
 */

public class RTPVideoReceiveImp implements RTPAppIntf {
    public static final String TAG = "RTPVideoReceiveImp";
    private final byte[] H264_STREAM_HEAD = {0x00, 0x00, 0x00, 0x01};
    private final StreamBuf streamBuf;
    private byte[] tempNal = new byte[200000];
    private int putNum;
    private int preSeq = -1;
    private int state = 0;
    boolean isKeyFrame = false;

    private RTPVideoReceiveSession mVideoReceiveSession;
    private SendActivePacketThread mSendActivePacketThread;
    public RTPVideoReceiveImp(String networkAddress, int remoteRtpPort) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        streamBuf = new StreamBuf(500, 10);
        for (int i = 0; i < VideoInfo.nalBuffers.length; i++) {
            VideoInfo.nalBuffers[i] = new NalBuffer();
        }
        putNum = 0;
        AudioRecordManager.getInstance().play();
        mVideoReceiveSession = new RTPVideoReceiveSession(networkAddress, remoteRtpPort, this);
        mSendActivePacketThread = new SendActivePacketThread(mVideoReceiveSession, H264ConfigUser.magic);
        mSendActivePacketThread.startThread();
    }

    @Override
    public void receiveData(DataFrame frame, Participant participant) {
        if (frame.payloadType() == 98) {
            StreamBufNode rtpFrameNode = new StreamBufNode(frame);
            streamBuf.addToBufBySeq(rtpFrameNode);
            if (streamBuf.isReady()) {
                try {
                    StreamBufNode streamBufNode = streamBuf.getFromBuf();
                    int seqNum = streamBufNode.getSeqNum();
                    byte[] data = streamBufNode.getDataFrame().getConcatenatedData();
                    int len = streamBufNode.getDataFrame().getTotalLength();
                    getNalData(data, seqNum, len);
                    H264Config.frameReceived = H264Config.FRAME_RECEIVED;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (frame.payloadType() == 69) {
            int frameSizeG711 = 160;
            byte[] audioBuffer = new byte[frameSizeG711];
            short[] audioData = new short[frameSizeG711];
            audioBuffer = frame.getConcatenatedData();
            G711.ulaw2linear(audioBuffer, audioData, frameSizeG711);
            AudioRecordManager.getInstance().write(audioData, 0, frameSizeG711);
        }
    }

    public void release() {
        if (mVideoReceiveSession != null) {
            mVideoReceiveSession.release();
        }
        if (mSendActivePacketThread != null) {
            mSendActivePacketThread.stopThread();
        }
    }

    @Override
    public void userEvent(int type, Participant[] participant) {

    }

    @Override
    public int frameSize(int payloadType) {
        return 1;
    }

    private boolean isFirstPacket(byte[] data) {
        return (data[0] & 31) == 28 && (data[1] & 0xe0) == 0x80;
    }

    private void handleFirstPacket(byte[] data, int seqNum, int len) {
        tempNal = new byte[200000];
        try {
            System.arraycopy(H264_STREAM_HEAD, 0, tempNal, 0, H264_STREAM_HEAD.length);
            System.arraycopy(data, 2, tempNal, 4, len - 2);
            VideoInfo.nalBuffers[putNum].setNalLen(len + 2);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        preSeq = seqNum;
        state = 1;
        Log.d(TAG, "handleFirstPacket");
    }

    private boolean isMiddlePacket(byte[] data) {
        return (data[0] & 31) == 28 && (data[1] & 0xe0) == 0x00;
    }

    private void handleMiddlePacket(byte[] data, int seqNum, int len) {
        try {
            System.arraycopy(data, 2, tempNal, VideoInfo.nalBuffers[putNum].getNalLen(), len - 2);
            VideoInfo.nalBuffers[putNum].addNalLen(len - 2);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        preSeq = seqNum;
        Log.d(TAG, "handleMiddlePacket");
    }

    private boolean isLastPacket(byte[] data) {
        return (data[0] & 31) == 28 && (data[1] & 0xe0) == 0x40;
    }

    private void handleLastPacket(byte[] data, int seqNum, int len) {
        try {
            System.arraycopy(data, 2, tempNal,
                    VideoInfo.nalBuffers[putNum].getNalLen(), len - 2);
            VideoInfo.nalBuffers[putNum].addNalLen(len - 2);
            VideoInfo.nalBuffers[putNum].isWriteable();
            System.arraycopy(tempNal, 0, VideoInfo.nalBuffers[putNum].getWriteable_Nalbuf(),
                    0, VideoInfo.nalBuffers[putNum].getNalLen());
            VideoInfo.nalBuffers[putNum].writeLock();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        preSeq = seqNum;
        ++putNum;
        if (putNum == 200) {
            putNum = 0;
        }
        state = 0;
        isKeyFrame = false;
        Log.d(TAG, "handleLastPacket");
    }

    public void getNalData(byte[] data, int seqNum, int len) {
        if (len <= 6) {
            resetTempNal(seqNum);
            return;
        }
        isKeyFrame = (data[6] & 31) == 7; //配套编码处关键帧前面必定是sps和pps，因此就判断是否未有sps
        if (isKeyFrame) {
            if (isFirstPacket(data)) {
                handleFirstPacket(data, seqNum, len);
            } else if (state == 1) {
                if (preSeq + 1 == seqNum) {
                    if (isLastPacket(data)) {
                        handleLastPacket(data, seqNum, len);
                    } else if (isMiddlePacket(data)) {
                        handleMiddlePacket(data, seqNum, len);
                    }
                } else {
                    resetTempNal(seqNum);
                }
            } else {
                resetTempNal(seqNum);
            }
        } else if ((data[0] & 31) == 28) {
            if (isFirstPacket(data)) {
                handleFirstPacket(data, seqNum, len);
            } else if (state == 1) {
                if (preSeq + 1 == seqNum) {
                    if (isLastPacket(data)) {
                        handleLastPacket(data, seqNum, len);
                    } else if (isMiddlePacket(data)) {
                        handleMiddlePacket(data, seqNum, len);
                    }
                } else {
                    resetTempNal(seqNum);
                }
            } else {
                resetTempNal(seqNum);
            }
        } else {
            if (preSeq == seqNum + 1) {
                handleCompletePacket(data, seqNum, len);
            } else {
                resetTempNal(seqNum);
            }
        }
    }

    private void resetTempNal(int seqNum) {
        tempNal = new byte[200000];
        VideoInfo.nalBuffers[putNum].setNalLen(0);
        preSeq = seqNum;
        state = 0;
    }

    private void handleCompletePacket(byte[] data, int seqNum, int len) {
        tempNal = new byte[100000];
        try {
            System.arraycopy(H264_STREAM_HEAD, 0, tempNal, 0, H264_STREAM_HEAD.length);
            System.arraycopy(data, 0, tempNal, H264_STREAM_HEAD.length, len);
            VideoInfo.nalBuffers[putNum].setNalLen(len + 4);
            VideoInfo.nalBuffers[putNum].isWriteable();
            System.arraycopy(tempNal, 0, VideoInfo.nalBuffers[putNum].getWriteable_Nalbuf(),
                    0, VideoInfo.nalBuffers[putNum].getNalLen());
            VideoInfo.nalBuffers[putNum].writeLock();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        preSeq = seqNum;
        ++putNum;
        if (putNum == 200) {
            putNum = 0;
        }
        state = 0;
        Log.d(TAG, "handleCompletePacket");
    }
}
