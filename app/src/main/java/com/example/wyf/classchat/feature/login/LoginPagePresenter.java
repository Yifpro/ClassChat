package com.example.wyf.classchat.feature.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wyf.classchat.bean.PersonBmob;
import com.example.wyf.classchat.constants.Constants;
import com.example.wyf.classchat.db.FileType;
import com.example.wyf.classchat.db.util.DatabaseUtils;
import com.example.wyf.classchat.listener.BmobCallbackImpl;
import com.example.wyf.classchat.util.BitmapUtils;
import com.example.wyf.classchat.util.BmobUtils;
import com.example.wyf.classchat.util.ProgressUtils;
import com.example.wyf.classchat.util.RxSchedulersHelper;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.HashMap;
import java.util.List;

import rx.Observable;

/**
 * @author Administrator on 2018/1/21/021.
 */
public class LoginPagePresenter implements LoginPageContract.Presenter {

    private static final String TAG = "ContactPagePresenter";
    private final Activity activity;
    private final LoginPageContract.View view;

    public LoginPagePresenter(Activity activity, LoginPageContract.View view) {
        this.activity = activity;
        this.view = view;
    }

    /**
     * 登录环信
     *
     * @param name 账号
     * @param psd  密码
     */
    public void loginHyphenate(final String name, final String psd) {
        EMClient.getInstance().login(name, psd, new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                ProgressUtils.dismiss();
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                view.goToMain();
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                ProgressUtils.dismiss();
                Log.e(TAG, "onError: " + code + ", " + message);
            }
        });
    }

    /**
     * 注册bmob、环信
     *
     * @param name 账号
     * @param psd  密码
     */
    @SuppressWarnings("unchecked")
    public void registerBmobWithHyphenate(final String name, final String psd) {
        new Thread(() -> {
            try {
                EMClient.getInstance().createAccount(name, psd);
                PersonBmob personBmob = new PersonBmob(name, name);
                BmobUtils.getInstance().saveData(personBmob, new BmobCallbackImpl() {
                    @Override
                    public void success(Object object) {
                        DatabaseUtils.insertPerson(personBmob);
                        loginHyphenate(name, psd);
                    }
                });
            } catch (final HyphenateException e) {
                Log.e(TAG, "registerBmobWithHyphenate: " + e.getErrorCode() + ", " + e.getMessage());
            }
        }).start();
    }

    /**
     * 是否注册过bmob、环信
     *
     * @param name 账号
     * @param psd  密码
     */
    @SuppressWarnings("unchecked")
    public void isRegisteredBmob(final String name, final String psd) {
        ProgressUtils.showProgress(activity, "登录中，请稍后...");
        HashMap<String, String> map = new HashMap<>();
        map.put("id", name);
        BmobUtils.getInstance().querryPerson(map, new BmobCallbackImpl() {
            @Override
            public void success(Object object) {
                PersonBmob p = ((List<PersonBmob>) object).get(0);
                DatabaseUtils.insertPerson(p);
                BitmapUtils.saveIconToLocal(FileType.CONTACT, p.getId(), p.getId(), p.getIcon());
                loginHyphenate(name, psd);
            }

            @Override
            public void fail() {
                registerBmobWithHyphenate(name, psd);
            }
        });
    }

    @Override
    public void requestLogin(EditText[] views) {
        ProgressUtils.showProgress(activity, "登录中，请稍后...");
        String error = null;
        if (!LoginHelper.isNetWorkConnected(activity)) {
            error = "网络异常，请检查网络";
        } else if (TextUtils.isEmpty(views[0].getText().toString().trim())) {
            error = "请输入用户名";
        } else if (TextUtils.isEmpty(views[1].getText().toString().trim())) {
            error = "请输入密码";
        } else if (TextUtils.isEmpty(views[2].getText().toString().trim())) {
            error = "请输入验证码";
        }
        if (error != null) {
            Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
        } else {
            Observable.defer(() -> Observable.just(LoginHelper.connectJw(activity, views)))
                    .compose(RxSchedulersHelper.ioToMain())
                    .subscribe(e -> {
                        if (e == null) {
                            view.connJwSuccess();
                        } else {
                            Toast.makeText(activity, e, Toast.LENGTH_SHORT).show();
                            ProgressUtils.dismiss();
                        }
                    });
        }
    }

    @Override
    public void getVertificationCode() {
        ProgressUtils.showProgress(activity, "正在获取验证码，请稍后");
        Observable.defer(() -> Observable.just(LoginHelper.loadVertificationCode()))
                .compose(RxSchedulersHelper.ioToMain())
                .subscribe(view::setVertificationCode);
    }

    @Override
    public void prepareScene(Intent intent, View loginTitle) {
        Observable.just(captureValue(intent, loginTitle))
                .compose(RxSchedulersHelper.ioToMain())
                .subscribe(view::prepareOver);
    }

    private float[] captureValue(Intent intent, View loginTitle) {
        //获取初始位置信息
        Bundle mStartValues = captureOriginValues(intent);

        //获取终点位置信息
        Bundle mEndValues = captureDestValues(loginTitle);

        //计算位移值，缩放值
        float scaleX = scaleDelta(mStartValues, mEndValues, true);
        float scaleY = scaleDelta(mStartValues, mEndValues, false);
        int deltaX = translationDelta(mStartValues, mEndValues, true);
        int deltaY = translationDelta(mStartValues, mEndValues, false);

        return new float[]{scaleX, scaleY, deltaX, deltaY};
    }

    private int translationDelta(Bundle mStartValues, Bundle mEndValues, boolean isCaptureX) {
        if (isCaptureX) {
            int xOffset = mStartValues.getInt(Constants.PROPNAME_SCREENLOCATION_LEFT) - mEndValues.getInt(Constants.PROPNAME_SCREENLOCATION_LEFT);
            return xOffset + (mStartValues.getInt(Constants.PROPNAME_WIDTH) - mEndValues.getInt(Constants.PROPNAME_WIDTH)) / 2;
        } else {
            int yOffset = mEndValues.getInt(Constants.PROPNAME_SCREENLOCATION_TOP) - mStartValues.getInt(Constants.PROPNAME_SCREENLOCATION_TOP);
            return -yOffset + (mStartValues.getInt(Constants.PROPNAME_HEIGHT) - mEndValues.getInt(Constants.PROPNAME_HEIGHT)) / 2;
        }
    }

    private float scaleDelta(Bundle mStartValues, Bundle mEndValues, boolean isCaptureX) {
        if (isCaptureX) {
            return (float) mStartValues.getInt(Constants.PROPNAME_WIDTH) / mEndValues.getInt(Constants.PROPNAME_WIDTH);
        } else {
            return (float) mStartValues.getInt(Constants.PROPNAME_HEIGHT) / mEndValues.getInt(Constants.PROPNAME_HEIGHT);
        }
    }

    private Bundle captureDestValues(View view) {
        Bundle bundle = new Bundle();
        int[] screenLocation = new int[2];
        view.getLocationOnScreen(screenLocation);
        bundle.putInt(Constants.PROPNAME_SCREENLOCATION_LEFT, screenLocation[0]);
        bundle.putInt(Constants.PROPNAME_SCREENLOCATION_TOP, screenLocation[1]);
        bundle.putInt(Constants.PROPNAME_WIDTH, view.getWidth());
        bundle.putInt(Constants.PROPNAME_HEIGHT, view.getHeight());
        return bundle;
    }

    private Bundle captureOriginValues(Intent intent) {
        Bundle mStartValues = new Bundle();
        Bundle bundle = intent.getBundleExtra(Constants.VIEW_INFO_EXTRA);
        mStartValues.putInt(Constants.PROPNAME_SCREENLOCATION_LEFT, bundle.getInt(Constants.PROPNAME_SCREENLOCATION_LEFT, 0));
        mStartValues.putInt(Constants.PROPNAME_SCREENLOCATION_TOP, bundle.getInt(Constants.PROPNAME_SCREENLOCATION_TOP, 0));
        mStartValues.putInt(Constants.PROPNAME_WIDTH, bundle.getInt(Constants.PROPNAME_WIDTH, 0));
        mStartValues.putInt(Constants.PROPNAME_HEIGHT, bundle.getInt(Constants.PROPNAME_HEIGHT, 0));
        return mStartValues;
    }
}
