package com.punuo.sys.app.agedcare.ui;


import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.adapter.PhoneRecyclerViewAdapter;
import com.punuo.sys.app.agedcare.db.FamilyMember;
import com.punuo.sys.app.agedcare.event.FamilyMemberManagerEvent;
import com.punuo.sys.app.router.HomeRouter;
import com.punuo.sys.sdk.account.UserInfoManager;
import com.punuo.sys.sdk.activity.BaseActivity;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

@Route(path = HomeRouter.ROUTER_FRIEND_CALL_ACTIVITY)
public class FriendCallActivity extends BaseActivity {
    private String TAG = "FriendCallActivity";
    private PhoneRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_call);
        EventBus.getDefault().register(this);
        initView();
        getData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FamilyMemberManagerEvent event) {
        getData();
    }

    private void getData() {
        SQLite.select()
                .from(FamilyMember.class)
                .async()
                .queryListResultCallback((transaction, tResult) -> {
                    adapter.addAllData(tResult);
                }).execute();

    }

    private void initView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);//定义3列的网格布局
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new FriendCallActivity.RecyclerViewItemDecoration(20, 3));//初始化子项距离和列数
        adapter = new PhoneRecyclerViewAdapter(this);
        recyclerView.setAdapter(adapter);
        View fab = findViewById(R.id.add);
        fab.setOnClickListener(v -> ARouter.getInstance().build(HomeRouter.ROUTER_FAMILY_MEMBER_MANAGER_ACTIVITY).navigation());
        TextView localIpNumber = findViewById(R.id.local_ip_number);
        localIpNumber.setText("本机号码：" + UserInfoManager.getUserInfo().ipNumber);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public static class RecyclerViewItemDecoration extends RecyclerView.ItemDecoration {
        private final int itemSpace;//定义子项间距
        private final int itemColumnNum;//定义子项的列数

        public RecyclerViewItemDecoration(int itemSpace, int itemColumnNum) {
            this.itemSpace = itemSpace;
            this.itemColumnNum = itemColumnNum;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.bottom = itemSpace;//底部留出间距
            if (parent.getChildLayoutPosition(view) % itemColumnNum == 0) {//每行第一项左边不留间距，其他留出间距
                outRect.left = 0;
            } else {
                outRect.left = itemSpace;
            }

        }
    }
}
