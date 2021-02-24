package com.punuo.sys.app.agedcare.friendCircle.viewholder;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.Util;
import com.punuo.sys.app.agedcare.friendCircle.adapter.FriendCommentAdapter;
import com.punuo.sys.app.agedcare.friendCircle.adapter.FriendPictureAdapter;
import com.punuo.sys.app.agedcare.friendCircle.adapter.FriendPraiseAdapter;
import com.punuo.sys.app.agedcare.friendCircle.domain.FirstMicroListFriendComment;
import com.punuo.sys.app.agedcare.friendCircle.domain.FirstMicroListFriendImage;
import com.punuo.sys.app.agedcare.friendCircle.domain.FirstMicroListFriendPraise;
import com.punuo.sys.app.agedcare.friendCircle.domain.FriendMicroListData;
import com.punuo.sys.app.agedcare.friendCircle.util.PopupWindowUtil;
import com.punuo.sys.sdk.recyclerview.BaseViewHolder;
import com.punuo.sys.sdk.util.TimeUtils;
import com.punuo.sys.sdk.util.ViewUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by han.chen.
 * Date on 2019-06-05.
 **/
public class FriendCircleViewHolder extends BaseViewHolder<FriendMicroListData> {
    private TextView mTime;
    private ImageView mAvatar;
    private TextView mName;
    private TextView mContent;
    private ImageView btnIgnore;
    private Context mContext;

    private RecyclerView mPraiseList;
    private FriendPraiseAdapter mFriendPraiseAdapter;

    private RecyclerView mFriendCommentList;
    private FriendCommentAdapter mFriendCommentAdapter;

    private RecyclerView mPictureList;
    private FriendPictureAdapter mFriendPictureAdapter;

    private PopupWindowUtil mPopupWindowUtil;

    public FriendCircleViewHolder(Context context, ViewGroup parent) {
        super(LayoutInflater.from(context).inflate(R.layout.micro_list_item, parent, false));
        mContext = context;
        initView(itemView);
    }

    private void initView(View itemView) {
        mTime = itemView.findViewById(R.id.time);
        mAvatar = itemView.findViewById(R.id.avator);
        mName = itemView.findViewById(R.id.name);
        mContent = itemView.findViewById(R.id.content);
        btnIgnore = itemView.findViewById(R.id.btnIgnore);

        mPraiseList = itemView.findViewById(R.id.praise_list);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(mContext);
        layoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        mPraiseList.setLayoutManager(layoutManager1);
        mFriendPraiseAdapter = new FriendPraiseAdapter(mContext, new ArrayList<>());
        mPraiseList.setAdapter(mFriendPraiseAdapter);

        mFriendCommentList = itemView.findViewById(R.id.friend_comment_list);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(mContext);
        layoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        mFriendCommentList.setLayoutManager(layoutManager2);
        mFriendCommentAdapter = new FriendCommentAdapter(mContext, new ArrayList<>());
        mFriendCommentList.setAdapter(mFriendCommentAdapter);

        mPictureList = itemView.findViewById(R.id.picture_recycler);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 3);
        mPictureList.setLayoutManager(gridLayoutManager);
        mFriendPictureAdapter = new FriendPictureAdapter(mContext, new ArrayList<>());
        mPictureList.setAdapter(mFriendPictureAdapter);

        mPopupWindowUtil = new PopupWindowUtil(mContext);

    }

    @Override
    protected void bindData(FriendMicroListData bean, int position) {
        //头像
        String avatarPath = Util.getImageUrl(bean.id, bean.avatar);
        RequestOptions options = new RequestOptions().error(R.drawable.defaultavator);
        Glide.with(mContext).load(avatarPath).apply(options).into(mAvatar);

        /*
         * 显示时间
         * 服务器返回的时间是：年-月-日 时：分，所以获取的时候应该是yyyy-MM-dd HH:mm
         */
        String strTime = bean.createTime.trim();
        if (!TextUtils.isEmpty(strTime)) {
            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            String date = sDateFormat.format(new Date());
            String t = TimeUtils.getTimes(date, strTime);
            mTime.setText(t);
        }
        /*
         * 显示姓名和内容
         */
        ViewUtil.setText(mName, bean.nickName);
        List<FirstMicroListFriendImage> postPic = bean.postPic;
        List<String> urls = new ArrayList<>();
        if (postPic != null && !postPic.isEmpty()) {
            for (int i = 0; i < postPic.size(); i++) {
                urls.add(Util.getImageUrl(bean.id, postPic.get(i).picName));
            }
        }

        if (!urls.isEmpty()) {
            mFriendPictureAdapter.getData().clear();
            mFriendPictureAdapter.addAll(urls);
            mPictureList.setVisibility(View.VISIBLE);
        } else {
           mPictureList.setVisibility(View.GONE);
        }

        ViewUtil.setText(mContent, bean.content);

        List<FirstMicroListFriendPraise> friendpraise = bean.addLikeNickname;
        //显示点赞holder.layoutPraise   friendpraise
        if (friendpraise != null && !friendpraise.isEmpty()) {
            mFriendPraiseAdapter.getData().clear();
            mFriendPraiseAdapter.addAll(friendpraise);
            mPraiseList.setVisibility(View.VISIBLE);
        } else {
            mPraiseList.setVisibility(View.GONE);
        }

        List<FirstMicroListFriendComment> friendComment = bean.friendComment;
        if (friendComment != null && !friendComment.isEmpty()) {
            mFriendCommentAdapter.getData().clear();
            mFriendCommentAdapter.addAll(friendComment);
            mFriendCommentList.setVisibility(View.VISIBLE);
        } else {
            mFriendCommentList.setVisibility(View.GONE);
        }
        mPopupWindowUtil.setFriendMicroListDatas(bean, position);
        //显示评论、点赞按钮
        btnIgnore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindowUtil.show(v);
            }
        });
    }
}
