package com.example.wyf.classchat.feature.group;

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
import com.hyphenate.exceptions.HyphenateException;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import rx.Observable;

/**
 * @author Administrator on 2018/1/21/021.
 */
public class GroupSettingsPresenter implements GroupSettingsContract.Presenter {

    private static final String TAG = "GroupSettingsPresenter";
    private final Activity activity;
    private final GroupSettingsContract.View view;

    public GroupSettingsPresenter(Activity activity, GroupSettingsContract.View view) {
        this.activity = activity;
        this.view = view;
    }

    @Override
    public void saveGroupIcon(String groupId, Bitmap bitmap) {
        Observable.defer(() -> Observable.just(uploadGroupIcon(groupId, bitmap)))
                .compose(RxSchedulersHelper.ioToMain())
                .subscribe();
    }

    private String uploadGroupIcon(String groupId, Bitmap bitmap) {
        HashMap<String, String> map = new HashMap<>();
        map.put("id", groupId);
        BmobUtils.getInstance().querryGroup(map, new BmobCallbackImpl() {
            @Override
            public void success(Object object) {
                String objectId = ((List<GroupBmob>) object).get(0).getObjectId();
                GroupBmob groupBmob = new GroupBmob();
                groupBmob.setIcon(BitmapUtils.bitmapToString(bitmap));
                groupBmob.update(objectId, new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            Log.e("save groupBmob icon", "更新成功");
                        } else {
                            Log.e("save groupBmob icon", "更新失败：" + e.getMessage() + "," + e.getErrorCode());
                        }
                    }
                });
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        String objectId = ((List<GroupBmob>) object).get(0).getObjectId();
//                        GroupBmob groupBmob = new GroupBmob();
//                        groupBmob.setIcon(BitmapUtils.bitmapToString(bitmap));
//                        groupBmob.update(objectId, new UpdateListener() {
//                            @Override
//                            public void done(BmobException e) {
//                                if (e == null) {
//                                    Log.e("save groupBmob icon: ", "更新成功");
//                                } else {
//                                    Log.e("save groupBmob icon: ", "更新失败：" + e.getMessage() + "," + e.getErrorCode());
//                                }
//                            }
//                        });
//                    }
//                }).start();
            }
        });
        BitmapUtils.saveIconToLocal(FileType.GROUP, groupId, groupId, bitmap);
        EventBus.getDefault().post(new MessageEvent(Constants.REFRESH_GROUP_ICON, groupId));
        return null;
    }

    @Override
    public void getAdminList(String groupId) {
        Observable.defer(() -> Observable.just(HyphenateUtils.getAdminList(groupId)))
                .compose(RxSchedulersHelper.ioToMain())
                .subscribe(view::getAdminListSuccess);
    }

    @Override
    public void getGroupCount(String groupId) {
        Observable.defer(() -> Observable.just(loadGroupCount(groupId)))
                .compose(RxSchedulersHelper.ioToMain())
                .subscribe(view::setGroupCount);
    }

    private int loadGroupCount(String groupId) {
        try {
            return EMClient.getInstance().groupManager().getGroupFromServer(groupId).getMemberCount();
        } catch (HyphenateException e) {
            Log.e(TAG, "loadGroupCount: " + e.getErrorCode() + ", " + e.getMessage());
        }
        return 1;
    }

    @Override
    public void addManager(String groupId, String userId) {
        Observable.defer(() -> Observable.just(appointManager(groupId, userId)))
                .compose(RxSchedulersHelper.ioToMain())
                .subscribe(s -> view.addManagerSuccess());
    }

    private String appointManager(String groupId, String userId) {
        try {
            EMClient.getInstance().groupManager().addGroupAdmin(groupId, userId);
        } catch (HyphenateException e) {
            Log.e(TAG, "appointManager: " + e.getErrorCode() + ", " + e.getMessage());
        }
        return null;
    }

    @Override
    public void dismissGroup(String id) {
        Observable.defer(() -> Observable.just(disbandGroup(id)))
                .compose(RxSchedulersHelper.ioToMain())
                .subscribe(s -> view.dismissSuccess());
    }

    private String disbandGroup(String id) {
        try {
            EMClient.getInstance().groupManager().destroyGroup(id);
        } catch (HyphenateException e) {
            Log.e(TAG, "disbandGroup: " + e.getErrorCode() + ", " + e.getMessage());
        }
        return null;
    }

    @Override
    public void exitGroup(String id) {
        Observable.defer(() -> Observable.just(quitGroup(id)))
                .compose(RxSchedulersHelper.ioToMain())
                .subscribe(s -> view.exitSuccess());
    }

    private String quitGroup(String id) {
        try {
            EMClient.getInstance().chatManager().deleteConversation(id, true);
            EMClient.getInstance().groupManager().leaveGroup(id);
        } catch (HyphenateException e) {
            Log.e(TAG, "quitGroup: " + e.getErrorCode() + ", " + e.getMessage());
        }
        return null;
    }
}
