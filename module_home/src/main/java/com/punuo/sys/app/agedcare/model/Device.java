package com.punuo.sys.app.agedcare.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by asus on 2018/1/15.
 */

public class Device implements Parcelable {
    private String id;
    private String userid;
    private String nickname;
    private String name;
    private String avatar;
    private String auth;
    private String devId;
    private String devType;

    protected Device(Parcel in) {
        id = in.readString();
        userid = in.readString();
        nickname = in.readString();
        name = in.readString();
        avatar = in.readString();
        auth = in.readString();
        devId = in.readString();
        devType = in.readString();
    }

    public static final Creator<Device> CREATOR = new Creator<Device>() {
        @Override
        public Device createFromParcel(Parcel in) {
            return new Device(in);
        }

        @Override
        public Device[] newArray(int size) {
            return new Device[size];
        }
    };

    public String getDevType() {
        return devType;
    }

    public void setDevType(String devType) {
        if (devType.isEmpty()){
            this.devType="0";
        }
        this.devType = devType;
    }

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        if (devId.isEmpty()){
            this.devId="0";
        }
        this.devId = devId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id.isEmpty()){
            this.id="0";
        }
        this.id = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        if (userid.isEmpty()){
            this.userid="0";
        }
        this.userid = userid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        if (nickname.isEmpty()){
            this.nickname="0";
        }
        this.nickname = nickname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name.isEmpty()){
            this.name="0";
        }
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        if (avatar.isEmpty()){
            this.avatar="0";
        }
        this.avatar = avatar;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        if (auth.isEmpty()){
            this.auth="0";
        }
        this.auth = auth;
    }

    @Override
    public String toString() {
        return "id:"+id+" userid:"+userid +" nickname:"+nickname+" name:"+name+" avatar:"+avatar+" auth:"+auth;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(userid);
        dest.writeString(nickname);
        dest.writeString(name);
        dest.writeString(avatar);
        dest.writeString(auth);
        dest.writeString(devId);
        dest.writeString(devType);
    }
}
