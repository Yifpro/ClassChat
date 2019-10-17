package com.example.wyf.classchat.file.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Administrator on 2017/11/4.
 */

public class FileInfo extends BmobObject{
    private String name;
    private String userId;
    private String groupId;
    private String noticeId;
    private BmobFile file;
    private String size;
    private String type;
    private String path;

    public FileInfo() {
    }

    public FileInfo(String name, String userId, String groupId, String noticeId, BmobFile file, String size, String type, String path) {
        this.name = name;
        this.userId = userId;
        this.groupId = groupId;
        this.noticeId = noticeId;
        this.file = file;
        this.size = size;
        this.type = type;
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(String noticeId) {
        this.noticeId = noticeId;
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

    public BmobFile getFile() {
        return file;
    }

    public void setFile(BmobFile file) {
        this.file = file;
    }
}
