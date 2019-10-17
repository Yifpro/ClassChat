package com.example.wyf.classchat.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.wyf.classchat.ClassChatApplication;
import com.example.wyf.classchat.constants.Constants;

import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2017/10/4.
 */

public class PreferencesUtils {

    private static final String FILENAME = ClassChatApplication.class.getPackage().getName();

    private PreferencesUtils() {}

    public static SharedPreferences getPreferences() {
        return ClassChatApplication.getInstance().getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
    }

    public static void savePreference(String key, Object value) {
        SharedPreferences.Editor edit = getPreferences().edit();
        if (value instanceof String) {
            edit.putString(key, (String)value);
        } else if (value instanceof Boolean) {
            edit.putBoolean(key, (Boolean)value);
        } else if (value instanceof Integer) {
            edit.putInt(key, (Integer)value);
        } else if (value instanceof Float) {
            edit.putFloat(key, (Float)value);
        } else if (value instanceof Long) {
            edit.putLong(key, (Long) value);
        } else if (value instanceof Set) {
            edit.putStringSet(key, (Set<String>) value);
        }
        edit.apply();
    }

    public static <T> void savePreference(Map<String, T> map) {
        SharedPreferences.Editor edit = getPreferences().edit();;
        for (Map.Entry<String, T> entry : map.entrySet()) {
            String key = entry.getKey();
            T value = entry.getValue();
            if (value instanceof String) {
                edit.putString(key, (String)value);
            } else if (value instanceof Boolean) {
                edit.putBoolean(key, (Boolean)value);
            } else if (value instanceof Integer) {
                edit.putInt(key, (Integer)value);
            } else if (value instanceof Float) {
                edit.putFloat(key, (Float)value);
            } else if (value instanceof Long) {
                edit.putLong(key, (Long) value);
            } else if (value instanceof Set) {
                edit.putStringSet(key, (Set<String>) value);
            }
        }
        edit.apply();
    }
}
