package com.punuo.sys.app.linphone.callback;

import android.app.Application;
import android.text.TextUtils;
import android.widget.Toast;

import com.punuo.sys.app.linphone.LinLog;
import com.punuo.sys.app.linphone.LinphoneHelper;
import com.punuo.sys.app.linphone.bean.ChatInfo;


/**
 * Created by dds on 2018/5/11.
 * android_shuai@163.com
 */

public class VoipCallBackDefault implements VoipCallBack {
    Application ac;

    public VoipCallBackDefault(Application ac) {
        this.ac = ac;
    }

    @Override
    public boolean isContactVisible(String userId) {
        return !TextUtils.isEmpty(userId);
    }

    @Override
    public void terminateCall(boolean isVideo, String friendId, String message) {
        LinLog.d(LinphoneHelper.TAG, "terminateCall friendId:" + friendId + ",message:" + message);
        Toast.makeText(ac, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void terminateIncomingCall(boolean isVideo, String friendId, String message, boolean isMiss) {
        LinLog.d(LinphoneHelper.TAG, "terminateIncomingCall friendId:" + friendId + ",message:" + message + ",isMiss:" + isMiss);
        Toast.makeText(ac, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public ChatInfo getChatInfo(String userId) {
        LinLog.d(LinphoneHelper.TAG, "getChatInfo friendId:" + userId);
        return null;
    }

    @Override
    public ChatInfo getGroupInFo(long groupId) {
        return null;
    }

}
