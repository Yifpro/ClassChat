package com.example.wyf.classchat.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wyf.classchat.R;
import com.example.wyf.classchat.weight.AutoToolbar;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by WYF on 2017/10/19.
 */

public abstract class BaseFragment extends Fragment {
    public FragmentActivity mActivity;
    public Bundle mBundle;
    public View mView;
    private Unbinder bind;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBundle = getArguments();
        mView = inflater.inflate(getLayoutId(), container, false);
        bind = ButterKnife.bind(this, mView);
        if (needEventBus()) {
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }
        }
        AutoToolbar toolbar = (AutoToolbar) mView.findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitleText(getTitleText());
            if (isShowHome()) {
                ((AppCompatActivity) mActivity).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mActivity.finish();
                    }
                });
            }
            initToolbar();
        }
        initData();
        return mView;
    }

    public abstract void initData();

    public void initView() {};

    public void initToolbar() {}

    public abstract int getLayoutId();

    public boolean needEventBus() {
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (needEventBus()) {
            EventBus.getDefault().unregister(this);
        }
        bind.unbind();
    }


    public boolean isShowHome() {
        return false;
    }

    public String getTitleText() {
        return "";
    }
}
