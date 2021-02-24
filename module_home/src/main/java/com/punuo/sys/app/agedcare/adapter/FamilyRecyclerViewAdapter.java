package com.punuo.sys.app.agedcare.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.punuo.sip.H264Config;
import com.punuo.sip.user.SipUserManager;
import com.punuo.sip.user.request.SipOperationRequest;
import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.Util;
import com.punuo.sys.sdk.model.BindUser;
import com.punuo.sys.app.agedcare.request.GetUserDevIdRequest;
import com.punuo.sys.app.agedcare.request.model.UserDevModel;
import com.punuo.sys.app.router.HomeRouter;
import com.punuo.sys.sdk.account.AccountManager;
import com.punuo.sys.sdk.activity.BaseActivity;
import com.punuo.sys.sdk.httplib.HttpManager;
import com.punuo.sys.sdk.httplib.RequestListener;
import com.punuo.sys.sdk.util.CommonUtil;
import com.punuo.sys.sdk.util.HandlerExceptionUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by 23578 on 2018/11/22.
 */

public class FamilyRecyclerViewAdapter extends RecyclerView.Adapter<FamilyRecyclerViewAdapter.MyViewHolder> {
    private Context mContext;
    private final List<BindUser> mBindUserList = new ArrayList<>();

    public FamilyRecyclerViewAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void appendData(List<BindUser> list) {
        mBindUserList.clear();
        if (list != null) {
            mBindUserList.addAll(list);
        }
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.picture_item, viewGroup, false);//加载item布局
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, final int i) {
        final BindUser bindUser = mBindUserList.get(i);
        myViewHolder.bindData(bindUser);

    }

    @Override
    public int getItemCount() {
        return mBindUserList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView textView;

        MyViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_icon);
            textView = itemView.findViewById(R.id.item_nickName);
        }

        public void bindData(BindUser bindUser) {
            ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
            int size = (CommonUtil.getWidth() - CommonUtil.dip2px(30f) * 4 - CommonUtil.dip2px(60f) -CommonUtil.dip2px(60f)) / 4;
            layoutParams.width = size;
            layoutParams.height = size;
            Glide.with(itemView.getContext()).load(Util.getImageUrl(bindUser.getId(), bindUser.getAvatar()))
                    .apply(new RequestOptions().override(150, 150)).into(imageView);
            textView.setText(bindUser.getNickname());
            imageView.setOnClickListener(v -> {
                if (itemView.getContext() instanceof BaseActivity) {
                    ((BaseActivity) itemView.getContext()).showLoadingDialog();
                }
                GetUserDevIdRequest request = new GetUserDevIdRequest();
                request.addUrlParam("id", bindUser.getId());
                request.addUrlParam("groupid", AccountManager.getGroupId());
                request.setRequestListener(new RequestListener<UserDevModel>() {
                    @Override
                    public void onComplete() {
                        if (itemView.getContext() instanceof BaseActivity) {
                            ((BaseActivity) itemView.getContext()).dismissLoadingDialog();
                        }
                    }

                    @Override
                    public void onSuccess(UserDevModel result) {
                        if (!TextUtils.isEmpty(result.devId)) {
                            AccountManager.setTargetUserId(bindUser.userid);
                            AccountManager.setTargetDevId(result.devId);
                            H264Config.monitorType = H264Config.DOUBLE_MONITOR_POSITIVE; //双向视频发起端
                            SipOperationRequest operationRequest = new SipOperationRequest();
                            SipUserManager.getInstance().addRequest(operationRequest);

                            ARouter.getInstance().build(HomeRouter.ROUTER_VIDEO_REQUEST_ACTIVITY)
                                    .withParcelable("model", bindUser)
                                    .navigation();
                        } else {

                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        HandlerExceptionUtils.handleException(e);
                    }
                });
                HttpManager.addRequest(request);
            });
        }
    }
}
