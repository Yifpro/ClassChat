package com.example.wyf.classchat.feature.contact;

import com.example.wyf.classchat.base.BasePresent;
import com.example.wyf.classchat.base.BaseView;
import com.example.wyf.classchat.bean.PersonBmob;

import java.util.List;

/**
 * Created by Administrator on 2018/1/22/022.
 */

public interface ContactPageContract {

    interface View extends BaseView<Presenter> {
        void loadFriendListSuccess(List<String> list);
    }

    interface Presenter extends BasePresent {
        void getFriendInfo(List<String> contactIdList);
        void addGroup(String id);
        void getFriendList();
        void addFriend(String id, List<PersonBmob> contactList);
    }
}
