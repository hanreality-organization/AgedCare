package com.punuo.sys.app.agedcare.friendCircleMain.domain;

import java.util.List;

/**
 * Created by 林逸磊 on 2018/1/9.
 */

public class UserFromGroup {
    List<UserList> userList;

    public void setUserList(List<UserList> userList) {
        this.userList = userList;
    }

    public List<UserList> getUserList() {
        return userList;
    }

}
