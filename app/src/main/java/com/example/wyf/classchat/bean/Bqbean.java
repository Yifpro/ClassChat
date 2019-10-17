package com.example.wyf.classchat.bean;

/**
 * Created by Administrator on 2017/9/22.
 */

public class Bqbean {
    private String imaUrl;
    private String className;
    private String content;

    public Bqbean() {
    }

    public String getImaUrl() {
        return imaUrl;
    }

    public void setImaUrl(String imaUrl) {
        this.imaUrl = imaUrl;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Bqbean{" +
                "imaUrl='" + imaUrl + '\'' +
                ", className='" + className + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
