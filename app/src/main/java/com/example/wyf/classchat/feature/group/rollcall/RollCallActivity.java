package com.example.wyf.classchat.feature.group.rollcall;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wyf.classchat.R;
import com.example.wyf.classchat.adapter.common.RvAdapter;
import com.example.wyf.classchat.adapter.common.RvViewHolder;
import com.example.wyf.classchat.base.IAppInitContract;
import com.example.wyf.classchat.bean.RegisterHistory;
import com.example.wyf.classchat.bean.RegisterInfo;
import com.example.wyf.classchat.bean.RegisterStatus;
import com.example.wyf.classchat.listener.BmobCallbackImpl;
import com.example.wyf.classchat.util.BmobUtils;
import com.example.wyf.classchat.util.ProgressUtils;
import com.example.wyf.classchat.weight.AutoToolbar;
import com.hyphenate.chat.EMClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import cn.bmob.v3.exception.BmobException;

public class RollCallActivity extends AppCompatActivity implements IAppInitContract.IActivity, IAppInitContract.IToolbar {

    @BindView(R.id.toolbar)
    AutoToolbar toolbar;
    @BindView(R.id.tv_info_times)
    TextView tvInfoTimes;
    @BindView(R.id.tv_info_clazz)
    TextView tvInfoClazz;
    @BindView(R.id.tv_info_status)
    TextView tvInfoStatus;
    @BindView(R.id.tv_no_data)
    TextView tvNoData;
    @BindView(R.id.rv_register)
    RecyclerView rvRegister;
    @BindView(R.id.fl_register)
    FrameLayout flRegister;

    private final static String TAG = RollCallActivity.class.getSimpleName();
    private final static String START_REGISTER_FREAGMENT = "start_register_fragment";
    private final static String REGISTER_MEMBER_LIST_FRAGMENT = "register_member_list_fragment";
    private String groupId;
    private RvAdapter<RegisterHistory> historyAdater;
    private ArrayList<RegisterHistory> historyList = new ArrayList<>();
    private ArrayList<String> adminList;
    private boolean isAdmin;
    private RegisterStatus currentStatuses;
    private RvAdapter<RegisterInfo> infoAdater;
    private List<RegisterInfo> infoList;

