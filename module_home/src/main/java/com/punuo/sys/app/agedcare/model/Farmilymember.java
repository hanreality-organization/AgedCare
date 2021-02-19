package com.punuo.sys.app.agedcare.model;

/**
 * Created by 23578 on 2018/7/26.
 */

public class Farmilymember {
    private String id;
    private String pic;
    private String linkman;
    private String telnum;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getLinkman() {
        return linkman;
    }

    public void setLinkman(String linkman) {
        this.linkman = linkman;
    }

    public String getTelnum() {
        return telnum;
    }

    public void setTelnum(String telnum) {
        this.telnum = telnum;
    }

    @Override
    public String toString() {
        return id+pic+linkman+telnum;
    }
}
