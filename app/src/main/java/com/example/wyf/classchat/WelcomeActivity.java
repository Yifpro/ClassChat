package com.example.wyf.classchat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.wyf.classchat.base.IAppInitContract;
import com.example.wyf.classchat.constants.Constants;
import com.example.wyf.classchat.feature.login.LoginActivity;
import com.example.wyf.classchat.feature.home.MainActivity;
import com.example.wyf.classchat.util.RxSchedulersHelper;

import butterknife.BindView;
import rx.Observable;

/**
 * @author WYF on 2017/9/23.
 */
public class WelcomeActivity extends AppCompatActivity implements IAppInitContract.IActivity {

    @BindView(R.id.iv_blackboard)
    ImageView ivBlackboard;
    @BindView(R.id.ll_anim_title)
    LinearLayout animTitle;

    private static final int SPLASH_SLEEP = 0;

    private Bundle getBundle(View view) {
        Bundle b = new Bundle();
        int[] screenLocation = new int[2];
        view.getLocationOnScreen(screenLocation);
        b.putInt(Constants.PROPNAME_SCREENLOCATION_LEFT, screenLocation[0]);
        b.putInt(Constants.PROPNAME_SCREENLOCATION_TOP, screenLocation[1]);
        b.putInt(Constants.PROPNAME_WIDTH, view.getWidth());
        b.putInt(Constants.PROPNAME_HEIGHT, view.getHeight());
        return b;
    }

    private void goToNextPage() {
        // TODO: 2017/10/19 自动登录
        if (false) {
            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation
                        (WelcomeActivity.this, animTitle, Constants.TRANSLATE_TITLE).toBundle());
            } else {
                //过渡动画兼容
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                intent.putExtra(Constants.VIEW_INFO_EXTRA, getBundle(animTitle));
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        }
        finish();
    }

    private String sleep() {
        SystemClock.sleep(SPLASH_SLEEP);
        return null;
    }

    /**
     * 闪屏动画
     */
    private void startAnimation() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(ivBlackboard, "translationY", 600, 0);
        objectAnimator.setDuration(1000);
        objectAnimator.start();
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                Observable.just(sleep())
                        .compose(RxSchedulersHelper.ioToMain())
                        .subscribe(result -> goToNextPage());
            }

        });
    }

    @Override
    public void init() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        startAnimation();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_welcome;
    }
}
