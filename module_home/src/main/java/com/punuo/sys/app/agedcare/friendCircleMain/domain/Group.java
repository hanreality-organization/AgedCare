package com.punuo.sys.app.agedcare.friendCircleMain.domain;

import java.util.List;

/**
 * Created by 林逸磊 on 2017/12/1.
 */

public class Group extends MyBaseBean{
    private List<GroupList> groupList;

    public List<GroupList> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<GroupList> groupList) {
        this.groupList = groupList;
    }
}
