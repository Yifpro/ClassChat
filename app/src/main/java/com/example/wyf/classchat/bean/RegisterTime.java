package com.example.wyf.classchat.bean;

/**
 * Created by WYF on 2017/11/6.
 */

public class RegisterTime {
    private String week;
    private String times;
    private String clazz;

    public RegisterTime() {

    }

    public RegisterTime(String week, String times, String clazz) {
        this.week = week;
        this.times = times;
        this.clazz = clazz;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }
}
