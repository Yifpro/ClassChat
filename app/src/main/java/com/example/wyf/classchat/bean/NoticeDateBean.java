package com.example.wyf.classchat.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2017/10/9.
 */

public class NoticeDateBean  extends BmobObject{
    private String user;
    private String time;
    private long MillisTime;
    private String groupId;

    public NoticeDateBean() {
    }

    public NoticeDateBean(String user, String time, long millisTime, String groupId) {
        this.user = user;
        this.time = time;
        MillisTime = millisTime;
        this.groupId = groupId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public long getMillisTime() {
        return MillisTime;
    }

    public void setMillisTime(long millisTime) {
        MillisTime = millisTime;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
