package com.punuo.sys.app.compat.screensaver;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.Glide;
import com.punuo.sys.app.compat.R;
import com.punuo.sys.app.router.CompatRouter;
import com.punuo.sys.sdk.PnApplication;
import com.punuo.sys.sdk.event.MessageEvent;
import com.punuo.sys.sdk.task.ImageTask;
import com.punuo.sys.sdk.util.BaseHandler;

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
public class ScreenSaverActivity extends AppCompatActivity implements BaseHandler.MessageHandler {

    private ViewPager mViewPager;
    private ImagePagerAdapter mImagePagerAdapter;
    private final int MSG_MESSAGE = 0x0001;
    private final int duration = 5000;
    private int currentPosition = 0;
    private int size = 0;
    private BaseHandler mBaseHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_saver);
        EventBus.getDefault().register(this);
        mBaseHandler = new BaseHandler(this);
        mViewPager = findViewById(R.id.view_pager);
        mImagePagerAdapter = new ImagePagerAdapter(this);
        mViewPager.setAdapter(mImagePagerAdapter);
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
            mImagePagerAdapter.appendData(imageList);
            size = imageList.size();
            startPlay();
        }).execute();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        ViewGroup contentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
        View childView = contentView.getChildAt(0);
        if (childView != null) {
            childView.setFitsSystemWindows(false);
            childView.requestApplyInsets();
        }
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

    private class ImagePagerAdapter extends PagerAdapter {
        private final List<String> imageList = new ArrayList<>();
        private Context context;
        private int mInvalidChildCount = 0;
        public ImagePagerAdapter(Context context) {
            this.context = context;
        }

        public void appendData(List<String> images) {
            imageList.clear();
            if (images != null) {
                imageList.addAll(images);
            }
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            String imageUrl = imageList.get(position % imageList.size());
            View view = LayoutInflater.from(context).inflate(R.layout.screen_saver_item, container, false);
            ImageView imageView = view.findViewById(R.id.image);
            Glide.with(PnApplication.getInstance()).load(imageUrl).into(imageView);

            imageView.setOnClickListener(v -> {
                finish();
            });
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            if (imageList.isEmpty()) {
                return 0;
            }
            return imageList.size() == 1 ? 1 : Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getItemPosition(Object object) {
            // 强制在notifyDatasetChange时重绘所有子节点
            if (mInvalidChildCount > 0) {
                --mInvalidChildCount;
                return POSITION_NONE;
            }

            return super.getItemPosition(object);
        }

        @Override
        public void notifyDataSetChanged() {
            mInvalidChildCount = getCount();
            super.notifyDataSetChanged();
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
        if (msg.what == MSG_MESSAGE) {
            currentPosition++;
            mViewPager.setCurrentItem(currentPosition);
            mBaseHandler.removeMessages(MSG_MESSAGE);
            mBaseHandler.sendEmptyMessageDelayed(MSG_MESSAGE, duration);
        }
    }
}
