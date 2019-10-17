package com.example.wyf.classchat.feature.contact.create;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wyf.classchat.util.RxSchedulersHelper;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.chat.EMGroupOptions;
import com.hyphenate.exceptions.HyphenateException;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/1/21/021.
 */

public class CreateGroupsPresenter implements CreateGroupsContract.Presenter {

    private static final String TAG = "ContactPagePresenter";
    private final Activity activity;
    private final CreateGroupsContract.View view;

    public CreateGroupsPresenter(Activity activity, CreateGroupsContract.View view) {
        this.activity = activity;
        this.view = view;
    }

    @Override
    public void createGroup(EditText groupName, EditText groupDesc) {
        String name = groupName.getText().toString().trim();
        String desc = groupDesc.getText().toString().trim();
        String error = null;
        if (TextUtils.isEmpty(name)) {
            error = "请输入群名";
        } else if (TextUtils.isEmpty(desc)) {
            error = "请输入群描述";
        } else if (name.length() > 10) {
            error = "群名过长";
        } else if (desc.length() > 40) {
            error = "群描述过长";
        }
        if (error != null) {
            Toast.makeText(activity, "error", Toast.LENGTH_SHORT).show();
        } else {
            Observable.defer(() -> Observable.just(requestCreate(name, desc)))
                    .compose(RxSchedulersHelper.ioToMain())
                    .subscribe(s -> view.onCreateSuccess());
        }
    }

    private String requestCreate(String name, String desc) {
        try {
            EMGroupOptions option = new EMGroupOptions();
            option.maxUsers = 200;
            option.style = EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin;
            EMClient.getInstance().groupManager().createGroup(name, desc, new String[]{}, "", option);
        } catch (HyphenateException e) {
            Log.e(TAG, "requestCreate: " + e.getErrorCode() + ", " + e.getMessage());
        }
        return null;
    }
}
