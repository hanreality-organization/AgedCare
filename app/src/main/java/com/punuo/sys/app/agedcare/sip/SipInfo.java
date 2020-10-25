package com.punuo.sys.app.agedcare.sip;



import android.os.Handler;

import com.punuo.sys.app.agedcare.db.MyDatabaseHelper;
import com.punuo.sys.app.agedcare.model.Cluster;
import com.punuo.sys.app.agedcare.model.Device;
import com.punuo.sys.app.agedcare.model.Farmilymember;
import com.punuo.sys.app.agedcare.model.Music;
import com.punuo.sys.app.agedcare.model.Musicitem;
import com.punuo.sys.app.agedcare.model.ShortMovie;
import com.punuo.sys.app.agedcare.ui.MyCamera;

import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.message.Message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Author chzjy
 * Date 2016/12/19.
 */

public class SipInfo {
    //服务器名
    public static String SERVER_NAME = "rvsup";
    //服务器ID
    public static String SERVER_ID = "330100000010000090";
    //用户端口
 public static int SERVER_PORT_USER = 6061;
//    public static int SERVER_PORT_USER = 7071;
    //设备端口
  public static int SERVER_PORT_DEV = 6060;
//   public static int SERVER_PORT_DEV = 7070;

    //用户注册获取用户ID使用
    public static String REGISTER_ID = "330100000010000190";
    //集群频道
    public static String port;
    //服务器ip
  public static String serverIp="sip.qinqingonline.com";
//    public static String serverIp="101.69.255.134";
//    public static String serverIp="192.168.1.154";
    //用户账号
    public static String userAccount;
    //用户密码
    public static String passWord="123456";
    //设备id
//    public static String devId = "310023000300380001";
   public static String devId ;

    //视频请求时获取的用户设备id
    public static String userdevid;
    //网络获取用户设备ID
    public static String netuserdevid;
    //用户id
    public static String userId;
    //视频请求时获取的用户userId
    public static String videouserId;
    //用户真实姓名
    public static String userRealname;
    //网络是否连接
    public static boolean isNetworkConnected;
    //用户账号是否存在
    public static boolean isAccountExist;
    //密码错误标志
    public static boolean passwordError=false;
    //用户登录状态标志
    public static boolean userLogined;
    //设备登录状态标志
    public static boolean devLogined;
    //登录超时标志
    public static boolean loginTimeout;
    //设备登录超时标志
    public static boolean dev_loginTimeout;
    //sip消息From地址
    public static NameAddress user_from;
    //sip消息To地址
    public static NameAddress user_to;
    //sip消息(聊天消息)To好友地址
    public static NameAddress toUser;
    //sip消息(设备)To地址
    public static NameAddress dev_to;
    //sip消息(设备)From地址
    public static NameAddress dev_from;
    //sip消息(用户)请求视频设备地址
    public static NameAddress toDev;
    //用户sip对象
    public static SipUser sipUser;
    //设备sip对象
    public static SipDev sipDev;
    //用户心跳保活
    public static KeepAlive keepUserAlive;
    //设备心跳保活
    public static KeepAlive keepDevAlive;
    //用户IP电话号码
    public static String userPhoneNumber;
    //一次加密种子
    public static String seed;
    //二次加密种子
    public static String salt;
    //用户心跳回复
    public static boolean user_heartbeatResponse;
    //设备心跳回复
    public static boolean dev_heartbeatResponse;
    //集群列表
    public static ArrayList<Cluster> clusters=new ArrayList<>();
    //缓存集群列表
    public static ArrayList<Cluster> cacheClusters=new ArrayList<>();
    //设备名
    public static String devName;
    //开机是否自启
    public static boolean isAutomatic=false;
    //异地登录
    public static Handler loginReplace;
    //是否点赞

    public static Boolean finish=false;
    public static String[] localurl;
    public static ArrayList<Device> devList=new ArrayList<>();
    public static ArrayList<String> qinliaouseridList=new ArrayList<>();
    public static ArrayList<String> pictureList=new ArrayList<>();
 public static ArrayList<Musicitem> musicitems=new ArrayList<>();
    public static List<Farmilymember> farmilymemberList = new ArrayList<>();
    public static ArrayList<Music> musics=new ArrayList<>();
    public static ArrayList<ShortMovie> movies=new ArrayList<>();
    public static int lastmusictype=0;

    public static MyDatabaseHelper dbHelper;
    //视频信息请求回复
    public static boolean queryResponse;
    //视频请求回复
    public static boolean inviteResponse;
    //视频编码状态
    public static boolean decoding = false;
    //是否允许调用摄像头
    public static boolean flag=true;
    //用户群组
    public static String groupid;
    public static Message msg;
    public static Handler notifymedia;
    public static boolean isSpeak=false;
    //头像网址
    public static String[] url;
   public static List<Device> devices=new ArrayList<>();
    public static SimpleDateFormat format;
    //手机端分享的图片地址；
    public static String shareurl;
    public static boolean isanswering;
 public static boolean isWaitingFeedback=false;
 public static String sipReqFromUser = null;
 public static boolean ismoniter=false;
    public static MyCamera myCamera;

    public static String nettype="11";
    public static float  bitErrorRate;
}
