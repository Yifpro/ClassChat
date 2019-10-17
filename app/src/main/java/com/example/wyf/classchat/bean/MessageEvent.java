package com.example.wyf.classchat.bean;

/**
 * Created by WYF on 2017/9/25.
 */

public class MessageEvent {
    private int what;
    private Object message;

    public MessageEvent() {}

    public MessageEvent(Object message) {
        this.message = message;
    }

    public MessageEvent(int what, Object message) {
        this.what = what;
        this.message = message;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public int getWhat() {
        return what;
    }

    public void setWhat(int what) {
        this.what = what;
    }
}
