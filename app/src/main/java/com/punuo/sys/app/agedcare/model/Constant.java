package com.punuo.sys.app.agedcare.model;


import com.punuo.sys.app.agedcare.sip.SipInfo;

/**
 * Created by acer on 2016/9/5.
 */
public class Constant {
    public static final int MESSAGE=1;
    public static final int CONTACT=2;
    public static final int Person=3;
    public static final int OLD=4;
    public static final int SHOP=5;
    public static final int NOTICE=6;
    public static final int FILESHARE=7;
    public static final int COMMUNITY=8;


    public static int SAVE_FRAGMENT_SELECT_STATE=1;
    public static int SAVE_GROUP_FRAGMENT_SELECT_STATE=6;

    public static final String FTP_CONNECT_SUCCESSS = "ftp连接成功";
    public static final String FTP_CONNECT_FAIL = "ftp连接失败";
    public static final String FTP_DISCONNECT_SUCCESS = "ftp断开连接";
    public static final String FTP_FILE_NOTEXISTS = "ftp上文件不存在";
    public static final String FTP_FILE_EXISTS = "ftp上文件存在";
    public static final String FTP_UPLOAD_SUCCESS = "ftp文件上传成功";
    public static final String FTP_UPLOAD_FAIL = "ftp文件上传失败";
    public static final String FTP_UPLOAD_LOADING = "ftp文件正在上传";
    public static final String FTP_DOWN_LOADING = "ftp文件正在下载";
    public static final String FTP_DOWN_SUCCESS = "ftp文件下载成功";
    public static final String FTP_DOWN_FAIL = "ftp文件下载失败";
    public static final String FTP_DELETEFILE_SUCCESS = "ftp文件删除成功";
    public static final String FTP_DELETEFILE_FAIL = "ftp文件删除失败";
    public static final String FTP_MIKEDIR_SUCCESS = "ftp路径创建成功";
    public static final String FTP_MIKEDIR_FAIL = "ftp路径创建失败";

    public static final int ERROR_CONNECT=1;//连接失败

    public static final String NEW_FRIENDS_USERNAME = "item_new_friends";
    public static final String GROUP_USERNAME = "item_groups";
    public static final String MESSAGE_ATTR_IS_VOICE_CALL = "is_voice_call";
    public static final String ACCOUNT_REMOVED = "account_removed";
public static Runnable runnable=new Runnable() {
    @Override
    public void run() {
        while (shan){

        }
    }
};
    public static boolean shan=false;
    //服务器端
//    public static final String FORMT="http://"+ SipInfo.serverIp+":8000/xiaoyupeihu/public/index.php/";
    public static final String FORMT="http://"+ SipInfo.serverIp+":8000/xiaoyupeihu/public/index.php/";
    public static final String URL_GetUserInfo=FORMT+"users/getUserInfo";//获取用户信息
    public static final String URL_GetDevInfo=FORMT+"devs/getDevInfo";
    public static final String URL_Register = FORMT+"register";//注册
    public static final String URL_ChPaw = FORMT+"users/updateUserPwd";//改密码
    public static final String URL_ChPhoneNum=FORMT+"users/updateUserPhone";//改绑定账号
    public static final String URL_Avatar = "http://"+ SipInfo.serverIp+":8000/static/xiaoyupeihu/";//照片获取地址
    public static final String URL_UPDATE_Avatar = FORMT+"users/updateUserPic";//换头像
    public static final String URL_UPDATE_Nick = FORMT+"users/updateUserName";//改昵称
    public static final String insertPost = FORMT+"posts/insertPost ";//发帖
    public static final String URL_Bind=FORMT+"devs/bindDev";//绑定设备
    public static final String URL_UnBind=FORMT+"devs/unbindDev";//解绑设备
    public static final String URL_joinGroup=FORMT+"groups/joinGroup";//加入群组
    public static  final String URL_queryCluster=FORMT+"groups/getGroupNumber_auth";//查询是否是群主
    public static final String URL_leaveGroup=FORMT+"groups/leaveGroup";//退出群组参数id，groupid
    public static  final String URL_getPostList=FORMT+"posts/getPostListFromGroup";//获取帖子信息
    public static final String URL_InquireGroup=FORMT+"users/getAllGroupFromUser";//获取用户的所有群组
    public static final String URL_InquireUser=FORMT+"groups/getAllUserFromGroup";//获取群组里的用户信息
    public static final String URL_InquireBind=FORMT+"devs/isDevBinded";//查询是否绑定设备
    public static final String URL_getuserDevid=FORMT+"devs/getUserDevid";//查询群组对应的手机devid
    public static final String URL_getallDevidfromid=FORMT+"devs/getDevIdFromId";//查询手机所有devid
    public static final String URL_updateLikes=FORMT+"posts/updateLikes";
    public static final String URL_addLikes=FORMT+"posts/addLikes";
    public static final String URL_deleteLikes=FORMT+"posts/deleteLikes";
    public static final String URL_addComments=FORMT+"posts/addComments";
    public static final String URL_insertAddressbook=FORMT+"users/insertAddressbook";
    public static final String URL_updateAddressbook=FORMT+"users/updateAddressbook";
    public static final String URL_getAddressbook=FORMT+"users/getAddressbook";
    public static final String URL_deleteAddressbook=FORMT+"users/deleteAddressbook";
    public static final String URL_getservicenumber=FORMT+"users/getServiceNumber";
    public static String nick;
    public static String avatar;
    public static String phone;
    public static String id;
    public static String res;
    public static String groupid;
    public static String groupid1;
    public static String groupid2;
    public static String groupid3;
    public static String devid1;
    public static String devid2;
    public static String devid3;
    public static String currentfriendavatar;
    public static String currentfriendid;
    public static String appdevid1;
    public static String appdevid2;
    public static String appdevid3;

}
