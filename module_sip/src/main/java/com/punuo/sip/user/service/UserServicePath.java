package com.punuo.sip.user.service;

import java.util.HashSet;

/**
 * Created by han.chen.
 * Date on 2019-08-20.
 **/
public class UserServicePath {

    public static final String PATH_MEDIA = "/user/media";
    public static final String PATH_QUERY = "/user/query_response";
    public static final String PATH_NOTIFY = "/user/notify";
    public static final String PATH_REGISTER = "/user/negotiate_response";
    public static final String PATH_LOGIN = "/user/login_response";
    public static final String PATH_DEV_NOTIFY = "/user/dev_notify";
    public static final String PATH_SESSION_NOTIFY = "/user/session_notify";
    public static final String PATH_ERROR = "/user/error";
    public static final String PATH_IMAGE_SHARE = "/user/image_share";
    public static final String PATH_IS_MONITOR = "/user/is_monitor";

    public static final HashSet<String> sMapping = new HashSet<>();

    static {
        sMapping.add(PATH_MEDIA);
        sMapping.add(PATH_QUERY);
        sMapping.add(PATH_NOTIFY);
        sMapping.add(PATH_REGISTER);
        sMapping.add(PATH_LOGIN);
        sMapping.add(PATH_DEV_NOTIFY);
        sMapping.add(PATH_SESSION_NOTIFY);
        sMapping.add(PATH_ERROR);
        sMapping.add(PATH_IMAGE_SHARE);
        sMapping.add(PATH_IS_MONITOR);
    }
}
