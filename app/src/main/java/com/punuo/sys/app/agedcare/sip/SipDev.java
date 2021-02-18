package com.punuo.sys.app.agedcare.sip;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.util.Log;

import com.punuo.sys.app.agedcare.groupvoice.GroupInfo;
import com.punuo.sys.app.agedcare.model.Device;
import com.punuo.sys.app.agedcare.ui.MessageEvent;
import com.punuo.sys.app.agedcare.video.VideoInfo;

import org.greenrobot.eventbus.EventBus;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;
import org.zoolu.sip.header.FromHeader;
import org.zoolu.sip.message.Message;
import org.zoolu.sip.provider.SipProvider;
import org.zoolu.sip.provider.Transport;
import org.zoolu.sip.provider.TransportConnId;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import android_serialport_api.SerialPort;


import static android.content.Context.MODE_PRIVATE;
import static com.punuo.sys.app.agedcare.sip.SipInfo.devName;
import static com.punuo.sys.app.agedcare.sip.SipInfo.isanswering;

import static com.punuo.sys.app.agedcare.sip.SipInfo.ismoniter;
import static com.punuo.sys.app.agedcare.sip.SipInfo.shareurl;
import static com.punuo.sys.app.agedcare.sip.SipInfo.userdevid;



/**
 * Author chzjy
 * Date 2016/12/19.
 */

public class SipDev extends SipProvider {
    FileOutputStream mOutputStream;
    SerialPort mSerialPort;
    public static final String TAG = "SipDev";
    public static final String[] PROTOCOLS = {"udp"};
    public static boolean isagree;

    private Context context;
    private ExecutorService pool = Executors.newFixedThreadPool(3);
    private WorkerLoginListener workerLoginListener;
    private NumberUpdateListener numberUpdateListener;
    private ScreenproUpdateListener screenproUpdateListener;
    public static final byte[] TURN_LEFT={(byte) 0xff,0x01,0x00,0x04, (byte) 0xff,0x00,0x04};
    public static final byte[] TURN_RIGHT={(byte) 0xff,0x01,0x00,0x02, (byte) 0xff,0x00,0x02};
    public static final byte[] STOP={(byte) 0xff,0x01,0x00,0x00,0x00,0x00,0x01};

