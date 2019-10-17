package com.example.wyf.classchat.feature.contact;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.wyf.classchat.R;
import com.example.wyf.classchat.adapter.common.RvAdapter;
import com.example.wyf.classchat.adapter.common.RvViewHolder;
import com.example.wyf.classchat.base.BaseFragment;
import com.example.wyf.classchat.bean.MessageEvent;
import com.example.wyf.classchat.bean.PersonBmob;
import com.example.wyf.classchat.constants.Constants;
import com.example.wyf.classchat.db.FileType;
import com.example.wyf.classchat.db.Person;
import com.example.wyf.classchat.db.Province;
import com.example.wyf.classchat.feature.contact.create.CreateGroupsActivity;
import com.example.wyf.classchat.feature.contact.detail.ContactInfoActivity;
import com.example.wyf.classchat.feature.contact.groups.GroupsActivity;
import com.example.wyf.classchat.feature.contact.manage.ManageGroupsActivity;
import com.example.wyf.classchat.util.BitmapUtils;
import com.example.wyf.classchat.util.HyphenateUtils;
import com.example.wyf.classchat.util.ForbidFastClickUtils;
import com.example.wyf.classchat.weight.AutoToolbar;
import com.github.promeg.pinyinhelper.Pinyin;
import com.gjiazhe.wavesidebar.WaveSideBar;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;

/**
 * @author WYF on 2017/9/26.
 */
public class ContactFragment extends BaseFragment implements ContactPageContract.View {

