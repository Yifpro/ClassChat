package com.example.wyf.classchat.feature.group.clazz;

import android.app.Activity;

import com.example.wyf.classchat.constants.Constants;
import com.example.wyf.classchat.util.HyphenateUtils;

import java.util.ArrayList;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * @author Administrator on 2018/1/21/021.
 */
public class ClazzPresenter implements ClazzContract.Presenter {

    private final Activity activity;
    private final ClazzContract.View view;

    public ClazzPresenter(Activity activity, ClazzContract.View view) {
        this.activity = activity;
        this.view = view;
    }

}
