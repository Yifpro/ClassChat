package com.example.wyf.classchat.listener;

import cn.bmob.v3.exception.BmobException;

/**
 * Created by Administrator on 2017/10/19.
 */

public interface BmobCallback {
    void success(Object object);

    void fail();

    void Error(BmobException e);

}
