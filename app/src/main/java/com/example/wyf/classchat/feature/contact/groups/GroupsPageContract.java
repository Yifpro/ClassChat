package com.example.wyf.classchat.feature.contact.groups;

import com.example.wyf.classchat.adapter.common.RvAdapter;
import com.example.wyf.classchat.adapter.common.RvViewHolder;
import com.example.wyf.classchat.base.BasePresent;
import com.example.wyf.classchat.base.BaseView;
import com.example.wyf.classchat.bean.GroupBmob;
import com.example.wyf.classchat.db.Group;
import com.hyphenate.chat.EMGroup;

import java.util.List;

/**
 * Created by Administrator on 2018/1/22/022.
 */

public interface GroupsPageContract {

    interface View extends BaseView<Presenter> {
        void refreshGroupList(List<Group> list);
    }

    interface Presenter extends BasePresent {
        void getNetTime(String user, String groupId);
        void getGroupIconFromServer(RvAdapter adapter, RvViewHolder holder, Group groupBmob);
        void getGroupsList();
    }
}
