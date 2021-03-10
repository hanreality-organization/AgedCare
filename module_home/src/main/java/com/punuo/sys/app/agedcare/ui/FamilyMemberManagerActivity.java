package com.punuo.sys.app.agedcare.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.punuo.sys.app.agedcare.R;
import com.punuo.sys.app.agedcare.R2;
import com.punuo.sys.app.agedcare.db.FamilyMember;
import com.punuo.sys.app.agedcare.db.FamilyMember_Table;
import com.punuo.sys.app.agedcare.event.FamilyMemberManagerEvent;
import com.punuo.sys.app.linphone.LinphoneHelper;
import com.punuo.sys.app.router.HomeRouter;
import com.punuo.sys.sdk.activity.BaseActivity;
import com.punuo.sys.sdk.util.ToastUtils;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

@Route(path = HomeRouter.ROUTER_FAMILY_MEMBER_MANAGER_ACTIVITY)
public class FamilyMemberManagerActivity extends BaseActivity {
    private final static String TAG = "FamilyMemberManagerActivity";
    @BindView(R2.id.edit_name)
    EditText edit_name;
    @BindView(R2.id.edit_number)
    EditText edit_number;
    @BindView(R2.id.call)
    Button callBtn;
    @BindView(R2.id.add)
    Button add;
    @BindView(R2.id.delete)
    Button delete;
    @BindView(R2.id.selectavator)
    ImageView selectavator;

    @Autowired(name = "family_member")
    FamilyMember mFamilyMember;

    private String mFamilyMemberName;
    private String mFamilyMemberPhoneNumber;
    private String mFamilyMemberAvatarUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_manager);
        ARouter.getInstance().inject(this);
        ButterKnife.bind(this);
        initView();
    }

    public void initView() {
        if (mFamilyMember != null) {
            edit_name.setText(mFamilyMember.name);
            edit_number.setText(mFamilyMember.phoneNumber);
            Glide.with(this)
                    .load(mFamilyMember.avatarUrl)
                    .apply(new RequestOptions().error(R.drawable.defaultavator))
                    .into(selectavator);
            add.setText("修改");
            delete.setVisibility(View.VISIBLE);
            callBtn.setVisibility(View.VISIBLE);
        } else {
            add.setText("添加");
            delete.setVisibility(View.GONE);
            callBtn.setVisibility(View.GONE);
        }
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFamilyMemberName = edit_name.getText().toString();
                mFamilyMemberPhoneNumber = edit_number.getText().toString();
                if (TextUtils.isEmpty(mFamilyMemberName)) {
                    ToastUtils.showToast("联系人为空");
                    return;
                } else if (TextUtils.isEmpty(mFamilyMemberPhoneNumber)) {
                    ToastUtils.showToast("电话号码为空");
                    return;
                } else {
                    if (mFamilyMember == null) {
                        FamilyMember person = new FamilyMember();
                        person.id = UUID.randomUUID();
                        person.avatarUrl = mFamilyMemberAvatarUrl;
                        person.name = mFamilyMemberName;
                        person.phoneNumber = mFamilyMemberPhoneNumber;
                        person.insert();
                        ToastUtils.showToast("添加成功");
                    } else {
                        FamilyMember person = SQLite.select()
                                .from(FamilyMember.class)
                                .where(FamilyMember_Table.id.eq(mFamilyMember.id))
                                .querySingle();
                        if (person != null) {
                            if (!TextUtils.isEmpty(mFamilyMemberAvatarUrl)) {
                                person.avatarUrl = mFamilyMemberAvatarUrl;
                            }
                            person.name = mFamilyMemberName;
                            person.phoneNumber = mFamilyMemberPhoneNumber;
                            person.update();
                            ToastUtils.showToast("修改成功");
                        }
                    }
                }
                EventBus.getDefault().post(new FamilyMemberManagerEvent());
                finish();
            }
        });

        delete.setOnClickListener(v -> {
            SQLite.delete().from(FamilyMember.class)
                    .where(FamilyMember_Table.id.eq(mFamilyMember.id))
                    .execute();
            EventBus.getDefault().post(new FamilyMemberManagerEvent());
            ToastUtils.showToast("删除成功");
            finish();
        });

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mFamilyMember.phoneNumber)) {
                    LinphoneHelper.getInstance().call(FamilyMemberManagerActivity.this,
                            mFamilyMember.phoneNumber, false, 0);
                } else {
                    ToastUtils.showToast("电话号码为空");
                }
            }
        });

        selectavator.setOnClickListener(v -> PictureSelector.create(FamilyMemberManagerActivity.this)
                .openGallery(PictureMimeType.ofImage())
                .imageSpanCount(4)
                .selectionMode(PictureConfig.SINGLE)
                .imageFormat(PictureMimeType.JPEG)
                .forResult(PictureConfig.CHOOSE_REQUEST));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PictureConfig.CHOOSE_REQUEST) {
                List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                LocalMedia localMedia = selectList.get(0);
                mFamilyMemberAvatarUrl = localMedia.getPath();
                Glide.with(this).load(mFamilyMemberAvatarUrl).into(selectavator);
            }
        }
    }
}

