package com.punuo.sys.app.linphone.frgment;

import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.punuo.sys.app.linphone.LinphoneHelper;
import com.punuo.sys.app.linphone.LinphoneService;
import com.punuo.sys.app.linphone.R;
import com.punuo.sys.app.linphone.bean.ChatInfo;
import com.punuo.sys.app.linphone.callback.VoipCallBack;

/**
 * Created by dds on 2018/8/3.
 * android_shuai@163.com
 */

public class AudioPreViewFragment extends Fragment {

    private ImageView iv_background;
    private ImageView voip_voice_chat_avatar;
    private TextView voice_chat_friend_name;
    private TextView voip_voice_chat_state_tips;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_audio_preview, container, false);
        initView(view);
        initVar();
        return view;
    }

    private void initView(View rootView) {
        voip_voice_chat_avatar = rootView.findViewById(R.id.voip_voice_chat_avatar);
        voice_chat_friend_name = rootView.findViewById(R.id.voice_chat_friend_name);
        iv_background = rootView.findViewById(R.id.iv_background);
        voip_voice_chat_state_tips = rootView.findViewById(R.id.voip_voice_chat_state_tips);
    }

    private ChatInfo info;

    private void initVar() {
        //显示头像和昵称
        info = LinphoneHelper.getInstance().getChatInfo();
        if (LinphoneHelper.mGroupId != 0) {
            VoipCallBack callBack = LinphoneService.instance.getCallBack();
            if (callBack != null) {
                ChatInfo info = callBack.getGroupInFo(LinphoneHelper.mGroupId);
                if (info != null) {
                    this.info = info;
                }
            }
        } else {
            if (!TextUtils.isEmpty(LinphoneHelper.friendName)) {
                VoipCallBack callBack = LinphoneService.instance.getCallBack();
                if (callBack != null) {
                    ChatInfo info = callBack.getChatInfo(LinphoneHelper.friendName);
                    if (info != null) {
                        this.info = info;
                    }
                }
            }

        }
        if (info != null) {
            RequestOptions requestOptions = new RequestOptions()
                    .error(info.getDefaultAvatar())
                    .placeholder(info.getDefaultAvatar());
            Glide.with(this)
                    .load(info.getRemoteAvatar())
                    .apply(requestOptions)
                    .into(voip_voice_chat_avatar);
            Glide.with(this)
                    .load(info.getRemoteAvatar())
                    .apply(requestOptions)
                    .into(iv_background);
            voice_chat_friend_name.setText(info.getIpPhoneNumber());
        }

    }

    //更新提示语
    public void updateChatStateTips(String tips) {
        voip_voice_chat_state_tips.setVisibility(View.VISIBLE);
        voip_voice_chat_state_tips.setText(tips);
    }

}
