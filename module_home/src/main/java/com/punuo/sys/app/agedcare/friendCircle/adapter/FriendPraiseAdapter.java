package com.punuo.sys.app.agedcare.friendCircle.adapter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.punuo.sys.app.agedcare.friendCircle.domain.FirstMicroListFriendPraise;
import com.punuo.sys.app.agedcare.friendCircle.viewholder.FriendPraiseViewHolder;
import com.punuo.sys.sdk.recyclerview.BaseRecyclerViewAdapter;

import java.util.List;

/**
 * Created by han.chen.
 * Date on 2019-06-09.
 **/
public class FriendPraiseAdapter extends BaseRecyclerViewAdapter<FirstMicroListFriendPraise> {

    public FriendPraiseAdapter(Context context, List<FirstMicroListFriendPraise> data) {
        super(context, data);
    }

    @Override
    public RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType) {
        return new FriendPraiseViewHolder(mContext, parent);
    }

    @Override
    public void onBindBasicItemView(RecyclerView.ViewHolder baseViewHolder, int position) {
        if (baseViewHolder instanceof FriendPraiseViewHolder) {
            ((FriendPraiseViewHolder) baseViewHolder).bind(getItem(position), position);
        }
    }

    @Override
    public int getBasicItemType(int position) {
        return 0;
    }

    @Override
    public int getBasicItemCount() {
        return mData == null ? 0 : mData.size() ;
    }
}
