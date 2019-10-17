package com.example.wyf.classchat.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by WYF on 2017/9/26.
 */

public class PersonBmob extends BmobObject {
    private String id;
    private String icon;
    private String name;
    private String letter;
    private String sex;
    private String sign;
    private String constellation;
    private String address;
    private boolean showLetter;

    public PersonBmob() {
    }

    public PersonBmob(String name) {
        this.name = name;
    }

    public PersonBmob(String id, String name){
        this.id = id;
        this.name = name;
    }

    public PersonBmob(String id, String icon, String name){
        this.id = id;
        this.icon = icon;
        this.name = name;
    }

    public PersonBmob(String id, String icon, String name, String letter){
        this.id = id;
        this.icon = icon;
        this.name = name;
        this.letter = letter;
    }

    public PersonBmob(String name, String sex, String sign, String constellation, String address){
        this.name = name;
        this.sex = sex;
        this.sign = sign;
        this.constellation = constellation;
        this.address = address;
    }

    public PersonBmob(String id, String name, String sex, String sign, String constellation, String address){
        this.id = id;
        this.name = name;
        this.sex = sex;
        this.sign = sign;
        this.constellation = constellation;
        this.address = address;
    }

    public boolean isShowLetter() {
        return showLetter;
    }

    public void setShowLetter(boolean showLetter) {
        this.showLetter = showLetter;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getConstellation() {
        return constellation;
    }

    public void setConstellation(String constellation) {
        this.constellation = constellation;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PersonBmob && this.getId().equals(((PersonBmob) obj).getId());
    }
}