    public SipDev(Context context, String viaAddr, int hostPort) {
        super(viaAddr, hostPort, PROTOCOLS, null);
        this.context = context;
        try {
            mSerialPort = new SerialPort(new File("/dev/" + "ttyMT1"), 2400,0);
            mOutputStream = (FileOutputStream) mSerialPort.getOutputStream();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TransportConnId sendMessage(Message msg) {
        return sendMessage(msg, SipInfo.serverIp, SipInfo.SERVER_PORT_DEV);
    }

    public TransportConnId sendMessage(final Message msg, final String destAddr, final int destPort) {
        Log.d(TAG, "<----------send sip message---------->");
        Log.d(TAG, msg.toString());
        TransportConnId id = null;
        try {
            id = pool.submit(new Callable<TransportConnId>() {
                public TransportConnId call() {
                    return sendMessage(msg, "udp", destAddr, destPort, 0);
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return id;
    }
    //结束线程池
    public void shutdown(){
        pool.shutdown();
    }
    public synchronized void onReceivedMessage(Transport transport, Message msg) {
        Log.d(TAG, "<----------received sip message---------->");
        Log.d(TAG, msg.toString());
        int port = msg.getRemotePort();
        if (port == SipInfo.SERVER_PORT_DEV) {
            Log.e(TAG, "onReceivedMessage: " + port);
            String body = msg.getBody();
            if (msg.isRequest()) {// 请求消息
                if (!requestParse(msg)) {
                    int requestType = bodyParse(body);
                }
            } else { // 响应消息
                int code = msg.getStatusLine().getCode();
                if (code == 200) {
                    if (!responseParse(msg)) {
                        bodyParse(body);
                    }
                } else if (code == 401) {
                    SipInfo.dev_loginTimeout = false;
                } else if (code == 402) {

                }
            }
        }
    }

    private int bodyParse(String body) {
        if (body != null) {
            StringReader sr = new StringReader(body);
            InputSource is = new InputSource(sr);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            Document document;
            try {
                builder = factory.newDocumentBuilder();
                document = builder.parse(is);
                Element root = document.getDocumentElement();
                String type = root.getTagName();
                switch (type) {
                    case "negotiate_response"://注册第一步响应
                        Element seedElement = (Element) root.getElementsByTagName("seed").item(0);
                        SipURL local = new SipURL(SipInfo.devId, SipInfo.serverIp, SipInfo.SERVER_PORT_DEV);
                        SipInfo.dev_from.setAddress(local);
                        Log.d(TAG, "收到设备注册第一步响应");
                        String password = "123456";
                        Message register = SipMessageFactory.createRegisterRequest(
                                SipInfo.sipDev, SipInfo.dev_to, SipInfo.dev_from,
                                BodyFactory.createRegisterBody(/*随便输*/password));
                        SipInfo.sipDev.sendMessage(register);
                        return 0;
                    case "login_response"://注册成功响应，心跳回复
                        if (SipInfo.devLogined) {
                            SipInfo.dev_heartbeatResponse = true;
                            Log.d(TAG, "设备收到心跳回复");
                        } else {
                            //获取电源锁,用于防止手机静默之后,心跳线程暂停
                            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                            GroupInfo.wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, context.getClass().getCanonicalName());
                            GroupInfo.wakeLock.setReferenceCounted(false);
                            GroupInfo.wakeLock.acquire();

                            SipInfo.devLogined = true;
                            SipInfo.dev_loginTimeout = false;
                            Log.d(TAG, "设备注册成功");
                            /*群组呼叫组别查询*/
                            SipInfo.sipDev.sendMessage(SipMessageFactory.createSubscribeRequest(SipInfo.sipDev,
                                    SipInfo.dev_to, SipInfo.dev_from, BodyFactory.createGroupSubscribeBody(SipInfo.devId)));
                        }
                        return 1;
                }
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Log.d(TAG, "body is null");
        }
        return -1;
    }

    private boolean requestParse(Message msg) {
        Log.e("echo_tag", "0 - sipDev - requestParse: " + msg);
        if(msg.isBye()){

            SipInfo.notifymedia.sendEmptyMessage(0x2222);
        if (!ismoniter){
            Message bye = SipMessageFactory.createByeRequest(SipInfo.sipUser, SipInfo.toDev, SipInfo.user_from);//创建结束视频请求
            SipInfo.sipUser.sendMessage(bye);
        }else {

            ismoniter=false;}
            return true;
        }
        String body = msg.getBody();
        if (body != null) {
            StringReader sr = new StringReader(body);
            InputSource is = new InputSource(sr);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            Document document;
            try {
                builder = factory.newDocumentBuilder();
                document = builder.parse(is);
                Element root = document.getDocumentElement();
                String type = root.getTagName();
                switch (type) {
                    case "query":
//                        Message message = SipMessageFactory.createResponse(msg, 200, "OK",
//                                BodyFactory.createOptionsBody("MOBILE_S9"));
                        Message message = SipMessageFactory.createResponse(msg, 200, "OK",
                                BodyFactory.createOptionsBody("MOBILE_S6"));
                        SipInfo.sipDev.sendMessage(message);


                        FromHeader fromHeader = msg.getFromHeader();
                        String userName = fromHeader.getNameAddress().getAddress().getUserName();
                        for (Device item : SipInfo.devList) {
                            if (item.getUserid().equals(userName)) {
                                SipInfo.sipReqFromUser = item.getId();
                                break;
                            }
                        }
                        return true;
                    case "is_monitor":

                        ismoniter=true;
                        Element devidElement1 = (Element) root.getElementsByTagName("devid").item(0);
                        SipInfo.userdevid=devidElement1.getFirstChild().getNodeValue();
                        Log.d(TAG,userdevid);
                        SipURL sipURL2 = new SipURL(userdevid, SipInfo.serverIp, SipInfo.SERVER_PORT_USER);
                        SipInfo.toDev = new NameAddress(devName, sipURL2);

                        SipInfo.sipDev.sendMessage(SipMessageFactory.createResponse(msg, 200, "Ok", ""));
                        return true;
                    case "media":
                        Log.e(TAG + "_echo" + "_request",   msg.toString());
                        Element peerElement = (Element) root.getElementsByTagName("peer").item(0);
                        Element magicElement = (Element) root.getElementsByTagName("magic").item(0);
                        String peer = peerElement.getFirstChild().getNodeValue();
                        String magic = magicElement.getFirstChild().getNodeValue();
                        VideoInfo.media_info_ip = peer.substring(0, peer.indexOf("UDP")).trim();
                        VideoInfo.media_info_port = Integer.parseInt(peer.substring(peer.indexOf("UDP") + 3).trim());
                        Log.e("media_info_port",VideoInfo.media_info_port+"");
                        VideoInfo.media_info_magic = new byte[magic.length() / 2 + magic.length() % 2];
                        for (int i = 0; i < VideoInfo.media_info_magic.length; i++) {
                            try {
                                VideoInfo.media_info_magic[i] = (byte) (0xff & Integer.parseInt(magic.substring(i * 2, i * 2 + 2), 16));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                        SipInfo.msg = msg;
                        if (ismoniter)
                        {
                            SipInfo.notifymedia.sendEmptyMessage(0x3333);

                        }else {
                        SipInfo.notifymedia.sendEmptyMessage(0x1111);

                        }
                        return true;
                    case "recvaddr":
                        VideoInfo.endView = true;
                        SipInfo.sipDev.sendMessage(SipMessageFactory.createResponse(msg, 200, "Ok", ""));
                        return true;
                    case "direction_control":
                        Element operateElement = (Element) root.getElementsByTagName("operate").item(0);
                        String operate=operateElement.getFirstChild().getNodeValue();
                        if (operate.equals("left")){
                            sendData(TURN_LEFT);
                            SipInfo.sipDev.sendMessage(SipMessageFactory.createResponse(msg, 200, "Ok", ""));
                        }else if (operate.equals("right")){
                            sendData(TURN_RIGHT);
                            SipInfo.sipDev.sendMessage(SipMessageFactory.createResponse(msg, 200, "Ok", ""));
                        }else if (operate.equals("stop")){
                            sendData(STOP);
                            SipInfo.sipDev.sendMessage(SipMessageFactory.createResponse(msg, 200, "Ok", ""));
                        }
                    case "list_update" :
                        this.numberUpdateListener.numberUpdate();
                        break;
                    case "call_response":
                        Element responseElement = (Element) root.getElementsByTagName("operate").item(0);
                        String response=responseElement.getFirstChild().getNodeValue();
                        if (response.equals("agree")){
                            isagree=true;
                            Log.d(TAG,"同意视频请求");
                            SipInfo.sipDev.sendMessage(SipMessageFactory.createResponse(msg, 200, "Ok", ""));
                            Intent intent = new Intent("com.example.broadcast.CALL_AGREE");
                            context.sendBroadcast(intent);

                        }else if (response.equals("refuse")){
                            isagree=false;
                            isanswering=false;
                            Log.d(TAG,"拒绝视频请求");
                            SipInfo.sipDev.sendMessage(SipMessageFactory.createResponse(msg, 200, "Ok", ""));
                            EventBus.getDefault().post(new MessageEvent("取消"));
                        }else if (response.equals("cancel"))
                        {
                            isanswering=false;
                            EventBus.getDefault().post(new MessageEvent("取消"));

                            Log.d(TAG,"取消拨打");
                        }
                        break;
                    case"operation":

                        if (!isanswering)
                        {
                        Element devidElement = (Element) root.getElementsByTagName("devId").item(0);
                        SipInfo.userdevid=devidElement.getFirstChild().getNodeValue();
                        Log.d(TAG,userdevid);
                        Element useridElement = (Element) root.getElementsByTagName("userId").item(0);
                        SipInfo.videouserId=useridElement.getFirstChild().getNodeValue();

                        Intent intent=new Intent("com.example.broadcast.CALL_REQUEST");
                        context.getApplicationContext().sendBroadcast(intent);
//                        EventBus.getDefault().post(new MessageEvent("视频来电"));
                        isanswering=true;
                        }
                        else if (isanswering=true)
                        {
                            Element devidElement = (Element) root.getElementsByTagName("devId").item(0);
                            SipInfo.userdevid=devidElement.getFirstChild().getNodeValue();
                            Log.d(TAG,userdevid);
                            Element useridElement = (Element) root.getElementsByTagName("userId").item(0);
                            SipInfo.videouserId=useridElement.getFirstChild().getNodeValue();
                            SipURL sipURL = new SipURL(userdevid, SipInfo.serverIp, SipInfo.SERVER_PORT_USER);
                            SipInfo.toDev = new NameAddress(devName, sipURL);
                            org.zoolu.sip.message.Message busy = SipMessageFactory.createNotifyRequest(SipInfo.sipUser, SipInfo.toDev,
                                    SipInfo.user_from, BodyFactory.createVideoBusy(""+isanswering));
                            SipInfo.sipUser.sendMessage(busy);
                        }
//                        else {
//                            SipURL sipURL = new SipURL(userdevid, SipInfo.serverIp, SipInfo.SERVER_PORT_USER);
//                            SipInfo.toDev = new NameAddress(devName, sipURL);
//                            org.zoolu.sip.message.Message request2 = SipMessageFactory.createNotifyRequest(SipInfo.sipUser, SipInfo.toDev,
//                                    SipInfo.user_from, BodyFactory.createCallReply("refuse"));
//                            SipInfo.sipUser.sendMessage(request2);
//
//                        }

//                    case "user_mode":
//                        Log.d("aaaaa","user_mode");
//
//                            Element userIdElement = (Element) root.getElementsByTagName("userId").item(0);
//                            qinliaouserid=userIdElement.getFirstChild().getNodeValue();
//                            Log.d("aaaaa",qinliaouserid);
                        break;
                    case "service_call":
                        Element itemElement = (Element) root.getElementsByTagName("item").item(0);
                        Element telephoneElement = (Element) root.getElementsByTagName("telephone").item(0);
                        String item = itemElement.getFirstChild().getNodeValue();
                        String telephone = telephoneElement.getFirstChild().getNodeValue();

                        SharedPreferences preferences= context.getSharedPreferences("data",MODE_PRIVATE);
                        SharedPreferences.Editor editor=preferences.edit();
                        editor.putString(item,telephone);
                        editor.apply();
                        Log.d("wuye111",item+telephone);
                        break;
                    case"stop_monitor":
                        EventBus.getDefault().post(new MessageEvent("关闭视频"));
                        break;

                    case "image_share":

                        Element urlElement = (Element) root.getElementsByTagName("image_url").item(0);
                        SipInfo.shareurl=urlElement.getFirstChild().getNodeValue();
                        Log.d("picture",shareurl);
                        this.screenproUpdateListener.screenUpdate();
                        break;
                        default:
                        return false;
                }
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }


        } else {
            Log.d(TAG, "body is null");
            return true;
        }
        return false;
    }



    private boolean responseParse(Message msg) {
        String body = msg.getBody();
        if (body != null) {
            StringReader sr = new StringReader(body);
            InputSource is = new InputSource(sr);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            Document document;
            try {
                builder = factory.newDocumentBuilder();
                document = builder.parse(is);
                Element root = document.getDocumentElement();
                String type = root.getTagName();
                switch (type) {
                    case "subscribe_grouppn_response":
                        Element codeElement = (Element) root.getElementsByTagName("code").item(0);
                        String code = codeElement.getFirstChild().getNodeValue();
                        if (code.equals("200")) {
                            Element groupNumElement = (Element) root.getElementsByTagName("group_num").item(0);
                            Element peerElement = (Element) root.getElementsByTagName("peer").item(0);
                            Element levelElement = (Element) root.getElementsByTagName("level").item(0);
                            Element nameElement = (Element) root.getElementsByTagName("name").item(0);
                            GroupInfo.groupNum = groupNumElement.getFirstChild().getNodeValue();
                            String peer = peerElement.getFirstChild().getNodeValue();
                            GroupInfo.ip = peer.substring(0, peer.indexOf("UDP")).trim();
//                            GroupInfo.port = Integer.parseInt(peer.substring(peer.indexOf("UDP") + 3).trim());
                            GroupInfo.level = levelElement.getFirstChild().getNodeValue();
                            SipInfo.devName = nameElement.getFirstChild().getNodeValue();

                        }
                        return true;
                    default:
                        return false;
                }
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }


        } else {
            Log.d(TAG, "BODY IS NULL");
            return true;
        }
        return false;
    }

    public interface WorkerLoginListener {
        void loginRes(String name);

        void loginAckRes(String result);
    }

    public void setWorkerLoginListener(WorkerLoginListener workerLoginListener) {
        this.workerLoginListener = workerLoginListener;
    }

    public interface NumberUpdateListener{
        void numberUpdate();
    }

    public void setNumberUpdateListener(NumberUpdateListener numberUpdateListener){
        this.numberUpdateListener=numberUpdateListener;
    }
    public interface ScreenproUpdateListener{
        void screenUpdate();
    }

    public void setScreenproUpdateListener(ScreenproUpdateListener screenproUpdateListener){
       this.screenproUpdateListener=screenproUpdateListener;
    }

    public  void sendData(byte[] writeBytes){
        if (mOutputStream!=null){
            try {
                mOutputStream.write(writeBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
