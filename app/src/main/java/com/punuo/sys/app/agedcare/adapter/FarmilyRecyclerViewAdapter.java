package com.punuo.sys.app.agedcare.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.application.AppContext;

import java.util.ArrayList;
import java.util.List;

import static com.punuo.sys.app.agedcare.sip.SipInfo.devices;
import static com.punuo.sys.app.agedcare.sip.SipInfo.serverIp;
import static com.punuo.sys.app.agedcare.sip.SipInfo.url;
import static com.punuo.sys.app.agedcare.sip.SipInfo.userAccount;

/**
 * Created by 23578 on 2018/11/22.
 */

public class FarmilyRecyclerViewAdapter extends RecyclerView.Adapter<FarmilyRecyclerViewAdapter.MyViewHolder>{
//Image资源，内容为图片的网络地址
private Context mContext;

private GridLayoutManager glm;
private OnItemClickListener mOnItemClickListener;
private OnLongItemClickListener mOnLongItemClickListener;
    public  String ip = "http://"+serverIp+":8000/static/xiaoyupeihu/";
public FarmilyRecyclerViewAdapter( Context mContext,  GridLayoutManager glm) {

        this.mContext = mContext;

        this.glm=glm;
        }

@Override
public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.picture_item,viewGroup,false);//加载item布局
        MyViewHolder myViewHolder=new MyViewHolder(view);
        return myViewHolder;
        }

@Override
public void onBindViewHolder(final MyViewHolder myViewHolder, final int i) {
       myViewHolder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);//设置图片充满ImageView并自动裁剪居中显示
        ViewGroup.LayoutParams parm = myViewHolder.imageView.getLayoutParams();
        parm.height = glm.getWidth()/glm.getSpanCount()
        - 2*myViewHolder.imageView.getPaddingLeft() - 2*((ViewGroup.MarginLayoutParams)parm).leftMargin;//设置imageView宽高相同
    Log.e("Farmily",ip+devices.get(i).getId()+"/"+devices.get(i).getAvatar());
//        ImageLoader.getInstance().displayImage(ip+devices.get(i).getId()+"/"+devices.get(i).getAvatar(),myViewHolder.imageView,options);//网络加载原图
    ImageSize targetSize = new ImageSize(150, 150); // result Bitmap will be fit to this size
//                            Log.d(TAG, url[iconorder]);
    AppContext.instance.loadImage(ip+devices.get(i).getId()+"/"+devices.get(i).getAvatar(), targetSize, new SimpleImageLoadingListener() {
        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            // Do whatever you want with Bitmap
            super.onLoadingComplete(imageUri, view, loadedImage);
           myViewHolder.imageView.setImageBitmap(loadedImage);
            devices.get(i).setBitmap(loadedImage);
        }
    });

//    Log.e("Farmily",devices.get(i).getNickname());
        myViewHolder.textView.setText(devices.get(i).getNickname());
        if(mOnItemClickListener!=null)//传递监听事件
        {
        myViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               mOnItemClickListener.onClick(myViewHolder.imageView,i);
                                  }
                            });
                          }
//        myViewHolder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
//@Override
//public boolean onLongClick(View v) {
//        mOnLongItemClickListener.onLongClick(myViewHolder.imageView,i);
//        return false;
//        }
//        });

}

    @Override
    public int getItemCount() {
        return devices.size();
        }
    class MyViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView imageView;
        private TextView textView;
        MyViewHolder(View itemView) {
        super(itemView);
        imageView=(ImageView)itemView.findViewById(R.id.item_icon);
            textView=(TextView) itemView.findViewById(R.id.item_nickName);
    }
}

    /**
     * 对外暴露子项点击事件监听器

     */
    public void setmOnItemClickListener(FarmilyRecyclerViewAdapter.OnItemClickListener mOnItemClickListener)
    {
        this.mOnItemClickListener=mOnItemClickListener;
    }
    public void setmOnLongItemClickListener(FarmilyRecyclerViewAdapter.OnLongItemClickListener mOnLongItemClickListener)
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
 interface OnLongItemClickListener
{
    void onLongClick(View view,int position);
}
}
