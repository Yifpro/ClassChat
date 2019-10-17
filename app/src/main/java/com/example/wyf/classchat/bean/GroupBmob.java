package com.example.wyf.classchat.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by WYF on 2017/10/4.
 */

public class GroupBmob extends BmobObject{
    private String id;
    private String icon;
    private String name;
    private String desc;
    private String clazz;

    public GroupBmob() {

    }

    public GroupBmob(String id) {
        this.id = id;
    }

    public GroupBmob(String id, String name, String desc){
        this.id = id;
        this.name = name;
        this.desc = desc;
    }

    public GroupBmob(String id, String icon, String name, String desc){
        this.id = id;
        this.icon = icon;
        this.name = name;
        this.desc = desc;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof GroupBmob && this.getId().equals(((GroupBmob) obj).getId());
    }
}
