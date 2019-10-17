package com.example.wyf.classchat.bean;

/**
 * Created by WYF on 2017/10/2.
 */

public class MessageBody {
    //0是自己，1是别人
    private int type;
    private String message;
    private String time;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MessageBody(int type, String message, String time, String id){
        this.type = type;
        this.message = message;
        this.time = time;
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
