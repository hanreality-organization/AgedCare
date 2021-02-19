package com.punuo.sys.app.compat.screensaver;

import android.os.Bundle;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.punuo.sys.app.compat.R;
import com.punuo.sys.app.router.CompatRouter;
import com.punuo.sys.sdk.activity.BaseActivity;
import com.punuo.sys.sdk.event.MessageEvent;
import com.punuo.sys.sdk.task.ImageTask;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by han.chen.
 * Date on 2021/2/19.
 **/
@Route(path = CompatRouter.ROUTER_SCREEN_SAVER_ACTIVITY)
public class ScreenSaverActivity extends BaseActivity {

    private ViewPager mViewPager;
    private ImageStatePagerAdapter mImageStatePagerAdapter;
    private final int MSG_MESSAGE = 0x0001;
    private final int duration = 5000;
    private int currentPosition = 0;
    private int size = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window window = getWindow();
        WindowManager.LayoutParams winParams = window.getAttributes();
        winParams.flags = winParams.flags | WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_saver);
        EventBus.getDefault().register(this);
        mViewPager = findViewById(R.id.view_pager);
        mImageStatePagerAdapter = new ImageStatePagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mImageStatePagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                mBaseHandler.removeMessages(MSG_MESSAGE);
                mBaseHandler.sendEmptyMessageDelayed(MSG_MESSAGE, duration);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        new ImageTask(imageList -> {
            mImageStatePagerAdapter.appendData(imageList);
            size = imageList.size();
            startPlay();
        }).execute();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event.getMessage().equals("视频开始")) {
            // 更新界面
            finish();
        } else if (event.getMessage().equals("等待通话")) {
            finish();
        }
    }

    private static class ImageStatePagerAdapter extends FragmentStatePagerAdapter {
        private final List<String> imageList = new ArrayList<>();
        public ImageStatePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void appendData(List<String> images) {
            imageList.clear();
            if (images != null) {
                imageList.addAll(images);
            }
            notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int position) {
            ScreenSaverFragment fragment = new ScreenSaverFragment();
            Bundle bundle = new Bundle();
            bundle.putString("imageUrl", imageList.get(position % imageList.size()));
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            if (imageList.isEmpty()) {
                return 0;
            }
            return imageList.size() == 1 ? 1 : Integer.MAX_VALUE;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mBaseHandler.removeMessages(MSG_MESSAGE);
    }

    private void startPlay() {
        mBaseHandler.sendEmptyMessageDelayed(MSG_MESSAGE, duration);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (msg.what == MSG_MESSAGE) {
            currentPosition = currentPosition % size;
            currentPosition++;
            mViewPager.setCurrentItem(currentPosition);
            mBaseHandler.removeMessages(MSG_MESSAGE);
            mBaseHandler.sendEmptyMessageDelayed(MSG_MESSAGE, duration);
        }
    }
}
