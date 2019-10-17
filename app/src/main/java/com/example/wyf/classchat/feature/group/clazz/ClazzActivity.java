package com.example.wyf.classchat.feature.group.clazz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wyf.classchat.R;
import com.example.wyf.classchat.adapter.ClazzAadpter;
import com.example.wyf.classchat.base.IAppInitContract;
import com.example.wyf.classchat.bean.GroupBmob;
import com.example.wyf.classchat.bean.RegisterHistory;
import com.example.wyf.classchat.bean.RegisterInfo;
import com.example.wyf.classchat.constants.Constants;
import com.example.wyf.classchat.listener.BmobCallbackImpl;
import com.example.wyf.classchat.util.BmobUtils;
import com.example.wyf.classchat.weight.AutoToolbar;
import com.example.wyf.classchat.weight.ClazzItemView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author WYF on 2017/11/3.
 */
public class ClazzActivity extends AppCompatActivity implements IAppInitContract.IActivity, IAppInitContract.IToolbar {

    @BindView(R.id.toolbar)
    AutoToolbar toolbar;
    @BindView(R.id.tv_no_data)
    TextView tvNoData;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.iv_add_clazz)
    CircleImageView ivAddClazz;
    @BindView(R.id.iv_del_clazz)
    CircleImageView ivDelClazz;
    @BindView(R.id.et_old_clazz)
    EditText etOldClazz;
    @BindView(R.id.et_new_clazz)
    EditText etNewClazz;
    @BindView(R.id.iv_exchange)
    ImageView ivExchange;
    @BindView(R.id.layout_exchange)
    LinearLayout layoutExchange;

    private static final String TAG = ClazzActivity.class.getSimpleName();
    private List<ClazzItemView> clazzList = new ArrayList<>();
    private String userId;
    private ClazzAadpter adapter;
    private boolean isShow;
    private boolean allSelected;
    private boolean lastDel;
    private boolean isUpdate;
    private String newClazz;
    private String oldClazz;

    private boolean isAllSelected() {
        for (ClazzItemView view : clazzList) {
            if (!view.isCheck()) {
                return false;
            }
        }
        return true;
    }

    private void showDel() {
        ObjectAnimator translationDown = ObjectAnimator.ofFloat(ivAddClazz, "translationY", 0, 250f);
        ObjectAnimator translationUp = ObjectAnimator.ofFloat(ivDelClazz, "translationY", 250f, 0);
        AnimatorSet set = new AnimatorSet();
        set.setDuration(300).play(translationDown).with(translationUp);
        set.start();
    }

    private void hiddeDel() {
        ivDelClazz.setVisibility(View.VISIBLE);
        ObjectAnimator translationDown = ObjectAnimator.ofFloat(ivAddClazz, "translationY", 250f, 0);
        ObjectAnimator translationUp = ObjectAnimator.ofFloat(ivDelClazz, "translationY", 0, 250f);
        AnimatorSet set = new AnimatorSet();
        set.setDuration(300).play(translationDown).with(translationUp);
        set.start();
    }

    private void showSelect(boolean show) {
        if (show) {
            for (ClazzItemView view : clazzList) {
                view.setVisibility(true);
            }
        } else {
            for (ClazzItemView view : clazzList) {
                view.setVisibility(false);
            }
        }
        isShow = show;
    }

    @Override
    public void onBackPressed() {
        if (isShow) {
            initToolbarStatus();
            return;
        }
        saveClazz();
        super.onBackPressed();
    }

    @SuppressWarnings("unchecked")
    private void saveClazz() {
        //更新群课程信息
        HashMap<String, String> map = new HashMap<>();
        map.put("id", userId);
        BmobUtils.getInstance().querryGroup(map, new BmobCallbackImpl() {
            @Override
            public void success(Object object) {
                GroupBmob groupBmob = ((List<GroupBmob>) object).get(0);
                String objectId = groupBmob.getObjectId();
                StringBuilder buffer = new StringBuilder();
                for (int i = 0; i < clazzList.size(); i++) {
                    if (i == 0) {
                        buffer.append(clazzList.get(i).getText());
                        continue;
                    }
                    buffer.append("-").append(clazzList.get(i).getText());
                }
                groupBmob.setClazz(buffer.toString());
                BmobUtils.getInstance().update(groupBmob, objectId, new BmobCallbackImpl() {
                    @Override
                    public void success(Object object) {
                    }
                });
            }
        });
        //更新已考勤的历史记录
        if (isUpdate) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("groupId", userId);
            hashMap.put("clazz", oldClazz);
            BmobUtils.getInstance().querryRegisterHistory(hashMap, new BmobCallbackImpl() {
                @Override
                public void success(Object object) {
                    ArrayList<RegisterHistory> histories = (ArrayList<RegisterHistory>) object;
                    for (RegisterHistory history : histories) {
                        if (history.getClazz().equals(oldClazz)) {
                            history.setClazz(newClazz);
                        }
                    }
                    BmobUtils.getInstance().updateBatchData(histories, new BmobCallbackImpl() {
                        @Override
                        public void success(Object object) {
                            Log.e(TAG, "success: update history suces");
                        }
                    });
                }
            });
            BmobUtils.getInstance().querryRegisterInfo(hashMap, new BmobCallbackImpl() {
                @Override
                public void success(Object object) {
                    ArrayList<RegisterInfo> infos = (ArrayList<RegisterInfo>) object;
                    for (RegisterInfo info : infos) {
                        info.setClazz(newClazz);
                    }
                    BmobUtils.getInstance().updateBatchData(infos, new BmobCallbackImpl() {
                        @Override
                        public void success(Object object) {
                            Log.e(TAG, "success: 批量更新成功");
                        }
                    });
                }
            });
        }
    }

    private void refresh() {
        if (clazzList.size() > 0) {
            tvNoData.setVisibility(View.GONE);
        } else {
            toolbar.setRightText("更改")
                    .setLeftTextVisibility(View.GONE)
                    .setLeftIconVisibility(View.VISIBLE);
            hiddeDel();
            isShow = false;
            tvNoData.setVisibility(View.VISIBLE);
            return;
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void init() {
        userId = getIntent().getStringExtra(Constants.GROUP_ID);
        //获取已设定课程
        HashMap<String, String> map = new HashMap();
        map.put("id", userId);
        BmobUtils.getInstance().querryGroup(map, new BmobCallbackImpl() {
            @Override
            public void success(Object object) {
                GroupBmob groupBmob = ((List<GroupBmob>) object).get(0);
                final String clazz = groupBmob.getClazz();
                if (clazz != null && clazz.length() > 0) {
                    //已设定课程
                    String[] split = clazz.split("-");
                    if (split.length > 0) {
                        for (String s : split) {
                            final ClazzItemView clazzItemView = new ClazzItemView(ClazzActivity.this);
                            clazzItemView.setText(s);
                            clazzItemView.setCheck(false);
                            clazzList.add(clazzItemView);
                        }
                        refresh();
                    }
                } else {
                    //课程为空
                    tvNoData.setVisibility(View.VISIBLE);
                }
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = getAdapter(clazzList);
        recyclerView.setAdapter(adapter);
    }

    @OnClick({R.id.btn_exchange_cancel, R.id.btn_exchange_confirm, R.id.iv_add_clazz, R.id.iv_del_clazz})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_exchange_cancel:
                if (layoutExchange.getVisibility() == View.VISIBLE) {
                    layoutExchange.setVisibility(View.GONE);
                }
                break;
            case R.id.btn_exchange_confirm:
                clazzExchange();
                break;
            case R.id.iv_add_clazz:
                showAddClazzDialog();
                break;
            case R.id.iv_del_clazz:
                showDelClazzDialog();
                break;
        }
    }

    private void clazzExchange() {
        final ObjectAnimator rotate = ObjectAnimator.ofFloat(ivExchange, "rotation", 0f, 360f);
        rotate.setDuration(1000);
        rotate.start();
        rotate.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                oldClazz = etOldClazz.getText().toString().trim();
                newClazz = etNewClazz.getText().toString().trim();
                boolean success = false;
                for (int i = 0; i < clazzList.size(); i++) {
                    if (clazzList.get(i).getText().equals(oldClazz)) {
                        clazzList.get(i).setText(newClazz);
                        success = true;
                    }
                }
                if (success) {
                    Toast.makeText(ClazzActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                    isUpdate = true;
                    refresh();
                } else {
                    Toast.makeText(ClazzActivity.this, "更新失败", Toast.LENGTH_SHORT).show();
                }
                layoutExchange.setVisibility(View.GONE);
                if (rotate.isRunning()) {
                    rotate.cancel();
                }
            }
        });
    }

    private void showAddClazzDialog() {
        if (!isShow) {
            final EditText et = new EditText(ClazzActivity.this);
            new AlertDialog.Builder(ClazzActivity.this).setTitle("课程")
                    .setIcon(R.drawable.ic_group_func_clazz)
                    .setView(et)
                    .setPositiveButton("确定", (dialog, which) -> {
                        String input = et.getText().toString();
                        if (!input.equals("")) {
                            ClazzItemView clazzItemView = new ClazzItemView(ClazzActivity.this);
                            clazzItemView.setText(et.getText().toString().trim());
                            clazzItemView.setCheck(false);
                            clazzList.add(clazzItemView);
                            adapter.notifyItemInserted(clazzList.size());
                            if (tvNoData.isShown()) {
                                tvNoData.setVisibility(View.GONE);
                            }
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
        }
    }

    private void showDelClazzDialog() {
        final int[] n = {0};
        new AlertDialog.Builder(ClazzActivity.this)
                .setTitle("确认删除选定课程")
                .setPositiveButton("确定", (dialogInterface, index) -> {
                    for (int i = 0; i < clazzList.size(); i++) {
                        if (lastDel) {
                            i--;
                            lastDel = false;
                        }
                        if (i != -1) {
                            if (clazzList.get(i).isCheck()) {
                                n[0] = i;
                                clazzList.remove(i);
                                lastDel = true;
                            }
                        }
                    }
                    adapter.notifyItemRemoved(n[0]);
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private ClazzAadpter getAdapter(List<ClazzItemView> list) {
        return new ClazzAadpter(ClazzActivity.this, list)
                .setOnItemClickListener((view, position) -> {
                    LinearLayout linearLayout = (LinearLayout) view;
                    ClazzItemView v = (ClazzItemView) linearLayout.getChildAt(0);
                    v.setCheck(!v.isCheck());
                    if (isAllSelected()) {
                        toolbar.setRightText("全不选");
                        allSelected = true;
                    } else {
                        toolbar.setRightText("全选");
                        allSelected = false;
                    }
                }).setOnItemLongClickListener((view, position) -> {
                    if (!isShow) {
                        toolbar.setLeftText("取消");
                        toolbar.setRightText("全选");
                        toolbar.setLeftIconVisibility(View.GONE);
                        ivDelClazz.setVisibility(View.VISIBLE);
                        showSelect(true);
                        showDel();
                    }
                });
    }

    private void initToolbarStatus() {
        if (clazzList.size() == 0) {
            tvNoData.setVisibility(View.VISIBLE);
        }
        hiddeDel();
        showSelect(false);
        toolbar.setLeftTextVisibility(View.GONE)
                .setRightText("更改")
                .setLeftIconVisibility(View.VISIBLE);
    }

    @Override
    public String getToolbarTitle() {
        return "课程";
    }

    @Override
    public boolean isShowHome() {
        return false;
    }

    @Override
    public void initToolbar() {
        toolbar.setLeftIcon(R.drawable.ic_left_back)
                .setOnLeftIconClickEvent(v -> {
                    saveClazz();
                    finish();
                })
                .setOnLeftTextClickEvent(v -> initToolbarStatus())
                .setRightText("更改")
                .setOnRightTextClickEvent(v -> {
                    setRightClickEvent();
                });
    }

    private void setRightClickEvent() {
        if (toolbar.getRightText().equals("更改")) {
            layoutExchange.setVisibility(View.VISIBLE);
            etOldClazz.setText("");
            etNewClazz.setText("");
        } else {
            allSelected = allSelected ? changeAllItemStatus(false) : changeAllItemStatus(true);
        }
    }

    private boolean changeAllItemStatus(boolean allSelected) {
        for (ClazzItemView clazzItemView : clazzList) {
            clazzItemView.setCheck(allSelected);
        }
        toolbar.setRightText(allSelected ? "全不选" : "全选");
        return allSelected;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_clazz;
    }

}
