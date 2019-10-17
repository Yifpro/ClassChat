package com.example.wyf.classchat.feature.group.notice;

import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wyf.classchat.R;
import com.example.wyf.classchat.adapter.FileAdapter;
import com.example.wyf.classchat.adapter.RecycleFileAdapter;
import com.example.wyf.classchat.adapter.common.RvAdapter;
import com.example.wyf.classchat.adapter.common.RvViewHolder;
import com.example.wyf.classchat.adapter.decoration.SimpleDividerDecoration;
import com.example.wyf.classchat.base.BaseFragment;
import com.example.wyf.classchat.bean.MessageEvent;
import com.example.wyf.classchat.bean.NoticeBean;
import com.example.wyf.classchat.bean.NoticeDiscussBean;
import com.example.wyf.classchat.bean.ReaderBean;
import com.example.wyf.classchat.constants.Constants;
import com.example.wyf.classchat.file.bean.FileInfo;
import com.example.wyf.classchat.listener.BmobCallbackImpl;
import com.example.wyf.classchat.util.BmobUtils;
import com.example.wyf.classchat.util.HyphenateUtils;
import com.example.wyf.classchat.util.ForbidFastClickUtils;
import com.example.wyf.classchat.weight.AutoToolbar;
import com.example.wyf.classchat.weight.NoScrollViewPager;
import com.hyphenate.chat.EMClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;

/**
 * Created by Administrator on 2017/10/9.
 * 此Fragment用于展示公告的详细信息
 */

public class NoticeSeeFragment extends BaseFragment implements View.OnClickListener {

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
    @BindView(R.id.tv_anoce_title)
    TextView tvAnoceTitle;
    @BindView(R.id.tv_notice_author)
    TextView tvName;
    @BindView(R.id.tv_notice_time)
    TextView tvTime;
    @BindView(R.id.tv_notice_read_count)
    TextView tvGroupCount;
    @BindView(R.id.tv_notice_content)
    TextView tvContent;
    @BindView(R.id.rv_file)
    RecyclerView rvFile;
    @BindView(R.id.rv_discuss)
    RecyclerView rvDiscuss;
    @BindView(R.id.et_rollcall_matching_code)
    EditText etContent;
    @BindView(R.id.btn_discuss_send)
    Button btnDiscussSend;
    @BindView(R.id.btn_upload_file)
    Button btnUploadFile;
    @BindView(R.id.ll_notice_discuss)
    LinearLayout llNoticeDiscuss;
    @BindView(R.id.ll_rootView)
    LinearLayout llRootView;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    NoScrollViewPager viewPager;
    @BindView(R.id.iv_toum)
    ImageView ivToum;
    @BindView(R.id.tv_download)
    TextView tvDownload;
    @BindView(R.id.tv_cancle)
    TextView tvCancle;
    @BindView(R.id.ll_download)
    LinearLayout llDownload;

    private final static String NOTICE_FRAGMENT = "notice_fragment";
    private static final int TAG_FILE_DATA = 12345;
    private FragmentManager fm;
    private String groupId;
    private NoticeBean bean;
    private NoticeFragment noticeFragment;
    private String userId;
    private String noticeId;
    private ArrayList<String> mAdminList;
    private RvAdapter<NoticeDiscussBean> mAdapter;
    private ArrayList<NoticeDiscussBean> mDiscussBeen;
    private NoticeActivity mNoticeActiviry;
    private List<FileInfo> fileInfos = new ArrayList<>();
    private RecycleFileAdapter mFileAdapter;
    private int mCurPos;
    private String mSendUser;
    private View icFile;

    @Override
    public void initData() {
        mSendUser = EMClient.getInstance().getCurrentUser();
        tvAnoceTitle.setText(bean.getTitle());
        tvName.setText(bean.getUser());
        tvTime.setText(bean.getTime());
        tvContent.setText(bean.getContent());
        //一进入此Fragment，就去获取一遍此公告的讨论区数据
        initDiscussData();
        querryFileData();
    }

