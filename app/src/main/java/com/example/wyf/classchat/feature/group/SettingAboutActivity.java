package com.example.wyf.classchat.feature.group;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.wyf.classchat.R;
import com.example.wyf.classchat.base.IAppInitContract;

import butterknife.BindView;

/**
 * Created by gsh on 2017/10/21.
 */

public class SettingAboutActivity extends AppCompatActivity implements IAppInitContract.IActivity {

    @BindView(R.id.setting_toolbar)
    Toolbar settingToolbar;
    @BindView(R.id.imageView)
    ImageView imageView;
    private Toolbar mToolbar;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.settingabout_activity;
    }

    @Override
    public void init() {
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_left_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
