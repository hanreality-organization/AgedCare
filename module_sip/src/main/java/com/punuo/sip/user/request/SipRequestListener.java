package com.punuo.sip.user.request;

/**
 * Created by han.chen.
 * Date on 2019-08-12.
 **/
public interface SipRequestListener<T> {
    void onComplete();
    void onSuccess(T result);
    void onError(Exception e);
}