    //初始化讨论区的数据
    private void initDiscussData() {
        mNoticeActiviry = (NoticeActivity) getActivity();
        HashMap<String, String> params = new HashMap<>();
        params.put("groupId", groupId);
        params.put("NoticeId", noticeId);
        //从bmob获取当前公告的讨论数据
        BmobUtils.getInstance().querryNoticeDiscussData(params, 500, new BmobCallbackImpl() {
            @Override
            public void success(final Object object) {
                //获取成功之后，就将数据展示到RecycleView上
                final ArrayList<NoticeDiscussBean> beans = (ArrayList<NoticeDiscussBean>) object;

                mNoticeActiviry.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //展示公告讨论区数据

                        if (mAdapter == null) {
                            mDiscussBeen = new ArrayList<>();
                            mDiscussBeen.addAll(beans);
                            rvDiscuss.setLayoutManager(new LinearLayoutManager(mNoticeActiviry));

                            //创建。，并填充数据讨论区的RecycleVIew
                            mAdapter = getAdapter(mDiscussBeen, mNoticeActiviry);

                        } else {

                            mDiscussBeen.clear();
                            mDiscussBeen.addAll(beans);

                        }
                        rvDiscuss.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void fail() {
                if (mDiscussBeen != null) {
                    mDiscussBeen.clear();
                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }
                }
                if (mAdapter == null) {
                    mDiscussBeen = new ArrayList<>();
                    rvDiscuss.setLayoutManager(new LinearLayoutManager(mNoticeActiviry));
                    //创建。，并填充数据讨论区的RecycleVIew
                    mAdapter = getAdapter(mDiscussBeen, mNoticeActiviry);
                    rvDiscuss.setAdapter(mAdapter);

                }
            }
        });

    }

    @NonNull
    private RvAdapter<NoticeDiscussBean> getAdapter(final List<NoticeDiscussBean> discussBeen, NoticeActivity noticeActiviry) {

        return new RvAdapter<NoticeDiscussBean>(noticeActiviry, R.layout.fragment_notice_see_dis_item, discussBeen) {
            @Override
            public void convert(RvViewHolder holder, int position, NoticeDiscussBean notice) {

                holder.setText(R.id.tv_discuss_name, notice.getUserId() + " :");
                holder.setText(R.id.tv_discuss_content, notice.getContetn());
            }
        };
    }

    public void setDatas(FragmentManager fm, String groupId, NoticeBean noticeBean, String userId, ArrayList<String> adminList) {
        this.fm = fm;
        this.groupId = groupId;
        this.bean = noticeBean;
        this.userId = userId;
        this.noticeId = noticeBean.getObjectId();
        mAdminList = adminList;

        //每次进此页面都要查询公告文件
        querryFileData();
    }

    private void querryFileData() {

        Map<String, String> param = new HashMap<>();
        param.put("groupId", groupId);
        param.put("noticeId", noticeId);
        //到Bmob获取公告文件
        BmobUtils.getInstance().querryFileInfo(param, 500, new BmobCallbackImpl() {
            @Override
            public void success(Object object) {
                //获取公告文件数据成功，就将数据发到主线程，用于更新UI
                EventBus.getDefault().post(new MessageEvent(TAG_FILE_DATA, object));
            }

            @Override
            public void fail() {
                //获取公告文件数据失败，也要跳回UI线程做相对应的操作
                EventBus.getDefault().post(new MessageEvent(TAG_FILE_DATA, null));
            }
        });

    }

    public void refreshData(FragmentManager fm, String groupId, NoticeBean noticeBean, String userId, ArrayList<String> adminList) {
        this.fm = fm;
        this.groupId = groupId;
        this.bean = noticeBean;
        this.userId = userId;
        this.noticeId = noticeBean.getObjectId();
        this.mAdminList = adminList;
        initData();
    }

    //此方法运行在主线程
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainThread(MessageEvent event) {
        if (event.getWhat() == TAG_FILE_DATA) {
            fileInfos.clear();
            if (event.getMessage() != null) {

                fileInfos.addAll((List<FileInfo>) event.getMessage());
            }
            //把此公告的文件数据展示到相对应的RecycleView上
            fillMRvFileData(fileInfos);

        } else if (event.getWhat() == Constants.REFRESH_NOTICE_SEE_FILE_LIST) {
            //从FileFragment页面返回后，走这里
            hideRootView(false);
            //重新获取此公告的文件数据，
            querryFileData();
        } else if (event.getWhat() == Constants.NOTICE_DELETE_DONE) {
            //公告已经被删除
            Toast.makeText(mNoticeActiviry, "文件上传失败", Toast.LENGTH_SHORT).show();
            hideCurrentFragment("Error");

        }

    }

