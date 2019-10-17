package com.example.wyf.classchat.bean;

import java.util.ArrayList;

import cn.bmob.v3.BmobObject;

/**
 * Created by gsh on 2017/10/12.
 */

public class VoteBean extends BmobObject {
    String userName;
    String voteContent;
    String userId;
    String voteOption1;
    String voteOption2;
    String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    boolean isSingle;
    int OPTION;
    String creatTime;
    String groupId;
    String state;
    ArrayList<String> options=new ArrayList<String>();
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getgroupId() {
        return groupId;
    }

    public void setgroupId(String classId) {
        this.groupId = classId;
    }



    public String getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(String creatTime) {
        this.creatTime = creatTime;
    }

    public int getOPTION() {
        return OPTION;
    }

    public void setOPTION(int OPTION) {
        this.OPTION = OPTION;
    }



    public ArrayList<String> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<String> options) {
        this.options = options;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getVoteContent() {
        return voteContent;
    }

    public void setVoteContent(String voteContent) {
        this.voteContent = voteContent;
    }

    public String getVoteOption1() {
        return voteOption1;
    }

    public void setVoteOption1(String voteOption1) {
        this.voteOption1 = voteOption1;
    }

    public String getVoteOption2() {
        return voteOption2;
    }

    public void setVoteOption2(String voteOption2) {
        this.voteOption2 = voteOption2;
    }



    public boolean isSingle() {
        return isSingle;
    }

    public void setSingle(boolean single) {
        isSingle = single;
    }


}
