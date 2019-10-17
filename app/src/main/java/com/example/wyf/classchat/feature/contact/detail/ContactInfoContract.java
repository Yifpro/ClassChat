package com.example.wyf.classchat.feature.contact.detail;

import android.graphics.Bitmap;

import com.example.wyf.classchat.base.BasePresent;
import com.example.wyf.classchat.base.BaseView;

/**
 * Created by Administrator on 2018/1/22/022.
 */

public interface ContactInfoContract {

    interface View extends BaseView<Presenter> {
        void setUpdateBg(Bitmap bm);
        void captureUpdateBgOver(boolean isShouldUpdate);
    }

    interface Presenter extends BasePresent {
        void getNewBg();
        void isUpdateBg();
        void getInfoFromServer(String id);
        void deleteContact(String id);
    }
}
