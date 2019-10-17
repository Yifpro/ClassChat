package com.example.wyf.classchat.db.util;

import com.example.wyf.classchat.bean.GroupBmob;
import com.example.wyf.classchat.bean.PersonBmob;
import com.example.wyf.classchat.db.FileType;
import com.example.wyf.classchat.db.Group;
import com.example.wyf.classchat.db.Group_Table;
import com.example.wyf.classchat.db.Person;
import com.example.wyf.classchat.db.Person_Table;
import com.raizlabs.android.dbflow.sql.language.Operator;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;

/**
 * @author WYF on 2017/10/26.
 */
public class DatabaseUtils {

    public static void insertPerson(PersonBmob p) {
        new Person(p.getId(),
                p.getName(),
                p.getSex(),
                p.getSign() != null ? p.getSign() : "这个人什么都没留下",
                p.getConstellation() != null ? p.getConstellation() : "0",
                p.getAddress() != null ? p.getAddress() : "可能来自火星").save();
    }

    public static void insertGroup(GroupBmob g) {
        new Group(g.getId(), g.getName(), g.getDesc()).save();
    }

    @SuppressWarnings("unchecked")
    public static Object queryData(@FileType.Kind int tableType, String id) {
        return new Select().from(getClazz(tableType)).where(getOperator(tableType, id)).querySingle();
    }

    @SuppressWarnings("unchecked")
    public static void delData(@FileType.Kind int tableType, String id) {
        SQLite.delete(getClazz(tableType)).where(getOperator(tableType, id)).execute();
    }

    private static Operator<String> getOperator(@FileType.Kind int tableType, String id) {
        Operator<String> eq = null;
        switch (tableType) {
            case FileType.CONTACT:
                eq = Person_Table.id.eq(id);
                break;
            case FileType.GROUP:
                eq = Group_Table.id.eq(id);
                break;
        }
        return eq;
    }

    private static Class getClazz(@FileType.Kind int tableType) {
        Class clazz = null;
        switch (tableType) {
            case FileType.CONTACT:
                clazz = Person.class;
                break;
            case FileType.GROUP:
                clazz = Group.class;
                break;
        }
        return clazz;
    }
}