    @BindView(R.id.toolbar)
    AutoToolbar toolbar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.side_bar)
    WaveSideBar sideBar;

    private List<PersonBmob> contactList = new ArrayList<>();
    private RvAdapter<PersonBmob> adapter;
    private ContactPageContract.Presenter present;

    //更新联系人信息至最新
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshUserInfo(MessageEvent event) {
        if (event.getWhat() == Constants.REFRESH_CONTACT_NEW_INFO) {
            PersonBmob personBmob = (PersonBmob) event.getMessage();
            int index = contactList.indexOf(personBmob);
            if (index != -1) {
                adapter.notifyItemChanged(index);
            }
        }
    }

    //删除联系人时更新联系人列表
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshOnContactDel(MessageEvent event) {
        if (event.getWhat() == Constants.REFRESH_CONTACT_ON_DEL) {
            String id = ((String) event.getMessage());
            PersonBmob personBmob = new PersonBmob();
            personBmob.setId(id);
            int i = contactList.indexOf(personBmob);
            if (i != -1) {
                contactList.remove(i);
                refresh();
                //DatabaseUtils.findPerson(id).ic_delete();
            }
        }
    }

    /**
     * 设置联系人名称首字母并排序
     *
     * @param event 联系人信息
     */
    @SuppressWarnings("unchecked")
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void handleName(MessageEvent event) {
        if (event.getWhat() == Constants.GET_CONTACT_LIST_INFO) {
            contactList = (List<PersonBmob>) event.getMessage();
            sortContactList();
            addHeadToList();
        }
    }

    /**
     * 联系人排序
     */
    private void sortContactList() {
        for (PersonBmob p : contactList) {
            String firstSpell = Pinyin.toPinyin(p.getName(), "");
            String substring = firstSpell.substring(0, 1);
            if (substring.matches("[A-Z]")) {
                p.setLetter(substring);
            } else {
                p.setLetter("#");
            }
        }
        Collections.sort(contactList, (p1, p2) -> {
            //#标签的p1放后面
            if (!p1.getLetter().matches("[A-z]+")) {
                return 1;
                //#标签的p2放后面
            } else if (!p2.getLetter().matches("[A-z]+")) {
                return -1;
            } else {
                return p1.getLetter().compareTo(p2.getLetter());
            }
        });
    }

    /**
     * 弹出添加群对话框
     */
    private void showAddGroupDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        final View inflate = View.inflate(mActivity, R.layout.dialog_add_friend, null);
        TextView tv = inflate.findViewById(R.id.view);
        tv.setText("请输入你要添加的群号: ");
        builder.setView(inflate)
                .setPositiveButton("确定", (dialogInterface, i) -> {
                    EditText et = inflate.findViewById(R.id.et_add_friend);
                    present.addGroup(et.getText().toString().trim());
                })
                .setNegativeButton("取消", null)
                .show();
    }

    /**
     * 设置联系人详细信息和点击事件
     * @param holder
     * @param personBmob
     */
    private void setContactInfo(RvViewHolder holder, final PersonBmob personBmob) {
        if (personBmob.isShowLetter()) {
            holder.setText(R.id.tv_title, personBmob.getLetter());
            holder.getView(R.id.tv_title).setVisibility(View.VISIBLE);
        }
        BitmapUtils.setIcon(mActivity, holder.getView(R.id.iv_icon), FileType.CONTACT, personBmob.getId());
        holder.setText(R.id.tv_notice_author, personBmob.getName());
        holder.setOnClickListener(R.id.ll_contact, view -> {
            if (ForbidFastClickUtils.isFastClick()) {
                startActivity(new Intent(mActivity, ContactInfoActivity.class)
                        .putExtra("userId", personBmob.getId()));
            }
        });
    }

    /**
     * 设置固定条目点击事件
     * @param holder
     * @param position
     * @param personBmob
     */
    private void setHeaderClickEvent(RvViewHolder holder, int position, PersonBmob personBmob) {
        holder.setText(R.id.tv_notice_author, personBmob.getName());
        switch (position) {
            case 0: //创建群
                holder.setImageResource(R.id.iv_icon, R.drawable.ic_group_create);
                holder.setOnClickListener(R.id.ll_contact, view -> startActivity(new Intent(mActivity, CreateGroupsActivity.class)));
                break;
            case 1: //加入群
                holder.setImageResource(R.id.iv_icon, R.drawable.ic_group_add);
                holder.setOnClickListener(R.id.ll_contact, view -> showAddGroupDialog());
                break;
            case 2: //管理群
                holder.setImageResource(R.id.iv_icon, R.drawable.ic_group_manage);
                holder.setOnClickListener(R.id.ll_contact, view -> startActivity(new Intent(mActivity, ManageGroupsActivity.class)));
                break;
            case 3: //已入群
                holder.setImageResource(R.id.iv_icon, R.drawable.ic_group_added);
                holder.setOnClickListener(R.id.ll_contact, view -> startActivity(new Intent(mActivity, GroupsActivity.class)));
                break;
        }
    }

    @NonNull
    private RvAdapter<PersonBmob> getAdapter() {
        return new RvAdapter<PersonBmob>(mActivity, R.layout.fragment_contact_item, contactList) {
            @Override
            public void convert(RvViewHolder holder, int position, PersonBmob personBmob) {
                holder.getView(R.id.tv_title).setVisibility(View.GONE);
                holder.getView(R.id.ll_contact).setBackgroundResource(R.drawable.sel_bg_me_menu);
                if (position < getResources().getStringArray(R.array.arr_contact_head).length) {
                    setHeaderClickEvent(holder, position, personBmob);
                } else {
                    setContactInfo(holder, personBmob);
                }
            }
        };
    }

    /**
     * 添加固定条目
     */
    private void addHeadToList() {
        String[] arr = getResources().getStringArray(R.array.arr_contact_head);
        for (int i = arr.length - 1; i >= 0; i--) {
            PersonBmob p = new PersonBmob(arr[i]);
            contactList.add(0, p);
        }
        for (int i = 0; i < contactList.size(); i++) {
            PersonBmob p = contactList.get(i);
            if (i < arr.length) {
                p.setShowLetter(false);
                continue;
            }
            if (!p.getLetter().equals(contactList.get(i - 1).getLetter())) {
                p.setShowLetter(true);
            }
        }
        refresh();
    }

    private void refresh() {
        HyphenateUtils.refresh(mActivity, getAdapter(), recyclerView);
    }

    @Override
    public void loadFriendListSuccess(List<String> list) {
        if (list != null) {
            if (list.size() > 0) {
                present.getFriendInfo(list);
            } else {
                addHeadToList();
            }
        }
    }

    @Override
    public void initData() {
        setPresent(new ContactPagePresenter(mActivity, this));
        //获取好友
        Log.e("test", "initData: "+new Select().from(Person.class).queryList().size());
        present.getFriendList();
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        adapter = getAdapter();
        recyclerView.setItemAnimator(null);
        recyclerView.setAdapter(adapter);
        String[] letterArr = getActivity().getResources().getStringArray(R.array.arrays_letter);
        sideBar.setIndexItems(letterArr);
        sideBar.setOnSelectIndexItemListener(index -> {
            for (int i = 0; i < contactList.size(); i++) {
                if (index.equals(contactList.get(i).getLetter())) {
                    recyclerView.scrollToPosition(i);
                    return;
                } else if ("☆".equals(index)) {
                    recyclerView.scrollToPosition(0);
                    return;
                }
            }
        });
    }

    @Override
    public void initToolbar() {
        toolbar.setTitleText("联系人")
                .setRightIcon(R.drawable.ic_add_friend)
                .setOnRightIconClickEvent(v -> showAddFriendDialog());
    }

    private void showAddFriendDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        final View view = View.inflate(mActivity, R.layout.dialog_add_friend, null);
        TextView tv = view.findViewById(R.id.view);
        tv.setText("请输入你要添加的学号: ");
        builder.setView(view)
                .setPositiveButton("确定", (dialogInterface, i) -> {
                    EditText etId = view.findViewById(R.id.et_add_friend);
                    present.addFriend(etId.getText().toString().trim(), contactList);
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_contact;
    }

    @Override
    public boolean needEventBus() {
        return true;
    }

    @Override
    public void setPresent(ContactPageContract.Presenter present) {
        this.present = present;
    }
}
