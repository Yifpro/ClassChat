package com.example.wyf.classchat.feature.group;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.wyf.classchat.R;
import com.example.wyf.classchat.adapter.common.RvAdapter;
import com.example.wyf.classchat.adapter.common.RvViewHolder;
import com.example.wyf.classchat.base.IAppInitContract;
import com.example.wyf.classchat.bean.MessageEvent;
import com.example.wyf.classchat.bean.PersonBmob;
import com.example.wyf.classchat.constants.Constants;
import com.example.wyf.classchat.db.FileType;
import com.example.wyf.classchat.util.BitmapUtils;
import com.example.wyf.classchat.util.HyphenateUtils;
import com.example.wyf.classchat.util.RxSchedulersHelper;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by WYF on 2017/10/26.
 */

public class GroupMembersActivity extends AppCompatActivity implements GroupMembersContract.View, IAppInitContract.IActivity,
        IAppInitContract.IToolbar, IAppInitContract.ISubscribe {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private GroupMembersContract.Presenter present;
    private List<PersonBmob> groupMemberList = new ArrayList<>();
    private ArrayList<String> adminList;
    private String ownerId;

    @SuppressWarnings("unchecked")
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onMessageEvent(MessageEvent event) {
        if (event.getWhat() == Constants.GET_MEMBERS_INFO) {
            rx.Observable.just(sort((List<PersonBmob>) event.getMessage()))
                    .compose(RxSchedulersHelper.ioToMain())
                    .subscribe(s -> refresh());
        }
    }

    private String sort(List<PersonBmob> list) {
        PersonBmob p1 = new PersonBmob();
        p1.setId(ownerId);
        groupMemberList.add(list.remove(list.indexOf(p1)));
        PersonBmob p2;
        for (String admin : adminList) {
            p2 = new PersonBmob();
            p2.setId(admin);
            groupMemberList.add(list.remove(list.indexOf(p2)));
        }
        groupMemberList.addAll(list);
        return null;
    }

    private void refresh() {
        HyphenateUtils.refresh(this, getAdapter(), recyclerView);
    }

    private RvAdapter getAdapter() {
        return new RvAdapter<PersonBmob>(this, R.layout.activity_member_item, groupMemberList) {
            @Override
            public void convert(RvViewHolder holder, int position, PersonBmob personBmob) {
                BitmapUtils.setIcon(GroupMembersActivity.this, holder.getView(R.id.iv_icon), FileType.CONTACT, personBmob.getId());
                holder.setText(R.id.tv_member_name, personBmob.getName());
                if (personBmob.getId().equals(ownerId)) {
                    holder.getView(R.id.tv_owner).setVisibility(View.VISIBLE);
                }
                if ( adminList.contains(personBmob.getId())) {
                    holder.getView(R.id.tv_manager).setVisibility(View.VISIBLE);
                }
            }
        };
    }

    String TAG = "testMembers";

    @Override
    public void init() {
        setPresent(new GroupMembersPresenter(this, this));

        String groupId = getIntent().getStringExtra(Constants.GROUP_ID);
        adminList = getIntent().getStringArrayListExtra(Constants.GROUP_ADMIN_LIST);
        ownerId = adminList.remove(0);
        recyclerView.setAdapter(getAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //获取群员
        present.getMembers(groupId, ownerId, adminList);
    }

    @Override
    public String getToolbarTitle() {
        return "群成员";
    }

    @Override
    public boolean isShowHome() {
        return true;
    }

    @Override
    public void initToolbar() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_member_list;
    }

    @Override
    public void setPresent(GroupMembersContract.Presenter present) {
        this.present = present;
    }
}
