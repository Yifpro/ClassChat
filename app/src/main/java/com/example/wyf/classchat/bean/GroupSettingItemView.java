package com.example.wyf.classchat.bean;

/**
 * Created by Administrator on 2017/9/26.
 */

public class GroupSettingItemView {
    private int img;
    private String title;

    public GroupSettingItemView() {

    }

    public GroupSettingItemView(int img, String title) {
        this.img = img;
        this.title = title;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
