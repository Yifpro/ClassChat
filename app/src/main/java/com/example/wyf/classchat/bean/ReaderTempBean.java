package com.example.wyf.classchat.bean;

/**
 * Created by Administrator on 2017/11/3.
 */

public class ReaderTempBean {
    private String userId;
    private String username;
    private boolean isLook;

    public ReaderTempBean() {
    }

    public ReaderTempBean(String userId, String username, boolean isLook) {
        this.userId = userId;
        this.username = username;
        this.isLook = isLook;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isLook() {
        return isLook;
    }

    public void setLook(boolean look) {
        isLook = look;
    }
}
