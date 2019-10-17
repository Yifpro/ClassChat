package com.example.wyf.classchat.feature.group.notice;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wyf.classchat.R;
import com.example.wyf.classchat.adapter.NoticeRecycleAdapter;
import com.example.wyf.classchat.base.IAppInitContract;
import com.example.wyf.classchat.bean.NoticeBean;
import com.example.wyf.classchat.bean.ReaderBean;
import com.example.wyf.classchat.listener.BmobCallbackImpl;
import com.example.wyf.classchat.util.BmobUtils;
import com.example.wyf.classchat.util.ForbidFastClickUtils;
import com.example.wyf.classchat.weight.AutoToolbar;
import com.hyphenate.chat.EMClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class NoticeActivity extends AppCompatActivity implements IAppInitContract.IActivity, IAppInitContract.IToolbar {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.toolbar)
    AutoToolbar toolbar;
    @BindView(R.id.rv_notice)
    RecyclerView rvNotice;
    @BindView(R.id.sf_up)
    SwipeRefreshLayout mSfLayout;

    private final static String NOTICE_FRAGMENT = "notice_fragment";
    private final static int NOTICE_QUERRY_SUCESS = 10010;
    private final static String NOTICE_FRAGMENT_SEE = "notice_fragment_see";
    private NoticeFragment noticeFragment;
    private String groupId;
    private NoticeSeeFragment Seefragment;
    private ArrayList<ArrayList<ReaderBean>> lists;
    private NoticeRecycleAdapter adapter;
    private String mUserId;
    private ArrayList<NoticeBean> mNoticeDatas;
    private ArrayList<String> mAdminList;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NOTICE_QUERRY_SUCESS:
                    //本群所有公告
                    List<NoticeBean> datas = (List<NoticeBean>) msg.obj;


                    mNoticeDatas = new ArrayList<>();


                    for (NoticeBean noticeBean : datas) {

                        if (noticeBean.getRederStr() != null) {

                            if (noticeBean.getRederStr().contains(mUserId) || noticeBean.getUser().equals(mUserId)) {
                                mNoticeDatas.add(noticeBean);
                            }
                        }
                    }
                    fillRecycleData(mNoticeDatas);
                    break;
            }
        }
    };

    private void fillRecycleData(final List<NoticeBean> mNoticeDatas) {
        mSfLayout.setRefreshing(false);
        lists = new ArrayList<>();
        rvNotice.setLayoutManager(new LinearLayoutManager(this));

        Collections.reverse(mNoticeDatas);

        adapter = new NoticeRecycleAdapter(this, mNoticeDatas, lists);

        adapter.setOnItemClickListener(new NoticeRecycleAdapter.onItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if (ForbidFastClickUtils.isFastClick()) {
                    //公告的objectId
                    NoticeBean noticeBean = mNoticeDatas.get(position);
                    final String noticeObjectId = noticeBean.getObjectId();

                    //查看此用户是否已阅读此公告
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("userId", mUserId);
                    params.put("noticeObjectId", noticeObjectId);
                    BmobUtils.getInstance().querryReader(params, 1, new BmobCallbackImpl() {
                        @Override
                        public void success(Object object) {

                        }

                        @Override
                        public void fail() {
                            //此用户没有阅读过公告，就会走此回调
//                        //保存看公告者的数据
                            ReaderBean bean = new ReaderBean(mUserId, noticeObjectId);
                            BmobUtils.getInstance().saveData(bean, new BmobCallbackImpl() {
                                @Override
                                public void success(Object object) {

                                }
                            });
                        }
                    });

                    //开始跳NoticeSeeFragment
                    FragmentManager fm = getSupportFragmentManager();

                    FragmentTransaction ft = fm.beginTransaction();

                    Seefragment = (NoticeSeeFragment) fm.findFragmentByTag(NOTICE_FRAGMENT_SEE);

                    if (Seefragment == null) {
                        Seefragment = new NoticeSeeFragment();

                        Seefragment.setDatas(fm, groupId, noticeBean, mUserId, mAdminList);

                        ft.replace(R.id.fl_container, Seefragment, NOTICE_FRAGMENT_SEE);

                    } else {

                        Seefragment.refreshData(fm, groupId, noticeBean, mUserId, mAdminList);

                        ft.show(Seefragment);

                    }
                    ft.commit();

                }
            }
        });

        rvNotice.setAdapter(adapter);
    }


    //将观看用户数据进行分类
    private void dealData(List<NoticeBean> mNoticeDatas, List<ReaderBean> object) {

        for (NoticeBean data : mNoticeDatas) {
            ArrayList<ReaderBean> baens = new ArrayList<>();
            String objectId = data.getObjectId();
            for (ReaderBean readerBean : object) {
                if (objectId.equals(readerBean.getNoticeObjectId())) {
                    baens.add(readerBean);
                }
            }
            lists.add(baens);
        }


    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        NoticeSeeFragment fragmentSee = (NoticeSeeFragment) fm.findFragmentByTag(NOTICE_FRAGMENT_SEE);
        if (fragmentSee != null && fragmentSee.isVisible()) {
            refreshData();
            fragmentSee.hideCurrentFragment("");
            return;
        }
        NoticeFragment noticeFragment = (NoticeFragment) fm.findFragmentByTag(NOTICE_FRAGMENT);
        if (noticeFragment != null && noticeFragment.isVisible()) {
            noticeFragment.hideCurrentFragment();
//            noticeFragment.clearData();
            refreshData();
            return;
        }
        super.onBackPressed();
    }


    @Override
    public int getLayoutId() {

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return R.layout.activity_notice;
    }

    @Override
    public void init() {
        mSfLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                //刷新数据
                getNoticeData();
//                refreshData();

            }
        });
        initToolbar();

        //当前用户id
        mUserId = EMClient.getInstance().getCurrentUser();
        groupId = getIntent().getStringExtra("userId");
        //管理员和群主
        mAdminList = getIntent().getStringArrayListExtra("adminList");

        //去bmob拉取公告数据
        getNoticeData();

    }

    private void getNoticeData() {
        Map<String, String> param = new HashMap<>();
        param.put("groupId", groupId);
        BmobUtils.getInstance().querryNoticeData(param, 500, new BmobCallbackImpl() {
            @Override
            public void success(Object object) {
                List<NoticeBean> list = (List<NoticeBean>) object;

                Message message = new Message();

                message.what = NOTICE_QUERRY_SUCESS;

                message.obj = object;

                handler.sendMessage(message);

                //查看有多少人阅读公告
                BmobUtils.getInstance().querryCount(new BmobCallbackImpl() {
                    @Override
                    public void success(Object object) {
                        List<ReaderBean> list = (List<ReaderBean>) object;
                        refreshRecycleData(list);
                    }
                });
            }
        });
    }


    private void refreshRecycleData(List<ReaderBean> object) {
        if (object == null && object.size() > 0) {
            return;
        }
        dealData(mNoticeDatas, object);
        if (adapter != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        }

    }

    public void refreshData() {
        init();

    }

    @Override
    public void initToolbar() {
        //点加号
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (ForbidFastClickUtils.isFastClick()) {
                    if (mAdminList.contains(mUserId)) {

                        //z准备跳到NoticeFragment
                        FragmentManager fm = getSupportFragmentManager();

                        FragmentTransaction ft = fm.beginTransaction();
                        Toast.makeText(NoticeActivity.this, "发布公告", Toast.LENGTH_SHORT).show();

                        noticeFragment = (NoticeFragment) fm.findFragmentByTag(NOTICE_FRAGMENT);


                        if (noticeFragment == null) {
                            //创建
                            noticeFragment = new NoticeFragment();
                            noticeFragment.setDatas(fm, groupId);
                            //跳到NoticeFragment
                            ft.replace(R.id.fl_container, noticeFragment, NOTICE_FRAGMENT);

                        } else {
                            //跳到NoticeFragment
                            ft.show(noticeFragment);
                        }
                        ft.commit();
                    } else {
                        Toast.makeText(NoticeActivity.this, "你不是管理员，不能发公告", Toast.LENGTH_SHORT).show();
                    }

                }
                return true;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_notice, menu);
        return true;
    }

    @Override
    public String getToolbarTitle() {
        return "圈公告";
    }

    @Override
    public boolean isShowHome() {
        return true;
    }
}
