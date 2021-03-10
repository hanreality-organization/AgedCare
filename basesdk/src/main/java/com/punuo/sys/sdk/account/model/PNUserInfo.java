package com.punuo.sys.sdk.account.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.punuo.sys.sdk.model.PNBaseModel;

/**
 * Created by han.chen.
 * Date on 2021/2/19.
 **/
public class PNUserInfo extends PNBaseModel implements Parcelable {

    @SerializedName("user")
    public UserInfo userInfo;

    protected PNUserInfo(Parcel in) {
        userInfo = in.readParcelable(UserInfo.class.getClassLoader());
    }

    public static final Creator<PNUserInfo> CREATOR = new Creator<PNUserInfo>() {
        @Override
        public PNUserInfo createFromParcel(Parcel in) {
            return new PNUserInfo(in);
        }

        @Override
        public PNUserInfo[] newArray(int size) {
            return new PNUserInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(userInfo, flags);
    }


    public static class UserInfo implements Parcelable {
        @SerializedName("id")
        public String id = "";
        @SerializedName("userid")
        public String userId = "";
        @SerializedName("name")
        public String name = "";
        @SerializedName("password")
        public String password = "";
        @SerializedName("avatar")
        public String avatar = "";
        @SerializedName("nickname")
        public String nickname = "";
        @SerializedName("gender")
        public String gender = "";
        @SerializedName("notify")
        public String isNotify = "";
        @SerializedName("ip_num")
        public String ipNumber;

        public UserInfo() {
        }

        protected UserInfo(Parcel in) {
            id = in.readString();
            userId = in.readString();
            name = in.readString();
            password = in.readString();
            avatar = in.readString();
            nickname = in.readString();
            gender = in.readString();
            isNotify = in.readString();
            ipNumber = in.readString();
        }

        public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
            @Override
            public UserInfo createFromParcel(Parcel in) {
                return new UserInfo(in);
            }

            @Override
            public UserInfo[] newArray(int size) {
                return new UserInfo[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeString(userId);
            dest.writeString(name);
            dest.writeString(password);
            dest.writeString(avatar);
            dest.writeString(nickname);
            dest.writeString(gender);
            dest.writeString(isNotify);
            dest.writeString(ipNumber);
        }
    }
}
