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
public class Group extends BaseModel {

    @PrimaryKey
    private String id;
    @Column
    private String name;
    @Column
    private String desc;

    public Group() {

    }

    public Group(String id) {
        this.id = id;
    }

    public Group(String id, String name, String desc) {
        this.id = id;
        this.name = name;
        this.desc = desc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
