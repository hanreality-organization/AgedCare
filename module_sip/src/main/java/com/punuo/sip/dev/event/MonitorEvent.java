package com.punuo.sip.dev.event;

/**
 * Created by han.chen.
 * Date on 2021/2/3.
 **/
public class MonitorEvent {
    public int monitorType;
    public String targetDevId;

    public MonitorEvent(int monitorType, String targetDevId) {
        this.monitorType = monitorType;
        this.targetDevId = targetDevId;
    }
}
