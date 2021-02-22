package com.punuo.sys.app.agedcare.friendCircle.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.R2;
import com.punuo.sys.app.agedcare.friendCircle.PraiseConst;
import com.punuo.sys.app.agedcare.friendCircle.domain.FirstMicroListFriendComment;
import com.punuo.sys.app.agedcare.friendCircle.domain.FirstMicroListFriendPraise;
import com.punuo.sys.app.agedcare.friendCircle.domain.FirstMicroListFriendPraiseType;
import com.punuo.sys.app.agedcare.friendCircle.domain.FriendMicroListData;
import com.punuo.sys.app.agedcare.friendCircle.event.FriendRefreshEvent;
import com.punuo.sys.app.agedcare.request.AddCommentRequest;
import com.punuo.sys.app.agedcare.request.AddLikeRequest;
import com.punuo.sys.app.agedcare.request.DeleteLikeRequest;
import com.punuo.sys.app.agedcare.request.UpdateLikeRequest;
import com.punuo.sys.sdk.account.UserInfoManager;
import com.punuo.sys.sdk.httplib.HttpManager;
import com.punuo.sys.sdk.httplib.RequestListener;
import com.punuo.sys.sdk.util.CommonUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by han.chen.
 * Date on 2019-06-10.
 **/
public class PopupWindowUtil {
    private static final String TAG = "PopupWindowUtil";

    @BindView(R2.id.btnPraise)
    ImageView mBtnPraise;
    @BindView(R2.id.express1)
    ImageView mExpress1;
    @BindView(R2.id.express2)
    ImageView mExpress2;
    @BindView(R2.id.express3)
    ImageView mExpress3;
    @BindView(R2.id.btnComment)
    LinearLayout mBtnComment;

    private PopupWindow mPopupWindow;
    private View mView;
    private FriendMicroListData bean;
    private int position; //bean 在列表中的位置
    private Context mContext;
    private MyCustomDialog mCommentDialog;

