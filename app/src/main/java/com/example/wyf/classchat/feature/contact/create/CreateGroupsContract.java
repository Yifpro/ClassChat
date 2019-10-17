package com.example.wyf.classchat.feature.contact.create;

import android.widget.EditText;

import com.example.wyf.classchat.base.BasePresent;
import com.example.wyf.classchat.base.BaseView;

/**
 * Created by Administrator on 2018/1/22/022.
 */

public interface CreateGroupsContract {

    interface View extends BaseView<Presenter> {
        void onCreateSuccess();
    }

    interface Presenter extends BasePresent {
        void createGroup(EditText name, EditText desc);
    }
}
