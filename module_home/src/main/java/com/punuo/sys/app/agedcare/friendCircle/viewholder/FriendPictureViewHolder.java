package com.punuo.sys.app.agedcare.friendCircle.viewholder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.R2;
import com.punuo.sys.sdk.recyclerview.BaseViewHolder;
import com.punuo.sys.sdk.util.CommonUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by han.chen.
 * Date on 2019-06-09.
 **/
public class FriendPictureViewHolder extends BaseViewHolder<String> {
    @BindView(R2.id.picture_image)
    public ImageView mPictureImage;

    public FriendPictureViewHolder(Context context, ViewGroup parent) {
        super(LayoutInflater.from(context).inflate(R.layout.friend_picture_item, parent, false));
        ButterKnife.bind(this, itemView);
        int size = (CommonUtil.getWidth() - CommonUtil.dip2px(300)) / 3;
        mPictureImage.getLayoutParams().width = size;
        mPictureImage.getLayoutParams().height = size;
    }

    @Override
    protected void bindData(String s, int position) {
        Glide.with(itemView.getContext()).load(s)
                .apply(
                        new RequestOptions()
                        .placeholder(R.drawable.default_loading)
                        .placeholder(R.drawable.default_error)
                ).into(mPictureImage);
    }
}
