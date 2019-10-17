package com.example.wyf.classchat.feature.group.file;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wyf.classchat.R;
import com.example.wyf.classchat.adapter.common.RvAdapter;
import com.example.wyf.classchat.adapter.common.RvViewHolder;
import com.example.wyf.classchat.base.IAppInitContract;
import com.example.wyf.classchat.bean.PersonBmob;
import com.example.wyf.classchat.db.FileType;
import com.example.wyf.classchat.db.Person;
import com.example.wyf.classchat.listener.BmobCallbackImpl;
import com.example.wyf.classchat.util.BmobUtils;
import com.example.wyf.classchat.feature.me.setting.CacheDataManager;
import com.example.wyf.classchat.db.util.DatabaseUtils;
import com.example.wyf.classchat.util.HyphenateUtils;
import com.example.wyf.classchat.weight.AutoToolbar;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMucSharedFile;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.DateUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

/**
 * Created by WYF on 2017/10/26.
 */

public class ShareFileActivity extends AppCompatActivity implements IAppInitContract.IActivity, IAppInitContract.IToolbar {

    private static final String TAG = ShareFileActivity.class.getSimpleName();
    private static final int pageSize = 20;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.tv_no_data)
    TextView tvNoData;

    @BindView(R.id.toolbar)
    AutoToolbar toolbar;
    private List<EMMucSharedFile> shareFileList;
    private String userId;

    @Override
    public void init() {
        initToolbar();
        shareFileList = new ArrayList<>();
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    List<EMMucSharedFile> emMucSharedFiles = EMClient.getInstance().groupManager().fetchGroupSharedFileList(userId, 0, pageSize);
                    if (emMucSharedFiles.size() > 0) {
                        shareFileList.addAll(emMucSharedFiles);
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                tvNoData.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                    HyphenateUtils.refresh(ShareFileActivity.this, getAdapter(), recyclerView);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(getAdapter());
    }

    private RvAdapter<EMMucSharedFile> getAdapter() {
        return new RvAdapter<EMMucSharedFile>(this, R.layout.activity_file_item, shareFileList) {
            @Override
            public void convert(RvViewHolder holder, int position, final EMMucSharedFile file) {
                String fileName = file.getFileName();
                int placeImg = R.drawable.ic_file_what;
                if (fileName.endsWith(".docx")) {
                    placeImg = R.drawable.ic_file_doc;
                } else if (fileName.endsWith(".xls")) {
                    placeImg = R.drawable.ic_file_xls;
                } else if (fileName.endsWith(".ppt")) {
                    placeImg = R.drawable.ic_file_ppt;
                } else if (fileName.endsWith(".zip") || fileName.endsWith(".rar")) {
                    placeImg = R.drawable.ic_file_zip;
                } else if (fileName.endsWith(".txt") || fileName.endsWith(".java")) {
                    placeImg = R.drawable.ic_file_txt;
                } else if (fileName.endsWith(".png") || fileName.endsWith(".jpg")) {
                    placeImg = R.drawable.ic_file_img;
                }
                holder.setImageResource(R.id.iv_file_icon, placeImg);
                holder.setText(R.id.tv_file_name, file.getFileName());
                holder.setText(R.id.tv_last_time, DateUtils.getTimestampString(new Date(file.getFileUpdateTime())));
                holder.setText(R.id.tv_file_size, CacheDataManager.getFormatSize(file.getFileSize()));
                final TextView tvFileId = (TextView) holder.getView(R.id.tv_file_id);
                tvFileId.setTag(file.getFileId());
                Person person = (Person)DatabaseUtils.queryData(FileType.CONTACT, file.getFileOwner());
                if (person != null) {
                    tvFileId.setText(person.getName());
                } else {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("id", file.getFileOwner());
                    BmobUtils.getInstance().querryPerson(map, new BmobCallbackImpl() {
                        @Override
                        public void success(Object object) {
                            List<PersonBmob> object1 = (List<PersonBmob>) object;
                            if (file.getFileId().equals((String) tvFileId.getTag())) {
                                tvFileId.setText(object1.get(0).getName());
                            }
                        }
                    });
                }

                holder.getView(R.id.msg_layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //String path = PathUtils.getCacheDirectory(ShareFileActivity.this, "other").getAbsolutePath();
                        //final File f = new File(path, file.getFileName());
                        final File f = null;
                        if (f.exists()) {
//                            openFile(ShareFileActivity.this, f);
                            HyphenateUtils.openFile(ShareFileActivity.this, f);
                        } else {
                            Toast.makeText(mContext, "开始下载文件...", Toast.LENGTH_SHORT).show();
                            Thread thread = new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        EMClient.getInstance().groupManager().downloadGroupSharedFile(userId, file.getFileId(), f.getAbsolutePath(), new EMCallBack() {
                                            @Override
                                            public void onSuccess() {
                                                ShareFileActivity.this.runOnUiThread(() -> Toast.makeText(mContext, "下载成功", Toast.LENGTH_SHORT).show());
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
                });
            }
        };
    }

    @Override
    public String getToolbarTitle() {
        return "圈文件";
    }

    @Override
    public boolean isShowHome() {
        return true;
    }

    @Override
    public void initToolbar() {
        toolbar.setRightIcon(R.drawable.ic_more)
                .setOnRightIconClickEvent(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(ShareFileActivity.this, SelectFileActivity.class).putExtra("userId", userId));
                    }
                });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_share_file;
    }

//    public void openFile(Context context, File file) {
//        try {
//            Intent intent = new Intent();
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            //设置intent的Action属性
//            intent.setAction(Intent.ACTION_VIEW);
//            //获取文件file的MIME类型
//            String type = getMIMEType(file);
//            //设置intent的data和Type属性。
//            intent.setDataAndType(Uri.fromFile(file), type);
//            //跳转
//            context.startActivity(intent);
//        } catch (ActivityNotFoundException e) {
//            ToastUtils.show("附件不能打开，请下载相关软件！");
//        }
//    }

//    private String getMIMEType(File file) {
//
//        String type = "*/*";
//        String fName = file.getName();
//        //获取后缀名前的分隔符"."在fName中的位置。
//        int dotIndex = fName.lastIndexOf(".");
//        if (dotIndex < 0) {
//            return type;
//        }
//        /* 获取文件的后缀名*/
//        String end = fName.substring(dotIndex, fName.length()).toLowerCase();
//        if (end == "") return type;
//        //在MIME和文件类型的匹配表中找到对应的MIME类型。
//        for (int i = 0; i < MIME_MapTable.length; i++) {
//            if (end.equals(MIME_MapTable[i][0]))
//                type = MIME_MapTable[i][1];
//        }
//        return type;
//    }

//    private String[][] MIME_MapTable = {
//            //{后缀名，MIME类型}
//            {".3gp", "video/3gpp"},
//            {".apk", "application/vnd.android.package-archive"},
//            {".asf", "video/x-ms-asf"},
//            {".avi", "video/x-msvideo"},
//            {".bin", "application/octet-stream"},
//            {".bmp", "image/bmp"},
//            {".c", "text/plain"},
//            {".class", "application/octet-stream"},
//            {".conf", "text/plain"},
//            {".cpp", "text/plain"},
//            {".doc", "application/msword"},
//            {".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
//            {".xls", "application/vnd.ms-excel"},
//            {".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
//            {".exe", "application/octet-stream"},
//            {".gif", "image/gif"},
//            {".gtar", "application/x-gtar"},
//            {".gz", "application/x-gzip"},
//            {".h", "text/plain"},
//            {".htm", "text/html"},
//            {".html", "text/html"},
//            {".jar", "application/java-archive"},
//            {".java", "text/plain"},
//            {".jpeg", "image/jpeg"},
//            {".jpg", "image/jpeg"},
//            {".js", "application/x-javascript"},
//            {".log", "text/plain"},
//            {".m3u", "audio/x-mpegurl"},
//            {".m4a", "audio/mp4a-latm"},
//            {".m4b", "audio/mp4a-latm"},
//            {".m4p", "audio/mp4a-latm"},
//            {".m4u", "video/vnd.mpegurl"},
//            {".m4v", "video/x-m4v"},
//            {".mov", "video/quicktime"},
//            {".mp2", "audio/x-mpeg"},
//            {".mp3", "audio/x-mpeg"},
//            {".mp4", "video/mp4"},
//            {".mpc", "application/vnd.mpohun.certificate"},
//            {".mpe", "video/mpeg"},
//            {".mpeg", "video/mpeg"},
//            {".mpg", "video/mpeg"},
//            {".mpg4", "video/mp4"},
//            {".mpga", "audio/mpeg"},
//            {".msg", "application/vnd.ms-outlook"},
//            {".ogg", "audio/ogg"},
//            {".pdf", "application/pdf"},
//            {".png", "image/png"},
//            {".pps", "application/vnd.ms-powerpoint"},
//            {".ppt", "application/vnd.ms-powerpoint"},
//            {".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
//            {".prop", "text/plain"},
//            {".rc", "text/plain"},
//            {".rmvb", "audio/x-pn-realaudio"},
//            {".rtf", "application/rtf"},
//            {".sh", "text/plain"},
//            {".tar", "application/x-tar"},
//            {".tgz", "application/x-compressed"},
//            {".txt", "text/plain"},
//            {".wav", "audio/x-wav"},
//            {".wma", "audio/x-ms-wma"},
//            {".wmv", "audio/x-ms-wmv"},
//            {".wps", "application/vnd.ms-works"},
//            {".xml", "text/plain"},
//            {".z", "application/x-compress"},
//            {".zip", "application/x-zip-compressed"},
//            {"", "*/*"}
//    };
}
