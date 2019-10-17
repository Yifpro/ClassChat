package com.example.wyf.classchat.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2017/10/17.
 */

public class ReaderBean  extends BmobObject{
    private String userId;
    private String noticeObjectId;

    public ReaderBean() {
    }

    public ReaderBean(String userId, String noticeObjectId) {
        this.userId = userId;
        this.noticeObjectId = noticeObjectId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNoticeObjectId() {
        return noticeObjectId;
    }

    public void setNoticeObjectId(String noticeObjectId) {
        this.noticeObjectId = noticeObjectId;
    }
}
