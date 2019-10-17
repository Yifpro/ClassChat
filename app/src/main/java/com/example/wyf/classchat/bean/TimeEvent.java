package com.example.wyf.classchat.bean;

/**
 * Created by Administrator on 2017/10/26.
 */

public class TimeEvent {
    private long tempTime;
    private String formatTime;
    private  String groupId;
    private String user;


    public TimeEvent() {
    }

    public TimeEvent(long tempTime, String formatTime) {
        this.tempTime = tempTime;
        this.formatTime = formatTime;
    }

    public TimeEvent(long tempTime, String formatTime, String groupId, String user) {
        this.groupId = groupId;
        this.user = user;
        this.tempTime = tempTime;
        this.formatTime = formatTime;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public long getTempTime() {
        return tempTime;
    }

    public void setTempTime(long tempTime) {
        this.tempTime = tempTime;
    }

    public String getFormatTime() {
        return formatTime;
    }

    public void setFormatTime(String formatTime) {
        this.formatTime = formatTime;
    }
}

