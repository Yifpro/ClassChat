package com.example.wyf.classchat.util;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/1/29/029.
 */

public class RxSchedulersHelper {

    private static rx.Observable.Transformer ioMain = o -> ((rx.Observable) o).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).unsubscribeOn(Schedulers.io());

    public static <T> rx.Observable.Transformer<T, T> ioToMain() {
        return (rx.Observable.Transformer<T, T>) ioMain;
    }
}
