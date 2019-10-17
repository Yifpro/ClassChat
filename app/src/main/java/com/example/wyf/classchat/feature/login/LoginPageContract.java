package com.example.wyf.classchat.feature.login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.EditText;

import com.example.wyf.classchat.base.BasePresent;
import com.example.wyf.classchat.base.BaseView;

/**
 * @author Administrator on 2018/1/22/022.
 */
public interface LoginPageContract {

    interface View extends BaseView<Presenter> {
        void setVertificationCode(Bitmap bm);
        void connJwSuccess();
        void prepareOver(float[] positions);
        void goToMain();
    }

    interface Presenter extends BasePresent {
        void isRegisteredBmob(String name, String psd);
        void requestLogin(EditText[] views);
        void getVertificationCode();
        void prepareScene(Intent intent, android.view.View loginTitle);
    }
}
