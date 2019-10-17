package com.example.wyf.classchat.feature.message.chat;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.EditText;

import com.example.wyf.classchat.bean.MessageEvent;
import com.example.wyf.classchat.bean.NoticeBean;
import com.example.wyf.classchat.bean.ReaderBean;
import com.example.wyf.classchat.bean.RegisterInfo;
import com.example.wyf.classchat.bean.RegisterStatus;
import com.example.wyf.classchat.constants.Constants;
import com.example.wyf.classchat.listener.BmobCallbackImpl;
import com.example.wyf.classchat.util.BmobUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.listener.ValueEventListener;

import static com.example.wyf.classchat.constants.Constants.CHATTYPE_GROUP;

/**
 * Created by Administrator on 2018/1/21/021.
 */

public class ChatPagePresenter implements ChatPageContract.Presenter {

    private final Activity activity;
    private final ChatPageContract.View view;
    private BmobRealTimeData realTimeData;
    private String mObjectId;
    private String rollcallId;

    public ChatPagePresenter(Activity activity, ChatPageContract.View view) {
        this.activity = activity;
        this.view = view;
    }

    @Override
    public void sendMessage(EditText etInput, String type, String userId) {
        String content = etInput.getText().toString().trim();
        etInput.setText("");
        EMMessage message = EMMessage.createTxtSendMessage(content, userId);
        if (type.equals(CHATTYPE_GROUP)) {
            message.setChatType(EMMessage.ChatType.GroupChat);
        }
        EMClient.getInstance().chatManager().sendMessage(message);
        message.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                activity.runOnUiThread(() -> view.onSendMessageSuccess(message, content));
            }

            @Override
            public void onError(int i, String s) {
                // 消息发送失败，打印下失败的信息，正常操作应该去刷新ui
            }

            @Override
            public void onProgress(int i, String s) {
                // 消息发送进度，一般只有在发送图片和文件等消息才会有回调，txt不回调
            }
        });
    }

    @SuppressWarnings("unchecked")
    public void querryReaderFromBmob(String noticeId, String userId) {
        HashMap<String, String> param = new HashMap<>();
        param.put("userId", userId);
        param.put("noticeObjectId", noticeId);
        //查看此用户是否阅读公告
        BmobUtils.getInstance().querryReader(param, 1, new BmobCallbackImpl() {
            @Override
            public void success(Object object) {

            }

            @Override
            public void fail() {
                //此用户没有阅读过公告
                ReaderBean bean = new ReaderBean(userId, noticeId);
                BmobUtils.getInstance().saveData(bean, new BmobCallbackImpl() {
                    @Override
                    public void success(Object object) {

                    }
                });
            }
        });

    }

    @Override
    @SuppressWarnings("unchecked")
    public void initNoticeData(long millsTime, String userId) {
        if (millsTime == 0) {
            return;
        }
        Map<String, String> param = new HashMap<>();
        param.put("groupId", userId);
        //查询公告数据
        BmobUtils.getInstance().querryNoticeData(param, 500, new BmobCallbackImpl() {
            @Override
            public void success(Object object) {
                //得到最近公告发布的时间
                List<NoticeBean> list = (List<NoticeBean>) object;
                NoticeBean bean = getMaxTimeBean(list);
                assert bean != null;
                String str = bean.getRederStr();
                String currentUser = EMClient.getInstance().getCurrentUser();
                if (str.contains(currentUser) && !bean.getUser().equals(currentUser)) {
                    long tempTime = bean.getMillsTime();
                    if (tempTime - millsTime > 0) {
                        EventBus.getDefault().post(new MessageEvent(Constants.DISPLAY_NOTICE, bean));
                    }
                }
            }
        });
    }

    private NoticeBean getMaxTimeBean(List<NoticeBean> object) {
        NoticeBean noticeBean = object.get(0);
        int totalCount = object.size();
        if (totalCount >= 1) {
            long max = object.get(0).getMillsTime();
            for (int i = 0; i < totalCount; i++) {
                long temp = object.get(i).getMillsTime();
                if (temp > max) {
                    max = temp;
                    noticeBean = object.get(i);
                }
            }
            return noticeBean;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public void savaRegisterToBmob(String rollcallId) {
        RegisterInfo registerInfo = new RegisterInfo();
        registerInfo.setStatus("到");
        //将考勤的人数保存到Bmob
        BmobUtils.getInstance().update(registerInfo, rollcallId, new BmobCallbackImpl() {
            @Override
            public void success(Object object) {

            }
        });
    }

    @Override
    public void removeRegisterListener() {
        //移除Bmob监听
        if (realTimeData != null && !TextUtils.isEmpty(mObjectId)) {
            realTimeData.unsubRowUpdate("RegisterInfo", mObjectId);
        }
    }

    public void addRegisterListener(String id) {
        if (realTimeData == null) {
            realTimeData = new BmobRealTimeData();
            realTimeData.start(new ValueEventListener() {
                @Override
                public void onConnectCompleted(Exception e) {
                    if (realTimeData.isConnected()) {
                        watchIsRegister(id);
                    }
                }

                @Override
                public void onDataChange(JSONObject jsonObject) {
                    //数据改变
                    //解析返回的数据，根据boolean判断是否取消监听，true就继续监听，如果为false就取消监听
                    JSONObject data = jsonObject.optJSONObject("data");
                    String week = data.optString("week"); //当前考勤周次
                    String times = data.optString("times"); //当前考勤次数
                    String clazz = data.optString("clazz"); //当前考勤课程
                    String matchingCode = data.optString("content"); //当前考勤匹配码
                    String member = data.optString("members");
                    boolean isRegister = data.optBoolean("isRegister");
                    if (isRegister) {
                        //监听到字段变化
                        String[] split = member.split("-");
                        if (split.length > 0) {
                            //过滤是否为需要考勤的成员
                            for (String s : split) {
                                if (EMClient.getInstance().getCurrentUser().equals(s)) {
                                    queryRegisterInfo(id, week, times, clazz);
                                    view.showRollcallView(rollcallId, matchingCode);
                                }
                            }
                        }
                    }
                }
            });

        }
    }

    @SuppressWarnings("unchecked")
    private void queryRegisterInfo(String id, String registerWeek, String registerTimes, String registerClazz) {
        HashMap<String, String> param = new HashMap<>();
        param.put("groupId", id);
        param.put("userId", EMClient.getInstance().getCurrentUser());
        param.put("week", registerWeek);
        param.put("times", registerTimes);
        param.put("clazz", registerClazz);
        BmobUtils.getInstance().querryRegisterInfo(param, new BmobCallbackImpl() {
            @Override
            public void success(Object object) {
                RegisterInfo registerInfo = ((List<RegisterInfo>) object).get(0);
                rollcallId = registerInfo.getObjectId();
            }
        });
    }

    /**
     * 监听考勤表
     */
    @SuppressWarnings("unchecked")
    private void watchIsRegister(String friendId) {
        HashMap<String, String> param = new HashMap<>();
        param.put("groupId", friendId);
        BmobUtils.getInstance().querryRegisterStatus(param, new BmobCallbackImpl() {
            @Override
            public void success(Object object) {
                List<RegisterStatus> list = (List<RegisterStatus>) object;
                mObjectId = list.get(0).getObjectId();
                realTimeData.subRowUpdate("RegisterStatus", mObjectId);
            }
        });
    }
}
