package com.punuo.sys.app.agedcare.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.model.Farmilymember;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.punuo.sys.app.agedcare.sip.SipInfo.dbHelper;
import static com.punuo.sys.app.agedcare.sip.SipInfo.farmilymemberList;




/**
 * Created by miner on 2018/7/26.
 */

public class FarmilyAdapter extends RecyclerView.Adapter<FarmilyAdapter.ViewHolder> {
    private List<Farmilymember> farmilymembers;


    private List mDatas=new ArrayList<>();
    public Context mcontext;

    public FarmilyAdapter(Context context,List<Farmilymember> farmilymemberList) {
        mcontext=context;
        farmilymembers=farmilymemberList;
    }
    public void refreshDatas(List mDataGoods) {
        mDatas.clear();
        mDatas.addAll(mDataGoods);
        notifyDataSetChanged();
    }
    public void removeData(int position) {
        farmilymembers.remove(position);
        //删除动画
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    @Override
    public FarmilyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.family_item, parent, false);
        FarmilyAdapter.ViewHolder viewHolder = new FarmilyAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Farmilymember farmilymember=farmilymemberList.get(position);

        holder.name.setText(farmilymember.getLinkman());
        holder.phonenumber.setText(farmilymember.getTelnum());
        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                Uri data = Uri.parse("tel:" + holder.phonenumber.getText().toString());
                intent.setData(data);
               mcontext.startActivity(intent);
            }
        });
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db=dbHelper.getWritableDatabase();
                        Log.d("ton", "onClick: "+holder.name.getText().toString());
                       db.execSQL("delete from Person where name = ?",new String[]{holder.name.getText().toString()});
//             db.delete("Person","name = ?",new String[]{holder.name.toString()});
                      removeData(position);

//                AlertDialog.Builder dialog=new AlertDialog.Builder(context.getApplicationContext());
//                dialog.setTitle("确定要删除该联系人吗");
//                dialog.setCancelable(true);
//                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        SQLiteDatabase db=dbHelper.getWritableDatabase();
//                        Log.d("ton", "onClick: "+holder.name.getText().toString());
//                        db.execSQL("delete from Person where name = ?",new String[]{holder.name.getText().toString()});
////              db.delete("Person","name = ?",new String[]{holder.name.toString()});
//                        removeData(position);
//                    }
//                });
//             dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                 @Override
//                 public void onClick(DialogInterface dialog, int which) {
//
//                 }
//             });
//                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return farmilymemberList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.name)
        TextView name;
        @Bind(R.id.phonenumber)
        TextView phonenumber;
        @Bind(R.id.remove)
        Button remove;
        @Bind(R.id.add)
        Button add;
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
