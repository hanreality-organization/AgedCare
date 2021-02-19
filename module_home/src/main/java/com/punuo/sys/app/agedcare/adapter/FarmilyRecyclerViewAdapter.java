package com.punuo.sys.app.agedcare.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.punuo.sys.app.agedcare.R;

import static com.punuo.sys.app.agedcare.sip.SipInfo.devices;
import static com.punuo.sys.app.agedcare.sip.SipInfo.serverIp;

/**
 * Created by 23578 on 2018/11/22.
 */

public class FarmilyRecyclerViewAdapter extends RecyclerView.Adapter<FarmilyRecyclerViewAdapter.MyViewHolder> {
    //Image资源，内容为图片的网络地址
    private Context mContext;

    private GridLayoutManager glm;
    private OnItemClickListener mOnItemClickListener;
    private OnLongItemClickListener mOnLongItemClickListener;
    public String ip = "http://" + serverIp + ":8000/static/xiaoyupeihu/";

    public FarmilyRecyclerViewAdapter(Context mContext, GridLayoutManager glm) {

        this.mContext = mContext;

        this.glm = glm;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.picture_item, viewGroup, false);//加载item布局
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, final int i) {
        myViewHolder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);//设置图片充满ImageView并自动裁剪居中显示
        ViewGroup.LayoutParams parm = myViewHolder.imageView.getLayoutParams();
        parm.height = glm.getWidth() / glm.getSpanCount()
                - 2 * myViewHolder.imageView.getPaddingLeft() - 2 * ((ViewGroup.MarginLayoutParams) parm).leftMargin;//设置imageView宽高相同
        Log.e("Farmily", ip + devices.get(i).getId() + "/" + devices.get(i).getAvatar());
        Glide.with(mContext).asBitmap().load(ip + devices.get(i).getId() + "/" + devices.get(i).getAvatar())
                .apply(new RequestOptions().override(150, 150))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        myViewHolder.imageView.setImageBitmap(resource);
                        devices.get(i).setBitmap(resource);
                    }
                });
        myViewHolder.textView.setText(devices.get(i).getNickname());
        if (mOnItemClickListener != null) {//传递监听事件
            myViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(myViewHolder.imageView, i);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;

        MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.item_icon);
            textView = (TextView) itemView.findViewById(R.id.item_nickName);
        }
    }

    /**
     * 对外暴露子项点击事件监听器
     */
    public void setmOnItemClickListener(FarmilyRecyclerViewAdapter.OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setmOnLongItemClickListener(FarmilyRecyclerViewAdapter.OnLongItemClickListener mOnLongItemClickListener) {
        this.mOnLongItemClickListener = mOnLongItemClickListener;
    }

    /**
     * 子项点击接口
     */
    public interface OnItemClickListener {
        void onClick(View view, int position);

    }

    interface OnLongItemClickListener {
        void onLongClick(View view, int position);
    }
}
