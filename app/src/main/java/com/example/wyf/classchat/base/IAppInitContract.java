package com.example.wyf.classchat.base;

/**
 * Created by Administrator on 2018/1/25/025.
 */

public class IAppInitContract {

    public interface IActivity {
        void init();
        int getLayoutId();
    }

    public interface ISubscribe {

    }

    public interface IToolbar {
        String getToolbarTitle();
        boolean isShowHome();
        void initToolbar();
    }
}
