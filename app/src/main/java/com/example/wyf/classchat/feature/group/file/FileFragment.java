package com.example.wyf.classchat.feature.group.file;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.wyf.classchat.R;
import com.example.wyf.classchat.adapter.common.RvAdapter;
import com.example.wyf.classchat.adapter.common.RvViewHolder;
import com.example.wyf.classchat.base.BaseFragment;
import com.example.wyf.classchat.bean.MessageEvent;
import com.example.wyf.classchat.constants.Constants;
import com.example.wyf.classchat.file.FileManager;
import com.example.wyf.classchat.file.bean.AppInfo;
import com.example.wyf.classchat.file.bean.FileBean;
import com.example.wyf.classchat.file.bean.FileInfo;
import com.example.wyf.classchat.file.bean.Image;
import com.example.wyf.classchat.file.bean.ImgFolderBean;
import com.example.wyf.classchat.file.bean.Video;
import com.example.wyf.classchat.listener.BmobCallbackImpl;
import com.example.wyf.classchat.util.BmobUtils;
import com.example.wyf.classchat.feature.me.setting.CacheDataManager;
import com.example.wyf.classchat.util.ForbidFastClickUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by WYF on 2017/10/28.
 */

public class FileFragment<T> extends BaseFragment {
    private static final String TAG = FileFragment.class.getSimpleName();
    private static final String KINDS_OF = "kinds_of";
    private List<Video> videos = new ArrayList<>();
    private int kind;
    private RecyclerView recyclerView;
    private List<FileBean> fileBeans;
    private List<AppInfo> appInfos;
    private SparseBooleanArray mCheckStates = new SparseBooleanArray();
    private SparseBooleanArray mFileCheckStates = new SparseBooleanArray();
    private Button commit;
    private List<Image> images;
    private String userId;
    private String mNoticeId;
    private int mFlag;
    private ArrayList<FileInfo> mFileInfos;
    private String mGroupId;


