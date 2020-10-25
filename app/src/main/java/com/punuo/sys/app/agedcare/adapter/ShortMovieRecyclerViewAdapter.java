package com.punuo.sys.app.agedcare.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.application.AppContext;
import com.punuo.sys.app.agedcare.ui.addressAddActivity;

import static com.punuo.sys.app.agedcare.sip.SipInfo.dbHelper;
import static com.punuo.sys.app.agedcare.sip.SipInfo.farmilymemberList;
import static com.punuo.sys.app.agedcare.sip.SipInfo.movies;
import static com.punuo.sys.app.agedcare.sip.SipInfo.musicitems;
import static com.punuo.sys.app.agedcare.sip.SipInfo.musics;
import static com.punuo.sys.app.agedcare.sip.SipInfo.serverIp;

/**
 * Created by 23578 on 2018/11/25.
 */

public class ShortMovieRecyclerViewAdapter extends RecyclerView.Adapter<ShortMovieRecyclerViewAdapter.MyViewHolder>{

    private Context mContext;

    private GridLayoutManager glm;
    private OnItemClickListener mOnItemClickListener;
    private OnLongItemClickListener mOnLongItemClickListener;



    public ShortMovieRecyclerViewAdapter( Context mContext, GridLayoutManager glm) {

        this.mContext = mContext;

        this.glm=glm;
    }

    @Override
    public ShortMovieRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.rv_item_shortmovie,viewGroup,false);//加载item布局
        ShortMovieRecyclerViewAdapter.MyViewHolder myViewHolder=new ShortMovieRecyclerViewAdapter.MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final ShortMovieRecyclerViewAdapter.MyViewHolder myViewHolder, final int i) {
        myViewHolder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);//设置图片充满ImageView并自动裁剪居中显示
        ViewGroup.LayoutParams parm = myViewHolder.imageView.getLayoutParams();
        parm.height = glm.getWidth()/glm.getSpanCount()
                - 2*myViewHolder.imageView.getPaddingLeft() - 2*((ViewGroup.MarginLayoutParams)parm).leftMargin;//设置imageView宽高相同
//        ImageLoader.getInstance().displayImage(images.get(i),myViewHolder.imageView,options);//网络加载原图

        myViewHolder.textView.setText(movies.get(i).getTitle());
        myViewHolder.movie_info.setText(movies.get(i).getInfo());
        Log.e("movie",movies.get(i).getId());
        if (movies.get(i).getId()==null)
        {
            myViewHolder.imageView.setImageResource(R.drawable.testcover);
        }else {
            AppContext.instance.displayImage("http://" + serverIp + ":8000/static/videoListCover/" + movies.get(i).getCover() + ".png", myViewHolder.imageView);
            Log.e("movie","http://" + serverIp + ":8000/static/videoListCover/" + movies.get(i).getId() + ".png");
        }

        if(mOnItemClickListener!=null)//传递监听事件
        {
            myViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(myViewHolder.imageView,i);
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return movies.size();
    }
    class MyViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView imageView;
        private TextView textView;
        private TextView movie_info;
        public MyViewHolder(View itemView) {
            super(itemView);
            imageView=(ImageView)itemView.findViewById(R.id.movie_item);
            textView=(TextView)itemView.findViewById(R.id.movie_name);
            movie_info=(TextView)itemView.findViewById(R.id.info);
        }
    }

    public void setmOnItemClickListener(ShortMovieRecyclerViewAdapter.OnItemClickListener mOnItemClickListener)
    {
        this.mOnItemClickListener=mOnItemClickListener;
    }
    public void setmOnLongItemClickListener(ShortMovieRecyclerViewAdapter.OnLongItemClickListener mOnLongItemClickListener)
    {
        this.mOnLongItemClickListener=mOnLongItemClickListener;
    }
    /**
     * 子项点击接口
     */
    public interface OnItemClickListener
    {
        void onClick(View view, int position);

    }
    public interface OnLongItemClickListener
    {
        void onLongClick(View view,int position);
    }
}
