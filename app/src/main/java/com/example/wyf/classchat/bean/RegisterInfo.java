package com.example.wyf.classchat.bean;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

/**
 * Created by WYF on 2017/11/6.
 */

public class RegisterInfo extends BmobObject implements Serializable {
    private String userId;
    private String groupId;
    private String week;
    private String times;
    private String clazz;
    private String status;

    public RegisterInfo() {

    }

    public RegisterInfo(String userId) {
        this.userId = userId;
    }

    public RegisterInfo(String week, String times, String clazz) {
        this.week = week;
        this.times = times;
        this.clazz = clazz;
    }

    public RegisterInfo(String userId, String groupId, String week, String times, String clazz, String status) {
        this.userId = userId;
        this.groupId = groupId;
        this.week = week;
        this.times = times;
        this.clazz = clazz;
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof RegisterInfo && this.getUserId().equals(((RegisterInfo) obj).getUserId());
    }
}
