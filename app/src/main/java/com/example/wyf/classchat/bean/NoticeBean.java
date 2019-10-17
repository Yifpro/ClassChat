package com.example.wyf.classchat.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2017/10/8.
 */

public class NoticeBean extends BmobObject {
    private String title;
    private String content;
    private String time;
    private long MillsTime;
    private String user;
    private String icon;
    private int count;
    private String groupId;
    private String rederStr;

    public NoticeBean(String title, String content, String time, long millsTime, String user, String icon, int count, String groupId, String rederStr) {
        this.title = title;
        this.content = content;
        this.time = time;
        MillsTime = millsTime;
        this.user = user;
        this.icon = icon;
        this.count = count;
        this.groupId = groupId;
        this.rederStr = rederStr;
    }

    public NoticeBean() {
    }

    public String getRederStr() {
        return rederStr;
    }

    public void setRederStr(String rederStr) {
        this.rederStr = rederStr;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public long getMillsTime() {
        return MillsTime;
    }

    public void setMillsTime(long millsTime) {
        MillsTime = millsTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "NoticeBean{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", time='" + time + '\'' +
                ", user=" + user +
                ", icon='" + icon + '\'' +
                ", count=" + count +
                '}';
    }
}
