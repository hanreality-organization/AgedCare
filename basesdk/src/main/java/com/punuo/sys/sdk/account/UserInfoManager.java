package com.punuo.sys.sdk.account;

import android.content.Context;

import com.punuo.sys.sdk.PnApplication;
import com.punuo.sys.sdk.account.model.PNUserInfo;
import com.punuo.sys.sdk.account.request.GetUserInfoRequest;
import com.punuo.sys.sdk.httplib.HttpManager;
import com.punuo.sys.sdk.httplib.JsonUtil;
import com.punuo.sys.sdk.httplib.RequestListener;
import com.punuo.sys.sdk.util.MMKVUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by han.chen.
 * Date on 2021/2/19.
 **/
public class UserInfoManager {
    private static final String PREF_KEY_USER = "pn_pref_user";
    private static UserInfoManager sUserInfoManager;
    private static PNUserInfo.UserInfo sUserInfo;
    private UserInfoManager() {
    }

    /**
     * 单例模式，获取instance实例
     *
     * @return
     */
    public static UserInfoManager getInstance() {
        if (sUserInfoManager == null) {
            sUserInfoManager = new UserInfoManager();
        }
        return sUserInfoManager;
    }

    public static void setUserInfo(PNUserInfo.UserInfo userInfo) {
        final Context context = PnApplication.getInstance();
        if (userInfo != null) {
            sUserInfo = userInfo;
            MMKVUtil.setString(PREF_KEY_USER, JsonUtil.toJson(userInfo));
            EventBus.getDefault().post(userInfo);
        }
    }

    public static PNUserInfo.UserInfo getUserInfo() {
        if (sUserInfo == null) {
            sUserInfo = JsonUtil.fromJson(MMKVUtil.getString(PREF_KEY_USER), PNUserInfo.UserInfo.class);
            sUserInfo = sUserInfo == null? new PNUserInfo.UserInfo() : sUserInfo;
        }
        return sUserInfo;
    }

    /**
     * 退出登录时清空用户信息
     */
    public static void clearUserData() {
        sUserInfo = null;
        MMKVUtil.removeData(PREF_KEY_USER);
    }

    public GetUserInfoRequest mGetUserInfoRequest;
    public void refreshUserInfo(RequestListener listener) {
        if (mGetUserInfoRequest != null && !mGetUserInfoRequest.isFinish()) {
            return;
        }
        mGetUserInfoRequest = new GetUserInfoRequest();
        mGetUserInfoRequest.addUrlParam("userid", AccountManager.getUserId());
        mGetUserInfoRequest.setRequestListener(listener);
        HttpManager.addRequest(mGetUserInfoRequest);
    }

    public void refreshUserInfo() {
        refreshUserInfo(new RequestListener<PNUserInfo>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(PNUserInfo result) {
                if (result == null) {
                    return;
                }
                setUserInfo(result.userInfo);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }
}
