package com.example.wyf.classchat.feature.group;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.wyf.classchat.bean.GroupBmob;
import com.example.wyf.classchat.bean.MessageEvent;
import com.example.wyf.classchat.constants.Constants;
import com.example.wyf.classchat.db.FileType;
import com.example.wyf.classchat.listener.BmobCallbackImpl;
import com.example.wyf.classchat.util.BitmapUtils;
import com.example.wyf.classchat.util.BmobUtils;
import com.example.wyf.classchat.util.HyphenateUtils;
import com.example.wyf.classchat.util.RxSchedulersHelper;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @author Administrator on 2018/1/21/021.
 */
public class GroupMembersPresenter implements GroupMembersContract.Presenter {

    private static final String TAG = "GroupSettingsPresenter";
    private final Activity activity;
    private final GroupMembersContract.View view;

    public GroupMembersPresenter(Activity activity, GroupMembersContract.View view) {
        this.activity = activity;
        this.view = view;
    }

    @Override
    public void getMembers(String groupId, String owner, ArrayList<String> adminList) {
        Observable.defer(() -> Observable.just(HyphenateUtils.getMembers(groupId, owner, adminList)))
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(memberList -> HyphenateUtils.requestContactInfo(memberList, Constants.GET_MEMBERS_INFO));
    }

}
