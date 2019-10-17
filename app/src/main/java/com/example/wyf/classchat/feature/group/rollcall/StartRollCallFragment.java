package com.example.wyf.classchat.feature.group.rollcall;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wyf.classchat.R;
import com.example.wyf.classchat.adapter.common.RvAdapter;
import com.example.wyf.classchat.adapter.common.RvViewHolder;
import com.example.wyf.classchat.base.BaseFragment;
import com.example.wyf.classchat.bean.GroupBmob;
import com.example.wyf.classchat.bean.MessageEvent;
import com.example.wyf.classchat.bean.PersonBmob;
import com.example.wyf.classchat.bean.RegisterHistory;
import com.example.wyf.classchat.bean.RegisterInfo;
import com.example.wyf.classchat.bean.RegisterStatus;
import com.example.wyf.classchat.constants.Constants;
import com.example.wyf.classchat.listener.BmobCallbackImpl;
import com.example.wyf.classchat.util.BmobUtils;
import com.example.wyf.classchat.util.HyphenateUtils;
import com.example.wyf.classchat.weight.AutoToolbar;
import com.hyphenate.chat.EMClient;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Administrator on 2017/10/10.
 */

public class StartRollCallFragment extends BaseFragment {

    @BindView(R.id.atb_tv_title)
    TextView tvTitle;
    @BindView(R.id.atb_iv_left)
    ImageView ivLeft;
    @BindView(R.id.atb_tv_left)
    TextView tvLeft;
    @BindView(R.id.atb_iv_right)
    ImageView ivRight;
    @BindView(R.id.atb_tv_right)
    TextView tvRight;
    @BindView(R.id.toolbar)
    AutoToolbar toolbar;
    @BindView(R.id.et_week)
    EditText etWeek;
    @BindView(R.id.et_times)
    EditText etTimes;
    @BindView(R.id.spinner_class)
    Spinner spinnerClazz;
    @BindView(R.id.et_start_reg)
    EditText etStartReg;
    @BindView(R.id.tv_count)
    TextView tvCount;
    @BindView(R.id.tv_select)
    TextView tvSelect;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.btn_start_reg)
    Button btnStartReg;

    private static final String TAG = StartRollCallFragment.class.getSimpleName();
    private static final String GROUP_ID = "group_id";
    private static final String STATUS_ID = "status_id";
    private String groupId;
    private String statusId;
    private Timer timer;
    private boolean[] checks;
    private List<PersonBmob> personBmobList;
    private boolean isAllSelected;
    private RvAdapter<PersonBmob> adapter;
    private String[] clazzs = {};
    private ArrayAdapter<String> clazzAdapter;
    private String members;
    private int count;
    private ArrayList<String> selectList;

    public static StartRollCallFragment newInstance(String groupId, String statusId) {
        StartRollCallFragment fragment = new StartRollCallFragment();
        Bundle args = new Bundle();
        args.putString(GROUP_ID, groupId);
        args.putString(STATUS_ID, statusId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            groupId = getArguments().getString(GROUP_ID);
            statusId = getArguments().getString(STATUS_ID);
        }
    }

    @Override
    public void initData() {
        //初始化课程
        initSpinner();
        //获取群成员id
        HyphenateUtils.getMembers(groupId);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = getAdapter();
        recyclerView.setAdapter(adapter);
    }

    private void initSpinner() {
        HashMap<String, String> map = new HashMap<>();
        map.put("id", groupId);
        BmobUtils.getInstance().querryGroup(map, new BmobCallbackImpl() {
            @Override
            public void success(Object object) {
                GroupBmob groupBmob = ((List<GroupBmob>) object).get(0);
                String clazz = groupBmob.getClazz();
                if (clazz != null && clazz.length() > 0) {
                    //已设定课程
                    String[] split = clazz.split("-");
                    if (split.length > 0) {
                        clazzs = split;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                clazzAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, clazzs);
                                clazzAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinnerClazz.setAdapter(clazzAdapter);
                                spinnerClazz.setPrompt("选择课程");
                                spinnerClazz.setSelection(0);
                            }
                        });
                    }
                }
            }
        });
    }

    @NonNull
    private RvAdapter<PersonBmob> getAdapter() {
        return new RvAdapter<PersonBmob>(getActivity(), R.layout.fragment_register_item, personBmobList) {
            @Override
            public void convert(RvViewHolder holder, final int position, PersonBmob personBmob) {
                Log.e(TAG, "convert: " + personBmob.getName());
                holder.setText(R.id.tv_notice_author, personBmob.getName());
                CheckBox checkBox = (CheckBox) holder.getView(R.id.check_box);
                checkBox.setChecked(checks[position]);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        checks[position] = b;
                        if (isAllselected()) {
                            tvSelect.setText("全不选");
                            isAllSelected = true;
                        } else {
                            tvSelect.setText("全选");
                            isAllSelected = false;
                        }
                    }
                });
            }
        };
    }

    /**
     * 判断是否全部群成员被选中
     *
     * @return true：全部被选中；false：不是全部被选中
     */
    private boolean isAllselected() {
        for (boolean b : checks) {
            if (!b) {
                return false;
            }
        }
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void requestMemberId(MessageEvent event) {
        if (event.getWhat() == Constants.GET_MEMBERS_INFO) {
            personBmobList = (List<PersonBmob>) event.getMessage();
            Iterator<PersonBmob> iterator = personBmobList.iterator();
            while (iterator.hasNext()) {
                PersonBmob p = iterator.next();
                if (EMClient.getInstance().getCurrentUser().equals(p.getId())) {
                    iterator.remove();
                }
            }
            checks = new boolean[personBmobList.size()];
            adapter = getAdapter();
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void initView() {
        tvSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAllSelected) {
                    for (int i = 0; i < checks.length; i++) {
                        checks[i] = false;
                    }
                    isAllSelected = false;
                } else {
                    for (int i = 0; i < checks.length; i++) {
                        checks[i] = true;
                    }
                    isAllSelected = true;
                }
                adapter.notifyDataSetChanged();
            }
        });
        btnStartReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String content = etStartReg.getText().toString().trim();
                int position = spinnerClazz.getSelectedItemPosition();
                String clazz = null;
                if (position != -1 && clazzs.length > 0) {
                    clazz = clazzs[position];
                }
                StringBuilder buffer = new StringBuilder();
                boolean isFirst = true;
                int memberCount = 0;
                selectList = new ArrayList<>();
                for (int i = 0; i < personBmobList.size(); i++) {
                    if (checks[i]) {
                        if (isFirst) {
                            buffer.append(personBmobList.get(i).getId());
                            selectList.add(personBmobList.get(i).getId());
                            isFirst = false;
                            memberCount++;
                            continue;
                        }
                        memberCount++;
                        buffer.append("-").append(personBmobList.get(i).getId());
                        selectList.add(personBmobList.get(i).getId());
                    }
                }
                count = memberCount;
                members = buffer.toString();
                if (TextUtils.isEmpty(etWeek.getText().toString().trim())) {
                    show("还未填写周数");
                    return;
                } else if (TextUtils.isEmpty(etTimes.getText().toString().trim())) {
                    show("还未填写次数");
                    return;
                } else if (TextUtils.isEmpty(clazz)) {
                    show("暂无课程数据, 请稍后重试");
                    return;
                } else if (TextUtils.isEmpty(content) || content.length() != 4) {
                    show("需输入四位匹配码");
                    return;
                } else if (TextUtils.isEmpty(members)) {
                    show("还未选取学生");
                    return;
                }
                new Thread() {
                    @Override
                    public void run() {
                        saveRegisterData(content);
                        timer = new Timer();
                    }
                }.start();
            }
        });
        setHasOptionsMenu(true);
    }

    private void show(String msg) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public String getTitleText() {
        return "点名";
    }

    @Override
    public void initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_left_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideCurrentFragment();
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_start_register;
    }

    private void hideCurrentFragment() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.hide(StartRollCallFragment.this).commit();
    }

    private void saveRegisterData(String content) {
        //更新群考勤状态
        RegisterStatus status = new RegisterStatus();
        status.setContent(content);
        status.setWeek(etWeek.getText().toString().trim());
        status.setTimes(etTimes.getText().toString().trim());
        status.setClazz(clazzs[spinnerClazz.getSelectedItemPosition()]);
        status.setMembers(members);
        status.setRegister(true);
        BmobUtils.getInstance().update(status, statusId, new BmobCallbackImpl() {
            @Override
            public void success(Object object) {

            }
        });
        //更新群考勤信息
        RegisterHistory history = new RegisterHistory();
        history.setUserId(EMClient.getInstance().getCurrentUser());
        history.setGroupId(groupId);
        history.setContent(content);
        history.setWeek(etWeek.getText().toString().trim());
        history.setTimes(etTimes.getText().toString().trim());
        history.setClazz(clazzs[spinnerClazz.getSelectedItemPosition()]);
        history.setMembers(members);
        history.setCount(count);
        history.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    hideCurrentFragment();
                    getActivity().finish();
                    timer.schedule(task, 1000 * 30);
                } else {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(mActivity, e.getErrorCode() + ", " + e.getMessage() + " 考勤发布失败，请稍后重试", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
        //上传考勤学生默认状态
        ArrayList<RegisterInfo> infos = new ArrayList<>();
        for (String id : selectList) {
            Log.e(TAG, "saveRegisterData: " + id);
            RegisterInfo info = new RegisterInfo();
            info.setUserId(id);
            info.setGroupId(groupId);
            info.setWeek(etWeek.getText().toString().trim());
            info.setTimes(etTimes.getText().toString().trim());
            info.setClazz(clazzs[spinnerClazz.getSelectedItemPosition()]);
            info.setStatus("缺勤");
            infos.add(info);
        }
        BmobUtils.getInstance().insertBatch(infos, new BmobCallbackImpl() {
            @Override
            public void success(Object object) {
                Log.e(TAG, "success: 批量上传成功");
            }
        });
    }

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            updateStatus();
        }
    };

    //更新状态为false
    private void updateStatus() {
        RegisterStatus registerStatus = new RegisterStatus();
        registerStatus.setRegister(false);
        BmobUtils.getInstance().update(registerStatus, statusId, new BmobCallbackImpl() {
            @Override
            public void success(Object object) {
                timer.cancel();
            }

            @Override
            public void Error(BmobException e) {
                timer.cancel();
            }
        });
    }

    @Override
    public boolean needEventBus() {
        return true;
    }

}
