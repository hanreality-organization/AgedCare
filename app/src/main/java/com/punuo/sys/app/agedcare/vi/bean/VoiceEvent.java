package com.punuo.sys.app.agedcare.vi.bean;

public class VoiceEvent {
    private String content;
    private int code;//1001 唤醒成功 1002播放音樂 1003 显示说的字 1004隐藏

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}