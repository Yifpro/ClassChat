package com.example.wyf.classchat.feature.contact;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.wyf.classchat.bean.PersonBmob;
import com.example.wyf.classchat.constants.Constants;
import com.example.wyf.classchat.util.HyphenateUtils;
import com.example.wyf.classchat.util.RxSchedulersHelper;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

import rx.Observable;

/**
 * @author Administrator on 2018/1/21/021.
 */
public class ContactPagePresenter implements ContactPageContract.Presenter {

    private final Activity activity;
    private final ContactPageContract.View view;

    public ContactPagePresenter(Activity activity, ContactPageContract.View view) {
        this.activity = activity;
        this.view = view;
    }

    @Override
    public void addGroup(String id) {
        if (TextUtils.isEmpty(id)) {
            Toast.makeText(activity, "请输入群号", Toast.LENGTH_SHORT).show();
            return;
        }
        HyphenateUtils.addGroup(activity, id);
    }

    @Override
    public void getFriendInfo(List<String> contactIdList) {
        HyphenateUtils.requestContactInfo(contactIdList, Constants.GET_CONTACT_LIST_INFO);
    }

    @Override
    public void getFriendList() {
        Observable.defer(() -> Observable.just(loadFriendList()))
                .compose(RxSchedulersHelper.ioToMain())
                .subscribe(view::loadFriendListSuccess);
    }

    private List<String> loadFriendList() {
        try {
            return EMClient.getInstance().contactManager().getAllContactsFromServer();
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void addFriend(String id, List<PersonBmob> contactList) {
        String error = null;
        if (TextUtils.isEmpty(id)) {
            error = "请输入学号";
        } else if (EMClient.getInstance().getCurrentUser().equals(id)) {
            error = "无法添加自己为好友";
        } else if (contactList.contains(id)) {
            error = "已添加此好友";
        }
        if (error != null) {
            Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
        } else {
            HyphenateUtils.addFriend(activity, id);
        }
    }
}
