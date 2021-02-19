package com.punuo.sip.dev.service;

import java.util.HashSet;

/**
 * Created by han.chen.
 * Date on 2019-08-20.
 **/
public class DevServicePath {

    public static final String PATH_MEDIA = "/dev/media";
    public static final String PATH_QUERY = "/dev/query";
    public static final String PATH_NOTIFY = "/dev/notify";
    public static final String PATH_REGISTER = "/dev/negotiate_response";
    public static final String PATH_LOGIN = "/dev/login_response";
    public static final String PATH_ERROR = "/dev/error";
    public static final String PATH_CALL_RESPONSE = "/dev/call_response";
    public static final String PATH_OPERATION = "/dev/operation";
    public static final String PATH_SUSPEND_MONITOR = "/dev/suspend_monitor";
    public static final String PATH_RECVADDR = "/dev/recvaddr";

    public static final HashSet<String> sMapping = new HashSet<>();

    static {
        sMapping.add(PATH_MEDIA);
        sMapping.add(PATH_QUERY);
        sMapping.add(PATH_NOTIFY);
        sMapping.add(PATH_REGISTER);
        sMapping.add(PATH_LOGIN);
        sMapping.add(PATH_ERROR);
        sMapping.add(PATH_CALL_RESPONSE);
        sMapping.add(PATH_OPERATION);
        sMapping.add(PATH_SUSPEND_MONITOR);
        sMapping.add(PATH_RECVADDR);
    }
}
