package com.punuo.sys.app.agedcare.friendCircle.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FriendMicroListData {
    @SerializedName("postid")
    public String postId;
    @SerializedName("id")
    public String id;
    @SerializedName("content")
    public String content;
    @SerializedName("create_time")
    public String createTime;
    @SerializedName("like_num")
    public String like_num;
    @SerializedName("nickname")
    public String nickName;
    @SerializedName("avatar")
    public String avatar;
    @SerializedName("addlike_nickname")
    public List<FirstMicroListFriendPraise> addLikeNickname;
    @SerializedName("post_pic")
    public List<FirstMicroListFriendImage> postPic;
    @SerializedName("owntype")
    public List<FirstMicroListFriendPraiseType> ownType;
    @SerializedName("praiseflag")
    public String praiseFlag;
    @SerializedName("friendcomment")
    public List<FirstMicroListFriendComment> friendComment;
}
