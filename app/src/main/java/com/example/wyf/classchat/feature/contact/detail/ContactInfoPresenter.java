package com.example.wyf.classchat.feature.contact.detail;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.wyf.classchat.bean.MessageEvent;
import com.example.wyf.classchat.bean.PersonBmob;
import com.example.wyf.classchat.constants.Constants;
import com.example.wyf.classchat.db.FileType;
import com.example.wyf.classchat.listener.BmobCallbackImpl;
import com.example.wyf.classchat.util.BitmapUtils;
import com.example.wyf.classchat.util.BmobUtils;
import com.example.wyf.classchat.db.util.DatabaseUtils;
import com.example.wyf.classchat.util.PreferencesUtils;
import com.example.wyf.classchat.util.RxSchedulersHelper;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Observable;

/**
 * @author Administrator on 2018/1/21/021.
 */
public class ContactInfoPresenter implements ContactInfoContract.Presenter {

    private static final String TAG = "ContactInfoPresenter";
    private final Activity activity;
    private final ContactInfoContract.View view;

    public ContactInfoPresenter(Activity activity, ContactInfoContract.View view) {
        this.activity = activity;
        this.view = view;
    }

    @SuppressWarnings("unchecked")
    public void getInfoFromServer(String id) {
        HashMap<String, String> map = new HashMap<>();
        map.put("id", id);
        BmobUtils.getInstance().querryPerson(map, new BmobCallbackImpl() {
            @Override
            public void success(Object object) {
                PersonBmob personBmob = ((List<PersonBmob>) object).get(0);
                DatabaseUtils.insertPerson(personBmob);
                BitmapUtils.saveIconToLocal(FileType.CONTACT, personBmob.getId(), personBmob.getId(), personBmob.getIcon());
                EventBus.getDefault().post(new MessageEvent(Constants.GET_DETAIL_INFO_FROM_SERVER, personBmob));
            }
        });
    }

    @Override
    public void getNewBg() {
        Observable.defer(() -> Observable.just(loadNewBg()))
                .compose(RxSchedulersHelper.ioToMain())
                .subscribe(view::setUpdateBg);
    }

    private Bitmap loadNewBg() {
        RequestBody requestBody = new FormBody.Builder()
                .add("key1", "value1")
                .add("key2", "value2")
                .build();
        Request request = new Request.Builder()
                .url("https://api.dujin.org/bing/1366.php")
                .post(requestBody)
                .build();
        Bitmap temp = null;
        try {
            Response response = new OkHttpClient().newCall(request).execute();
            if (response.code() == 200) {
                final Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                temp = BitmapUtils.compressScale(bitmap);
                BitmapUtils.saveIconToLocal(FileType.CONTACT, EMClient.getInstance().getCurrentUser(), "contact_bg", temp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return temp;
    }

    /**
     *  判断是否更新每日必应
     */
    public void isUpdateBg() {
        Observable.just(captureUpdateBgDate())
                .compose(RxSchedulersHelper.ioToMain())
                .subscribe(view::captureUpdateBgOver);
    }

    private boolean captureUpdateBgDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int[] now = {year, month, day};
        String date = PreferencesUtils.getPreferences().getString(Constants.BG_CONTACT_INFO_UPDATE_DATE, "");
        if (date.length() == 0) {
            PreferencesUtils.savePreference(Constants.BG_CONTACT_INFO_UPDATE_DATE, year + "-" + month + "-" + day);
            return true;
        } else {
            int[] old = new int[3];
            String[] split = date.split("-");
            for (int i = 0; i < split.length; i++) {
                old[i] = Integer.parseInt(split[i]);
            }
            for (int i = 0; i < old.length; i++) {
                if (now[i] > old[i]) {
                    PreferencesUtils.savePreference(Constants.BG_CONTACT_INFO_UPDATE_DATE, year + "-" + month + "-" + day);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 删除联系人相关数据
     * @param id 联系人id
     */
    @Override
    public void deleteContact(String id) {
        Observable.defer(() -> Observable.just(delContactData(id)))
                .compose(RxSchedulersHelper.ioToMain())
                .subscribe(s -> activity.finish());
    }

    private String delContactData(String userId) {
        try {
            DatabaseUtils.delData(FileType.CONTACT, userId);
            EMClient.getInstance().chatManager().deleteConversation(userId, true);
            EMClient.getInstance().contactManager().deleteContact(userId);
            EventBus.getDefault().post(new MessageEvent(Constants.REFRESH_CONTACT_ON_DEL, userId));
        } catch (HyphenateException e) {
            Log.e(TAG, "run: " + e.getErrorCode() + ", " + e.getMessage());
        }
        return null;
    }

}
