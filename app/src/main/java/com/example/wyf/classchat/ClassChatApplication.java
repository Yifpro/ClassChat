package com.example.wyf.classchat;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.wyf.classchat.base.IAppInitContract;
import com.example.wyf.classchat.weight.AutoToolbar;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.raizlabs.android.dbflow.config.FlowManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;
import cn.bmob.v3.Bmob;

/**
 * @author WYF on 2017/9/24.
 */
public class ClassChatApplication extends Application {

    private static List<Activity> activities = new ArrayList<>();
    private static ClassChatApplication mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        initBmob();
        initHyphenate();
        initDataBase();
        subscribeActivityLifecycle();
    }

    public static ClassChatApplication getInstance(){
        return mContext;
    }

    private void subscribeActivityLifecycle() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
                if (activity instanceof IAppInitContract.IActivity) {
                    IAppInitContract.IActivity a = (IAppInitContract.IActivity) activity;
                    activity.setContentView(a.getLayoutId());
                    ButterKnife.bind(activity);
                    a.init();
                }
                if (activity instanceof IAppInitContract.IToolbar) {
                    final AppCompatActivity appCompatActivity = (AppCompatActivity) activity;
                    AutoToolbar toolbar = (AutoToolbar) appCompatActivity.findViewById(R.id.toolbar);
                    IAppInitContract.IToolbar iToolbar = (IAppInitContract.IToolbar) activity;
                    toolbar.setTitleText(iToolbar.getToolbarTitle());
                    appCompatActivity.setSupportActionBar(toolbar);
                    if (iToolbar.isShowHome()) {
                        assert appCompatActivity.getSupportActionBar() != null;
                        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        toolbar.setNavigationOnClickListener(view -> appCompatActivity.finish());
                    } else {
                        iToolbar.initToolbar();
                    }
                }
                if (activity instanceof IAppInitContract.ISubscribe) {
                    if (!EventBus.getDefault().isRegistered(activity)) {
                        EventBus.getDefault().register(activity);
                    }
                }
                activities.add(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                if (activity instanceof IAppInitContract.ISubscribe) {
                    EventBus.getDefault().unregister(activity);
                }
                activities.remove(activity);
            }
        });
    }

    private void initDataBase() {
        FlowManager.init(this);
    }

    private void initBmob() {
        Bmob.initialize(this, "4369883fab68fed5c069a3f243284f91");
    }

    private void initHyphenate() {
        // 获取当前进程 id 并取得进程名
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        /**
         * 如果app启用了远程的service，此application:onCreate会被调用2次
         * 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
         * 默认的app会在以包名为默认的process name下运行，如果查到的process name不是app的process name就立即返回
         */
        if (processAppName == null || !processAppName.equalsIgnoreCase(mContext.getPackageName())) {
            // 则此application的onCreate 是被service 调用的，直接返回
            return;
        }
        /**
         * SDK初始化的一些配置
         * 关于 EMOptions 可以参考官方的 API 文档
         * http://www.easemob.com/apidoc/android/chat3.0/classcom_1_1hyphenate_1_1chat_1_1_e_m_options.html
         */
        EMOptions options = new EMOptions();
        // 设置Appkey，如果配置文件已经配置，这里可以不用设置
        // options.setAppKey("guaju");
        // 设置自动登录
        options.setAutoLogin(true);
        // 设置是否需要发送已读回执
        options.setRequireAck(true);
        // 设置是否需要发送回执
        options.setRequireDeliveryAck(true);
        // 收到好友申请是否自动同意，如果是自动同意就不会收到好友请求的回调，因为sdk会自动处理，默认为true
        options.setAcceptInvitationAlways(true);
        // 设置是否自动接收加群邀请，如果设置了当收到群邀请会自动同意加入
        options.setAutoAcceptGroupInvitation(false);
        // 设置（主动或被动）退出群组时，是否删除群聊聊天记录
        options.setDeleteMessagesAsExitGroup(false);
        // 设置是否允许聊天室的Owner 离开并删除聊天室的会话
        options.allowChatroomOwnerLeave(true);
        // 设置google GCM推送id，国内可以不用设置
        // options.setGCMNumber(MLConstants.ML_GCM_NUMBER);
        // 设置集成小米推送的appid和appkey
        // options.setMipushConfig(MLConstants.ML_MI_APP_ID, MLConstants.ML_MI_APP_KEY);

        // 调用初始化方法初始化sdk
        EMClient.getInstance().init(mContext, options);
        // 设置开启debug模式
        EMClient.getInstance().setDebugMode(true);

    }

    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        assert am != null;
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }

    public static void removeAll() {
        for (Activity activity : activities) {
            activity.finish();
        }
    }
}
