package com.punuo.sys.app.agedcare.video;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Environment;
import android.util.Log;

import com.punuo.sys.app.agedcare.groupvoice.G711;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.DatagramSocket;
import java.net.SocketException;

import jlibrtp.DataFrame;
import jlibrtp.Participant;
import jlibrtp.RTPAppIntf;
import jlibrtp.RTPSession;

/**
 * Author chzjy
 * Date 2016/12/19.
 */

public class RtpVideo implements RTPAppIntf {

    BufferedOutputStream outputStream;
    BufferedOutputStream outputStream2;
    private final byte H264_STREAM_HEAD[] = {0x00, 0x00, 0x00, 0x01};
    private RTPSession rtpSession;
    private DatagramSocket rtpSocket;
    private StreamBuf streamBuf;
    private byte tempNal[] = new byte[1000000];
    private int tempNalLen = 0;
    private int putNum;
    private int preSeq;
    private boolean isPacketLost = true;
    private int frameSizeG711 = 160;
    private int samp_rate = 8000;
    private int maxjitter = AudioTrack.getMinBufferSize(samp_rate,
            AudioFormat.CHANNEL_CONFIGURATION_MONO,
            AudioFormat.ENCODING_PCM_16BIT);
    private boolean isEndRtp = true;
    private boolean isIFrame = false;
    Participant p;
    File f1;
    File f;
//    File f1 = new File(Environment.getExternalStorageDirectory(), "DCIM/video_afterMecoded.264");

