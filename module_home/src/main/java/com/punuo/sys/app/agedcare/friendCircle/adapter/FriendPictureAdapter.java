package com.punuo.sys.app.agedcare.friendCircle.adapter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.punuo.sys.app.agedcare.friendCircle.viewholder.FriendPictureViewHolder;
import com.punuo.sys.app.agedcare.ui.ImagePagerActivity;
import com.punuo.sys.app.router.HomeRouter;
import com.punuo.sys.sdk.recyclerview.BaseRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by han.chen.
 * Date on 2019-06-09.
 **/
public class FriendPictureAdapter extends BaseRecyclerViewAdapter<String> {
    public FriendPictureAdapter(Context context, List<String> data) {
        super(context, data);
    }

    @Override
    public RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType) {
        return new FriendPictureViewHolder(mContext, parent);
    }

    @Override
    public void onBindBasicItemView(RecyclerView.ViewHolder baseViewHolder, int position) {
        if (baseViewHolder instanceof FriendPictureViewHolder) {
            ((FriendPictureViewHolder) baseViewHolder).bind(getItem(position), position);
            ((FriendPictureViewHolder) baseViewHolder).mPictureImage.setOnClickListener(v ->
                    ARouter.getInstance().build(HomeRouter.ROUTER_IMAGE_PAGER_ACTIVITY)
                    .withStringArrayList(ImagePagerActivity.EXTRA_IMAGE_URLS, (ArrayList<String>) mData)
                    .withInt(ImagePagerActivity.EXTRA_IMAGE_INDEX, position)
                    .navigation());
        }
    }

    @Override
    public int getBasicItemType(int position) {
        return 0;
    }

    @Override
    public int getBasicItemCount() {
        return mData == null ? 0 : mData.size();
    }
}