    public PopupWindowUtil(Context context) {
        mContext = context;
        mView = LayoutInflater.from(context).inflate(R.layout.praise_pop_layout, null);
        ButterKnife.bind(this, mView);
        mPopupWindow = new PopupWindow(mView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        mView.setFocusableInTouchMode(true);
        mView.setFocusable(true);
        initView();
    }

    private void initView() {
        //评论按钮
        mBtnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示评论的对话框
                if (mCommentDialog != null && mCommentDialog.isShowing()) {
                    return;
                }
                mCommentDialog = new MyCustomDialog(mContext, R.style.add_dialog, "评论" +
                        bean.nickName + "的说说", new MyCustomDialog.OnCustomDialogListener() {
                    //点击对话框'提交'以后
                    public void back(String content) {
                        if (!TextUtils.isEmpty(content)) {
                            addComment(content);//提交评论
                        }
                    }
                });
                mCommentDialog.setCanceledOnTouchOutside(true);
                mCommentDialog.show();
                mPopupWindow.dismiss();
            }
        });
        //点赞按钮       praise:是否已经点赞了
        // true:已经点赞了，这样textView上面应该显示“取消”；false:没有点赞，textView上面应该显示“点赞”；默认为false
        mBtnPraise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPraise(PraiseConst.TYPE_DIANZAN);
            }
        });

        mExpress1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPraise(PraiseConst.TYPE_WEIXIAO);
            }
        });

        mExpress2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPraise(PraiseConst.TYPE_DAXIAO);
            }
        });

        mExpress3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPraise(PraiseConst.TYPE_KUXIAO);
            }
        });
    }

    public void setFriendMicroListDatas(FriendMicroListData bean, int position) {
        this.bean = bean;
        this.position = position;
    }

    private List<FirstMicroListFriendPraise> friendPraise;
    private List<FirstMicroListFriendPraiseType> ownType;
    private FirstMicroListFriendPraiseType own;

    /**
     * @param praiseType 点击的类型
     */
    private void submitPraise(String praiseType) {
        friendPraise = bean.addLikeNickname;
        if (friendPraise == null) {
            friendPraise = new ArrayList<>();
            bean.addLikeNickname = friendPraise;
        }
        ownType = bean.ownType;
        if (ownType == null) {
            ownType = new ArrayList<>();
            bean.ownType = ownType;
        }
        String prePraiseType = "N";
        if (!ownType.isEmpty()) {
            own = ownType.get(0);
            prePraiseType = own.praiseType;
        } else {
            own = new FirstMicroListFriendPraiseType();
        }
        FirstMicroListFriendPraise praise = new FirstMicroListFriendPraise();
        praise.nickName = UserInfoManager.getUserInfo().nickname;
        if ("N".equals(bean.praiseFlag)) {
            addLike(praiseType);
        } else if ("Y".equals(bean.praiseFlag)) {
            if (praiseType.equals(prePraiseType)) {
                deleteLike(praiseType);
            } else {
                updateLike(praiseType);
            }
        }
    }

    private AddLikeRequest mAddLikeRequest;

    private void addLike(String praiseType) {
        if (mAddLikeRequest != null && !mAddLikeRequest.isFinish()) {
            return;
        }
        mAddLikeRequest = new AddLikeRequest();
        mAddLikeRequest.addUrlParam("postid", bean.postId);
        mAddLikeRequest.addUrlParam("id", UserInfoManager.getUserInfo().id);
        mAddLikeRequest.addUrlParam("praisetype", praiseType);
        mAddLikeRequest.setRequestListener(new RequestListener<String>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(String result) {
                if (result == null) {
                    return;
                }
                FirstMicroListFriendPraise praise = new FirstMicroListFriendPraise();
                praise.id = UserInfoManager.getUserInfo().id;
                praise.nickName = UserInfoManager.getUserInfo().nickname;
                praise.praiseType = praiseType;
                own.praiseType = praiseType;
                ownType.add(own);
                friendPraise.add(praise);
                bean.praiseFlag = "Y";
                EventBus.getDefault().post(new FriendRefreshEvent());
                mPopupWindow.dismiss();
            }

            @Override
            public void onError(Exception e) {

            }
        });
        HttpManager.addRequest(mAddLikeRequest);
    }

    private DeleteLikeRequest mDeleteLikeRequest;

    private void deleteLike(String praiseType) {
        if (mDeleteLikeRequest != null && !mDeleteLikeRequest.isFinish()) {
            return;
        }
        mDeleteLikeRequest = new DeleteLikeRequest();
        mDeleteLikeRequest.addUrlParam("postid", bean.postId);
        mDeleteLikeRequest.addUrlParam("id", UserInfoManager.getUserInfo().id);
        mDeleteLikeRequest.addUrlParam("praisetype", praiseType);
        mDeleteLikeRequest.setRequestListener(new RequestListener<String>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(String result) {
                if (result == null) {
                    return;
                }
                FirstMicroListFriendPraise praise = new FirstMicroListFriendPraise();
                praise.id = UserInfoManager.getUserInfo().id;
                praise.nickName = UserInfoManager.getUserInfo().nickname;
                int index = friendPraise.indexOf(praise);
                if (index != -1) {
                    friendPraise.remove(index);
                }
                bean.praiseFlag = "N";
                ownType.clear();
                EventBus.getDefault().post(new FriendRefreshEvent());
                mPopupWindow.dismiss();
            }

            @Override
            public void onError(Exception e) {

            }
        });
        HttpManager.addRequest(mDeleteLikeRequest);
    }

    private UpdateLikeRequest mUpdateLikeRequest;

    private void updateLike(String praiseType) {
        if (mUpdateLikeRequest != null && !mUpdateLikeRequest.isFinish()) {
            return;
        }
        mUpdateLikeRequest = new UpdateLikeRequest();
        mUpdateLikeRequest.addUrlParam("postid", bean.postId);
        mUpdateLikeRequest.addUrlParam("id", UserInfoManager.getUserInfo().id);
        mUpdateLikeRequest.addUrlParam("praisetype", praiseType);
        mUpdateLikeRequest.setRequestListener(new RequestListener<String>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(String result) {
                if (result == null) {
                    return;
                }
                FirstMicroListFriendPraise praise = new FirstMicroListFriendPraise();
                praise.id = UserInfoManager.getUserInfo().id;
                praise.nickName = UserInfoManager.getUserInfo().nickname;
                int index = friendPraise.indexOf(praise);
                if (index != -1) {
                    FirstMicroListFriendPraise firstMicroListFriendPraise = friendPraise.get(index);
                    firstMicroListFriendPraise.praiseType = praiseType;
                    own.praiseType = praiseType;
                }
                bean.praiseFlag = "Y";
                EventBus.getDefault().post(new FriendRefreshEvent());
                mPopupWindow.dismiss();
            }

            @Override
            public void onError(Exception e) {

            }
        });
        HttpManager.addRequest(mUpdateLikeRequest);
    }

    private AddCommentRequest mAddCommentRequest;

    private void addComment(String content) {
        if (mAddCommentRequest != null && !mAddCommentRequest.isFinish()) {
            return;
        }
        mAddCommentRequest = new AddCommentRequest();
        mAddCommentRequest.addUrlParam("id", UserInfoManager.getUserInfo().id);
        mAddCommentRequest.addUrlParam("postid", bean.postId);
        mAddCommentRequest.addUrlParam("content", content);
        mAddCommentRequest.setRequestListener(new RequestListener<String>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(String result) {
                List<FirstMicroListFriendComment> firendcomments = bean.friendComment;
                if (null == firendcomments) {
                    firendcomments = new ArrayList<>();
                    bean.friendComment = firendcomments;
                }
                FirstMicroListFriendComment comments = new FirstMicroListFriendComment();
                comments.id = UserInfoManager.getUserInfo().id;
                comments.replyName = UserInfoManager.getUserInfo().nickname;
                comments.comment = content;
                firendcomments.add(comments);
                EventBus.getDefault().post(new FriendRefreshEvent());
            }

            @Override
            public void onError(Exception e) {

            }
        });
        HttpManager.addRequest(mAddCommentRequest);
    }

    public void show(View view) {
        if (bean == null) {
            Log.e(TAG, "data is null");
            return;
        }
        mPopupWindow.showAsDropDown(view, -CommonUtil.dip2px(170f), -CommonUtil.dip2px(70f));
    }
}