    public RtpVideo(String networkAddress, int remoteRtpPort) throws SocketException {
        rtpSocket = new DatagramSocket();
        rtpSocket.setReceiveBufferSize(64*2048);
        rtpSession = new RTPSession(rtpSocket, null);
        rtpSession.RTPSessionRegister(this, null, null);
        p = new Participant(networkAddress, remoteRtpPort, remoteRtpPort + 1);
        rtpSession.addParticipant(p);
        rtpSession.naivePktReception(false);
        rtpSession.frameReconstruction(false);
        streamBuf = new StreamBuf(2000, 20);
        for (int i = 0; i < VideoInfo.nalBuffers.length; i++) {
            VideoInfo.nalBuffers[i] = new NalBuffer();
        }
        putNum = 0;
        VideoInfo.track = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                samp_rate,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                maxjitter,
                AudioTrack.MODE_STREAM
        );
        VideoInfo.track.play();
        f = new File(Environment.getExternalStorageDirectory(), "DCIM/video_beforeMecoded.264");
        f1 = new File(Environment.getExternalStorageDirectory(), "DCIM/video_afterMecoded.264");
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(f));
            outputStream2= new BufferedOutputStream(new FileOutputStream(f1));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void receiveData(DataFrame frame, Participant participant) {
        if (frame.payloadType() == 98) {
            StreamBufNode rtpFrameNode = new StreamBufNode(frame);
            streamBuf.addToBufBySeq(rtpFrameNode);
            //Log.e("rtp", "step01");
            if (streamBuf.isReady()) {
                isEndRtp = true;
                Log.e("rtp", "step02");
                while (isEndRtp) {
                    StreamBufNode streamBufNode = streamBuf.getFromBuf();
                    if (streamBufNode==null){
                        break;
                    }
                    int seqNum = streamBufNode.getSeqNum();
                    byte[] data = streamBufNode.getDataFrame().getConcatenatedData();
                    int len = streamBufNode.getDataFrame().getTotalLength();
//                    Log.e("Rtp", "len:" + len + "  seqNum:" + seqNum );
//                    Log.e("rtp", "step03");
                    getNalDm365(streamBufNode,data,seqNum,len);
                 //   Log.e("rtp", "isEndRtp "+isEndRtp);
//                    getNalDm365(data, seqNum, len);
//                    VideoInfo.isrec = 2;
                }
                checkNalDm365();
                VideoInfo.isrec = 2;
            }
        } else if (frame.payloadType() == 69) {
            byte[] audioBuffer = new byte[frameSizeG711];
            short[] audioData = new short[frameSizeG711];
            audioBuffer = frame.getConcatenatedData();
            G711.ulaw2linear(audioBuffer, audioData, frameSizeG711);
            VideoInfo.track.write(audioData, 0, frameSizeG711);


        } else if (frame.payloadType() == 70) {
            byte data1[] = frame.getConcatenatedData();
            Log.e("wumalv", "receiveData: " + data1[0]);
//                    StringBuffer ss=new StringBuffer();
//                    for (int i=0;i<data1.length;i++){
//                Log.e("wumalv", "receiveData: "+intToHex(data1[12]));

        }
    }

    private static String intToHex(int n) {
        StringBuffer s = new StringBuffer();
        String a;
        char[] b = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        while (n != 0) {
            s = s.append(b[n % 16]);
            n = n / 16;
        }
        a = s.reverse().toString();
        return a;
    }

    public void endSession() {
        rtpSession.endSession();
    }

    @Override
    public void userEvent(int type, Participant[] participant) {

    }

    //移除当前监听的端口
    public void removeParticipant() {
        rtpSession.removeParticipant(p);
    }

    @Override
    public int frameSize(int payloadType) {
        return 1;
    }

    public void sendActivePacket(byte[] msg) {
        rtpSession.payloadType(0x7a);

        for (int i = 0; i < 2; i++) {
            rtpSession.sendData(msg);
        }
    }

    public void getNalDm365(StreamBufNode streamBufNode,byte[] data, int seqNum, int len) {

        switch (frameParseDm365(data)) {
            case 0:
//                Log.e("danbao",bytesToHexString(data));
                addCompleteRtpPacketToTemp(data, seqNum, len);
                copyFromTempToNal();
//                try {
//                    outputStream.write(data,0,len);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                break;
            case 1:
                LinkedNode.head.addBySeqNum(streamBufNode);
          //      Log.e("rtp", "step03 1");
//                try {
//                    outputStream.write(data,2,len-2);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                break;
            case 2:
                LinkedNode.head.addBySeqNum(streamBufNode);
           //     Log.e("rtp", "step03 2");
//                try {
//                    outputStream.write(data,2,len-2);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                break;
            case 3:
                isEndRtp = false;
                LinkedNode.head.addBySeqNum(streamBufNode);
           //     Log.e("rtp", "step03 3");
//                try {
//                    outputStream.write(data,2,len-2);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                break;
        }
    }

    public void checkNalDm365() {
        Log.e("rtp", "step04");
        if (LinkedNode.head.getSize() <= 1) {
            return;
        }
        if (LinkedNode.head.getSize() >= 2) {
            LinkedNode temp = LinkedNode.head;
            while (temp.getNext()!= null){
                temp = temp.getNext();
                StreamBufNode streamBufNode = temp.getStreamBufNode();
                int seqNum = streamBufNode.getSeqNum();
                byte[] data = streamBufNode.getDataFrame().getConcatenatedData();
                int len = streamBufNode.getDataFrame().getTotalLength();
                if (frameParseDm365(data) == 1){
                    if((data[6]&0x1f)==5){
                        isIFrame =true;
                    }
                    addFirstRtpPacketToTemp(data, seqNum, len);
                    isPacketLost = false;
                    break;
                }
            }
            while (temp.getNext()!= null) {
                temp=temp.getNext();
                StreamBufNode streamBufNode = temp.getStreamBufNode();
                int seqNum = streamBufNode.getSeqNum();
                byte[] data = streamBufNode.getDataFrame().getConcatenatedData();
                int len = streamBufNode.getDataFrame().getTotalLength();
                if (!isPacketLost) {
                    if (frameParseDm365(data) == 3) {
                        if (preSeq + 1 == seqNum) {
                            addLastRtpPacketToTemp(data, seqNum, len);
                            Log.e("RtpVideo", "当前帧完整");
                       //     if(isIFrame){
                                copyFromTempToNal();
                      //      }
                        } else {
                            Log.e("RtpVideo", "当前帧丢包");
                            isIFrame=false;
                            addLastRtpPacketToTemp(data, seqNum, len);
                            copyFromTempToNal();
//                            jumpNal();
                        }
                        if(temp.getNext()!=null){
                            LinkedNode.head.setNext(temp.getNext());
                        }else {
                            LinkedNode.head.setNext(null);
                        }
                        return;
                    }
                    if (frameParseDm365(data) == 2){
                        if (preSeq + 1 == seqNum) {
                            addMiddleRtpPacketToTemp(data, seqNum, len);
                        } else {
                            Log.e("RtpVideo", "当前帧丢包");
                            addMiddleRtpPacketToTemp(data, seqNum, len);
//                            jumpNal();
                            isIFrame=false;
                            if(temp.getNext()!=null){
                                LinkedNode.head.setNext(temp.getNext());
                            }else {
                                LinkedNode.head.setNext(null);
                            }
                            return;
                        }
                    }
                }
            }
           // Log.e("RtpVideo", "当前帧丢包");
           // jumpNal();
        }
    }

    public int frameParseDm365(byte[] data) {
        if ((data[0] & 0x1f) == 28 || (data[0] & 0x1f) == 29) {//先判断是否是分片
            if ((data[1] & 0xe0) == 0x80) {
                return 1;//分片首包
            } else if ((data[1] & 0xe0) == 0x00) {
                return 2;//分片中包
            } else {
                return 3;//分片末包
            }
        } else {//不是分片
            return 0;//单包
        }
    }

    private void addCompleteRtpPacketToTemp(byte[] data, int seqNum, int len) {
        tempNal = new byte[1000000];
        tempNal[0] = H264_STREAM_HEAD[0];
        tempNal[1] = H264_STREAM_HEAD[1];
        tempNal[2] = H264_STREAM_HEAD[2];
        tempNal[3] = H264_STREAM_HEAD[3];
//        System.arraycopy(data, 0, tempNal, 4, len);
//        tempNalLen = len + 4;
        System.arraycopy(data, 4, tempNal, 4, len-4);
     tempNalLen = len ;
        preSeq = seqNum;
    }

    private void addFirstRtpPacketToTemp(byte[] data, int seqNum, int len) {
        tempNal = new byte[1000000];
        tempNal[0] = H264_STREAM_HEAD[0];
        tempNal[1] = H264_STREAM_HEAD[1];
        tempNal[2] = H264_STREAM_HEAD[2];
        tempNal[3] = H264_STREAM_HEAD[3];
//        System.arraycopy(data, 2, tempNal, 4, len - 2);
//        tempNalLen = len + 2;
        System.arraycopy(data, 6, tempNal, 4, len-6);
       tempNalLen = len -2;
        preSeq = seqNum;
    }

    private void addMiddleRtpPacketToTemp(byte[] data, int seqNum, int len) {
        System.arraycopy(data, 2, tempNal, tempNalLen, len - 2);
        tempNalLen += len - 2;
        preSeq = seqNum;
    }

    private void addLastRtpPacketToTemp(byte[] data, int seqNum, int len) {
        System.arraycopy(data, 2, tempNal, tempNalLen, len - 2);
        tempNalLen += len - 2;
        preSeq = seqNum;
    }

    private void copyFromTempToNal() {
        VideoInfo.nalBuffers[putNum].setNalBuf(tempNal, tempNalLen);
        VideoInfo.nalBuffers[putNum].writeLock();
//        try {
//            outputStream2.write(tempNal,0,tempNalLen);
//            }
//         catch (IOException e) {
//            e.printStackTrace();
//        }
//        Log.d("rtp", "nalLen:" + tempNalLen);
        putNum++;
        if (putNum == 200) {
            putNum = 0;
        }
    }

    private void jumpNal() {
        VideoInfo.nalBuffers[putNum].writeLock();
        VideoInfo.nalBuffers[putNum].cleanNalBuf2();
       // preSeq = seqNum;
        isPacketLost = true;
        putNum++;
        if (putNum == 200) {
            putNum = 0;
        }
    }

    /**
     * 数组转换成十六进制字符串
     *
     * @param
     * @return HexString
     */
    public static final String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    //判断有无收到PPS；
    private Boolean isSpsAndPps(byte[] data, int seqNum, int len) {
        boolean isSpsAndPps = false;
        if (((data[4] & 0x1f) == 7) && ((data[0] & 0xff) == 0) && ((data[1] & 0xff) == 0)
                && ((data[2] & 0xff) == 0) && ((data[3] & 0xff) == 1)) {
            isSpsAndPps = true;
        }
        return isSpsAndPps;
    }

}
