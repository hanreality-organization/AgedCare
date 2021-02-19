package com.punuo.sys.app.agedcare.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.R2;
import com.punuo.sys.app.agedcare.model.MyApplicationInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author chzjy
 * Date 2016/12/19.
 */

public class AppGridViewAdapter extends BaseAdapter {
    private Context context;
    private List<MyApplicationInfo> myApplicationInfoList;
    private List<MyApplicationInfo> currentAppList;
    private int PAGE_SIZE = 16;
    private int currentPage;
    public AppGridViewAdapter(Context context, List<MyApplicationInfo> myApplicationInfoList, int page) {
        this.context = context;
        this.myApplicationInfoList = myApplicationInfoList;
        this.currentPage=page;
        getData();
    }
    private void getData(){
        if (currentAppList==null) {
            currentAppList = new ArrayList<>();
        }else {
            currentAppList.clear();
        }
        int start = currentPage * PAGE_SIZE;
        int end = start + PAGE_SIZE;
        for (int i = start; i < end && i < myApplicationInfoList.size(); i++) {
            currentAppList.add(myApplicationInfoList.get(i));
        }
    }
    @Override
    public int getCount() {
        return currentAppList.size();
    }

    @Override
    public Object getItem(int position) {
        return currentAppList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.labelicon, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.imageview.setImageDrawable(currentAppList.get(position).getIcon());

        return view;
    }

    static class ViewHolder {
        @BindView(R2.id.imageview)
        ImageView imageview;


        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
