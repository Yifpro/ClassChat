package com.example.wyf.classchat.feature.contact.groups;

import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;

import com.example.wyf.classchat.R;
import com.example.wyf.classchat.adapter.common.RvAdapter;
import com.example.wyf.classchat.adapter.common.RvViewHolder;
import com.example.wyf.classchat.base.IAppInitContract;
import com.example.wyf.classchat.bean.MessageEvent;
import com.example.wyf.classchat.constants.Constants;
import com.example.wyf.classchat.db.FileType;
import com.example.wyf.classchat.db.Group;
import com.example.wyf.classchat.util.BitmapUtils;
import com.example.wyf.classchat.util.HyphenateUtils;
import com.example.wyf.classchat.util.ForbidFastClickUtils;
import com.hyphenate.chat.EMClient;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by WYF on 2017/10/4.
 */

public class GroupsActivity extends AppCompatActivity implements IAppInitContract.IActivity, IAppInitContract.
        IToolbar, IAppInitContract.ISubscribe, GroupsPageContract.View {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private List<Group> groupList = new ArrayList<>();
    private RvAdapter<Group> adapter;
    private GroupsPageContract.Presenter present;
    private int lastPosition;
    private ImageView lastImageView;
    private long lastIconLength;

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
                Log.e("test", "onStart: will set");
                lastImageView.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
            }
            adapter.notifyItemChanged(lastPosition);
        }
        super.onStart();
    }

    @NonNull
    private RvAdapter<Group> getAdapter() {
        return new RvAdapter<Group>(GroupsActivity.this, R.layout.activity_groups_item, groupList) {
            @Override
            public void convert(RvViewHolder holder, final int position, final Group group) {
                ImageView ivGroupIcon = holder.getView(R.id.group_icon);
                File file = new File(BitmapUtils.getPath(FileType.GROUP, group.getId(), group.getId()));
                if (file.exists()) {
                    ivGroupIcon.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
                } else {
                    present.getGroupIconFromServer(adapter, holder, group);
                }
                holder.setText(R.id.group_name, group.getName());
                holder.setText(R.id.group_desc, group.getDesc());
                //得到和保存进入群会话界面的时间
                holder.setOnClickListener(R.id.group_layout, view -> {
                    lastIconLength = file.length();
                    lastPosition = position;
                    lastImageView = ivGroupIcon;
                    if (ForbidFastClickUtils.isFastClick()) {
                        final String sendUser = EMClient.getInstance().getCurrentUser();
                        //当前用户进入群的时间保存到Bmob，用于是否弹出公告
                        //修改当前网络时间
                        present.getNetTime(sendUser, group.getId());
                    }

                });
            }
        };
    }

    @Override
    public void refreshGroupList(List<Group> list) {
        groupList = list;
        HyphenateUtils.refresh(this, getAdapter(), recyclerView);
    }

    @Override
    public void init() {
        setPresent(new GroupsPagePresenter(this, this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(null);
        adapter = getAdapter();
        recyclerView.setAdapter(adapter);
        //获取群列表
        present.getGroupsList();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_groups;
    }

    @Override
    public void initToolbar() {

    }

    @Override
    public String getToolbarTitle() {
        return "已加入的群";
    }

    @Override
    public boolean isShowHome() {
        return true;
    }

    @Override
    public void setPresent(GroupsPageContract.Presenter present) {
        this.present = present;
    }
}
