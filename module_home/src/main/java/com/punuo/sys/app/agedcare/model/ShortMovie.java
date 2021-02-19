package com.punuo.sys.app.agedcare.model;

/**
 * Created by 23578 on 2018/11/25.
 */

public class ShortMovie {
    private String url;
    private String title;
    private String info;
    private String cover;
    private String id;
    public ShortMovie(String cover, String info, String title, String url) {
        this.cover = cover;
        this.info = info;
        this.title = title;
       this. url = url;
    }

    public String getInfo() {
        return info;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ShortMovie(String cover, String id, String info, String title, String url) {
        this.cover = cover;
        this.id = id;
        this.info = info;
        this.title = title;
        this.url = url;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        url = url;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }
}
