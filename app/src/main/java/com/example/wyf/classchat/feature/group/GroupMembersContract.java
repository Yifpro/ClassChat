package com.example.wyf.classchat.feature.group;

import android.graphics.Bitmap;

import com.example.wyf.classchat.base.BasePresent;
import com.example.wyf.classchat.base.BaseView;

import java.util.ArrayList;

/**
 * @author Administrator on 2018/1/22/022.
 */
public interface GroupMembersContract {

    interface View extends BaseView<Presenter> {
    }

    interface Presenter extends BasePresent {
        void getMembers(String groupId, String owner, ArrayList<String> adminList);
    }
}
