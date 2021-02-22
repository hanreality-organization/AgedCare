package com.punuo.sys.app.agedcare.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.friendCircle.adapter.ImageDetailFragment;
import com.punuo.sys.app.router.HomeRouter;
import com.punuo.sys.sdk.activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;

@Route(path = HomeRouter.ROUTER_IMAGE_PAGER_ACTIVITY)
public class ImagePagerActivity extends BaseActivity {
    private static final String STATE_POSITION = "STATE_POSITION";
    public static final String EXTRA_IMAGE_INDEX = "image_index";
    public static final String EXTRA_IMAGE_URLS = "image_urls";
    private ViewPager mViewPager;

    private ImagePagerAdapter adapter;
    private TextView vp_text;

    @Autowired(name = EXTRA_IMAGE_URLS)
    ArrayList<String> urls;

    @Autowired(name = EXTRA_IMAGE_INDEX)
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_pager);
        ARouter.getInstance().inject(this);
        mViewPager = (ViewPager) findViewById(R.id.vp);
        vp_text = (TextView) findViewById(R.id.vp_text);
        adapter = new ImagePagerAdapter(getSupportFragmentManager(), urls);
        mViewPager.setAdapter(adapter);
        CharSequence text = getString(R.string.viewpager_indicator, position + 1, urls.size());
        vp_text.setText(text);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position1) {
                CharSequence text = getString(R.string.viewpager_indicator, position + 1, urls.size());
                vp_text.setText(text);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        if (savedInstanceState != null) {
            position = savedInstanceState.getInt(STATE_POSITION);
        }
        mViewPager.setCurrentItem(position);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_POSITION, mViewPager.getCurrentItem());
    }

    private static class ImagePagerAdapter extends FragmentStatePagerAdapter {
        private final List<String> urls;

        public ImagePagerAdapter(FragmentManager fm, List<String> urls) {
            super(fm);
            this.urls = urls;
        }

        @Override
        public int getCount() {
            return urls.size();
        }

        @Override
        public Fragment getItem(int position) {
            String url = urls.get(position);
            return ImageDetailFragment.newInstance(url);
        }

    }

}
