package com.punuo.sys.app.agedcare.adapter;

import android.content.Context;
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
import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.db.FamilyMember;
import com.punuo.sys.app.router.HomeRouter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by 23578 on 2018/10/31.
 */

public class PhoneRecyclerViewAdapter extends RecyclerView.Adapter<PhoneRecyclerViewAdapter.MyViewHolder> {

    private final Context mContext;
    private final List<FamilyMember> mFamilyMemberList = new ArrayList<>();
    public PhoneRecyclerViewAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void clear() {
        mFamilyMemberList.clear();
    }

    public void addAllData(List<FamilyMember> list) {
        clear();
        if (list != null) {
            mFamilyMemberList.addAll(list);
        }
        notifyDataSetChanged();
    }

    @Override
    @NonNull
    public PhoneRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.rv_item_layout, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PhoneRecyclerViewAdapter.MyViewHolder myViewHolder, final int i) {
        FamilyMember familyMember = mFamilyMemberList.get(i);
        myViewHolder.textView.setText(familyMember.name);
        RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.defaultavator);
        Glide.with(mContext).load(familyMember.avatarUrl).apply(requestOptions).into(myViewHolder.imageView);
        myViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build(HomeRouter.ROUTER_FAMILY_MEMBER_MANAGER_ACTIVITY)
                        .withParcelable("family_member", familyMember)
                        .navigation();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFamilyMemberList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView textView;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_item);
            textView = (TextView) itemView.findViewById(R.id.iv_name);
        }
    }
}
