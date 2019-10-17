package com.example.wyf.classchat.feature.contact.manage;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.wyf.classchat.R;
import com.example.wyf.classchat.adapter.common.RvAdapter;
import com.example.wyf.classchat.adapter.common.RvViewHolder;
import com.example.wyf.classchat.base.IAppInitContract;
import com.example.wyf.classchat.bean.MessageEvent;
import com.example.wyf.classchat.constants.Constants;
import com.example.wyf.classchat.db.FileType;
import com.example.wyf.classchat.db.Group;
import com.example.wyf.classchat.feature.message.chat.ChatActivity;
import com.example.wyf.classchat.util.BitmapUtils;
import com.example.wyf.classchat.util.HyphenateUtils;
import com.example.wyf.classchat.util.ForbidFastClickUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @author WYF on 2017/10/5.
 */
public class ManageGroupsActivity extends AppCompatActivity implements IAppInitContract.IActivity, IAppInitContract.IToolbar,
        IAppInitContract.ISubscribe, ManageGroupsContract.View {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private List<Group> groupList = new ArrayList<>();
    private RvAdapter<Group> adapter;
    private ManageGroupsContract.Presenter present;
    private long lastIconLength;
    private int lastPosition;
    private ImageView lastImageView;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshIcon(MessageEvent event) {
        if (event.getWhat() == Constants.REFRESH_GROUP_ICON) {
            int i = groupList.indexOf(new Group((String) event.getMessage()));
            if (i != -1) {
                adapter.notifyItemChanged(i);
            }
        }
    }

    @Override
    protected void onStart() {
        if (groupList.size() > 0) {
            File file = new File(BitmapUtils.getPath(FileType.GROUP, groupList.get(lastPosition).getId(), groupList.get(lastPosition).getId()));
            if (file.exists() && file.length() != lastIconLength) {
                lastImageView.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
            }
            adapter.notifyItemChanged(lastPosition);
        }
        super.onStart();
    }

    @Override
    public void refreshManageGroupList(List<Group> list) {
        this.groupList = list;
        HyphenateUtils.refresh(this, getAdapter(), recyclerView);
    }

    @Override
    public void init() {
        setPresent(new ManageGroupsPresenter(this, this));
        adapter = getAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(null);
        present.getManageGroupList();
    }

    @NonNull
    private RvAdapter<Group> getAdapter() {
        return new RvAdapter<Group>(this, R.layout.activity_groups_item, groupList) {
            @Override
            public void convert(RvViewHolder holder, int position, final Group group) {
                ImageView iv = holder.getView(R.id.group_icon);
                File file = new File(BitmapUtils.getPath(FileType.GROUP, group.getId(), group.getId()));
                if (file.exists()) {
                    Glide.with(ManageGroupsActivity.this).load(file).into(iv);
                } else {
                    present.getGroupIconFromServer(holder, group);
                }
                holder.setText(R.id.group_name, group.getName());
                holder.setText(R.id.group_desc, group.getDesc());
                holder.setOnClickListener(R.id.group_layout, view -> {
                    if (ForbidFastClickUtils.isFastClick()) {
                        lastIconLength = file.length();
                        lastPosition = position;
                        lastImageView = iv;
                        startActivity(new Intent(ManageGroupsActivity.this, ChatActivity.class)
                                .putExtra("userId", group.getId())
                                .putExtra("type", Constants.CHATTYPE_GROUP));
                    }
                });
            }
        };
    }

    @Override
    public void initToolbar() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_manage_groups;
    }

    @Override
    public String getToolbarTitle() {
        return "我管理的群";
    }

    @Override
    public boolean isShowHome() {
        return true;
    }

    @Override
    public void setPresent(ManageGroupsContract.Presenter present) {
        this.present = present;
    }
}
