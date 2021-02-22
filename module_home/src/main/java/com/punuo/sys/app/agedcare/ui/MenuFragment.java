package com.punuo.sys.app.agedcare.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.launcher.ARouter;
import com.google.android.flexbox.FlexboxLayout;
import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.R2;
import com.punuo.sys.app.agedcare.model.MenuItem;
import com.punuo.sys.app.router.HomeRouter;
import com.punuo.sys.sdk.SDKRouter;
import com.punuo.sys.sdk.account.AccountManager;
import com.punuo.sys.sdk.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MenuFragment extends Fragment {
    private static final String TAG = "MenuFragment";
    @BindView(R2.id.menu_container)
    FlexboxLayout menuContainer;

    public final int CHAT = 0;
    public final int COMMUNITY = 1;
    public final int PHONECALL = 2;
    public final int HOUSEKEEPING = 3;
    public final int EAT = 4;
    public final int SHOP = 5;
    public final int MUSIC = 5;
    public final int OTHER = 6;

    private final List<MenuItem> mMenuItems = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_menu_fragment, container, false);
        ButterKnife.bind(this, view);
        initData();
        return view;
    }

    private void initData() {
        mMenuItems.add(new MenuItem(CHAT, R.drawable.qinliao1));
        mMenuItems.add(new MenuItem(COMMUNITY, R.drawable.shequ1));
        mMenuItems.add(new MenuItem(PHONECALL, R.drawable.album));
        mMenuItems.add(new MenuItem(HOUSEKEEPING, R.drawable.xiagnce));
        mMenuItems.add(new MenuItem(EAT, R.drawable.fuwu));
        mMenuItems.add(new MenuItem(MUSIC, R.drawable.yingyue1));
        mMenuItems.add(new MenuItem(OTHER, R.drawable.shezhi));
        menuContainer.removeAllViews();
        int size = (CommonUtil.getWidth() - CommonUtil.dip2px(30f) * 4 - CommonUtil.dip2px(60f) - CommonUtil.dip2px(60f)) / 4;
        for (int i = 0; i < mMenuItems.size(); i++) {
            MenuItem item = mMenuItems.get(i);
            View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.menu_item, menuContainer, false);
            itemView.getLayoutParams().width = size;
            itemView.getLayoutParams().height = size;
            ImageView imageView = itemView.findViewById(R.id.image_view);
            imageView.setImageResource(item.drawable);
            itemView.setOnClickListener(v -> {
                switch (item.id) {
                    case EAT:
                        ARouter.getInstance().build(HomeRouter.ROUTER_SERVICE_CALL_ACTIVITY).navigation();
                        break;
                    case CHAT:
                        startActivity(new Intent(getActivity(), FamilyCircleActivity.class));
                        break;
                    case COMMUNITY:
                        ARouter.getInstance().build(SDKRouter.ROUTER_WEB_VIEW_ACTIVITY)
                                .withString("url", "http://pet.qinqingonline.com:8889?user_id="+ AccountManager.getUserId())
                                .withBoolean("showTopBar", false)
                                .navigation();
                        break;
                    case HOUSEKEEPING:
                        ARouter.getInstance().build(HomeRouter.ROUTER_ALBUM_ACTIVITY).navigation();
                        break;
                    case PHONECALL:
                        startActivity(new Intent(getActivity(), FriendCallActivity.class));
                        break;
                    case MUSIC:
                        ARouter.getInstance().build(HomeRouter.ROUTER_ENTERTAINMENT_ACTIVITY).navigation();
                        break;
                    case OTHER:
                        ARouter.getInstance().build(HomeRouter.ROUTER_CODE_ACTIVITY).navigation();
                        break;
                }
            });
            menuContainer.addView(itemView);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}





