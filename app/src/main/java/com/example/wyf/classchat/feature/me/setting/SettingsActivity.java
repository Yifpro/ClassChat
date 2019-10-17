package com.example.wyf.classchat.feature.me.setting;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wyf.classchat.ClassChatApplication;
import com.example.wyf.classchat.R;
import com.example.wyf.classchat.base.IAppInitContract;
import com.example.wyf.classchat.constants.Constants;
import com.example.wyf.classchat.feature.login.LoginActivity;
import com.example.wyf.classchat.feature.group.SettingAboutActivity;
import com.example.wyf.classchat.util.ForbidFastClickUtils;
import com.example.wyf.classchat.util.PreferencesUtils;
import com.example.wyf.classchat.util.RxSchedulersHelper;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;

public class SettingsActivity extends AppCompatActivity implements IAppInitContract.IActivity{

    @BindView(R.id.setting_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.appBar)
    AppBarLayout appBar;
    @BindView(R.id.setting_changeskin)
    LinearLayout settingChangeskin;
    @BindView(R.id.setting_clearsize)
    TextView tv_clearSize;
    @BindView(R.id.setting_voice)
    LinearLayout settingVoice;
    @BindView(R.id.setting_shake)
    LinearLayout settingShake;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.setting_clear, R.id.setting_updata, R.id.setting_exitlogin, R.id.setting_aboutus})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_clear:
                if (ForbidFastClickUtils.isFastClick()) {
                    Observable.just(clearCache())
                            .compose(RxSchedulersHelper.ioToMain())
                            .subscribe(isSuccess -> {
                                if (isSuccess) {
                                    tv_clearSize.setText(CacheDataManager.getTotalCacheSize(SettingsActivity.this));
                                }
                            });
                }
                break;
            case R.id.setting_updata:
                UpdateManger updateManger = new UpdateManger(SettingsActivity.this);
                updateManger.checkUpdateInfo();
                break;
            case R.id.setting_exitlogin:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final View view = View.inflate(this, R.layout.dialog_exit_login, null);
                builder.setView(view);
                final AlertDialog alertDialog = builder.create();
                view.findViewById(R.id.btn_cancel).setOnClickListener(view2 -> alertDialog.dismiss());
                view.findViewById(R.id.btn_exit).setOnClickListener(view1 -> exitLogin());
                alertDialog.show();
                break;
            case R.id.setting_aboutus:
                startActivity(new Intent(SettingsActivity.this, SettingAboutActivity.class));
                break;
        }
    }

    public boolean clearCache() {
        try {
            CacheDataManager.clearAllCache(SettingsActivity.this);
            Thread.sleep(2000);
            if (CacheDataManager.getTotalCacheSize(SettingsActivity.this).startsWith("0")) {
                return true;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void exitLogin() {
        PreferencesUtils.savePreference(Constants.LAST_ACCOUNT, EMClient.getInstance().getCurrentUser());
        EMClient.getInstance().logout(false, new EMCallBack() {

            @Override
            public void onSuccess() {
                startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                ClassChatApplication.removeAll();
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {

            }
        });
    }

    @Override
    public void init() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
        mCollapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);
        mCollapsingToolbarLayout.setTitle("设置");

        String totalCacheSize = CacheDataManager.getTotalCacheSize(SettingsActivity.this);
        tv_clearSize.setText(totalCacheSize);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_me_fragment_settings;
    }

}
