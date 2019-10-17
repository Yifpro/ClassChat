package com.example.wyf.classchat.feature.group.file;

import android.content.res.Resources;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

import com.example.wyf.classchat.R;
import com.example.wyf.classchat.adapter.FileAdapter;
import com.example.wyf.classchat.base.IAppInitContract;
import com.example.wyf.classchat.weight.NoScrollViewPager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by WYF on 2017/10/28.
 */

public class SelectFileActivity extends AppCompatActivity implements IAppInitContract.IActivity, IAppInitContract.IToolbar {

    private static final String TAG = SelectFileActivity.class.getSimpleName();

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    NoScrollViewPager viewPager;

    @Override
    public void init() {
        initToolbar();
        List<String> titles = new ArrayList<>();
        titles.add("影音");
        titles.add("文档");
        titles.add("图片");
        titles.add("应用");
        viewPager.setScroll(false);
//        viewPager.getCurrentItem()
        viewPager.setAdapter(new FileAdapter(getSupportFragmentManager(), titles, getIntent().getStringExtra("userId"), -1));

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                setIndicator(tabLayout, 28, 28);
            }
        });
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setIndicator(TabLayout tabs, int leftDip, int rightDip) {
        Class<?> tabLayout = tabs.getClass();
        Field tabStrip = null;
        try {
            tabStrip = tabLayout.getDeclaredField("mTabStrip");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        tabStrip.setAccessible(true);
        LinearLayout llTab = null;
        try {
            llTab = (LinearLayout) tabStrip.get(tabs);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        int left = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, leftDip, Resources.getSystem().getDisplayMetrics());
        int right = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightDip, Resources.getSystem().getDisplayMetrics());

        for (int i = 0; i < llTab.getChildCount(); i++) {
            View child = llTab.getChildAt(i);
            child.setPadding(0, 0, 0, 0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
            params.leftMargin = left;
            params.rightMargin = right;
            child.setLayoutParams(params);
            child.invalidate();
        }
    }

    @Override
    public String getToolbarTitle() {
        return "选择文件";
    }

    @Override
    public boolean isShowHome() {
        return true;
    }

    @Override
    public void initToolbar() {}

    @Override
    public int getLayoutId() {
        return R.layout.activity_select_file;
    }
}