    @NonNull
    private RvAdapter<RegisterHistory> getHistoryAdapter() {
        return new RvAdapter<RegisterHistory>(RollCallActivity.this, R.layout.fragment_chat_register_item, historyList) {
            @Override
            public void convert(final RvViewHolder holder, int position, final RegisterHistory history) {
                if (history != null) {
                    //课次，课程，应到人数
                    holder.setText(R.id.tv_notice_time, history.getWeek() + "-" + history.getTimes());
                    holder.setText(R.id.tv_clazz, history.getClazz());
                    holder.setText(R.id.tv_should, history.getCount() + "");

                    //判读history是否正在点名中,true:显示正在点名中, false:无
                    if (currentStatuses != null && currentStatuses.isRegister()) {
                        if (currentStatuses.getWeek().equals(history.getWeek()) && currentStatuses.getTimes().equals(history.getTimes()) &&
                                currentStatuses.getClazz().equals(history.getClazz())) {
                            holder.getView(R.id.tv_registering).setVisibility(View.VISIBLE);
                            return;
                        }
                    } else {
                        holder.getView(R.id.tv_registering).setVisibility(View.GONE);
                    }

                    HashMap<String, String> params = new HashMap<>();
                    params.put("groupId", groupId);
                    params.put("week", history.getWeek());
                    params.put("times", history.getTimes());
                    params.put("clazz", history.getClazz());
                    BmobUtils.getInstance().querryRegisterInfo(params, new BmobCallbackImpl() {
                        @Override
                        public void success(Object object) {
                            final ArrayList<RegisterInfo> currentList = new ArrayList<>();
                            final ArrayList<RegisterInfo> illList = new ArrayList<>();
                            final ArrayList<RegisterInfo> lackList = new ArrayList<>();
                            List<RegisterInfo> registerInfoList = (List<RegisterInfo>) object;
                            for (RegisterInfo info : registerInfoList) {
                                if ("到".equals(info.getStatus())) {
                                    currentList.add(info);
                                } else if ("请假".equals(info.getStatus())) {
                                    illList.add(info);
                                } else if ("缺勤".equals(info.getStatus())) {
                                    lackList.add(info);
                                }
                            }
                            RegisterInfo i = registerInfoList.get(0);
                            if (i.getWeek().equals(history.getWeek()) && i.getTimes().equals(history.getTimes()) &&
                                    i.getClazz().equals(history.getClazz())) {
                                holder.setText(R.id.tv_actually, currentList.size() + "");
                                holder.setText(R.id.tv_ill, illList.size() + "");
                                holder.setText(R.id.tv_lack, lackList.size() + "");

                                holder.getView(R.id.ll_layout).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        RollCallListFragment rollCallListFragment = RollCallListFragment.
                                                newInstance(groupId, currentList, illList, lackList);
                                        getSupportFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.fl_register, rollCallListFragment, REGISTER_MEMBER_LIST_FRAGMENT)
                                                .commit();
                                    }
                                });
                            }
                        }
                    });
                }
            }
        };
    }

    private ArrayList<String> getMemberList(String members) {
        ArrayList<String> list = new ArrayList<>();
        if (members != null && members.length() > 0) {
            String[] split = members.split("-");
            if (split.length > 0) {
                for (String s : split) {
                    list.add(s);
                }
            }
        }
        return list;
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        RollCallListFragment memberListFragment = (RollCallListFragment) fm.findFragmentByTag(REGISTER_MEMBER_LIST_FRAGMENT);
        StartRollCallFragment startRollCallFragment = (StartRollCallFragment) fm.findFragmentByTag(START_REGISTER_FREAGMENT);
        if (memberListFragment != null && memberListFragment.isVisible()) {
            fm.beginTransaction().hide(memberListFragment).commit();
            return;
        } else if (startRollCallFragment != null && startRollCallFragment.isVisible()) {
            fm.beginTransaction().hide(startRollCallFragment).commit();
            return;
        }
        super.onBackPressed();
    }

    //刷新Register数据
    public void refreshData(String week, String times, String clazz) {
        //querryRegisterHistory();
        int index = historyList.indexOf(new RegisterHistory(week, times, clazz));
        historyList.remove(index);
        if (historyList != null && historyList.size() == 0) {
            tvNoData.setVisibility(View.VISIBLE);
        }
//        historyAdater = getHistoryAdapter();
//        rvRegister.setAdapter(historyAdater);
        historyAdater.notifyDataSetChanged();
    }


    private void querryRegisterHistory() {
        historyList.clear();
        //查询当前群是否正在点名
        HashMap<String, String> map = new HashMap<>();
        map.put("groupId", groupId);
        BmobUtils.getInstance().querryRegisterStatus(map, new BmobCallbackImpl() {
            @Override
            public void success(Object object) {
                currentStatuses = ((List<RegisterStatus>) object).get(0);
            }
        });
        //获取在该群点过名的学生
        final HashMap<String, String> params = new HashMap<>();
        params.put("groupId", groupId);
        BmobUtils.getInstance().querryRegisterHistory(params, new BmobCallbackImpl() {
            @Override
            public void success(final Object object) {
                ProgressUtils.dismiss();
                historyList = ((ArrayList<RegisterHistory>) object);
                for (RegisterHistory history : historyList) {
                    Log.e(TAG, "success: data from bmob: " + history.getWeek() + ", " + history.getTimes() + ", " + history.getClazz());
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (historyAdater == null) {
                            rvRegister.setLayoutManager(new LinearLayoutManager(RollCallActivity.this));
                            historyAdater = getHistoryAdapter();
                            rvRegister.setAdapter(historyAdater);
                        } else {
                            historyAdater.notifyDataSetChanged();
                        }
                    }
                });
            }

            @Override
            public void fail() {
                Log.e(TAG, "fail: data no found");
                ProgressUtils.dismiss();
                tvNoData.setVisibility(View.VISIBLE);
            }

            @Override
            public void Error(BmobException e) {
                ProgressUtils.dismiss();
                super.Error(e);
            }
        });

    }

    private RvAdapter<RegisterInfo> getInfoAdapter() {
        return new RvAdapter<RegisterInfo>(this, R.layout.activity_register_info_item, infoList) {
            @Override
            public void convert(RvViewHolder holder, int position, RegisterInfo registerInfo) {
                holder.setText(R.id.tv_info_times, registerInfo.getWeek() + "-" + registerInfo.getTimes());
                holder.setText(R.id.tv_info_clazz, registerInfo.getClazz());
                holder.setText(R.id.tv_info_status, registerInfo.getStatus());
            }
        };
    }

    private void querryRegisterUser() {
        HashMap<String, String> params = new HashMap<>();
        params.put("groupId", groupId);
        params.put("userId", EMClient.getInstance().getCurrentUser());
        BmobUtils.getInstance().querryRegisterInfo(params, new BmobCallbackImpl() {
            @Override
            public void success(Object object) {
                ProgressUtils.dismiss();
                infoList = (List<RegisterInfo>) object;
                if (infoAdater == null) {
                    rvRegister.setLayoutManager(new LinearLayoutManager(RollCallActivity.this));
                    infoAdater = getInfoAdapter();
                    rvRegister.setAdapter(infoAdater);
                } else {
                    infoAdater.notifyDataSetChanged();
                }
            }

            @Override
            public void fail() {
                ProgressUtils.dismiss();
                tvNoData.setVisibility(View.VISIBLE);
            }

            @Override
            public void Error(BmobException e) {
                ProgressUtils.dismiss();
                super.Error(e);
            }
        });
    }

    @Override
    public void init() {
        //判断是否为管理员
        groupId = getIntent().getStringExtra("groupId");
        adminList = getIntent().getStringArrayListExtra("adminList");
        isAdmin = adminList.contains(EMClient.getInstance().getCurrentUser());
        ProgressUtils.showProgress(this, "加载中...");
        if (isAdmin) {
            Log.e(TAG, "init: i am a admin");
            querryRegisterHistory();
        } else {
            Log.e(TAG, "init: i not a admin");
            findViewById(R.id.layout_register_header).setVisibility(View.VISIBLE);
            querryRegisterUser();
        }
    }

    @Override
    public void initToolbar() {
        if (isAdmin) {
            toolbar.inflateMenu(R.menu.toolbar_register_fragment);
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    HashMap<String, String> param = new HashMap<String, String>();
                    param.put("groupId", groupId);
                    BmobUtils.getInstance().querryRegisterStatus(param, new BmobCallbackImpl() {
                        @Override
                        public void success(Object object) {
                            final RegisterStatus registerStatus = ((List<RegisterStatus>) object).get(0);
                            final boolean register = registerStatus.isRegister();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (register) {
                                        Toast.makeText(RollCallActivity.this, "正在点名，请稍后重试...", Toast.LENGTH_SHORT).show();
                                    } else {
                                        StartRollCallFragment startRollCallFragment = StartRollCallFragment.newInstance(groupId, registerStatus.getObjectId());
                                        getSupportFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.fl_register, startRollCallFragment, START_REGISTER_FREAGMENT)
                                                .commit();
                                    }
                                }
                            });
                        }
                    });
                    return false;
                }
            });
        }
    }

    @Override
    public String getToolbarTitle() {
        return "考勤记录";
    }

    @Override
    public boolean isShowHome() {
        return true;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_register;
    }
}
