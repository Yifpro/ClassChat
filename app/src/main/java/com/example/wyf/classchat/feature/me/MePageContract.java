package com.example.wyf.classchat.feature.me;

import android.graphics.Bitmap;

import com.example.wyf.classchat.base.BasePresent;
import com.example.wyf.classchat.base.BaseView;

/**
 * Created by Administrator on 2018/1/22/022.
 */

public interface MePageContract {

    interface View extends BaseView<Presenter> {
        void displayQr(Bitmap bitmap);
    }

    interface Presenter extends BasePresent {
        void generateQr();
    }
}
