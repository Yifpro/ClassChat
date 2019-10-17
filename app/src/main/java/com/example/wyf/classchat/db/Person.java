package com.example.wyf.classchat.db;

import com.example.wyf.classchat.db.util.ClassChatDataBase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by WYF on 2017/10/3.
 */

@Table(database = ClassChatDataBase.class)
public class Person extends BaseModel {

    @PrimaryKey
    private String id;
    @Column
    private String name;
    @Column
    private String sex;
    @Column
    private String sign;
    @Column
    private String constellation;
    @Column
    private String address;

    public Person() {

    }

    public Person(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Person(String id, String name, String sex, String sign, String constellation, String address) {
        this.id = id;
        this.name = name;
        this.sex = sex;
        this.sign = sign;
        this.constellation = constellation;
        this.address = address;
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
}
