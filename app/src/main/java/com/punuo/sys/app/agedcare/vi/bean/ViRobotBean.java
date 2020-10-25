package com.punuo.sys.app.agedcare.vi.bean;

public class ViRobotBean {
    private String text;
    private String type;

    public ViRobotBean( String type,String text) {
        this.text = text;
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}