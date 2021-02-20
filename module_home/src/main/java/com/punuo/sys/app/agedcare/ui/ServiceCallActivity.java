package com.punuo.sys.app.agedcare.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.R2;
import com.punuo.sys.app.agedcare.request.GetServiceNumberRequest;
import com.punuo.sys.app.agedcare.request.model.ServiceNumberModel;
import com.punuo.sys.app.router.HomeRouter;
import com.punuo.sys.sdk.account.AccountManager;
import com.punuo.sys.sdk.activity.BaseActivity;
import com.punuo.sys.sdk.httplib.HttpManager;
import com.punuo.sys.sdk.httplib.RequestListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
@Route(path = HomeRouter.ROUTER_SERVICE_CALL_ACTIVITY)
public class ServiceCallActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R2.id.jiazheng)
    ImageView jiazheng;
    @BindView(R2.id.wuye)
    ImageView wuye;
    @BindView(R2.id.dingcan)
    ImageView dingcan;
    private ServiceNumberModel mServiceNumberModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_call);
        ButterKnife.bind(this);
        jiazheng.setOnClickListener(this);
        wuye.setOnClickListener(this);
        dingcan.setOnClickListener(this);
        getServiceNumber();
    }

    private void getServiceNumber() {
        GetServiceNumberRequest request = new GetServiceNumberRequest();
        request.addUrlParam("devid", AccountManager.getDevId());
        request.setRequestListener(new RequestListener<List<ServiceNumberModel>>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(List<ServiceNumberModel> result) {
                if (result != null && !result.isEmpty()) {
                    mServiceNumberModel = result.get(0);
                }

            }

            @Override
            public void onError(Exception e) {

            }
        });
        HttpManager.addRequest(request);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.jiazheng) {
            if (mServiceNumberModel != null) {
                call(mServiceNumberModel.houseKeepNumber);
            }
        } else if (id == R.id.wuye) {
            if (mServiceNumberModel != null) {
                call(mServiceNumberModel.propertyNumber);
            }
        } else if (id == R.id.dingcan) {
            if (mServiceNumberModel != null) {
                call(mServiceNumberModel.orderFoodNumber);
            }
        }
    }

    private void call(String item) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + item);
        intent.setData(data);
        startActivity(intent);
    }
}
