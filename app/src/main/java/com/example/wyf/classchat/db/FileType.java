package com.example.wyf.classchat.db;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Administrator on 2018/2/10/010.
 */

public class FileType {
    @IntDef({CONTACT, GROUP, OTHER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Kind {
    }

    public static final int CONTACT = 0;//联系人
    public static final int GROUP = 1;//群组
    public static final int OTHER = 2;//其他
}
