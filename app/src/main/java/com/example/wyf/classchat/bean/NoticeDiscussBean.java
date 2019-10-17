package com.example.wyf.classchat.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2017/11/3.
 */

public class NoticeDiscussBean extends  BmobObject {
    private String userId;
    private String groupId;
    private String NoticeId;
    private String contetn;

    public NoticeDiscussBean() {
    }

    public NoticeDiscussBean(String userId, String groupId, String noticeId, String contetn) {
        this.userId = userId;
        this.groupId = groupId;
        NoticeId = noticeId;
        this.contetn = contetn;
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

    public String getNoticeId() {
        return NoticeId;
    }

    public void setNoticeId(String noticeId) {
        NoticeId = noticeId;
    }

    public String getContetn() {
        return contetn;
    }

    public void setContetn(String contetn) {
        this.contetn = contetn;
    }
}
