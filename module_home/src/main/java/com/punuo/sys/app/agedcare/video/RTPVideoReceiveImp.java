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
    private byte[] tempNal = new byte[200000];
    private int tempNalLength = 0;
    private int preSeq = -1;
    private int state = 0;
    boolean isKeyFrame = false;

    private RTPVideoReceiveSession mVideoReceiveSession;
    private SendActivePacketThread mSendActivePacketThread;
    private final VideoStreamBuffer mVideoStreamBuffer;
    public final static VideoNalBuffer mVideoNalBuffer = new VideoNalBuffer();

    public RTPVideoReceiveImp(String networkAddress, int remoteRtpPort) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mVideoStreamBuffer = new VideoStreamBuffer();
        mVideoNalBuffer.clear();
        AudioRecordManager.getInstance().play();
        mVideoReceiveSession = new RTPVideoReceiveSession(networkAddress, remoteRtpPort, this);
        mSendActivePacketThread = new SendActivePacketThread(mVideoReceiveSession, H264ConfigUser.magic);
        mSendActivePacketThread.startThread();
    }

    @Override
    public void receiveData(DataFrame frame, Participant participant) {
        if (frame.payloadType() == 98) {
            mVideoStreamBuffer.appendDateFrame(frame);
            H264Config.frameReceived = H264Config.FRAME_RECEIVED;
            if (mVideoStreamBuffer.isReady()) {
                try {
                    DataFrame dataFrame = mVideoStreamBuffer.getDataFrame();
                    if (dataFrame != null) {
                        int seqNum = dataFrame.sequenceNumbers()[0];
                        byte[] data = dataFrame.getConcatenatedData();
                        int len = dataFrame.getTotalLength();
                        getNalData(data, seqNum, len);
                    }
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
        if (mVideoStreamBuffer != null) {
            mVideoStreamBuffer.clear();
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
        tempNalLength = 0;
        try {
            System.arraycopy(H264_STREAM_HEAD, 0, tempNal, 0, H264_STREAM_HEAD.length);
            System.arraycopy(data, 2, tempNal, 4, len - 2);
            tempNalLength = len + 2;
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        preSeq = seqNum;
        state = 1;
    }

    private boolean isMiddlePacket(byte[] data) {
        return (data[0] & 31) == 28 && (data[1] & 0xe0) == 0x00;
    }

    private void handleMiddlePacket(byte[] data, int seqNum, int len) {
        try {
            System.arraycopy(data, 2, tempNal, tempNalLength, len - 2);
            tempNalLength += len - 2;
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        preSeq = seqNum;
    }

    private boolean isLastPacket(byte[] data) {
        return (data[0] & 31) == 28 && (data[1] & 0xe0) == 0x40;
    }

    private void handleLastPacket(byte[] data, int seqNum, int len) {
        try {
            System.arraycopy(data, 2, tempNal, tempNalLength, len - 2);
            tempNalLength += len - 2;
            mVideoNalBuffer.offerData(tempNal, tempNalLength);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        preSeq = seqNum;
        state = 0;
        isKeyFrame = false;
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
        tempNalLength = 0;
        preSeq = seqNum;
        state = 0;
    }

    private void handleCompletePacket(byte[] data, int seqNum, int len) {
        tempNal = new byte[200000];
        tempNalLength = 0;
        try {
            System.arraycopy(H264_STREAM_HEAD, 0, tempNal, 0, H264_STREAM_HEAD.length);
            System.arraycopy(data, 0, tempNal, H264_STREAM_HEAD.length, len);
            tempNalLength = len + 4;
            mVideoNalBuffer.offerData(tempNal, tempNalLength);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        preSeq = seqNum;
        state = 0;
    }
}
