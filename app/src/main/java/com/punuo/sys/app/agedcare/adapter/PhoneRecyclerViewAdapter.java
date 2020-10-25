package com.punuo.sys.app.agedcare.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.http.GetPostUtil;
import com.punuo.sys.app.agedcare.model.Constant;
import com.punuo.sys.app.agedcare.ui.MessageEvent;
import com.punuo.sys.app.agedcare.ui.addressAddActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import static com.punuo.sys.app.agedcare.sip.SipInfo.dbHelper;
import static com.punuo.sys.app.agedcare.sip.SipInfo.farmilymemberList;




/**
 * Created by 23578 on 2018/10/31.
 */

public class PhoneRecyclerViewAdapter extends RecyclerView.Adapter<PhoneRecyclerViewAdapter.MyViewHolder>{

    private Context mContext;

    private GridLayoutManager glm;
    private PhoneRecyclerViewAdapter.OnItemClickListener mOnItemClickListener;
    private PhoneRecyclerViewAdapter.OnLongItemClickListener mOnLongItemClickListener;
    Button cancle;
    Button delete;
    Button edit;
    Dialog mCameraDialog;

    public PhoneRecyclerViewAdapter( Context mContext, GridLayoutManager glm) {

        this.mContext = mContext;

        this.glm=glm;
    }

    @Override
    public PhoneRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.rv_item_layout,null);//加载item布局
        PhoneRecyclerViewAdapter.MyViewHolder myViewHolder=new PhoneRecyclerViewAdapter.MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final PhoneRecyclerViewAdapter.MyViewHolder myViewHolder, final int i) {
        myViewHolder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);//设置图片充满ImageView并自动裁剪居中显示
        ViewGroup.LayoutParams parm = myViewHolder.imageView.getLayoutParams();
        parm.height = glm.getWidth()/glm.getSpanCount()
                - 2*myViewHolder.imageView.getPaddingLeft() - 2*((ViewGroup.MarginLayoutParams)parm).leftMargin;//设置imageView宽高相同
//        ImageLoader.getInstance().displayImage(images.get(i),myViewHolder.imageView,options);//网络加载原图

        myViewHolder.textView.setText(farmilymemberList.get(i).getLinkman());
        if (farmilymemberList.get(i).getPic().equals("nopic"))
        {
            myViewHolder.imageView.setImageResource(R.drawable.defaultavator);
        }else {
            ImageLoader.getInstance().displayImage(farmilymemberList.get(i).getPic(), myViewHolder.imageView);
        }
        myViewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.d("onlongclick","success");
                setDialog();
                cancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCameraDialog.dismiss();
                    }
                });
                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String avatorurl=farmilymemberList.get(i).getPic();
                        String name=farmilymemberList.get(i).getLinkman();
                        String phonenumber=farmilymemberList.get(i).getTelnum();
                        Intent intent=new Intent(mContext,addressAddActivity.class);
                        intent.putExtra("extra_avatorurl",avatorurl);
                        intent.putExtra("extra_name",name);
                        intent.putExtra("extra_phonenumber",phonenumber);
                        intent.putExtra("extra_id",farmilymemberList.get(i).getId());
                        intent.putExtra("extra_position",i);
                        mContext.startActivity(intent);
                        mCameraDialog.dismiss();
                    }
                });
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                GetPostUtil.sendGet1111(Constant.URL_deleteAddressbook, "id=" + farmilymemberList.get(i).getId());
                                EventBus.getDefault().post(new MessageEvent("addcompelete"));
                                farmilymemberList.remove(i);
                            }
                        }).start();

                        mCameraDialog.dismiss();
                    }
                });

            }
        });
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
        return farmilymemberList.size();
    }
    class MyViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView imageView;
        private TextView textView;
        private ImageView delete;
        public MyViewHolder(View itemView) {
            super(itemView);
            imageView=(ImageView)itemView.findViewById(R.id.iv_item);
            textView=(TextView)itemView.findViewById(R.id.iv_name);
            delete=(ImageView)itemView.findViewById(R.id.delete);
        }
    }

    public void setmOnItemClickListener(PhoneRecyclerViewAdapter.OnItemClickListener mOnItemClickListener)
    {
        this.mOnItemClickListener=mOnItemClickListener;
    }
    public void setmOnLongItemClickListener(PhoneRecyclerViewAdapter.OnLongItemClickListener mOnLongItemClickListener)
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
    private void setDialog() {
         mCameraDialog = new Dialog(mContext, R.style.BottomDialog);
        LinearLayout root = (LinearLayout) LayoutInflater.from(mContext).inflate(
                R.layout.editpop, null);
        //初始化视图
        cancle=(Button) root.findViewById(R.id.pop_cancle);
        edit=(Button)root.findViewById(R.id.pop_edit);
        delete=(Button)root.findViewById(R.id.pop_delete);
        root.findViewById(R.id.pop_delete);
        root.findViewById(R.id.pop_edit);
        mCameraDialog.setContentView(root);
        Window dialogWindow = mCameraDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
//        dialogWindow.setWindowAnimations(R.style.dialogstyle); // 添加动画
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = 0; // 新位置Y坐标
        lp.width = mContext.getResources().getDisplayMetrics().widthPixels; // 宽度
        root.measure(0, 0);
        lp.height = root.getMeasuredHeight();

        lp.alpha = 9f; // 透明度
        dialogWindow.setAttributes(lp);
        mCameraDialog.show();
    }

}
