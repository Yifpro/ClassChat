package com.example.wyf.classchat.feature.group.notice;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wyf.classchat.R;
import com.example.wyf.classchat.adapter.FileAdapter;
import com.example.wyf.classchat.adapter.common.RvAdapter;
import com.example.wyf.classchat.adapter.common.RvViewHolder;
import com.example.wyf.classchat.base.BaseFragment;
import com.example.wyf.classchat.bean.MessageEvent;
import com.example.wyf.classchat.bean.NoticeBean;
import com.example.wyf.classchat.bean.PersonBmob;
import com.example.wyf.classchat.bean.ReaderTempBean;
import com.example.wyf.classchat.constants.Constants;
import com.example.wyf.classchat.file.bean.FileBean;
import com.example.wyf.classchat.file.bean.FileInfo;
import com.example.wyf.classchat.file.bean.AppInfo;
import com.example.wyf.classchat.file.bean.Image;
import com.example.wyf.classchat.file.bean.Video;
import com.example.wyf.classchat.listener.BmobCallbackImpl;
import com.example.wyf.classchat.util.BmobUtils;
import com.example.wyf.classchat.feature.me.setting.CacheDataManager;
import com.example.wyf.classchat.util.HyphenateUtils;
import com.example.wyf.classchat.util.ForbidFastClickUtils;
import com.example.wyf.classchat.weight.NoScrollViewPager;
import com.hyphenate.chat.EMClient;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by Administrator on 2017/10/8.
 */

