package com.punuo.sys.app.agedcare.friendCircle.domain;

import com.google.gson.annotations.SerializedName;

public class FirstMicroListFriendComment{
	@SerializedName("id")
	public String id;
	@SerializedName("replyName")
	public String replyName;//姓名
	@SerializedName("comment")
	public String comment;
}
