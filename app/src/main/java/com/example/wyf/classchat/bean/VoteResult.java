package com.example.wyf.classchat.bean;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by gsh on 2017/10/14.
 */

public class VoteResult extends BmobObject {
    private int Option1=0;
    private int Option2=0;

    ArrayList<Integer> list;
    ArrayList<String> doneUser;
    String vote_content;
    String groupId;

    public ArrayList<String> getDoneUser() {
        return doneUser;
    }

    public void setDoneUser(ArrayList<String> doneUser) {
        this.doneUser = doneUser;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }


    public String getgroupId() {
        return groupId;
    }

    public void setgroupId(String classId) {
        this.groupId = classId;
    }

    public ArrayList<Integer> getList() {
        return list;
    }

    public void setList(ArrayList<Integer> list) {
        this.list = list;
    }



    public String getVote_content() {
        return vote_content;
    }

    public void setVote_content(String vote_content) {
        this.vote_content = vote_content;
    }

    public int getOption1() {
        return Option1;
    }

    public void setOption1(int option1) {
        Option1 = option1;
    }

    public int getOption2() {
        return Option2;
    }

    public void setOption2(int option2) {
        Option2 = option2;
    }


}
