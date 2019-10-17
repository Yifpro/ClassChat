package com.example.wyf.classchat.feature.contact.manage;

import android.app.Activity;

import com.example.wyf.classchat.adapter.common.RvViewHolder;
import com.example.wyf.classchat.bean.GroupBmob;
import com.example.wyf.classchat.db.Group;
import com.example.wyf.classchat.util.HyphenateUtils;
import com.example.wyf.classchat.util.RxSchedulersHelper;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * Created by Administrator on 2018/1/21/021.
 */

public class ManageGroupsPresenter implements ManageGroupsContract.Presenter {

    private final ManageGroupsContract.View view;
    private final Activity activity;

    public ManageGroupsPresenter(Activity activity, ManageGroupsContract.View view) {
        this.activity = activity;
        this.view = view;
    }

    @Override
    public  void getGroupIconFromServer(RvViewHolder holder, Group group) {
        HyphenateUtils.loadGroupIcon(activity, holder, group);
    }

    /**
     * 获取管理员列表
     */
    @Override
    public void getManageGroupList() {
        Observable.defer(() -> Observable.just(loadManageGroupList()))
                .compose(RxSchedulersHelper.ioToMain())
                .subscribe(view::refreshManageGroupList);
    }

    private List<Group> loadManageGroupList() {
        List<Group> list = new ArrayList<>();
        try {
            List<EMGroup> grouplist = EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
            String currentUser = EMClient.getInstance().getCurrentUser();
            for (EMGroup emGroup : grouplist) {
                EMGroup group = EMClient.getInstance().groupManager().getGroupFromServer(emGroup.getGroupId());
                List<String> adminList = group.getAdminList();
                if (adminList.contains(currentUser)) {
                    list.add(new Group(group.getGroupId(), group.getGroupName(), group.getDescription()));
                }
            }
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
        return list;
    }
}
