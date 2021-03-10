package com.punuo.sys.app.linphone.frgment;

/*
CallAudioFragment.java
Copyright (C) 2017  Belledonne Communications, Grenoble, France

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.punuo.sys.app.linphone.LinphoneActivity;
import com.punuo.sys.app.linphone.LinphoneHelper;
import com.punuo.sys.app.linphone.LinphoneManager;
import com.punuo.sys.app.linphone.LinphoneService;
import com.punuo.sys.app.linphone.R;
import com.punuo.sys.app.linphone.bean.ChatInfo;
import com.punuo.sys.app.linphone.callback.VoipCallBack;
import com.punuo.sys.app.linphone.utils.StatusBarCompat;

import org.linphone.core.LinphoneCall;

import static com.punuo.sys.app.linphone.LinphoneActivity.CHAT_TYPE;
import static com.punuo.sys.app.linphone.LinphoneActivity.VOIP_CALL;
import static com.punuo.sys.app.linphone.LinphoneActivity.VOIP_INCOMING;

public class CallAudioFragment extends Fragment implements View.OnClickListener {

    private Button narrow_button;
    private ImageView iv_background;
    private ImageView voip_voice_chat_avatar;
    private TextView voice_chat_friend_name;
    private TextView voip_voice_chat_state_tips;


    private LinphoneActivity incallActvityInstance;
    private View rootView = null;

    private int chatType;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        chatType = bundle.getInt(CHAT_TYPE, VOIP_CALL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.voip_audio, container, false);
            initView(rootView);
        }
        initVar();
        initListener();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        incallActvityInstance = (LinphoneActivity) getActivity();
        if (incallActvityInstance != null) {
            incallActvityInstance.bindAudioFragment(this);
        }
    }

    private void initView(View rootView) {
        narrow_button = rootView.findViewById(R.id.narrow_button);
        voip_voice_chat_state_tips = rootView.findViewById(R.id.voip_voice_chat_state_tips);
        voip_voice_chat_avatar = rootView.findViewById(R.id.voip_voice_chat_avatar);
        voice_chat_friend_name = rootView.findViewById(R.id.voice_chat_friend_name);
        iv_background = rootView.findViewById(R.id.iv_background);

        if (chatType == VOIP_INCOMING) {
            updateChatStateTips(getString(R.string.voice_chat_invite));
            narrow_button.setVisibility(View.INVISIBLE);
        } else if (chatType == VOIP_CALL) {
            narrow_button.setVisibility(View.VISIBLE);
            voip_voice_chat_state_tips.setVisibility(View.INVISIBLE);
            LinphoneCall call = LinphoneManager.getLc().getCurrentCall();
            if (call != null) {
                if (incallActvityInstance != null) {
                    incallActvityInstance.registerCallDurationTimer(null, call);
                }

            }
        }

        //设置narrow的位置
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int statusBarHeight = StatusBarCompat.getStatusBarHeight(getActivity());
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(narrow_button.getLayoutParams());
            lp.setMargins(32, statusBarHeight + 32, 0, 0);
            narrow_button.setLayoutParams(lp);
        }
    }

    private void initListener() {
        narrow_button.setOnClickListener(this);

    }

    private ChatInfo info;

    private void initVar() {
        //显示头像和昵称
        info = LinphoneHelper.getInstance().getChatInfo();
        if (LinphoneHelper.mGroupId != 0) {
           // LinLog.e("dds_test","test000");
            VoipCallBack callBack = LinphoneService.instance.getCallBack();
            if (callBack != null) {
                info = callBack.getGroupInFo(LinphoneHelper.mGroupId);
            }
        } else {
            //LinLog.e("dds_test","test111");
            if (!TextUtils.isEmpty(LinphoneHelper.friendName)) {
                VoipCallBack callBack = LinphoneService.instance.getCallBack();
                if (callBack != null) {
                   // LinLog.e("dds_test","test222");
                    info = callBack.getChatInfo(LinphoneHelper.friendName);
                }
            }

        }

        if (info != null) {
           // LinLog.e("dds_test","test3333");
            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(info.getDefaultAvatar())
                    .error(info.getDefaultAvatar());

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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.narrow_button) {
            // 开启悬浮窗
            incallActvityInstance.openNarrow();
        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (rootView != null) {
            ViewGroup viewGroup = (ViewGroup) rootView.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(rootView);
            }
        }
    }

    @Override
    public void onDestroy() {
        rootView = null;
        super.onDestroy();

    }


    public void setNarrowVisible(boolean isVisible) {
        if (narrow_button != null) {
            narrow_button.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
        }

    }

    //更新提示语
    public void updateChatStateTips(String tips) {
        voip_voice_chat_state_tips.setVisibility(View.VISIBLE);
        voip_voice_chat_state_tips.setText(tips);
    }


}
