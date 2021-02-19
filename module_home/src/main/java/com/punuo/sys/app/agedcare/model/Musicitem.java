package com.punuo.sys.app.agedcare.model;

import java.util.List;

/**
 * Created by 23578 on 2018/9/20.
 */

public class Musicitem {

    private String type;
    private String typeName;
    public String getType() {
        return type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "type:"+type+typeName;
    }
}
