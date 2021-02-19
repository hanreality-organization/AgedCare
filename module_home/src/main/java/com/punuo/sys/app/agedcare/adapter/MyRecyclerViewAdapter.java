package com.punuo.sys.app.agedcare.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.punuo.sys.app.agedcare.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/4/28 0028.
 */

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> {

    private List<String> images = new ArrayList<String>();//Image资源，内容为图片的网络地址
    private Context mContext;
    private GridLayoutManager glm;
    private OnItemClickListener mOnItemClickListener;
    private OnLongItemClickListener mOnLongItemClickListener;

    public MyRecyclerViewAdapter(List<String> images, Context mContext, GridLayoutManager glm) {
        this.images = images;
        this.mContext = mContext;
        this.glm = glm;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.rv_pictureitem, null);//加载item布局
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, final int i) {
        myViewHolder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);//设置图片充满ImageView并自动裁剪居中显示
        ViewGroup.LayoutParams parm = myViewHolder.imageView.getLayoutParams();
        parm.height = glm.getWidth() / glm.getSpanCount()
                - 2 * myViewHolder.imageView.getPaddingLeft() - 2 * ((ViewGroup.MarginLayoutParams) parm).leftMargin;//设置imageView宽高相同
        Glide.with(mContext).load(images.get(i)).into(myViewHolder.imageView);
        if (mOnItemClickListener != null) {
            myViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(myViewHolder.imageView, i);
                }

            });


        }
        myViewHolder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                try {
                    mOnLongItemClickListener.onLongClick(myViewHolder.imageView, i);

                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.album_item);
        }
    }

    /**
     * 对外暴露子项点击事件监听器
     *
     * @param mOnItemClickListener
     */
    public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setmOnLongItemClickListener(OnLongItemClickListener mOnLongItemClickListener) {
        this.mOnLongItemClickListener = mOnLongItemClickListener;
    }

    /**
     * 子项点击接口
     */
    public interface OnItemClickListener {
        void onClick(View view, int position);

    }

    public interface OnLongItemClickListener {
        void onLongClick(View view, int position);
    }
}
