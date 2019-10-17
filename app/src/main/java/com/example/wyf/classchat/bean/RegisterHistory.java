package com.example.wyf.classchat.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by WYF on 2017/11/6.
 */

public class RegisterHistory extends BmobObject {
    private String userId; //发起考勤人员id
    private String groupId; //发起考勤的群
    private String content; //匹配码
    private String week; //周
    private String times; //次
    private String clazz; //课程
    private String members; // 成员
    private int count; //人数

    public RegisterHistory() {

    }

    public RegisterHistory(String groupId) {
        this.groupId = groupId;
    }

    public RegisterHistory(String week, String times, String clazz) {
        this.week = week;
        this.times = times;
        this.clazz = clazz;
    }

    public RegisterHistory(String userId, String groupId, String content, String week, String times, String clazz,
                           String members, int count) {
        this.userId = userId;
        this.groupId = groupId;
        this.content = content;
        this.week = week;
        this.times = times;
        this.clazz = clazz;
        this.members = members;
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
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

    public String getMembers() {
        return members;
    }

    public void setMembers(String members) {
        this.members = members;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof RegisterHistory
                && this.getWeek().equals(((RegisterHistory) obj).getWeek())
                && this.getTimes().equals(((RegisterHistory) obj).getTimes())
                && this.getClazz().equals(((RegisterHistory) obj).getClazz());
    }
}
