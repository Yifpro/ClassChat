package com.example.wyf.classchat.feature.login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wyf.classchat.R;
import com.example.wyf.classchat.base.IAppInitContract;
import com.example.wyf.classchat.feature.home.MainActivity;
import com.example.wyf.classchat.util.ForbidFastClickUtils;
import com.example.wyf.classchat.util.PermissionManager;
import com.example.wyf.classchat.util.ProgressUtils;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnEditorAction;

/**
 * @author WYF on 2017/10/15.
 */
public class LoginActivity extends AppCompatActivity implements IAppInitContract.IActivity,
        LoginPageContract.View {

    @BindView(R.id.ll_login_title)
    LinearLayout loginTitle;
    @BindView(R.id.et_name)
    EditText userName;
    @BindView(R.id.et_password)
    EditText userPsd;
    @BindView(R.id.et_code)
    EditText userCode;
    @BindView(R.id.iv_code_image)
    ImageView codeImg;

    private static final long DEFAULT_DURATION = 400;
    private LoginPageContract.Presenter presenter;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100: {
                for (int i = 0; i < grantResults.length && grantResults.length > 0; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "拒绝的权限可能影响程序的正常运行", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
            break;
        }
    }

    @Override
    public void goToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void connJwSuccess() {
        presenter.isRegisteredBmob(userName.getText().toString().trim(), userPsd.getText().toString().trim().substring(12));
    }

    @OnClick({R.id.iv_code_image, R.id.btn_rollcall_commit, R.id.tv_service_terms, R.id.iv_clear_id, R.id.iv_clear_identity})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_code_image:
                if (ForbidFastClickUtils.isFastClick()) {
                    presenter.getVertificationCode();
                }
                break;
            case R.id.btn_rollcall_commit:
                if (ForbidFastClickUtils.isFastClick()) {
                    //presenter.requestLogin(new EditText[]{userName, userPsd, userCode});
                    presenter.isRegisteredBmob(userName.getText().toString().trim(), userPsd.getText().toString().trim().substring(12));
                }
                break;
            case R.id.tv_service_terms:
                startActivity(new Intent(LoginActivity.this, ServiceTermsActivity.class));
                break;
            case R.id.iv_clear_id:
                resetText(userName);
                break;
            case R.id.iv_clear_identity:
                resetText(userPsd);
                break;
        }
    }

    private void resetText(EditText et) {
        et.setText("");
        et.requestFocus();
    }

    /**
     * 获取验证码成功
     * @param bm 验证码
     */
    @Override
    public void setVertificationCode(Bitmap bm) {
        codeImg.setImageBitmap(bm);
        ProgressUtils.dismiss();
    }

    private void runEnterAnim() {
        loginTitle.setVisibility(View.VISIBLE);
        loginTitle.animate()
                .setDuration(DEFAULT_DURATION)
                .setInterpolator(new LinearInterpolator())
                .scaleX(1f)
                .scaleY(1f)
                .translationX(0)
                .translationY(0)
                .start();
    }

    @Override
    public void prepareOver(float[] positions) {
        loginTitle.setScaleX(positions[0]);
        loginTitle.setScaleY(positions[1]);
        loginTitle.setTranslationX(positions[2]);
        loginTitle.setTranslationY(positions[3]);
    }

    @SuppressLint("ObsoleteSdkInt")
    @Override
    public void init() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setPresent(new LoginPagePresenter(this, this));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            loginTitle.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    loginTitle.getViewTreeObserver().removeOnPreDrawListener(this);
                    presenter.prepareScene(getIntent(), loginTitle);
                    runEnterAnim();
                    return true;
                }
            });
        }

        PermissionManager.requestPermission(this, 100, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO});

        presenter.getVertificationCode();
    }

    @OnEditorAction({R.id.et_name, R.id.et_password, R.id.et_code})
    public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
            switch (view.getId()) {
                case R.id.et_name:
                    userPsd.requestFocus();
                    break;
                case R.id.et_password:
                    userCode.requestFocus();
                    break;
                case R.id.et_code:
                    //presenter.requestLogin(new EditText[]{userName, userPsd, userCode});
                    presenter.isRegisteredBmob(userName.getText().toString().trim(), userPsd.getText().toString().trim().substring(12));
                    break;
            }
            return true;
        }
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void setPresent(LoginPageContract.Presenter presenter) {
        this.presenter = presenter;
    }
}