    //把此公告的文件数据展示到相对应的RecycleView上
    private void fillMRvFileData(List<FileInfo> fileInfos) {

        if (mFileAdapter == null) {

            mFileAdapter = new RecycleFileAdapter(getActivity(), fileInfos);

            rvFile.addItemDecoration(new SimpleDividerDecoration(getActivity(), 1));
        } else {
            mFileAdapter.notifyDataSetChanged();
        }

        rvFile.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvFile.setAdapter(mFileAdapter);

        //点击RecycleView的文件展示item，就弹出文件下载布局
        mFileAdapter.setOnItemClickListener(new RecycleFileAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                mCurPos = position;
                llDownload.setVisibility(View.VISIBLE);
            }
        });

    }


    @Override
    public void initView() {
        icFile = (View) mView.findViewById(R.id.ic_file);
        mView.findViewById(R.id.tv_download).setOnClickListener(this);
        mView.findViewById(R.id.tv_cancle).setOnClickListener(this);
        mView.findViewById(R.id.iv_toum).setOnClickListener(this);
        btnDiscussSend.setOnClickListener(this);
        btnUploadFile.setOnClickListener(this);
        initFileViews();
        initToolbarView();
    }

    private void initFileViews() {

    }

    @Override
    public String getTitleText() {
        return "圈公告";
    }

    @Override
    public void initToolbar() {
        toolbar.setRightIcon(R.drawable.ic_delete)
                .setLeftIcon(R.drawable.ic_left_back);

    }

    private void initToolbarView() {
        toolbar.inflateMenu(R.menu.notice_see_menu);
        toolbar.setLeftIcon(R.drawable.ic_left_back)
                .setOnLeftIconClickEvent(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NoticeActivity activity = (NoticeActivity) getActivity();
                        activity.refreshData();
                        hideCurrentFragment("");
                    }
                });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.iv_right:
                        if (mAdminList.contains(mSendUser)) {
                            if (ForbidFastClickUtils.isFastClick()) {
                                //删除公告
                                new Thread() {
                                    @Override
                                    public void run() {
                                        //处理删除公告的逻辑
                                        deleteNotice();
                                    }
                                }.start();
                            }
                        } else {
                            Toast.makeText(mNoticeActiviry, "你不是管理员不能删除公告", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.iv_right_t:
                        //编辑
                        editNotice();
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public boolean needEventBus() {
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_upload_file:
                //文件上传

                ///// TODO: 2017/11/6 文件上传
                //跳到文件检索Fragment，
                upLoadFile();

                hideRootView(true);
                break;
            case R.id.btn_discuss_send:
                //公告讨论
                //将发送的讨论数据保存
                if (ForbidFastClickUtils.isFastClick()) {
                    saveDiscussData();
                } else {
                    Toast.makeText(mNoticeActiviry, "说话太快,请稍后", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_download:
                if (ForbidFastClickUtils.isFastClick()) {
                    //文件下载
                    if (fileInfos.size() > 0) {
                        llDownload.setVisibility(View.GONE);
                        //填充要下载的文件数据
                        BmobFile file = fileInfos.get(mCurPos).getFile();
                        String path = Environment.getExternalStorageDirectory() + "/bmob/" + fileInfos.get(mCurPos).getName();
                        //判断文件是否已经下载过，是 则预览，否 则下载后再预览
                        boolean isExists = HyphenateUtils.fileIsExists(path);
                        if (!isExists) {
//                     /文件不存在，就下载
                            Toast.makeText(mNoticeActiviry, "开始下载", Toast.LENGTH_SHORT).show();
                            //从bmob获取文件数据
                            BmobUtils.getInstance().downloadFile(file, new BmobCallbackImpl() {
                                @Override
                                public void success(Object object) {
                                    //获取成功之后，就预览文件
                                    String savePath = (String) object;
                                    final File file = new File(savePath);
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            llDownload.setVisibility(View.GONE);
                                            //开始预览文件
                                            openFile(getActivity(), file);
                                        }
                                    });
                                }
                            });
                        } else {
                            Toast.makeText(mNoticeActiviry, "预览文件", Toast.LENGTH_SHORT).show();
                            //存在，不下载，直接打开
                            File existsFile = new File(path);
                            //开始预览文件
                            openFile(getActivity(), existsFile);
                        }

                    }
                } else {
                    Toast.makeText(mNoticeActiviry, "正在下载", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_cancle:
                //取消
                llDownload.setVisibility(View.GONE);
                break;
            case R.id.iv_toum:
                //取消
                llDownload.setVisibility(View.GONE);

                break;
        }
    }

    private void hideRootView(boolean b) {
        if (b) {
            icFile.setVisibility(View.VISIBLE);
            llRootView.setVisibility(View.GONE);
        } else {
            icFile.setVisibility(View.GONE);
            llRootView.setVisibility(View.VISIBLE);
        }
    }

    public void openFile(Context context, File file) {
        //调用真正的文件预览方法
        HyphenateUtils.openFile(getActivity(), file);
    }

    private void upLoadFile() {
        List<String> titles = new ArrayList<>();
        titles.add("影音");
        titles.add("文档");
        titles.add("图片");
        titles.add("应用");
        viewPager.setScroll(false);
        FileAdapter fileAdapter = new FileAdapter(getActivity().getSupportFragmentManager(), titles, mSendUser, groupId, noticeId, Constants.NOTICE_SEE_FALG_UP_LOAD_FILE_TO_BMOB);
        viewPager.setAdapter(fileAdapter);
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                setIndicator(tabLayout, 28, 28);
            }
        });
        tabLayout.setupWithViewPager(viewPager);

    }

    //保存要发送的文本数据
    private void saveDiscussData() {
        //        先去bmob查询，看此公告有没有被删除
        Map<String, String> param = new HashMap<String, String>();
        param.put("objectId", noticeId);
        BmobUtils.getInstance().querryNoticeData(param, 1, new BmobCallbackImpl() {
            @Override
            public void success(Object object) {

                //填充要发送的文本数据
                String content = etContent.getText().toString().trim();
                //判断数据是否为空，是，则不保存；否，保存到Bmob
                if (!TextUtils.isEmpty(content)) {
                    etContent.setText("");
                    //填充讨论数据
                    NoticeDiscussBean discussBean = new NoticeDiscussBean(userId, groupId, noticeId, content);
                    //将数据保存到Bmob
                    BmobUtils.getInstance().saveData(discussBean, new BmobCallbackImpl() {
                        @Override
                        public void success(Object object) {
                            //保存成功后，重新获取数据，更新讨论RecycleView的数据
                            initDiscussData();
                        }
                    });
                } else {
                    Toast.makeText(mNoticeActiviry, "输入内容不能为空", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void fail() {
                Toast.makeText(mNoticeActiviry, "该公告已经被删除", Toast.LENGTH_SHORT).show();
                hideCurrentFragment("Error");

            }
        });


    }


    @Override
    public int getLayoutId() {
        return R.layout.fragment_notice_see;
    }


    //编辑公告
    private void editNotice() {
        //[1]判断当前用户是否为管理员或群主，是，可以发公告；否 不可以
        if (mAdminList.contains(mSendUser)) {
            //是管理员
//            hideSoftInput();
//            [1.1]跳到发公告的Fragment
            FragmentManager fm = getActivity().getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            if (noticeFragment == null) {
                noticeFragment = new NoticeFragment();
                noticeFragment.setDatas(fm, bean, groupId);
                ft.replace(R.id.fl_container, noticeFragment, NOTICE_FRAGMENT);
            } else {
                noticeFragment.refreshData(fm, bean, groupId);
//                noticeFragment.refreshData(fm, bean, groupId);
                ft.show(noticeFragment);
            }
            ft.commit();
//            [1.2]隐藏当前这个Fragment
            hideCurrentFragment("");
        } else {
            //不是，就弹出一个TOAST,不进行操作
            Toast.makeText(mNoticeActiviry, "你不是管理员，不能编辑公告", Toast.LENGTH_SHORT).show();
        }
    }

    //删除公告
    private void deleteNotice() {
        //填充要删除的公告数据
        NoticeBean noticeBean = new NoticeBean();
        noticeBean.setObjectId(bean.getObjectId());
        //把数据从bmob上删除
        BmobUtils.getInstance().deleteDate(noticeBean, new BmobCallbackImpl() {
            @Override
            public void success(Object object) {
                //公告删除成功之后，就删除对应本公告的 观看人，讨论区，文件等数据
                //获取看公告的人的数据，才能进行删除
                getNoticeReader();
                //获取公告的讨论数据，才能进行删除
                getNoticeDiscuss();
                //获取公告文件的数据，才能进行删除
                getNoticeFileInfos();

            }

            @Override
            public void Error(BmobException e) {
                hideCurrentFragment("Error");
            }
        });
    }

    private void getNoticeFileInfos() {
        //获取公告文件的数据
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("noticeId", bean.getObjectId());
        BmobUtils.getInstance().querryFileInfo(params, 500, new BmobCallbackImpl() {
            @Override
            public void success(Object object) {
                //获取成功，填充此公告的文件数据，用于删除
                final List<FileInfo> infos = (List<FileInfo>) object;
                final ArrayList<BmobObject> FileBeans = new ArrayList<BmobObject>();
                for (FileInfo info : infos) {
                    FileInfo fileInfo = new FileInfo();
                    fileInfo.setObjectId(info.getObjectId());
                    FileBeans.add(fileInfo);
                }
                String[] urls = new String[infos.size()];
                for (int i = 0; i < infos.size(); i++) {
                    urls[i] = infos.get(i).getFile().getUrl();
                }
                //删除公告文件
                BmobUtils.getInstance().deleteBatchFile(urls, new BmobCallbackImpl() {
                    @Override
                    public void success(Object object) {

                    }
                });

                //删除公告文件相对应的表的数据
                BmobUtils.getInstance().deleteBatchData(FileBeans, new BmobCallbackImpl() {
                    @Override
                    public void success(Object object) {
                        Log.i("文件删除", "FileInfo数据删除成功");
                    }
                });

            }
        });
    }


    //获取公告的讨论数据
    private void getNoticeDiscuss() {
        HashMap<String, String> params1 = new HashMap<String, String>();

        params1.put("NoticeId", bean.getObjectId());
        //获取公告的讨论数据
        BmobUtils.getInstance().querryNoticeDiscussData(params1, 500, new BmobCallbackImpl() {
            @Override
            public void success(Object object) {
                //填充此公告的讨论区数据，用于删除
                List<NoticeDiscussBean> discussList = (List<NoticeDiscussBean>) object;
                ArrayList<BmobObject> discussBeans = new ArrayList<BmobObject>();
                for (NoticeDiscussBean discussBean : discussList) {
                    NoticeDiscussBean noticeDiscussBean = new NoticeDiscussBean();
                    noticeDiscussBean.setObjectId(discussBean.getObjectId());
                    discussBeans.add(discussBean);
                }
                //删除公告讨论数据
                BmobUtils.getInstance().deleteBatchData(discussBeans, new BmobCallbackImpl() {
                    @Override
                    public void success(Object object) {
                    }
                });
            }
        });
    }

    //获取看公告的人，然后进行删除
    private void getNoticeReader() {
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("noticeObjectId", bean.getObjectId());
        //获取看公告的人
        BmobUtils.getInstance().querryReader(params, 500, new BmobCallbackImpl() {
            @Override
            public void success(Object object) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideCurrentFragment("");
                        NoticeActivity activity = (NoticeActivity) getActivity();
                        activity.refreshData();
                    }
                });

                //填充要删除的观看公告的人的数据，用于删除
                List<ReaderBean> list = (List<ReaderBean>) object;
                ArrayList<BmobObject> readers = new ArrayList<BmobObject>();
                for (ReaderBean readerBean : list) {
                    ReaderBean bean = new ReaderBean();
                    bean.setObjectId(readerBean.getObjectId());
                    readers.add(bean);
                }
                //删除看公告的人
                BmobUtils.getInstance().deleteBatchData(readers, new BmobCallbackImpl() {
                    @Override
                    public void success(Object object) {

                    }

                    @Override
                    public void fail() {
                    }
                });


            }
        });
    }


    public void hideCurrentFragment(String str) {
        if (str.equals("Error")) {
            hideRootView(false);
            NoticeActivity activity = (NoticeActivity) getActivity();
            activity.refreshData();
            etContent.setText("");
            FragmentTransaction ft = fm.beginTransaction();
            ft.hide(NoticeSeeFragment.this);
            ft.commit();
        } else {
            //如果文件检索页面可见，就隐藏，不进行操作；否隐隐藏当前Fragment
            int visibility = icFile.getVisibility();
            if (visibility == View.VISIBLE) {
                hideRootView(false);
            } else {
                etContent.setText("");
                FragmentTransaction ft = fm.beginTransaction();
                ft.hide(NoticeSeeFragment.this);
                ft.commit();
            }
        }
    }

    //隐藏软件盘
    private void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        // 获取软键盘的显示状态
        boolean isOpen = imm.isActive();

        // 如果软键盘已经显示，则隐藏，反之则显示
        if (isOpen) {
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void setIndicator(TabLayout tabs, int leftDip, int rightDip) {
        Class<?> tabLayout = tabs.getClass();
        Field tabStrip = null;
        try {
            tabStrip = tabLayout.getDeclaredField("mTabStrip");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        tabStrip.setAccessible(true);
        LinearLayout llTab = null;
        try {
            llTab = (LinearLayout) tabStrip.get(tabs);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        int left = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, leftDip, Resources.getSystem().getDisplayMetrics());
        int right = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightDip, Resources.getSystem().getDisplayMetrics());

        for (int i = 0; i < llTab.getChildCount(); i++) {
            View child = llTab.getChildAt(i);
            child.setPadding(0, 0, 0, 0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
            params.leftMargin = left;
            params.rightMargin = right;
            child.setLayoutParams(params);
            child.invalidate();
        }
    }
}
