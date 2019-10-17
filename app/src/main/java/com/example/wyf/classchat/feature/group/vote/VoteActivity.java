package com.example.wyf.classchat.feature.group.vote;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.wyf.classchat.R;
import com.example.wyf.classchat.adapter.VoteAdapter;
import com.example.wyf.classchat.base.IAppInitContract;
import com.example.wyf.classchat.bean.MessageEvent;
import com.example.wyf.classchat.bean.VoteBean;
import com.example.wyf.classchat.bean.VoteResult;
import com.example.wyf.classchat.constants.Constants;
import com.example.wyf.classchat.util.ForbidFastClickUtils;
import com.example.wyf.classchat.weight.AutoToolbar;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


public class VoteActivity extends AppCompatActivity implements IAppInitContract.IActivity, IAppInitContract.IToolbar {

    @BindView(R.id.toolbar)
    AutoToolbar toolbar;
    @BindView(R.id.vote_recylcerview)
    RecyclerView mRecyclerView;

    private String userId;
    private List<VoteResult> datas;
    private String groupId;
    private ArrayList<String> adminList;
    private int REFRESH_GROUP_ADMIN = 151318;
    private String owner;
    private ArrayList<String> admin;
    private Boolean isAdmin = false;

    @Override
    public void init() {
        init();
        initToolbar();

        userId = EMClient.getInstance().getCurrentUser();
        groupId = getIntent().getStringExtra("groupId");
        adminList = getIntent().getStringArrayListExtra("adminList");
        if (adminList == null) {
            getGroupAdmin(groupId);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BmobQuery<VoteResult> resultQuery = new BmobQuery<VoteResult>();
                resultQuery.addWhereEqualTo("groupId", groupId);
                resultQuery.findObjects(new FindListener<VoteResult>() {
                    @Override
                    public void done(List<VoteResult> list, BmobException e) {
                        datas = list;
                        queryVoteBean();
                    }
                });
            }
        });
    }

    private void getGroupAdmin(final String userId) {
        new Thread() {
            @Override
            public void run() {
                EMGroup group = null;
                try {
                    group = EMClient.getInstance().groupManager().getGroupFromServer(userId);
                    owner = group.getOwner();
                    List<String> adminList = group.getAdminList();
                    adminList.add(owner);
                    EventBus.getDefault().post(new MessageEvent(REFRESH_GROUP_ADMIN, adminList));

                } catch (HyphenateException e) {
                    e.printStackTrace();
                }

                super.run();
            }
        }.start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
        if (event.getWhat() == Constants.REFRESH_GROUP_ADMIN) {
            admin = (ArrayList<String>) event.getMessage();
            for (int i = 0; i < admin.size(); i++) {
                adminList.add(admin.get(i));
            }
            ;
            Log.i("groupsetting adminList", adminList + "");
        }
    }

    @Override
    public String getToolbarTitle() {
        return "评优";
    }

    @Override
    public boolean isShowHome() {
        return true;
    }

    @Override
    public void initToolbar() {
        toolbar.setRightText("发起评优")
                .setOnRightTextClickEvent(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (adminList != null) {
                            for (int i = 0; i < adminList.size(); i++) {

                                if (adminList.get(i).equals(userId)) {
                                    isAdmin = true;


                                } else {
                                    Log.i("voteactivyt adminList", adminList + "");
                                    Log.i("voteactivyt userId", userId + "");
                        /*Toast.makeText(this, "对不起 只有管理才可以发起评优", Toast.LENGTH_SHORT).show();*/

                                }
                            }
                        } else {

                        }

                        openAcitivity();
                    }
                });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_vote;
    }

    private void queryVoteBean() {
        BmobQuery<VoteBean> query = new BmobQuery<VoteBean>();
        query.addWhereEqualTo("groupId", groupId);
        //执行查询方法
        query.findObjects(new FindListener<VoteBean>() {
            @Override
            public void done(List<VoteBean> object, BmobException e) {
                if (e == null) {
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(VoteActivity.this));
                    mRecyclerView.setAdapter(new VoteAdapter(VoteActivity.this, object, datas, groupId, userId, adminList));
                } else {
                    Log.i("bmob", "失败voteacitivity：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }


    private void openAcitivity() {
        if (ForbidFastClickUtils.isFastClick()) {
            if (isAdmin) {
                Intent intent = new Intent(VoteActivity.this, StartVoteActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("groupId", groupId);
                Log.e("voteactivyt", groupId);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "对不起 只有管理才可以发起评优", Toast.LENGTH_SHORT).show();
            }
        }


    }
}
