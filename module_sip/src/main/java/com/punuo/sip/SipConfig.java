package com.punuo.sip;

import org.zoolu.sip.address.NameAddress;

/**
 * Created by han.chen.
 * Date on 2019-08-12.
 **/
public class SipConfig {

    private static ISipConfig sSipConfig;
    //服务器ip
    private static String host = "39.98.36.250";//101.69.255.134
    //端口号
    private static int port = 6061;
    //服务器Id
    public static String SERVER_ID = "330100000010000090";
    //用户注册获取用户ID使用
    public static String REGISTER_ID = "330100000010000190";
    //服务器名
    public static String SERVER_NAME = "rvsup";

    public static void init(ISipConfig sipConfig) {
        sSipConfig = sipConfig;
    }

    public static String getServerIp() {
        if (sSipConfig != null) {
            return sSipConfig.getServerIp();
        }
        return host;
    }

    public static int getUserPort() {
        if (sSipConfig != null) {
            return sSipConfig.getUserPort();
        }
        return port;
    }

    public static int getDevPort() {
        if (sSipConfig != null) {
            return sSipConfig.getDevPort();
        }
        return port;
    }

    public static NameAddress getUserRegisterAddress() {
        if (sSipConfig != null) {
            return sSipConfig.getUserRegisterAddress();
        } else {
            throw new RuntimeException("RegisterUserNameAddress is null, please set RegisterUserNameAddress");
        }
    }

    public static NameAddress getDevRegisterAddress() {
        if (sSipConfig != null) {
            return sSipConfig.getDevRegisterAddress();
        } else {
            throw new RuntimeException("RegisterDevNameAddress is null, please set RegisterDevNameAddress");
        }
    }

    public static NameAddress getUserServerAddress() {
        if (sSipConfig != null) {
            return sSipConfig.getUserServerAddress();
        } else {
            throw new RuntimeException("UserServerNameAddress is null, please set UserServerNameAddress");
        }
    }

    public static NameAddress getDevServerAddress() {
        if (sSipConfig != null) {
            return sSipConfig.getDevServerAddress();
        } else {
            throw new RuntimeException("DevServerNameAddress is null, please set DevServerNameAddress");
        }
    }

    public static NameAddress getUserNormalAddress() {
        if (sSipConfig != null) {
            return sSipConfig.getUserNormalAddress();
        } else {
            throw new RuntimeException("NormalUserNameAddress is null, please set NormalUserNameAddress");
        }
    }

    public static NameAddress getDevNormalAddress() {
        if (sSipConfig != null) {
            return sSipConfig.getDevNormalAddress();
        } else {
            throw new RuntimeException("NormalDevNameAddress is null, please set NormalDevNameAddress");
        }
    }

    public static void reset() {
        if (sSipConfig != null) {
            sSipConfig.reset();
        }
    }
}
