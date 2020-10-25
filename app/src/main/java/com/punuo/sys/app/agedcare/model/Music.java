package com.punuo.sys.app.agedcare.model;

/**
 * Created by 23578 on 2018/9/18.
 */

public class Music {
    //name 类型
    private String name;
    private String id;
    //path 类型
    private String path;
    //获取艺术家
    private String author;
    private String type;
    private String time;
    public Music() {
    }

    public Music(String id) {
        this.id = id;
    }

    public Music(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    @Override
    public String toString() {
        return "id:"+id+" name:"+name +" author:"+author+"type:"+type;
    }


}