    public static FileFragment newInstance(int param1, String userId, String groupId, String noticeId, int flag) {
        FileFragment fragment = new FileFragment();
        Bundle args = new Bundle();
        args.putInt(KINDS_OF, param1);
        args.putString("userId", userId);
        args.putString("groupId", groupId);
        args.putString("noticeId", noticeId);
        args.putInt("flag", flag);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            kind = getArguments().getInt(KINDS_OF);
            userId = getArguments().getString("userId");
            mGroupId = getArguments().getString("groupId");
            mNoticeId = getArguments().getString("noticeId");
            mFlag = getArguments().getInt("flag");
        }
    }

    private void initAdapter() {
        final FileManager fm = FileManager.getInstance(mActivity);
        switch (kind) {
            case 0:
                //影音
                new Thread() {
                    @Override
                    public void run() {
                        videos = fm.getVideos();
                        // 将检索到的影音数据发送到 getGroupsMember() 方法
                        EventBus.getDefault().post(new MessageEvent(Constants.NOTICE_FILE_VIDEO, videos));
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.setAdapter(new RvAdapter<Video>(getActivity(), R.layout.fragment_file_item, videos) {
                                    @Override
                                    public void convert(RvViewHolder holder, int position, final Video video) {
                                        final CheckBox checkBox = (CheckBox) holder.getView(R.id.cb_check);
                                        ImageView videoIcon = (ImageView) holder.getView(R.id.iv_video);
                                        TextView videoName = (TextView) holder.getView(R.id.tv_video_name);
                                        TextView videoSize = (TextView) holder.getView(R.id.tv_video_size);
                                        TextView videoTime = (TextView) holder.getView(R.id.tv_video_time);
                                        videoIcon.setImageBitmap(video.getBitmap());
                                        videoName.setText(video.getName());
                                        videoSize.setText(CacheDataManager.getFormatSize(video.getSize()));
                                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd   HH:mm");
                                        videoTime.setText(format.format(new Date(video.getDate() * 1000)));
//                                        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                                            @Override
//                                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                                                video.setSelected(b);
//                                            }
//                                        });
                                        checkBox.setTag(position);
                                        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                            @Override
                                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                                int pos = (int) checkBox.getTag();
                                                if (b) {
                                                    mFileCheckStates.put(pos, true);
                                                } else {
                                                    mFileCheckStates.delete(pos);
                                                }
                                                video.setSelected(b);
                                            }
                                        });
                                        checkBox.setChecked(mCheckStates.get(position, false));
                                    }
                                });
                            }
                        });
                    }
                }.start();
                break;
            case 1:
                //文档
                new Thread() {
                    @Override
                    public void run() {
                        fileBeans = fm.getFilesByType(0);
                        // 将检索到的文件数据发送到 getGroupsMember() 方法
                        EventBus.getDefault().post(new MessageEvent(Constants.NOTICE_FILE_FILES, fileBeans));
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.setAdapter(new RvAdapter<FileBean>(getActivity(), R.layout.fragment_file_item, fileBeans) {
                                    @Override
                                    public void convert(RvViewHolder holder, int position, final FileBean file) {
                                        final CheckBox checkBox = (CheckBox) holder.getView(R.id.cb_check);
                                        ImageView videoIcon = (ImageView) holder.getView(R.id.iv_video);
                                        TextView videoName = (TextView) holder.getView(R.id.tv_video_name);
                                        TextView videoSize = (TextView) holder.getView(R.id.tv_video_size);
                                        TextView videoTime = (TextView) holder.getView(R.id.tv_video_time);
                                        videoIcon.setImageResource(file.iconId);
                                        videoName.setText(file.path.substring(file.path.lastIndexOf("/") + 1));
                                        videoSize.setText(CacheDataManager.getFormatSize(file.size));
                                        videoTime.setText(file.path.substring(file.path.lastIndexOf(".") + 1));
//                                        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                                            @Override
//                                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                                                file.setSelected(b);
//                                            }
//                                        });
                                        checkBox.setTag(position);
                                        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                            @Override
                                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                                int pos = (int) checkBox.getTag();
                                                if (b) {
                                                    mFileCheckStates.put(pos, true);
                                                } else {
                                                    mFileCheckStates.delete(pos);
                                                }
                                                file.setSelected(b);
                                            }
                                        });
                                        checkBox.setChecked(mCheckStates.get(position, false));
                                    }
                                });
                            }
                        });
                    }
                }.start();
                break;
            case 2:
                //图片
                new Thread() {
                    @Override
                    public void run() {
                        images = new ArrayList<>();
                        for (ImgFolderBean imgFolderBean : fm.getImageFolders()) {
                            List<String> imgListByDir = FileManager.getInstance(getActivity()).getImgListByDir(imgFolderBean.getDir());
                            for (String s : imgListByDir) {
                                Image image = new Image(s);
                                images.add(image);
                            }
                        }
                        // 将图片到的文件数据发送到 getGroupsMember() 方法
                        EventBus.getDefault().post(new MessageEvent(Constants.NOTICE_FILE_IMAGES, images));
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
                                recyclerView.setAdapter(new RvAdapter<Image>(getActivity(), R.layout.fragment_commit_img, images) {
                                    @Override
                                    public void convert(final RvViewHolder holder, int position, final Image img) {
                                        ImageView iv = (ImageView) holder.getView(R.id.iv);
                                        final ImageView commit = (ImageView) holder.getView(R.id.iv_commit);
                                        Glide.with(getActivity()).load(img.getPath()).override(200, 200).into(iv);
                                        iv.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                if (commit.getVisibility() == View.VISIBLE) {
                                                    commit.setVisibility(View.GONE);
                                                    img.setSelected(false);
                                                } else {
                                                    commit.setVisibility(View.VISIBLE);
                                                    img.setSelected(true);
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                }.start();
                break;
            case 3:
                //应用
                new Thread() {
                    @Override
                    public void run() {
                        appInfos = fm.getAppInfos();
                        // 将图片到的应用数据发送到 getGroupsMember() 方法
                        EventBus.getDefault().post(new MessageEvent(Constants.NOTICE_FILE_APP, appInfos));
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.setAdapter(new RvAdapter<AppInfo>(getActivity(), R.layout.fragment_file_item, appInfos) {
                                    @Override
                                    public void convert(RvViewHolder holder, int position, final AppInfo appInfo) {
                                        final CheckBox checkBox = (CheckBox) holder.getView(R.id.cb_check);
                                        ImageView videoIcon = (ImageView) holder.getView(R.id.iv_video);
                                        TextView videoName = (TextView) holder.getView(R.id.tv_video_name);
                                        TextView videoSize = (TextView) holder.getView(R.id.tv_video_size);
                                        TextView videoTime = (TextView) holder.getView(R.id.tv_video_size);
                                        videoIcon.setImageDrawable(appInfo.getIcon());
                                        videoName.setText(appInfo.getApkName());
                                        videoSize.setText(CacheDataManager.getFormatSize(appInfo.getApkSize()));
                                        checkBox.setTag(position);
                                        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                            @Override
                                            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                                int pos = (int) checkBox.getTag();
                                                if (b) {
                                                    mCheckStates.put(pos, true);
                                                } else {
                                                    mCheckStates.delete(pos);
                                                }
                                                appInfo.setSelected(b);
                                            }
                                        });
                                        checkBox.setChecked(mCheckStates.get(position, false));
                                    }
                                });
                            }
                        });
                    }
                }.start();
                break;
        }
    }

    @Override
    public void initData() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        initAdapter();
        if (mFlag == Constants.NOTICE_FRAGMENT_FALG_UP_LOAD_FILE_TO_BMOB) {
            commit.setText("确定");
        }

    }


    @Override
    public void initView() {
        commit = (Button) mView.findViewById(R.id.btn_rollcall_commit);
        recyclerView = (RecyclerView) mView.findViewById(R.id.recycler_view);
        if (mFileInfos == null) {
            mFileInfos = new ArrayList();
        } else {
            mFileInfos.clear();
        }
        commit.setOnClickListener(new View.OnClickListener() {
            public AbstractList<AppInfo> infoList = new ArrayList<>();
            public AbstractList<Image> imgList = new ArrayList<>();
            public AbstractList<FileBean> fileList = new ArrayList<>();
            public AbstractList<Video> videoList = new ArrayList<>();


            @Override
            public void onClick(View view) {
                if (ForbidFastClickUtils.isFastClick()) {
                    if (mFlag == Constants.NOTICE_FRAGMENT_FALG_UP_LOAD_FILE_TO_BMOB) {

                        //所有数据发送完成，返回到NoticeFragment 的getGroupsMember() 方法，在NoticeFragment进行文件展示和上传操作
                        EventBus.getDefault().post(new MessageEvent(Constants.HIDE_NOTICE_FILE_LIST, null));

                    } else if (mFlag == Constants.NOTICE_SEE_FALG_UP_LOAD_FILE_TO_BMOB) {
                        //从NoticeSeeFragment跳到此页面时会走这里，直接在本页面进行文件上传操作
                        upLoadFile(infoList, imgList, fileList, videoList);

                    } else {
                        upLoadFile(infoList, imgList, fileList, videoList);
                    }

                }
            }


        });
    }



    private void upLoadFile(AbstractList<AppInfo> infoList, AbstractList<Image> imgList, AbstractList<FileBean> fileList, AbstractList<Video> videoList) {


        switch (kind) {
            case 0:
                if (videos != null) {
                    for (Video video : videos) {
                        if (video.isSelected()) {
                            videoList.add(video);
                            //填充要上传的videos文件数据
                            mFileInfos.add(new FileInfo(video.getName(), userId, mGroupId, mNoticeId, null, CacheDataManager.getFormatSize(video.getSize()), "video", video.getPath()));
                        }
                    }
                    if (videoList.size() > 0) {
                        if (mFlag == Constants.NOTICE_SEE_FALG_UP_LOAD_FILE_TO_BMOB) {
                            //从NoticeSeeFragment跳到此页面时会走这里，调用此方法开始文件上传
                            upFileToBmob();
                        } else {
//                        BmobUtils.getInstance().upLoadFiles();
                            for (final Video v : videoList) {
                                Thread thread = new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            EMClient.getInstance().groupManager().uploadGroupSharedFile(userId, v.getPath(), new EMCallBack() {
                                                @Override
                                                public void onSuccess() {
                                                    getActivity().runOnUiThread(() -> Toast.makeText(mActivity, "上传成功", Toast.LENGTH_SHORT).show());
                                                }

                                                @Override
                                                public void onError(int i, String s) {

                                                }

                                                @Override
                                                public void onProgress(int i, String s) {

                                                }
                                            });
                                        } catch (HyphenateException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };
                                thread.start();
                            }
                        }
                    } else {
                        if (mFlag == Constants.NOTICE_SEE_FALG_UP_LOAD_FILE_TO_BMOB) {
                            Log.i("上传文件为空 FileFragment", "");
                            EventBus.getDefault().post(new MessageEvent(Constants.REFRESH_NOTICE_SEE_FILE_LIST, null));
                        } else {
                            Toast.makeText(mActivity, "还没有选择要提交的文件", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if (mFlag == Constants.NOTICE_SEE_FALG_UP_LOAD_FILE_TO_BMOB) {
                        Log.i("上传文件为空 FileFragment", "");
                        EventBus.getDefault().post(new MessageEvent(Constants.REFRESH_NOTICE_SEE_FILE_LIST, null));
                    } else {
                        Toast.makeText(mActivity, "暂无可上传的文件", Toast.LENGTH_SHORT).show();
                    }

                }
                break;
            case 1:
                if (fileBeans != null) {
                    for (FileBean file : fileBeans) {
                        if (file.isSelected()) {
                            fileList.add(file);
                            String fileName = CacheDataManager.getFileName(file.path);
                            String fileSize = CacheDataManager.getFormatSize(file.size);
                            mFileInfos.add(new FileInfo(fileName, userId, mGroupId, mNoticeId, null, fileSize, "file", file.path));
                        }
                    }
                    if (fileList.size() > 0) {
                        if (mFlag == Constants.NOTICE_SEE_FALG_UP_LOAD_FILE_TO_BMOB) {
                            upFileToBmob();
                        } else {

                            for (final FileBean file : fileList) {
                                Thread thread = new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            EMClient.getInstance().groupManager().uploadGroupSharedFile(userId, file.path, new EMCallBack() {
                                                @Override
                                                public void onSuccess() {
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(mActivity, "上传成功", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onError(int i, String s) {

                                                }

                                                @Override
                                                public void onProgress(int i, String s) {

                                                }
                                            });
                                        } catch (HyphenateException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };
                                thread.start();
                            }
                        }
                    } else {
                        if (mFlag == Constants.NOTICE_SEE_FALG_UP_LOAD_FILE_TO_BMOB) {
                            Log.i("上传文件为空 FileFragment", "");
                            EventBus.getDefault().post(new MessageEvent(Constants.REFRESH_NOTICE_SEE_FILE_LIST, null));
                        } else {
                            Toast.makeText(mActivity, "还没有选择要提交的文件", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if (mFlag == Constants.NOTICE_SEE_FALG_UP_LOAD_FILE_TO_BMOB) {
                        Log.i("上传文件为空 FileFragment", "");
                        EventBus.getDefault().post(new MessageEvent(Constants.REFRESH_NOTICE_SEE_FILE_LIST, null));
                    } else {
                        Toast.makeText(mActivity, "暂无可上传的文件", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case 2:
                if (images != null) {
                    for (Image image : images) {
                        if (image.isSelected()) {
                            imgList.add(image);
                            String imgName = CacheDataManager.getFileName(image.getPath());
                            mFileInfos.add(new FileInfo(imgName, userId, mGroupId, mNoticeId, null, "", "img", image.getPath()));
                        }
                    }
                    if (imgList.size() > 0) {

                        if (mFlag == Constants.NOTICE_SEE_FALG_UP_LOAD_FILE_TO_BMOB) {
                            upFileToBmob();
                        } else {
                            for (final Image img : imgList) {
                                Thread thread = new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            EMClient.getInstance().groupManager().uploadGroupSharedFile(userId, img.getPath(), new EMCallBack() {
                                                @Override
                                                public void onSuccess() {
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(mActivity, "上传成功", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onError(int i, String s) {

                                                }

                                                @Override
                                                public void onProgress(int i, String s) {

                                                }
                                            });
                                        } catch (HyphenateException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };
                                thread.start();
                            }
                        }
                    } else {
                        if (mFlag == Constants.NOTICE_SEE_FALG_UP_LOAD_FILE_TO_BMOB) {
                            Log.i("上传文件为空 FileFragment", "");
                            EventBus.getDefault().post(new MessageEvent(Constants.REFRESH_NOTICE_SEE_FILE_LIST, null));
                        } else {
                            Toast.makeText(mActivity, "还没有选择要提交的文件", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if (mFlag == Constants.NOTICE_SEE_FALG_UP_LOAD_FILE_TO_BMOB) {
                        Log.i("上传文件为空 FileFragment", "");
                        EventBus.getDefault().post(new MessageEvent(Constants.REFRESH_NOTICE_SEE_FILE_LIST, null));
                    } else {
                        Toast.makeText(mActivity, "暂无可上传的文件", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case 3:
                if (appInfos != null) {
                    for (AppInfo info : appInfos) {
                        if (info.isSelected()) {
                            infoList.add(info);
                            String size = CacheDataManager.getFormatSize(info.getApkSize());
                            mFileInfos.add(new FileInfo(info.getApkName(), userId, mGroupId, mNoticeId, null, size, "app", info.getSourceDir()));
                        }
                    }
                    if (infoList.size() > 0) {
                        if (mFlag == Constants.NOTICE_SEE_FALG_UP_LOAD_FILE_TO_BMOB) {
                            upFileToBmob();
                        } else {
                            for (final AppInfo info : infoList) {
                                Thread thread = new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            EMClient.getInstance().groupManager().uploadGroupSharedFile(userId, info.getSourceDir(), new EMCallBack() {
                                                @Override
                                                public void onSuccess() {
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(mActivity, "上传成功", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onError(int i, String s) {

                                                }

                                                @Override
                                                public void onProgress(int i, String s) {

                                                }
                                            });
                                        } catch (HyphenateException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };
                                thread.start();
                            }
                        }
                    } else {
                        if (mFlag == Constants.NOTICE_SEE_FALG_UP_LOAD_FILE_TO_BMOB) {
                            Log.i("上传文件为空 FileFragment", "");
                            EventBus.getDefault().post(new MessageEvent(Constants.REFRESH_NOTICE_SEE_FILE_LIST, null));
                        } else {
                            Toast.makeText(mActivity, "还没有选择要提交的文件", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if (mFlag == Constants.NOTICE_SEE_FALG_UP_LOAD_FILE_TO_BMOB) {
                        Log.i("上传文件为空 FileFragment", "");
                        EventBus.getDefault().post(new MessageEvent(Constants.REFRESH_NOTICE_SEE_FILE_LIST, null));
                    } else {
                        Toast.makeText(mActivity, "暂无可上传的文件", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    private void upFileToBmob() {
//        先去bmob查询，看此公告有没有被删除
        Map<String, String> param = new HashMap<String, String>();
        param.put("objectId", mNoticeId);
        BmobUtils.getInstance().querryNoticeData(param, 1, new BmobCallbackImpl() {
            @Override
            public void success(Object object) {
                commit.setEnabled(false);
                commit.setText("正在提交文件....");
                if (mFlag == Constants.NOTICE_SEE_FALG_UP_LOAD_FILE_TO_BMOB) {
                    //从NoticeSeeFragment跳到此页面时会走这里，上传文件到Bmob
                    BmobUtils.getInstance().upLoadFiles(mFileInfos, new BmobCallbackImpl() {
                        @Override
                        public void success(Object object) {
                            //文件上传成功，
                            ArrayList<FileInfo> fileInfo = (ArrayList<FileInfo>) object;
                            //向Bmob插入与此文件相关联的表数据，用于后续对此文件的管理
                            BmobUtils.getInstance().insertBatch(fileInfo, new BmobCallbackImpl() {
                                @Override
                                public void success(Object object) {
                                    Log.i("文件上传成功 FileFragment", object + "");
                                    //文件上传成功之后，返回NoticeSeeFragment 的 onMainThread()方法，进行相对应的出路
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            commit.setEnabled(true);
                                            commit.setText("提交");
                                        }
                                    });
                                    EventBus.getDefault().post(new MessageEvent(Constants.REFRESH_NOTICE_SEE_FILE_LIST, null));
                                }
                            });
                        }
                    }, commit);
                }

            }

            @Override
            public void fail() {
                //此公告已经被删除
                EventBus.getDefault().post(new MessageEvent(Constants.NOTICE_DELETE_DONE, null));
            }
        });

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_file;
    }
}
