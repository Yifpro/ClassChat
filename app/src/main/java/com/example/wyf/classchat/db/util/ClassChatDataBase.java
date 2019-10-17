package com.example.wyf.classchat.db.util;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by Administrator on 2018/2/9/009.
 */

@Database(name = ClassChatDataBase.NAME, version = ClassChatDataBase.VERSION)
public class ClassChatDataBase {
    //数据库名称
    public static final String NAME = "classchat";
    //数据库版本
    public static final int VERSION = 3;
}
