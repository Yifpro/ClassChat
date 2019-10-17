package com.example.wyf.classchat.feature.contact.manage;

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

public interface ManageGroupsContract {

    interface View extends BaseView<Presenter> {
        void refreshManageGroupList(List<Group> list);
    }

    interface Presenter extends BasePresent {
        void getGroupIconFromServer(RvViewHolder holder, Group group);
        void getManageGroupList();
    }
}
