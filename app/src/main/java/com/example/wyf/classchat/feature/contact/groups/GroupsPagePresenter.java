package com.example.wyf.classchat.feature.contact.groups;

import android.app.Activity;

import com.example.wyf.classchat.adapter.common.RvAdapter;
import com.example.wyf.classchat.adapter.common.RvViewHolder;
import com.example.wyf.classchat.db.Group;
import com.example.wyf.classchat.util.HyphenateUtils;
import com.example.wyf.classchat.util.RxSchedulersHelper;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/1/21/021.
 */

public class GroupsPagePresenter implements GroupsPageContract.Presenter {

    private final Activity activity;
    private final GroupsPageContract.View view;

    public GroupsPagePresenter(Activity activity, GroupsPageContract.View view) {
        this.activity = activity;
        this.view = view;
    }

    @Override
    public void getNetTime(String user, String groupId) {
        Observable.defer(() -> Observable.just(HyphenateUtils.loadNetTime(user, groupId)))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(event -> HyphenateUtils.getDateFromServer(activity, event));
    }

    @Override
    public void getGroupIconFromServer(RvAdapter adapter, RvViewHolder holder, Group group) {
        HyphenateUtils.loadGroupIcon(activity, holder, group);
    }

    @Override
    public void getGroupsList() {
        Observable.defer(() -> Observable.just(loadGroupsList()))
                .compose(RxSchedulersHelper.ioToMain())
                .subscribe(view::refreshGroupList);
    }

    private List<Group> loadGroupsList() {
        List<Group> list = new ArrayList<>();
        try {
            for (EMGroup group : EMClient.getInstance().groupManager().getJoinedGroupsFromServer()) {
                list.add(new Group(group.getGroupId(), group.getGroupName(), group.getDescription()));
            }
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
        return list;
    }
}
