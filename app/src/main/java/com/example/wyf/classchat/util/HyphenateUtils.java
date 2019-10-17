package com.example.wyf.classchat.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.wyf.classchat.R;
import com.example.wyf.classchat.adapter.common.RvAdapter;
import com.example.wyf.classchat.adapter.common.RvViewHolder;
import com.example.wyf.classchat.bean.GroupBmob;
import com.example.wyf.classchat.bean.MessageEvent;
import com.example.wyf.classchat.bean.NoticeDateBean;
import com.example.wyf.classchat.bean.PersonBmob;
import com.example.wyf.classchat.bean.RegisterStatus;
import com.example.wyf.classchat.bean.TimeEvent;
import com.example.wyf.classchat.constants.Constants;
import com.example.wyf.classchat.db.FileType;
import com.example.wyf.classchat.db.Group;
import com.example.wyf.classchat.db.util.DatabaseUtils;
import com.example.wyf.classchat.db.Person;
import com.example.wyf.classchat.feature.message.chat.ChatActivity;
import com.example.wyf.classchat.listener.BmobCallbackImpl;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCursorResult;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import rx.Observable;

/**
 * Created by WYF on 2017/10/16.
 */

public class HyphenateUtils {

    /**
     * 添加好友
     *
     * @param activity 上下文
     * @param id       用户id
     */
    public static void addFriend(final Activity activity, final String id) {
        BmobQuery<PersonBmob> query = new BmobQuery<>();
        query.addWhereEqualTo("id", id);
        query.findObjects(new FindListener<PersonBmob>() {
            @Override
            public void done(final List<PersonBmob> object, final BmobException e) {
                if (e == null) {
                    if (object.size() > 0) {
                        try {
                            EMClient.getInstance().contactManager().addContact(id, "");
                            PersonBmob personBmob = object.get(0);
                            DatabaseUtils.insertPerson(personBmob);
                            BitmapUtils.saveIconToLocal(FileType.CONTACT, id, id, personBmob.getIcon());
                            Toast.makeText(activity, "添加好友成功", Toast.LENGTH_SHORT).show();
                        } catch (HyphenateException e1) {
                            e1.printStackTrace();
                        }
                    }
                } else {
                    Log.e("addFriend", "addFriend failed：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    /**
     * 添加群
     *
     * @param activity 上下文
     * @param id       群id
     */
    public static void addGroup(Activity activity, String id) {
        Observable.defer(() -> Observable.just(requestCreate(id)))
                .compose(RxSchedulersHelper.ioToMain())
                .subscribe(error -> {
                    if (error == null) {
                        Toast.makeText(activity, "加入新群成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private static String requestCreate(String id) {
        try {
            EMClient.getInstance().groupManager().joinGroup(id);
        } catch (HyphenateException e) {
            e.printStackTrace();
            return e.getErrorCode() + "， " + e.getMessage();
        }
        return null;
    }

    /**
     * 封装用户详细信息
     *
     * @param contactIdList 环信id列表
     */
    @SuppressWarnings("unchecked")
    public static void requestContactInfo(final List<String> contactIdList, final int what) {
        final List<PersonBmob> contactList = new ArrayList<>();
        for (String id : contactIdList) {
            final Person person = (Person) DatabaseUtils.queryData(FileType.CONTACT, id);
            //优先从数据库查找，否则从bmob获取
            if (person != null) {
                contactList.add(new PersonBmob(person.getId(), person.getName()));
                if (contactList.size() == contactIdList.size()) {
                    EventBus.getDefault().post(new MessageEvent(what, contactList));
                }
            } else {
                HashMap<String, String> map = new HashMap<>();
                map.put("id", id);
                BmobUtils.getInstance().querryPerson(map, new BmobCallbackImpl() {
                    @Override
                    public void success(Object object) {
                        PersonBmob personBmob = ((List<PersonBmob>) object).get(0);
                        contactList.add(personBmob);
                        DatabaseUtils.insertPerson(personBmob);
                        BitmapUtils.saveIconToLocal(FileType.CONTACT, personBmob.getId(), personBmob.getId(), personBmob.getIcon());
                        if (contactList.size() == contactIdList.size()) {
                            EventBus.getDefault().post(new MessageEvent(what, contactList));
                        }
                    }
                });
            }
        }
    }

    public static void refresh(final Activity activity, final RvAdapter rvAdapter, final RecyclerView recyclerView) {
        activity.runOnUiThread(() -> {
            recyclerView.setAdapter(rvAdapter);
            rvAdapter.notifyDataSetChanged();
        });
    }

    /**
     * 加载群头像
     *
     * @param activity
     * @param holder
     * @param group
     */
    @SuppressWarnings("unchecked")
    public static void loadGroupIcon(Activity activity, final RvViewHolder holder, final Group group) {
        ImageView v = holder.getView(R.id.group_icon);
        v.setTag(group.getId());
        HashMap<String, String> map = new HashMap<>();
        map.put("id", group.getId());
        BmobUtils.getInstance().querryGroup(map, new BmobCallbackImpl() {
            @Override
            public void success(Object object) {
                GroupBmob g = ((List<GroupBmob>) object).get(0);
                if (g.getId().equals(v.getTag())) {
                    Log.e("test", "success: go equals");
                    if (g.getIcon() != null) {
                        BitmapUtils.saveIconToLocal(FileType.GROUP, g.getId(), g.getId(), g.getIcon());
                        //((ImageView) holder.getView(R.id.group_icon)).setImageBitmap(BitmapFactory.decodeFile(BitmapUtils.getPath(FileType.GROUP, g.getId(), g.getId())));
                        //Glide.with(activity).load(new File(BitmapUtils.getPath(FileType.GROUP, g.getId(), g.getId()))).into((ImageView)holder.getView(R.id.group_icon));
                    } else {
                        BitmapUtils.saveIconToLocal(FileType.GROUP, g.getId(), g.getId(), R.drawable.ic_head_place);
                    }
                    BitmapUtils.setIcon(activity, holder.getView(R.id.group_icon), FileType.GROUP, g.getId());
                    Log.e("test", "success: i will send1");
                    DatabaseUtils.insertGroup(g);
                    Log.e("test", "success: i will send2");
                }
            }

            @Override
            public void fail() {
                //表中找不到，初始化群信息
                BmobUtils.getInstance().saveData(new RegisterStatus(group.getId(), false), new BmobCallbackImpl() {
                    @Override
                    public void success(Object object) {

                    }
                });
                BmobUtils.getInstance().saveData(new GroupBmob(group.getId(), group.getName(), group.getDesc()), new BmobCallbackImpl() {
                    @Override
                    public void success(Object object) {

                    }
                });
            }
        });
    }

    /**
     * 得到当前网络时间
     *
     * @param user    用户id
     * @param groupId 群id
     * @return 网络时间
     */
    public static TimeEvent loadNetTime(final String user, final String groupId) {
        URL url;
        try {
            url = new URL("http://www.baidu.com");
            URLConnection uc = url.openConnection();
            uc.connect();
            //取得网站日期时间
            long tempTime = uc.getDate();
            //将日期格式化
            SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss");
            format.setTimeZone(TimeZone.getTimeZone("GMT+08"));
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(tempTime);
            String formatTime = format.format(calendar.getTime());
            //查看数据库是否有信息，有则更新，没有则保存
            return new TimeEvent(tempTime, formatTime, groupId, user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static void getDateFromServer(Activity activity, TimeEvent event) {
        String user = event.getUser();
        String time = event.getFormatTime();
        long tempTime = event.getTempTime();
        String groupId = event.getGroupId();
        final HashMap<String, String> params = new HashMap<>();
        params.put("groupId", groupId);
        params.put("user", user);
        BmobUtils.getInstance().querryNoticeDataBean(params, 1, new BmobCallbackImpl() {
            @Override
            public void success(Object object) {
                List<NoticeDateBean> beans = (List<NoticeDateBean>) object;
                long lastTime = beans.get(0).getMillisTime();
                String objectId = beans.get(0).getObjectId();
                NoticeDateBean bean = new NoticeDateBean();
                bean.setTime(time);
                bean.setMillisTime(tempTime);
                BmobUtils.getInstance().update(bean, objectId, new BmobCallbackImpl() {
                    @Override
                    public void success(Object object) {

                    }
                });
                goToChat(activity, lastTime, groupId);
            }

            @Override
            public void fail() {
                //此判断表示该用户从没有进入过该群
                //保存数据
                NoticeDateBean gameScore = new NoticeDateBean(user, time, tempTime, groupId);
                BmobUtils.getInstance().saveData(gameScore, new BmobCallbackImpl() {
                    @Override
                    public void success(Object object) {

                    }
                });
                goToChat(activity, -1, groupId);
            }
        });
    }

    private static void goToChat(Activity activity, long lastTime, String groupId) {
        activity.startActivity(new Intent(activity, ChatActivity.class)
                .putExtra("type", Constants.CHATTYPE_GROUP)
                .putExtra("userId", groupId)
                .putExtra("lastTime", lastTime));
    }

    public static void getMembers(final String userId) {
        new Thread() {
            @Override
            public void run() {
                EMCursorResult<String> result = null;
                List<String> memberList = new ArrayList<>();
                final int pageSize = 20;
                try {
                    EMGroup group = EMClient.getInstance().groupManager().getGroupFromServer(userId);
                    String owner = group.getOwner();
                    List<String> adminList = group.getAdminList();

                    do {
                        result = EMClient.getInstance().groupManager().fetchGroupMembers(userId,
                                result != null ? result.getCursor() : "", pageSize);
                        memberList.addAll(result.getData());
                    }
                    while (!TextUtils.isEmpty(result.getCursor()) && result.getData().size() == pageSize);

                    memberList.add(owner);
                    memberList.addAll(adminList);
                    HyphenateUtils.requestContactInfo(memberList, Constants.GET_MEMBERS_INFO);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 获取群成员
     *
     * @param userId
     * @param adminList
     */
    public static List<String> getMembers(final String userId, String owner, List<String> adminList) {
        EMCursorResult<String> result = null;
        List<String> memberList = new ArrayList<>();
        final int pageSize = 20;
        try {
            do {
                result = EMClient.getInstance().groupManager().fetchGroupMembers(userId,
                        result != null ? result.getCursor() : "", pageSize);
                memberList.addAll(result.getData());
            }
            while (!TextUtils.isEmpty(result.getCursor()) && result.getData().size() == pageSize);
            memberList.add(owner);
            memberList.addAll(adminList);
            return memberList;
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<String> getAdminList(String id) {
        try {
            EMGroup group = EMClient.getInstance().groupManager().getGroupFromServer(id);
            ArrayList<String> list = (ArrayList<String>) group.getAdminList();
            list.add(0, group.getOwner());
            return list;
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
        return null;
    }

    //观看文件
    public static void openFile(Context context, File file) {
        try {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //设置intent的Action属性
            intent.setAction(Intent.ACTION_VIEW);
            //获取文件file的MIME类型
            String type = getMIMEType(file);
            Log.i("type", type);
            //设置intent的data和Type属性。
            intent.setDataAndType(Uri.fromFile(file), type);
//            intent.setDataAndType(FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file), type);
            //跳转
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "附件不能打开，请下载相关软件！", Toast.LENGTH_SHORT).show();
        }
    }

    private static String getMIMEType(File file) {
//.FileUriExposedException: file:///storage/head.png exposed beyond app through Intent.getData()
        String type = "*/*";
        String fName = file.getName();
        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }
        /* 获取文件的后缀名*/
        String end = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (end == "") return type;
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for (int i = 0; i < MIME_MapTable.length; i++) {
            if (end.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }

    private static String[][] MIME_MapTable = {
            //{后缀名，MIME类型}
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {".xls", "application/vnd.ms-excel"},
            {".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
            {".prop", "text/plain"},
            {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".rtf", "application/rtf"},
            {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".txt", "text/plain"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/x-zip-compressed"},
            {"", "*/*"}
    };

    /**
     * 文件是否存在
     *
     * @param strFile
     * @return
     */
    public static boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                return false;
            }

        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
