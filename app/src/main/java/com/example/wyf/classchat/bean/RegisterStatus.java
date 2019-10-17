package com.example.wyf.classchat.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by WYF on 2017/11/6.
 */

public class RegisterStatus extends BmobObject {
    private String groupId;
    private String content;
    private String week;
    private String times;
    private String clazz;
    private String members;
    private boolean isRegister;

    public RegisterStatus() {

    }

    public RegisterStatus(String groupId, boolean isRegister) {
        this.groupId = groupId;
        this.isRegister = isRegister;
    }

    public RegisterStatus(String groupId, String content, String week, String times, String clazz, String members,
                          boolean isRegister) {
        this.groupId = groupId;
        this.content = content;
        this.week = week;
        this.times = times;
        this.clazz = clazz;
        this.members = members;
        this.isRegister = isRegister;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public void setMembers(String members) {
        this.members = members;
    }

    public String getMembers() {
        return members;
    }

    public boolean isRegister() {
        return isRegister;
    }

    public void setRegister(boolean register) {
        isRegister = register;
    }
}
