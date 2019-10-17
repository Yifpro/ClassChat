package com.example.wyf.classchat.listener;

import android.util.Log;

import cn.bmob.v3.exception.BmobException;

/**
 * Created by WYF on 2017/10/25.
 */

public abstract class BmobCallbackImpl implements BmobCallback {
    private static final String TAG = BmobCallbackImpl.class.getSimpleName();

    @Override
    public void fail() {

    }

    @Override
    public void Error(BmobException e) {
        Log.e(TAG, "Error: " + e.getErrorCode() + ", " + e.getMessage());
    }
}
