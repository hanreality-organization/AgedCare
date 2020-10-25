package com.punuo.sys.app.agedcare.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.model.Device;
import com.punuo.sys.app.agedcare.sip.SipInfo;
import com.punuo.sys.app.agedcare.view.CustomProgressDialog;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;




/**
 * Created by asus on 2018/1/22.
 */

public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.ViewHolder> {
    String apkPath;
    private Context mContext;
    Object obj = new Object();
    private CustomProgressDialog inviting;
    private Handler handler=new Handler();
    public static String ip = "http://101.69.255.134:8000/static/xiaoyupeihu/";
    String devId;

    private Drawable mDefaultBitmapDrawable;


    public PictureAdapter(Context mContext) {
        this.mContext = mContext;
        mDefaultBitmapDrawable = mContext.getResources().getDrawable(R.drawable.image_default);
    }

    public void appendData(List<Device> devices) {
        synchronized (obj) {
            if (devices.isEmpty()) return;
            SipInfo.devList.clear();
            SipInfo.devList.addAll(devices);
            notifyDataSetChanged();
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.picture_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        return holder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Device device = SipInfo.devList.get(position);
        Log.d("posit",""+position);
        ImageView imageView = holder.icon;

    }

    @Override
    public int getItemCount() {
        return SipInfo.devList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.icon)
        ImageView icon;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
