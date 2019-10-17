package com.example.wyf.classchat.db;

import com.example.wyf.classchat.db.util.ClassChatDataBase;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by Administrator on 2018/2/28/028.
 */

@Table(database = ClassChatDataBase.class)
public class Conversation extends BaseModel {

    @PrimaryKey
    String id;

    public Conversation() {

    }

    public Conversation(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