public class NoticeFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "TAG";
    private EditText mEtContent;
    private EditText mEtNoticeTitle;
    private Toolbar mToolbar;
    private TextView mTvTitle;
    private FragmentManager fm;
    private long tempTime;
    private NoticeBean noticeBean;
    private String groupId;
    private RecyclerView mRv;
    private RecyclerView mRvDisplay;
    private ArrayList<ReaderTempBean> mReaderTempBeens;
    private SparseBooleanArray mCheckStates = new SparseBooleanArray();//用于存储和操作公告接受者的checkBox状态
    private HashMap<Integer, ReaderTempBean> mReaderTempstates = new HashMap<>();
    private RvAdapter<ReaderTempBean> mAdapter;
    private LinearLayout mLlCheck;
    private LinearLayout mLlFile;
    private StringBuffer mSb;
    private TabLayout mTabLayout;
    private NoScrollViewPager mVp;
    private String mSendUser; //当前登录的用户id
    private View mIcFragmentFile; //文件检索页面的布局
    private ArrayList<FileInfo> mAppBeens;//用于文件上传，数据是文件检索页面勾选的数据
    private HashMap<Integer, List> mHashMap = new HashMap<>();//用于对文件检索页面的数据进行分类
    private ArrayList<String[]> mDataList = new ArrayList<>();
    private ArrayList<String[]> mDataMemberList = new ArrayList<>();
    private RvAdapter<String[]> mFileAdapter;
    private ArrayList<FileInfo> mTempAppBeans;//用于展示即将上传的文件数据
    private boolean mIsAllcheck;//判断公告接收者是否全选中，是 true ; 不是 false
    private Button mBtnCheck;
    private Button mBtnBackCheck;
    private RecyclerView mRvMemberDisplay;
    private RvAdapter<String[]> mMemberListAdapter;
    private LinearLayout mLlMemberDisplay;
    private LinearLayout mLlFileDisplay;


    public void setDatas(FragmentManager fm, NoticeBean noticeBean, String groupId) {
        this.fm = fm;
        this.noticeBean = noticeBean;
        this.groupId = groupId;
    }

    public void setDatas(FragmentManager fm, String groupId) {
        this.fm = fm;
        this.groupId = groupId;
    }


    public void refreshData(FragmentManager fm, NoticeBean noticeBean, String groupId) {
        this.fm = fm;
        this.groupId = groupId;
        this.noticeBean = noticeBean;

        initData();
    }

    @Override
    public void initToolbar() {
        mToolbar.inflateMenu(R.menu.toolbar_notice_fragmenr);
        mToolbar.setNavigationIcon(R.drawable.ic_left_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ForbidFastClickUtils.isFastClick()) {
                    hideCurrentFragment();
//                    clearData();
                }
            }
        });
        //点发布按钮，走下面
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                final String title = mEtNoticeTitle.getText().toString().trim();
                final String content = mEtContent.getText().toString().trim();
                int titleLength = title.length();
                int contentLength = content.length();
                String error = null;
                if (titleLength < 4) {
                    error = "标题字数不能少于4字";
                } else if (titleLength > 20) {
                    error = "标题字数不能多余于20字";
                } else if (contentLength < 15) {
                    error = "正文字数不能少于15字";
                } else if ((contentLength > 500)) {
                    error = "正文字数不能多余于500字";
                } else if (mSb == null || TextUtils.isEmpty(mSb.toString().trim())) {
                    error = "请选择要接收的人";
                } else if (mRv.getVisibility() == View.VISIBLE) {
                    mRv.setVisibility(View.GONE);
                } else if (mIcFragmentFile.getVisibility() == View.VISIBLE) {
                    mIcFragmentFile.setVisibility(View.GONE);
                } else {
                    //以上条件都满足，走这里
                    //即将要进行文件上传
                    if (ForbidFastClickUtils.isFastClick()) {
                        new Thread() {
                            @Override
                            public void run() {
                                URL url = null;
                                try {
                                    url = new URL("http://www.baidu.com");
                                    URLConnection uc = url.openConnection();
                                    uc.connect();
                                    //获取网络时间
                                    String time = getNetTime(uc);
                                    //填充即将要上传的公告数据2
                                    NoticeBean noticeBean = new NoticeBean(title, content, time, tempTime, mSendUser, "", 0, groupId, mSb.toString());
                                    //将公告数据保存到bmob
                                    BmobUtils.getInstance().saveData(noticeBean, new BmobCallbackImpl() {
                                        @Override
                                        public void success(Object object) {

                                            String objectId = (String) object;
                                            Toast.makeText(getActivity(), "发布成功", Toast.LENGTH_SHORT).show();
                                            //公告上传成功，隐藏当前fragment，并刷新NoticeActivity的数据
                                            hideFragment();
//                                        hideSoftInput();
                                            //
                                            NoticeActivity activity = (NoticeActivity) getActivity();

                                            activity.refreshData();

                                            Log.i(TAG, "success: 开始上传文件");
                                            //公告发布成功之后，就上传文件
                                            if (mAppBeens != null) {
                                                //将要上传的文件进行分类
                                                Log.i(TAG, "success: 上传文件不为空");
                                                dealFileData(mHashMap, mAppBeens, objectId);
                                                if (mAppBeens.size() > 0) {
                                                    Log.i(TAG, "success: 上传文件大小大于零");
                                                    //开始上传文件
                                                    upLoadFile();
                                                }
                                            }
                                        }

                                        @Override
                                        public void fail() {
                                            Toast.makeText(getActivity(), "发布失败，请稍后重试", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    }
                }
                if (error != null) {
                    Toast.makeText(mActivity, error, Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }

    @Override
    public String getTitleText() {
        return "编辑圈公告";
    }

    //将文件上传到Bmob
    private void upLoadFile() {
        BmobUtils.getInstance().upLoadFiles(mAppBeens, new BmobCallbackImpl() {
            @Override
            public void success(Object object) {
                final ArrayList<FileInfo> fileInfo = (ArrayList<FileInfo>) object;
                //文件上传成功后，将文件对应的数据也上传到Bmob，用于以后管理文件
                BmobUtils.getInstance().insertBatch(fileInfo, new BmobCallbackImpl() {
                    @Override
                    public void success(Object object) {
                        int total = (int) object;
                        if (total == fileInfo.size() - 1) {
                            clearData();
                        }
                    }
                });
            }
        }, null);

    }


    private String getNetTime(URLConnection uc) {
        tempTime = uc.getDate();
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(tempTime);
        String str = format.format(calendar.getTime());
        return str;
    }


    private void hideFragment() {
        hideButton(true);
        clearText();
        FragmentTransaction ft = fm.beginTransaction();
        ft.hide(NoticeFragment.this);
        ft.commit();
    }


    //用于隐藏当前Fragment或者布局
    public void hideCurrentFragment() {

        if (mDataList != null) {
            mDataList.clear();
            if (mFileAdapter != null) {
                mFileAdapter.notifyDataSetChanged();
            }

        }
        //按回退键或按钮时，判断公告接收人页面是否显示，是 隐藏，不返回；否 返回
        if (mRv.getVisibility() == View.VISIBLE) {
            hideButton(true);
            return;
        }
        if (mIcFragmentFile.getVisibility() == View.VISIBLE) {
            mIcFragmentFile.setVisibility(View.GONE);
            mLlFileDisplay.setVisibility(View.VISIBLE);
            mLlFile.setVisibility(View.VISIBLE);
            return;
        }
        if (mIcFragmentFile.getVisibility() == View.GONE && mRv.getVisibility() == View.GONE) {
            clearData();
        }
        clearText();
        FragmentTransaction ft = fm.beginTransaction();
        ft.hide(NoticeFragment.this);
        ft.commit();
    }

    //清空本次上传公告的观看人，文件的数据
    public void clearData() {

        if (mHashMap != null) {
            mHashMap.clear();
        }
        if (mCheckStates != null) {
            mCheckStates.clear();
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }
        if (mSb != null) {
            mSb.delete(0, mSb.length());
        }
        if (mDataMemberList != null) {
            mDataMemberList.clear();
            if (mMemberListAdapter != null) {
                mMemberListAdapter.notifyDataSetChanged();
            }
        }
    }

//    private void clearUpLoadFileData(){
//
//        if (mHashMap != null) {
//            mHashMap.clear();
//        }
//    }

    public void clearText() {
        mEtNoticeTitle.setText("");
        mEtContent.setText("");

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_notice_fragmenr, menu);
    }

    @Override
    public void initData() {
        mSendUser = EMClient.getInstance().getCurrentUser();
        if (noticeBean != null) {
            mEtContent.setText(noticeBean.getContent());
            mEtNoticeTitle.setText(noticeBean.getTitle());


        }
        HyphenateUtils.getMembers(groupId);

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

    @Override
    public void initView() {
        mEtNoticeTitle = (EditText) mView.findViewById(R.id.et_notice_title);
        mEtContent = (EditText) mView.findViewById(R.id.et_rollcall_matching_code);
        mToolbar = (Toolbar) mView.findViewById(R.id.toolbar);
        mTvTitle = (TextView) mView.findViewById(R.id.tv_title);
        mRv = (RecyclerView) mView.findViewById(R.id.rv_member_file);
        mRvDisplay = (RecyclerView) mView.findViewById(R.id.rv_display);
        mRvMemberDisplay = (RecyclerView) mView.findViewById(R.id.rv_member_display);
        mLlMemberDisplay = (LinearLayout) mView.findViewById(R.id.ll_member_display);
        mLlFileDisplay = (LinearLayout) mView.findViewById(R.id.ll_file_display);
        mLlFile = (LinearLayout) mView.findViewById(R.id.ll_file);
        mLlCheck = (LinearLayout) mView.findViewById(R.id.ll_check);
        mBtnCheck = mView.findViewById(R.id.btn_check);
        mBtnBackCheck = mView.findViewById(R.id.btn_back_check);
        mView.findViewById(R.id.btn_member).setOnClickListener(this);
        mView.findViewById(R.id.btn_file).setOnClickListener(this);
        mBtnCheck.setOnClickListener(this);
        mBtnBackCheck.setOnClickListener(this);
        mView.findViewById(R.id.btn_ok).setOnClickListener(this);
        mIcFragmentFile = mView.findViewById(R.id.ic_fragment_file);
        initFileViews();
        initToolbar();
    }

    private void initFileViews() {
        mTabLayout = (TabLayout) mView.findViewById(R.id.tab_layout_fragment_notice);
        mVp = (NoScrollViewPager) mView.findViewById(R.id.vp_fragment_notice);


    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_notice;
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getGroupsMember(MessageEvent event) {
        if (event.getWhat() == Constants.GET_MEMBERS_INFO) {
            //获取圈成员列表
            ArrayList<PersonBmob> mPersonBmobs = (ArrayList<PersonBmob>) event.getMessage();
            mReaderTempBeens = new ArrayList<>();
            for (PersonBmob personBmob : mPersonBmobs) {
                ReaderTempBean readerTempBean = new ReaderTempBean(personBmob.getId(), personBmob.getName(), false);
                mReaderTempBeens.add(readerTempBean);
            }

        }

        //将文件检索页面选择的文件填充到本页面的List上
        if (event.getWhat() == Constants.HIDE_NOTICE_FILE_LIST) {
            mIcFragmentFile.setVisibility(View.GONE);
            hideButton(true);
            //处理要上传的文件数据
            dealUpLoadFile(mHashMap);
        } else if (event.getWhat() == Constants.NOTICE_FILE_APP) {
            Log.i(TAG, "getGroupsMember: 放回app");
            //应用信息
            List<AppInfo> appList = (List<AppInfo>) event.getMessage();
            mHashMap.put(0, appList);
        } else if (event.getWhat() == Constants.NOTICE_FILE_IMAGES) {
            Log.i(TAG, "getGroupsMember: 方茴图片");
            List<Image> imageList = (List<Image>) event.getMessage();
            mHashMap.put(1, imageList);
        } else if (event.getWhat() == Constants.NOTICE_FILE_FILES) {
            //文档文件


            List<FileBean> fileist = (List<FileBean>) event.getMessage();
            mHashMap.put(2, fileist);
        } else if (event.getWhat() == Constants.NOTICE_FILE_VIDEO) {
            //视频文件

            List<Video> videoList = (List<Video>) event.getMessage();
            mHashMap.put(3, videoList);
        }
    }

    private void dealUpLoadFile(HashMap<Integer, List> map) {
        if (mAppBeens == null) {
            mAppBeens = new ArrayList<>();
            mTempAppBeans = new ArrayList<>();

        } else {
            mAppBeens.clear();
            mTempAppBeans.clear();
        }
        //将文件选择页面返回来的数据进行归类，并将数据添加到上传的List
        dealFileData(map, mTempAppBeans, "");
        //展示即将上传的文件
        fillFileAdapterData();
    }

    //展示即将上传的文件
    private void fillFileAdapterData() {
        if (mFileAdapter == null) {
            //填充要展示的数据
            for (FileInfo appBeen : mTempAppBeans) {
                String[] arr = {appBeen.getName(), appBeen.getSize(), appBeen.getType()};
                mDataList.add(arr);
            }
            mFileAdapter = getFileAdapter();
        } else {
            //填充要展示的数据
            mDataList.clear();
            for (FileInfo appBeen : mTempAppBeans) {
                String[] arr = {appBeen.getName(), appBeen.getSize(), appBeen.getType()};
                mDataList.add(arr);
            }
            mFileAdapter.notifyDataSetChanged();
        }

        mRvDisplay.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRvDisplay.setAdapter(mFileAdapter);
    }

    //将文件选择页面返回来的数据进行归类，并将数据添加到上传的List
    private void dealFileData(HashMap<Integer, List> map, ArrayList<FileInfo> beans, String noticeId) {
        for (int i = 0; i < 4; i++) {
            if (map.get(i) != null && map.get(i).size() > 0) {
                for (int j = 0; j < map.get(i).size(); j++) {

                    if (i == 0) {
                        //应用类型数据

                        AppInfo appInfo = (AppInfo) map.get(0).get(j);
                        if (appInfo.isSelected()) {
                            Log.i(TAG, "dealFileData: 应用类型");
                            String size = CacheDataManager.getFormatSize(appInfo.getApkSize());
                            beans.add(new FileInfo(appInfo.getApkName(), mSendUser, groupId, noticeId, null, size, "app", appInfo.getSourceDir()));
                        }
                    } else if (i == 1) {
                        //图片类型数据

                        Image image = (Image) map.get(1).get(j);
                        if (image.isSelected()) {
                            Log.i(TAG, "dealFileData: 图片类型");
                            String imgName = CacheDataManager.getFileName(image.getPath());
                            beans.add(new FileInfo(imgName, mSendUser, groupId, noticeId, null, "", "img", image.getPath()));
                        }

                    } else if (i == 2) {
                        //文件类型数据
                        FileBean file = (FileBean) map.get(2).get(j);
                        if (file.isSelected()) {
                            String fileName = CacheDataManager.getFileName(file.path);
                            String fileSize = CacheDataManager.getFormatSize(file.size);
                            beans.add(new FileInfo(fileName, mSendUser, groupId, noticeId, null, fileSize, "file", file.path));

                        }
                    } else {
                        //video类型数据
                        Video video = (Video) map.get(3).get(j);
                        if (video.isSelected()) {
                            String videoSize = CacheDataManager.getFormatSize(video.getSize());
                            beans.add(new FileInfo(video.getName(), mSendUser, groupId, noticeId, null, videoSize, "video", video.getPath()));
                        }
                    }
                }
            }
        }
        Log.i(TAG, "dealFileData: "+beans.size());
    }

    @NonNull
    private RvAdapter<String[]> getFileAdapter() {
        return new RvAdapter<String[]>(getActivity(), R.layout.fragment_notice_display_file_item, mDataList) {

            @Override
            public void convert(RvViewHolder holder, int position, String[] strings) {
                holder.setText(R.id.tv_rv_name, strings[0]);
                holder.setText(R.id.tv_rv_other, strings[1]);
                ImageView ivIcon = holder.getView(R.id.iv_icon);
                String type = strings[2];
                ivIcon.setImageResource(type.equals("app")
                        ? R.mipmap.ic_launcher : type.equals("img")
                        ? R.drawable.ic_file_img : type.equals("file") ? R.drawable.ic_file_doc : R.drawable.ic_file_img);
            }
        };
    }


    @Override
    public boolean needEventBus() {
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_member:
                if (ForbidFastClickUtils.isFastClick()) {
                    //展示成员数据 ,点击接收人按钮
                    fillRecycleData();

                    hideButton(false);
                }
                break;
            case R.id.btn_file:

                if (ForbidFastClickUtils.isFastClick()) {
                    //展示文件，点附带文件按钮，走这里
                    Toast.makeText(mActivity, "展示文件", Toast.LENGTH_SHORT).show();
                    ////// TODO: 2017/11/6 文件上传
                    fillFileData();
                    mIcFragmentFile.setVisibility(View.VISIBLE);
                    mRv.setVisibility(View.GONE);
                    mLlFile.setVisibility(View.GONE);
                    mLlCheck.setVisibility(View.GONE);
                    mLlFileDisplay.setVisibility(View.GONE);
//                    mRvDisplay.setVisibility(View.GONE);
//                    mRvMemberDisplay.setVisibility(View.GONE);
                    mLlMemberDisplay.setVisibility(View.GONE);
                }
                break;
            case R.id.btn_check:

//                if (ForbidFastClickUtils.isFastClick()) {
                //全选
                if (mReaderTempBeens != null && mReaderTempBeens.size() > 0 && mAdapter != null) {
                    for (int i = 0; i < mReaderTempBeens.size(); i++) {
                        if (mIsAllcheck) {
                            mCheckStates.put(i, false);
                            mReaderTempstates.remove(i);
                        } else {
                            mCheckStates.put(i, true);
                            mReaderTempstates.put(i,mReaderTempBeens.get(i));
                        }
//                        mCheckStates.put(i,true);
                    }
                    dealCheckState(mCheckStates);
                    mAdapter.notifyDataSetChanged();

                }
//                }
                break;
            case R.id.btn_ok:
                if (ForbidFastClickUtils.isFastClick()) {
                    //确定，存看公告的人
                    mSb = new StringBuffer();
                    mDataMemberList.clear();
                    for (Map.Entry<Integer, ReaderTempBean> entry : mReaderTempstates.entrySet()) {
                        String[] arr = {entry.getValue().getUsername(), entry.getValue().getUserId()};
                        mDataMemberList.add(arr);
                        //把选中的UseID拼接起来
                        mSb.append(entry.getValue().getUserId()).append("~*~");
//                        1513157103"~*~"1513157106"~*~"

                    }
                    Log.i(TAG, "onClick: MSB"+mSb);
                    hideButton(true);

                    if (mMemberListAdapter == null) {
                        mMemberListAdapter = getMemberListAdapter();
                    } else {
                        mMemberListAdapter.notifyDataSetChanged();
                    }
                    mRvMemberDisplay.setLayoutManager(new LinearLayoutManager(getActivity()));
                    mRvMemberDisplay.setAdapter(mMemberListAdapter);
                }
                break;
            case R.id.btn_back_check:
                if (ForbidFastClickUtils.isFastClick()) {
                    //反选
                    if (mReaderTempBeens != null && mReaderTempBeens.size() > 0 && mAdapter != null) {
                        for (int i = 0; i < mReaderTempBeens.size(); i++) {
                            mCheckStates.put(i, !mCheckStates.get(i));
                            if (mCheckStates.get(i)){
                                mReaderTempstates.put(i,mReaderTempBeens.get(i));
                            }else {
                                mReaderTempstates.remove(i);
                            }
                        }
                        dealCheckState(mCheckStates);
                        mAdapter.notifyDataSetChanged();
                    }
                }
                break;
        }
    }

    @NonNull
    private RvAdapter<String[]> getMemberListAdapter() {
        return new RvAdapter<String[]>(getActivity(), R.layout.fragment_notice_display_file_item, mDataMemberList) {

            @Override
            public void convert(RvViewHolder holder, int position, String[] strings) {
                holder.setText(R.id.tv_rv_name, strings[0]);
                holder.setText(R.id.tv_rv_other, strings[1]);
                holder.setImageResource(R.id.iv_icon, R.drawable.ic_receive);
            }
        };
    }

    private void fillFileData() {
        List<String> titles = new ArrayList<>();
        titles.add("影音");
        titles.add("文档");
        titles.add("图片");
        titles.add("应用");
        mVp.setScroll(false);


        FileAdapter mFileFragmentAdapter = new FileAdapter(getActivity().getSupportFragmentManager(), titles, mSendUser, Constants.NOTICE_FRAGMENT_FALG_UP_LOAD_FILE_TO_BMOB);
        mVp.setAdapter(mFileFragmentAdapter);
        mTabLayout.post(new Runnable() {
            @Override
            public void run() {
                setIndicator(mTabLayout, 28, 28);
            }
        });
        mTabLayout.setupWithViewPager(mVp);

    }

    private void hideButton(boolean b) {
        if (b) {
            //展示上传文件按钮
            mLlCheck.setVisibility(View.GONE);
            mLlFile.setVisibility(View.VISIBLE);
            mRv.setVisibility(View.GONE);
//            mRvMemberDisplay.setVisibility(View.VISIBLE);
            mLlMemberDisplay.setVisibility(View.VISIBLE);
//            mRvDisplay.setVisibility(View.VISIBLE);
            mLlFileDisplay.setVisibility(View.VISIBLE);
        } else {
            //展示全选按钮
            mLlCheck.setVisibility(View.VISIBLE);
            mLlFile.setVisibility(View.GONE);
//            mRvDisplay.setVisibility(View.GONE);
            mLlFileDisplay.setVisibility(View.GONE);
            mLlMemberDisplay.setVisibility(View.GONE);
//            mRvMemberDisplay.setVisibility(View.GONE);
            mRv.setVisibility(View.VISIBLE);

        }

    }

    private void fillRecycleData() {
        mRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (mAdapter == null) {
            mAdapter = getAdapter();
        } else {
            mAdapter.notifyDataSetChanged();
        }

        mRv.setAdapter(mAdapter);

        mRv.setVisibility(View.VISIBLE);
    }

    @NonNull
    private RvAdapter<ReaderTempBean> getAdapter() {

        return new RvAdapter<ReaderTempBean>(getActivity(), R.layout.fragment_notice_member, mReaderTempBeens) {

            @Override
            public void convert(RvViewHolder holder, int position, final ReaderTempBean readerTempBean) {

                holder.setText(R.id.tv_notice_id, readerTempBean.getUserId());
                holder.setText(R.id.tv_notice_name, readerTempBean.getUsername());
                CheckBox checkBox = (CheckBox) holder.getView(R.id.cb_notice_check);

                checkBox.setTag(position);//0

                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton checkBox, boolean b) {

                        int pos = (int) checkBox.getTag();//0
                        if (b) {

                            mCheckStates.put(pos, true);//0
                            dealCheckState(mCheckStates);
                            mReaderTempstates.put(pos, readerTempBean);
                        } else {
                            //用于全选或反选
                            mCheckStates.put(pos, false);//0
                            dealCheckState(mCheckStates);
                            //用于获取选中的ItemId
                            mReaderTempstates.remove(pos);
                        }
                    }
                });
                checkBox.setChecked(mCheckStates.get(position, false));


            }


        };
    }

    //点击全选和反选
    private void dealCheckState(SparseBooleanArray checkStates) {
//        mIsAllcheck = false;
        for (int i = 0; i < checkStates.size(); i++) {
            if (!checkStates.get(i)) {
                //至少有一个没有选中
                mIsAllcheck = false;
                break;
            } else {
                //都选中了
                mIsAllcheck = true;
            }
        }
        if (mIsAllcheck) {
            mBtnCheck.setText("全不选");
        } else {
            mBtnCheck.setText("全选");
        }

//        for (int i = 0; i < mCheckStates.size(); i++) {
//            if (mCheckStates.get(i)){
//                mReaderTempstates.put(i,)
//            }else {
//                mReaderTempstates.remove(i);
//            }
//        }
    }


}
