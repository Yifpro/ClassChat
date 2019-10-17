package com.example.wyf.classchat.feature.group.rollcall;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.wyf.classchat.R;
import com.example.wyf.classchat.adapter.common.RvAdapter;
import com.example.wyf.classchat.adapter.common.RvViewHolder;
import com.example.wyf.classchat.base.BaseFragment;
import com.example.wyf.classchat.bean.RegisterHistory;
import com.example.wyf.classchat.bean.RegisterInfo;
import com.example.wyf.classchat.listener.BmobCallbackImpl;
import com.example.wyf.classchat.util.BmobUtils;
import com.example.wyf.classchat.weight.AutoToolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2017/10/16.
 */

public class RollCallListFragment extends BaseFragment {

    private static final String TAG = RollCallListFragment.class.getSimpleName();
    private static final String GROUP_ID = "group_id";
    private static final String CURRENT_LIST = "current_list";
    private static final String ILL_LIST = "ill_list";
    private static final String LACK_LIST = "lack_list";

    @BindView(R.id.toolbar)
    AutoToolbar toolbar;
    @BindView(R.id.rv_register_member)
    RecyclerView mRv;

    private String groupId;
    private ArrayList<RegisterInfo> currents;
    private ArrayList<RegisterInfo> ills;
    private ArrayList<RegisterInfo> lacks;
    private ArrayList<RegisterInfo> registerList;
    private RvAdapter<RegisterInfo> adapter;
    private String week;
    private String times;
    private String clazz;

    public static RollCallListFragment newInstance(String groupId, ArrayList<RegisterInfo> currents, ArrayList<RegisterInfo> ills, ArrayList<RegisterInfo> lacks) {
        RollCallListFragment fragment = new RollCallListFragment();
        Bundle args = new Bundle();
        args.putString(GROUP_ID, groupId);
        args.putSerializable(CURRENT_LIST, currents);
        args.putSerializable(ILL_LIST, ills);
        args.putSerializable(LACK_LIST, lacks);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            groupId = getArguments().getString(GROUP_ID);
            currents = (ArrayList<RegisterInfo>) getArguments().getSerializable(CURRENT_LIST);
            ills = (ArrayList<RegisterInfo>) getArguments().getSerializable(ILL_LIST);
            lacks = (ArrayList<RegisterInfo>) getArguments().getSerializable(LACK_LIST);
        }
        registerList = new ArrayList<>();
        registerList.addAll(currents);
        registerList.addAll(ills);
        registerList.addAll(lacks);
    }

    private void hideCurrentFragment(boolean refresh) {
        Log.e(TAG, "hideCurrentFragment: go this");
        FragmentManager fm = getActivity().getSupportFragmentManager();
        //刷新RegisterActivity的数据
        if (refresh) {
            Log.e(TAG, "hideCurrentFragment: go true");
            refreshMemberRegisterDate(fm);
        }
        FragmentTransaction ft = fm.beginTransaction();
        ft.hide(RollCallListFragment.this);
        ft.commit();
    }


    private void refreshMemberRegisterDate(FragmentManager fm) {
        RollCallActivity rollCallActivity = (RollCallActivity) getActivity();
        rollCallActivity.refreshData(week, times, clazz);
    }

    @Override
    public void initData() {
        mRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = getAdapter();
        mRv.setAdapter(adapter);
    }

    @NonNull
    private RvAdapter<RegisterInfo> getAdapter() {
        return new RvAdapter<RegisterInfo>(getActivity(), R.layout.fragment_register_member_item, registerList) {

            @Override
            public void convert(RvViewHolder holder, int position, RegisterInfo info) {
                week = info.getWeek();
                times = info.getTimes();
                clazz = info.getClazz();
                holder.setText(R.id.tv_notice_author, info.getUserId());
                holder.setText(R.id.tv_register, info.getStatus());
            }
        };
    }

    @Override
    public String getTitleText() {
        return "考勤名单";
    }

    @Override
    public void initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_left_back);
        toolbar.inflateMenu(R.menu.toolbar_register_member_fragmenr);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //隐藏当前Fragment
                hideCurrentFragment(false);
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.register_dele:
                        HashMap<String, String> map = new HashMap<>();
                        map.put("groupId", groupId);
                        map.put("week", week);
                        map.put("times", times);
                        map.put("clazz", clazz);
                        BmobUtils.getInstance().querryRegisterHistory(map, new BmobCallbackImpl() {
                            @Override
                            public void success(Object object) {
                                RegisterHistory history = new RegisterHistory();
                                history.setObjectId(((List<RegisterHistory>) object).get(0).getObjectId());
                                history.delete(new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if (e == null) {
                                            Log.e(TAG, "done: go e != null");
                                            hideCurrentFragment(true);
                                        } else {
                                            Log.e(TAG, "done: go e == null");
                                            Log.e(TAG, "失败：" + e.getMessage() + "," + e.getErrorCode());
                                        }
                                    }
                                });
                            }
                        });
                        BmobUtils.getInstance().querryRegisterInfo(map, new BmobCallbackImpl() {
                            @Override
                            public void success(Object object) {
                                ArrayList<RegisterInfo> registerInfos = (ArrayList<RegisterInfo>) object;
                                BmobUtils.getInstance().deleteBatchData(registerInfos, new BmobCallbackImpl() {
                                    @Override
                                    public void success(Object object) {
                                    }
                                });
                            }
                        });
                        break;
                    case R.id.register_leave:
                        final EditText et = new EditText(getActivity());
                        new AlertDialog.Builder(getActivity())
                                .setTitle("更改为请假的学生id：")
                                .setView(et)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        String id = et.getText().toString().trim();
                                        int index = registerList.indexOf(new RegisterInfo(id));
                                        Log.e(TAG, "onClickonClick: " + index);
                                        if (index != -1) {
                                            registerList.get(index).setStatus("请假");
                                            adapter.notifyDataSetChanged();
                                            HashMap<String, String> map = new HashMap<>();
                                            String week = registerList.get(0).getWeek();
                                            String times = registerList.get(0).getTimes();
                                            String clazz = registerList.get(0).getClazz();
                                            map.put("userId", id);
                                            map.put("week", week);
                                            map.put("times", times);
                                            map.put("clazz", clazz);
                                            Log.e(TAG, "onClick: " + id + ", " + week + ", " + times + ", " + clazz);
                                            BmobUtils.getInstance().querryRegisterInfo(map, new BmobCallbackImpl() {
                                                @Override
                                                public void success(Object object) {
                                                    RegisterInfo info = ((List<RegisterInfo>) object).get(0);
                                                    info.setStatus("请假");
                                                    Log.e(TAG, "success: " + info.getObjectId());
                                                    BmobUtils.getInstance().update(info, info.getObjectId(), new BmobCallbackImpl() {
                                                        @Override
                                                        public void success(Object object) {
                                                            Log.e(TAG, "success: update leave success");
                                                        }
                                                    });
                                                }
                                            });
                                        } else {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(mActivity, "不存在此id", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }
                                })
                                .setNegativeButton("取消", null)
                                .show();
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void initView() {
        setHasOptionsMenu(true);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_register_member_list;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_register_member_fragmenr, menu);
    }

}
