package com.punuo.sys.app.agedcare.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshRecyclerView;
import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.R2;
import com.punuo.sys.app.agedcare.friendCircle.adapter.FriendCircleAdapter;
import com.punuo.sys.app.agedcare.friendCircle.domain.FriendMicroList;
import com.punuo.sys.app.agedcare.friendCircle.domain.FriendMicroListData;
import com.punuo.sys.app.agedcare.friendCircle.domain.FriendsMicro;
import com.punuo.sys.app.agedcare.friendCircle.event.FriendReLoadEvent;
import com.punuo.sys.app.agedcare.friendCircle.event.FriendRefreshEvent;
import com.punuo.sys.app.agedcare.request.GetPostListFromGroupRequest;
import com.punuo.sys.sdk.account.AccountManager;
import com.punuo.sys.sdk.account.UserInfoManager;
import com.punuo.sys.sdk.activity.BaseActivity;
import com.punuo.sys.sdk.httplib.HttpManager;
import com.punuo.sys.sdk.httplib.RequestListener;
import com.punuo.sys.sdk.recyclerview.CompletedFooter;
import com.punuo.sys.sdk.recyclerview.OnLoadMoreHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FamilyCircleActivity extends BaseActivity {
    @BindView(R2.id.iv_back7)
    Button ivBack7;
    @BindView(R2.id.pull_to_refresh)
    PullToRefreshRecyclerView mPullToRefreshRecyclerView;
    private RecyclerView mRecyclerView;
    private static final String TAG = "FamilyCircleActivity";

    public FriendCircleAdapter mFriendCircleAdapter;
    private boolean hasMore = false;
    private int pageNum = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_family_circle);
        ButterKnife.bind(this);
        init();
    }

    public void refresh() {
        pageNum = 1;
        getPostList(pageNum);
    }

    private GetPostListFromGroupRequest mGetPostListFromGroupRequest;

    private void getPostList(int page) {
        if (mGetPostListFromGroupRequest != null && !mGetPostListFromGroupRequest.isFinish()) {
            return;
        }
        boolean isFirstPage = (page == 1);
        mGetPostListFromGroupRequest = new GetPostListFromGroupRequest();
        mGetPostListFromGroupRequest.addUrlParam("id", UserInfoManager.getUserInfo().id);
        mGetPostListFromGroupRequest.addUrlParam("currentPage", page);
        mGetPostListFromGroupRequest.addUrlParam("groupid", AccountManager.getGroupId());
        mGetPostListFromGroupRequest.setRequestListener(new RequestListener<FriendsMicro>() {
            @Override
            public void onComplete() {
                if (isFirstPage) {
                    mPullToRefreshRecyclerView.onRefreshComplete();
                }
                mFriendCircleAdapter.onLoadMoreCompleted();
            }

            @Override
            public void onSuccess(FriendsMicro result) {
                if (result == null) {
                    return;
                }
                FriendMicroList friendMicroList = result.postList;
                if (friendMicroList == null) {
                    return;
                }
                List<FriendMicroListData> list = friendMicroList.data;
                if (isFirstPage) {
                    mFriendCircleAdapter.resetData(list);
                } else {
                    mFriendCircleAdapter.addAll(list);
                }
                hasMore = (friendMicroList.total - friendMicroList.perPage * friendMicroList.currentPage) > 0;
                pageNum = friendMicroList.currentPage + 1;
            }

            @Override
            public void onError(Exception e) {
                if (isFirstPage) {
                    mPullToRefreshRecyclerView.onRefreshComplete();
                } else {
                    mFriendCircleAdapter.onLoadMoreFailed();
                }
            }
        });
        HttpManager.addRequest(mGetPostListFromGroupRequest);
    }

    private void init() {
        mPullToRefreshRecyclerView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerView>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                refresh();
            }
        });
        mRecyclerView = mPullToRefreshRecyclerView.getRefreshableView();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mFriendCircleAdapter = new FriendCircleAdapter(this, new ArrayList<>());
        mFriendCircleAdapter.setOnLoadMoreHelper(new OnLoadMoreHelper() {
            @Override
            public boolean canLoadMore() {
                return hasMore;
            }

            @Override
            public void onLoadMore() {
                getPostList(pageNum);
            }
        });
        mFriendCircleAdapter.setCompletedFooterListener(new CompletedFooter.CompletedFooterListener() {
            @Override
            public boolean enableFooter() {
                return !hasMore;
            }

            @Override
            public View generateCompletedFooterView(Context context, ViewGroup parent) {
                return LayoutInflater.from(context).inflate(
                        R.layout.recycle_item_completed_foot, parent, false);
            }
        });
        mRecyclerView.setAdapter(mFriendCircleAdapter);
        refresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FriendRefreshEvent event) {
        mFriendCircleAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FriendReLoadEvent event) {
        refresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @OnClick({R2.id.iv_back7})
    public void onClock(View v) {
        int id = v.getId();
        if (id == R.id.iv_back7) {
            finish();
        }
    }
}
