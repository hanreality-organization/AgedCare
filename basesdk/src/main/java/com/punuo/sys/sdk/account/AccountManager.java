package com.punuo.sys.sdk.account;

import android.os.Environment;

import com.punuo.sys.sdk.model.BindUser;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

/**
 * Created by han.chen.
 * Date on 2021/2/19.
 **/
public class AccountManager {
    private static String userId; //用户id
    private static String devId; //用户设备id
    private static String groupPort; //集群端口
    private static String groupId;
    private static boolean login = false;
    private static String targetDevId; //双向视频的目标devId
    private static String targetUserId; //双向视频的目标userId
    private static List<BindUser> bindUsers; //绑定该设备的用户信息

    public static void setBindUsers(List<BindUser> bindUsers) {
        AccountManager.bindUsers = bindUsers;
    }

    public static List<BindUser> getBindUsers() {
        return bindUsers;
    }

    public static String getUserId() {
        return userId;
    }

    public static void setUserId(String userId) {
        AccountManager.userId = userId;
    }

    public static String getDevId() {
        return devId;
    }

    public static void setDevId(String devId) {
        AccountManager.devId = devId;
    }

    public static String getGroupId() {
        return groupId;
    }

    public static void setGroupId(String groupId) {
        AccountManager.groupId = groupId;
    }

    public static boolean isLogin() {
        return login;
    }

    public static void setLogin(boolean login) {
        AccountManager.login = login;
    }

    public static String getTargetDevId() {
        return targetDevId;
    }

    public static void setTargetDevId(String targetDevId) {
        AccountManager.targetDevId = targetDevId;
    }

    public static void setTargetUserId(String targetUserId) {
        AccountManager.targetUserId = targetUserId;
    }

    public static String getTargetUserId() {
        return targetUserId;
    }

    public static String getGroupPort() {
        return groupPort;
    }

    public static void setGroupPort(String groupPort) {
        AccountManager.groupPort = groupPort;
    }

    /**
     * 读取配置文件
     */
    private static Properties loadConfig(String file) {
        Properties properties = new Properties();
        try {
            FileInputStream s = new FileInputStream(file);
            properties.load(s);
        } catch (Exception e) {
            return null;
        }
        return properties;
    }

    /**
     * 加载配置文件
     */
    public static void loadProperties() {
        String configPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/qinqingshixian/config.properties";
        Properties properties;
        File config = new File(configPath);
        if (config.exists()) {
            properties = loadConfig(configPath);
            if (properties != null) {
                AccountManager.setUserId(properties.getProperty("userAccount"));
                AccountManager.setDevId(properties.getProperty("devId"));
                AccountManager.setGroupPort(properties.getProperty("port"));
            }
        }
    }
}
