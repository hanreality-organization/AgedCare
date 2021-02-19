package com.punuo.sys.app.agedcare.friendCircleMain.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 林逸磊 on 2018/1/31.
 */

public class Alldevid extends MyBaseBean{
    List<String> devid=new ArrayList<String>();

    public List<String> getDevid() {
        return devid;
    }

    public void setDevid(List<String> devid) {
        this.devid = devid;
    }
}
