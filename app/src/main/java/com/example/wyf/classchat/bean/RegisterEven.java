package com.example.wyf.classchat.bean;

/**
 * Created by Administrator on 2017/10/16.
 */

public class RegisterEven {
    private String content;
    private int count;

    public RegisterEven() {
    }

    public RegisterEven(String content, int count) {
        this.content = content;
        this.count = count;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
