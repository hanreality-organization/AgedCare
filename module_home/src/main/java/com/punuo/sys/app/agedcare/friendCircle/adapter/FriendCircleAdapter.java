package com.punuo.sys.app.agedcare.friendCircle.adapter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.punuo.sys.app.agedcare.friendCircle.domain.FriendMicroListData;
import com.punuo.sys.app.agedcare.friendCircle.viewholder.FriendCircleViewHolder;
import com.punuo.sys.sdk.recyclerview.PageRecyclerViewAdapter;

import java.util.List;

/**
 * Created by han.chen.
 * Date on 2019-06-05.
 **/
public class FriendCircleAdapter extends PageRecyclerViewAdapter<FriendMicroListData> {
    public FriendCircleAdapter(Context context, List<FriendMicroListData> data) {
        super(context, data);
    }

    public void resetData(List<FriendMicroListData> list) {
        manuaRemoveFooterView();
        mData.clear();
        if (list != null) {
            mData.addAll(list);
            notifyDataSetChanged();
        }
    }

    public void manuaRemoveFooterView() {
        mFootView = null;
    }

    @Override
    public RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType) {
        return new FriendCircleViewHolder(mContext, parent);
    }

    @Override
    public void onBindBasicItemView(RecyclerView.ViewHolder baseViewHolder, int position) {
        if (baseViewHolder instanceof FriendCircleViewHolder) {
            ((FriendCircleViewHolder) baseViewHolder).bind(getItem(position), position);
        }
    }
}
