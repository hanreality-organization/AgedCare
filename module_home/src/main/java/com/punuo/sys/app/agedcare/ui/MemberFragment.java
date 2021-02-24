package com.punuo.sys.app.agedcare.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshRecyclerView;
import com.punuo.sip.dev.event.UpdateBindUserEvent;
import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.R2;
import com.punuo.sys.app.agedcare.adapter.FamilyRecyclerViewAdapter;
import com.punuo.sys.app.agedcare.request.GetAllUserFromGroupRequest;
import com.punuo.sys.app.agedcare.request.GetDevInfoRequest;
import com.punuo.sys.app.agedcare.request.model.DevModel;
import com.punuo.sys.app.agedcare.request.model.DeviceModel;
import com.punuo.sys.sdk.account.AccountManager;
import com.punuo.sys.sdk.fragment.BaseFragment;
import com.punuo.sys.sdk.httplib.HttpManager;
import com.punuo.sys.sdk.httplib.RequestListener;
import com.punuo.sys.sdk.util.HandlerExceptionUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MemberFragment extends BaseFragment {
    private Context mContext;

    @BindView(R2.id.farmily_rv)
    PullToRefreshRecyclerView mPullToRefreshRecyclerView;
    private RecyclerView mRecyclerView;
    private FamilyRecyclerViewAdapter adapter;
    private GridLayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();
        View view = inflater.inflate(R.layout.activity_member_fragment, container, false);
        view.setClickable(true);
        ButterKnife.bind(this, view);
        mRecyclerView = mPullToRefreshRecyclerView.getRefreshableView();
        mLayoutManager = new GridLayoutManager(mContext, 4);
        mRecyclerView.setLayoutManager(mLayoutManager);
        adapter = new FamilyRecyclerViewAdapter(mContext);
        mRecyclerView.setAdapter(adapter);
        getDevInfo();
        mPullToRefreshRecyclerView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerView>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                getDevInfo();
            }
        });
        EventBus.getDefault().register(this);
        return view;
    }

    public void getDevInfo() {
        GetDevInfoRequest request = new GetDevInfoRequest();
        request.addUrlParam("devid", AccountManager.getDevId());
        request.setRequestListener(new RequestListener<DevModel>() {
            @Override
            public void onComplete() {
                mPullToRefreshRecyclerView.onRefreshComplete();
            }

            @Override
            public void onSuccess(DevModel result) {
                AccountManager.setGroupId(result.mDevInfo.groupId);
                getAllUserFromGroup();
            }

            @Override
            public void onError(Exception e) {
                HandlerExceptionUtils.handleException(e);
            }
        });
        HttpManager.addRequest(request);
    }

    private void getAllUserFromGroup() {
        GetAllUserFromGroupRequest request = new GetAllUserFromGroupRequest();
        request.addUrlParam("groupid", AccountManager.getGroupId());
        request.setRequestListener(new RequestListener<DeviceModel>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(DeviceModel result) {
                AccountManager.setBindUsers(result.mBindUsers);
                adapter.appendData(result.mBindUsers);
            }

            @Override
            public void onError(Exception e) {

            }
        });
        HttpManager.addRequest(request);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpdateBindUserEvent event) {
        getDevInfo();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}