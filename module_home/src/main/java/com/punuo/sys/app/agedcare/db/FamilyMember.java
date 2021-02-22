package com.punuo.sys.app.agedcare.db;

import android.os.Parcel;
import android.os.Parcelable;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.UUID;

/**
 * Created by 23578 on 2018/7/26.
 */
@Table(database = FamilyMemberTable.class)
public class FamilyMember extends BaseModel implements Parcelable {

    @PrimaryKey
    public UUID id;
    @Column
    public String avatarUrl;
    @Column
    public String name;
    @Column
    public String phoneNumber;

    public FamilyMember() {

    }


    protected FamilyMember(Parcel in) {
        id = (UUID) in.readSerializable();
        avatarUrl = in.readString();
        name = in.readString();
        phoneNumber = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(id);
        dest.writeString(avatarUrl);
        dest.writeString(name);
        dest.writeString(phoneNumber);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FamilyMember> CREATOR = new Creator<FamilyMember>() {
        @Override
        public FamilyMember createFromParcel(Parcel in) {
            return new FamilyMember(in);
        }

        @Override
        public FamilyMember[] newArray(int size) {
            return new FamilyMember[size];
        }
    };
}
