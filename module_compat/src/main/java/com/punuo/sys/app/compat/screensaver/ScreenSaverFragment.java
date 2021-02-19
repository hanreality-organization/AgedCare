package com.punuo.sys.app.compat.screensaver;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.punuo.sys.app.compat.R;
import com.punuo.sys.sdk.PnApplication;
import com.punuo.sys.sdk.fragment.BaseFragment;

/**
 * Created by han.chen.
 * Date on 2021/2/19.
 **/
public class ScreenSaverFragment extends BaseFragment {
    private String imageUrl;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragmentView = inflater.inflate(R.layout.screen_saver_fragment, container, false);
        return mFragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            imageUrl = arguments.getString("imageUrl", "");
        }
        ImageView imageView = mFragmentView.findViewById(R.id.image);
        Glide.with(PnApplication.getInstance()).load(imageUrl).into(imageView);

        imageView.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
    }
}
