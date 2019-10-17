package com.example.wyf.classchat.bean;

import android.os.Parcel;
import android.os.Parcelable;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2017/10/14.
 */

public class MemberRegisterBean  extends BmobObject implements Parcelable{
    private String userId;
    private String groupId;
    private String week;
    private String times;
    private String clazz;
    private String status;
    private String count;
    private String members;

    public MemberRegisterBean() {
    }

    public MemberRegisterBean(String groupId, String userId, String week, String times, String clazz,
                              String status, String count, String members) {
        this.groupId = groupId;
        this.userId = userId;
        this.week = week;
        this.times = times;
        this.clazz = clazz;
        this.status = status;
        this.count = count;
        this.members = members;
    }

    public String getMembers() {
        return members;
    }

    public void setMembers(String members) {
        this.members = members;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(groupId);
        parcel.writeString(userId);
        parcel.writeString(week);
        parcel.writeString(times);
        parcel.writeString(clazz);
        parcel.writeString(status);
        parcel.writeString(count);
        parcel.writeString(members);
    }

    public static final Parcelable.Creator<MemberRegisterBean> CREATOR = new Parcelable.Creator<MemberRegisterBean>() {
        public MemberRegisterBean createFromParcel(Parcel in) {
            return new MemberRegisterBean(in.readString(), in.readString(), in.readString(), in.readString()
                    , in.readString(), in.readString(), in.readString(), in.readString());
        }

        public MemberRegisterBean[] newArray(int size) {
            return new MemberRegisterBean[size];
        }
    };
}
