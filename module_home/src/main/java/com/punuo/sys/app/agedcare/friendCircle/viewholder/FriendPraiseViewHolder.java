package com.punuo.sys.app.agedcare.friendCircle.viewholder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.R2;
import com.punuo.sys.app.agedcare.friendCircle.PraiseConst;
import com.punuo.sys.app.agedcare.friendCircle.domain.FirstMicroListFriendPraise;
import com.punuo.sys.sdk.recyclerview.BaseViewHolder;
import com.punuo.sys.sdk.util.ViewUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by han.chen.
 * Date on 2019-06-09.
 **/
public class FriendPraiseViewHolder extends BaseViewHolder<FirstMicroListFriendPraise> {

    @BindView(R2.id.icon)
    ImageView mIcon;
    @BindView(R2.id.nick_name)
    TextView mNickName;
    @BindView(R2.id.desc)
    TextView mDesc;

    private int[] drawables = new int[]{
            R.drawable.l_xin,
            R.drawable.emoji_1,
            R.drawable.emoji_9,
            R.drawable.emoji_19
    };

    public FriendPraiseViewHolder(Context context, ViewGroup parent) {
        super(LayoutInflater.from(context).inflate(R.layout.friend_praise_item, parent, false));
        ButterKnife.bind(this, itemView);
    }

    @Override
    protected void bindData(FirstMicroListFriendPraise data, int position) {
        ViewUtil.setText(mNickName, data.nickName);
        switch (data.praiseType) {
            case PraiseConst.TYPE_DIANZAN:
                mIcon.setImageResource(drawables[0]);
                mDesc.setText(PraiseConst.DESC_LIST[0]);
                break;
            case PraiseConst.TYPE_WEIXIAO:
                mIcon.setImageResource(drawables[1]);
                mDesc.setText(PraiseConst.DESC_LIST[1]);
                break;
            case PraiseConst.TYPE_DAXIAO:
                mIcon.setImageResource(drawables[2]);
                mDesc.setText(PraiseConst.DESC_LIST[2]);
                break;
            case PraiseConst.TYPE_KUXIAO:
                mIcon.setImageResource(drawables[3]);
                mDesc.setText(PraiseConst.DESC_LIST[3]);
                break;
            default:
                break;
        }
    }
}
