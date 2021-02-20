package com.punuo.sys.app.agedcare.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.sdk.activity.BaseActivity;
import com.punuo.sys.sdk.view.LoopIndicator;

import java.util.ArrayList;
import java.util.List;


public class EntertainmentActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entertainment);
        LoopIndicator loopIndicator = findViewById(R.id.indicator);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new MusicPlayFragment());
        fragments.add(new ShortMovieFragment());
        loopIndicator.select(fragments.size());
        loopIndicator.select(0);
        FragAdapter adapter = new FragAdapter(getSupportFragmentManager(), fragments);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                loopIndicator.select(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public static class FragAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragments;

        FragAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            mFragments = fragments;
        }

        @Override
        public Fragment getItem(int arg0) {
            return mFragments.get(arg0);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }
}
