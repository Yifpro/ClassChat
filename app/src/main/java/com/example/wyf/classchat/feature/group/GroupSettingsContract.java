package com.example.wyf.classchat.feature.group;

import android.graphics.Bitmap;

import com.example.wyf.classchat.base.BasePresent;
import com.example.wyf.classchat.base.BaseView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator on 2018/1/22/022.
 */
public interface GroupSettingsContract {

    interface View extends BaseView<Presenter> {
        void getAdminListSuccess(ArrayList<String> admins);
        void setGroupCount(int count);
        void addManagerSuccess();
        void dismissSuccess();
        void exitSuccess();
    }

    interface Presenter extends BasePresent {
        void saveGroupIcon(String groupId, Bitmap bitmap);
        void getAdminList(String groupId);
        void getGroupCount(String groupId);
        void addManager(String groupId, String userId);
        void dismissGroup(String id);
        void exitGroup(String id);
    }
}
