package com.example.wyf.classchat.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2017/10/12.
 */

public class GroupMember extends BmobObject  {
    private String groupId;
    private String userId;
    private int status;

    public GroupMember() {
    }

    public GroupMember(String groupId, String userId, int status) {
        this.groupId = groupId;
        this.userId = userId;
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
