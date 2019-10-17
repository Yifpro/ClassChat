package com.example.wyf.classchat.feature.message.chat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wyf.classchat.R;
import com.example.wyf.classchat.adapter.common.RvAdapter;
import com.example.wyf.classchat.adapter.common.RvViewHolder;
import com.example.wyf.classchat.base.IAppInitContract;
import com.example.wyf.classchat.bean.MessageBody;
import com.example.wyf.classchat.bean.MessageEvent;
import com.example.wyf.classchat.bean.NoticeBean;
import com.example.wyf.classchat.constants.Constants;
import com.example.wyf.classchat.db.FileType;
import com.example.wyf.classchat.db.Person;
import com.example.wyf.classchat.db.util.DatabaseUtils;
import com.example.wyf.classchat.feature.contact.detail.ContactInfoActivity;
import com.example.wyf.classchat.feature.group.GroupSettingsActivity;
import com.example.wyf.classchat.feature.group.notice.NoticeActivity;
import com.example.wyf.classchat.feature.keyboard.AutoInputFragment;
import com.example.wyf.classchat.feature.keyboard.EmotionUtils;
import com.example.wyf.classchat.feature.keyboard.SpanStringUtils;
import com.example.wyf.classchat.util.BitmapUtils;
import com.example.wyf.classchat.weight.AutoToolbar;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMucSharedFile;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.util.DateUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

import static com.example.wyf.classchat.constants.Constants.CHATTYPE_GROUP;

public class ChatActivity extends AppCompatActivity implements EMMessageListener, IAppInitContract.IActivity,
        IAppInitContract.IToolbar, IAppInitContract.ISubscribe, ChatPageContract.View {

    @BindView(R.id.toolbar)
    AutoToolbar toolbar;
    @BindView(R.id.et_input)
    EditText etInput;
    @BindView(R.id.iv_voice)
    ImageView ivVoice;
    @BindView(R.id.iv_expression)
    ImageView ivExpression;
    @BindView(R.id.iv_more)
    ImageView ivMore;
    @BindView(R.id.btn_send)
    Button btnSend;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.ll_notice_more)
    LinearLayout llNoticeMore;
    @BindView(R.id.tv_notice_title)
    TextView tvNoticeTitle;
    @BindView(R.id.tv_notice_author)
    TextView tvNoticeAuthor;
    @BindView(R.id.tv_notice_time)
    TextView tvNoticeTime;
    @BindView(R.id.tv_notice_read_count)
    TextView tvNoticeReadCount;
    @BindView(R.id.tv_notice_content)
    TextView tvNoticeContent;
    @BindView(R.id.btn_notice_know)
    Button btnNoticeKnow;
    @BindView(R.id.et_rollcall_matching_code)
    EditText etRollcallMatchingCode;
    @BindView(R.id.tv_rollcall_rest_time)
    TextView tvRollcallRestTime;
    @BindView(R.id.btn_rollcall_cancel)
    Button btnCancel;
    @BindView(R.id.btn_rollcall_commit)
    Button btnCommit;
    @BindView(R.id.notice)
    View notice;
    @BindView(R.id.register)
    View register;

    private static final String TAG = "ChatActivity";
    private String id;
    private RvAdapter<MessageBody> adapter;
    private List<MessageBody> list = new ArrayList<>();
    private EMConversation conversation;
    private String indexMsgId;
    private int count = 0;
    private String type;
    private Timer timer;
    private static String noticeId; //公告数据的objectId
    private ArrayList<String> admin;
    private ChatPageContract.Presenter present;

    private int emotion_map_type = EmotionUtils.EMOTION_CLASSIC_TYPE;
    private int closeTime = 30;
    private String preTime;
    private String curTime;
    private AutoInputFragment autoInputFragment;

    @Override
    public void showRollcallView(String rollcallId, String matchingCode) {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                closeTime--;
                runOnUiThread(() -> {
                    tvRollcallRestTime.setText(String.valueOf(closeTime));
                    if (closeTime == 0 && timer != null) {
                        closeTime = 30;
                        timer.cancel();
                        register.setVisibility(View.GONE);
                    }
                });
            }
        };
        etRollcallMatchingCode.setText("");
        register.setVisibility(View.VISIBLE);
        timer.schedule(task, 1000, 1000);
        btnCancel.setOnClickListener(view -> register.setVisibility(View.GONE));
        btnCommit.setOnClickListener(view -> {
            //点名提交按钮
            String content = etRollcallMatchingCode.getText().toString().trim();
            if (content.equals(matchingCode)) {
                //将参与点名的名单保存到Bomb
                Toast.makeText(ChatActivity.this, "签到成功", Toast.LENGTH_SHORT).show();
                present.savaRegisterToBmob(rollcallId);
                register.setVisibility(View.GONE);
            } else {
                Toast.makeText(ChatActivity.this, "匹配信息不一致", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private MessageBody getMsgBody(EMMessage msg) {
        return new MessageBody(EMClient.getInstance().getCurrentUser().equals(msg.getFrom()) ? 0 : 1, ((EMTextMessageBody) msg.getBody())
                .getMessage(), DateUtils.getTimestampString(new Date(msg.getMsgTime())), msg.getFrom());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "扫描取消", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "扫描成功，条码值" + result.getContents(), Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onMessageReceived(List<EMMessage> list) {
        // 循环遍历当前收到的消息
        for (final EMMessage message : list) {
            runOnUiThread(() -> {
                this.list.add(getMsgBody(message));
                adapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(this.list.size() - 1);
            });
        }
    }

    @SuppressWarnings("unchecked")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void displayNotice(MessageEvent event) {
        if (event.getWhat() == Constants.DISPLAY_NOTICE) {
            //填充公告布局数据
            NoticeBean noticeBean = ((NoticeBean) event.getMessage());
            noticeId = noticeBean.getObjectId();
            notice.setVisibility(View.VISIBLE);
            tvNoticeContent.setText(noticeBean.getContent());
            tvNoticeAuthor.setText(noticeBean.getUser());
            tvNoticeTime.setText(noticeBean.getTime());
            tvNoticeTitle.setText(noticeBean.getTitle());
        } else if (event.getWhat() == Constants.REFRESH_GROUP_ADMIN) {
            if (admin == null || admin.size() == 0) {
                admin = (ArrayList<String>) event.getMessage();
            }
        }
    }

    @OnClick({R.id.ll_notice_more, R.id.btn_notice_know, R.id.layout_edit, R.id.btn_send})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_edit:
                LinearLayout llExpression = autoInputFragment.getEmotionBar();
                if (llExpression.isShown()) {
                    llExpression.setVisibility(View.GONE);
                }
                etInput.requestFocus();
                showSoftInput(ChatActivity.this, etInput);
                recyclerView.postDelayed(() -> recyclerView.scrollToPosition(list.size() - 1), 250);
                break;
            case R.id.btn_send:
                present.sendMessage(etInput, type, id);
                break;
            case R.id.ll_notice_more:
                present.querryReaderFromBmob(noticeId, EMClient.getInstance().getCurrentUser());
                startActivity(new Intent(ChatActivity.this, NoticeActivity.class).putExtra("userId", id));
                notice.setVisibility(View.GONE);
                break;
            case R.id.btn_notice_know:
                //查看此用户是否已阅读此公告
                present.querryReaderFromBmob(noticeId, EMClient.getInstance().getCurrentUser());
                notice.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onSendMessageSuccess(EMMessage message, String content) {
        runOnUiThread(() -> {
            list.add(new MessageBody(0, content, DateUtils.getTimestampString(new Date(message.getMsgTime())), message.getFrom()));
            adapter.notifyItemChanged(list.size() - 1);
            recyclerView.smoothScrollToPosition(list.size() - 1);
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void init() {
        setPresent(new ChatPagePresenter(this, this));

        id = getIntent().getStringExtra("userId");
        type = getIntent().getStringExtra("type");
        long millsTime = getIntent().getLongExtra("lastTime", 0);
        conversation = EMClient.getInstance().chatManager().getConversation(id);

        //添加Bmob数据监听
        present.addRegisterListener(id);

        initAutoInputFragment();
        initRecyclerView();
        initConversation();
        if (type.equals(CHATTYPE_GROUP)) {
            present.initNoticeData(millsTime, id);
        }
    }

    private void initAutoInputFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        autoInputFragment = AutoInputFragment.newInstance()
                .bindToContentView(recyclerView)
                .bindToEditText(etInput)
                .bindToExpressionButton(ivExpression)
                .setOnClickExpressionBtnListener(() -> recyclerView.postDelayed(() -> recyclerView.scrollToPosition(list.size() - 1), 250));
        transaction.add(R.id.fl_expression_bar, autoInputFragment);
        transaction.commit();
    }

    private void initRecyclerView() {
        adapter = getMsgAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setOnTouchListener((view, motionEvent) -> {
            hideSoftInput(ChatActivity.this, etInput);
            autoInputFragment.getKeyboard().hideEmotionLayout(false);
            return false;
        });
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(true);
            loadChatHistory();
            refreshLayout.setRefreshing(false);
        });
    }

    public void showSoftInput(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    public void hideSoftInput(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

    @NonNull
    private RvAdapter<MessageBody> getMsgAdapter() {
        return new RvAdapter<MessageBody>(this, R.layout.activity_chat_item, list) {
            @Override
            public void convert(RvViewHolder holder, int position, MessageBody body) {
                LinearLayout leftLayout = holder.getView(R.id.ll_left);
                LinearLayout rightLayout = holder.getView(R.id.ll_right);
                ImageView leftIcon = holder.getView(R.id.iv_left_icon);
                ImageView rightIcon = holder.getView(R.id.iv_right_icon);
                if (isShowTime(body)) {
                    holder.getView(R.id.tv_send_time).setVisibility(View.VISIBLE);
                    holder.setText(R.id.tv_send_time, curTime);
                } else {
                    holder.getView(R.id.tv_send_time).setVisibility(View.GONE);
                }
                if (body.getType() == 0) {
                    //显示在右边
                    leftLayout.setVisibility(View.GONE);
                    rightLayout.setVisibility(View.VISIBLE);
                    TextView tvRightMsg = (TextView) holder.getView(R.id.tv_right_message);
                    tvRightMsg.setText(SpanStringUtils.getEmotionContent(emotion_map_type, ChatActivity.this,
                            tvRightMsg, body.getMessage()));
                    BitmapUtils.setIcon(ChatActivity.this, rightIcon, FileType.CONTACT, EMClient.getInstance().getCurrentUser());
                } else {
                    //显示在左边
                    rightLayout.setVisibility(View.GONE);
                    leftLayout.setVisibility(View.VISIBLE);
                    TextView tvLeftMsg = (TextView) holder.getView(R.id.tv_left_message);
                    tvLeftMsg.setText(SpanStringUtils.getEmotionContent(emotion_map_type, ChatActivity.this,
                            tvLeftMsg, body.getMessage()));
                    BitmapUtils.setIcon(ChatActivity.this, leftIcon, FileType.CONTACT, id);
                }
            }
        };
    }

    private boolean isShowTime(MessageBody body) {
        curTime = body.getTime();
        if (preTime == null) {
            preTime = curTime;
            return true;
        }
        int preSpace = preTime.indexOf(" ");
        int curSpace = curTime.indexOf(" ");
        String preWeek = preTime.substring(0, preSpace);
        String curWeek = curTime.substring(0, curSpace);
        Log.e(TAG, "Week: " + preWeek + ", " + curWeek + ", " + curTime);
        int preIndex = preTime.indexOf(":");
        int curIndex = curTime.indexOf(":");
        String preHour = preTime.substring(preIndex - 2, preIndex);
        String curHour = curTime.substring(curIndex - 2, curIndex);
        Log.e(TAG, "Hour: " + preHour + ", " + curHour + ", " + curTime);
        String preMinute = preTime.substring(preIndex + 1);
        String curMinute = curTime.substring(curIndex + 1);
        Log.e(TAG, "Minute: " + preMinute + ", " + curMinute + ", " + curTime);
        if (preWeek.equals(curWeek) && preHour.equals(curHour) && preMinute.equals(curMinute)) {
            preTime = curTime;
            return false;
        }
        preTime = curTime;

        return true;
    }

    /**
     * 加载聊天历史记录
     */
    private void loadChatHistory() {
        if (count > 0) {
            int page = count > 10 ? 10 : count;
            List<EMMessage> emMessages = conversation.loadMoreMsgFromDB(indexMsgId, page);
            if (emMessages.size() > 0) {
                indexMsgId = emMessages.get(0).getMsgId();
                for (int i = emMessages.size() - 1; i >= 0; i--) {
                    EMMessage msg = emMessages.get(i);
                    list.add(0, getMsgBody(msg));
                }
            }
            adapter.notifyDataSetChanged();
            count = count - page;
        }
    }

    /**
     * 初始化会话
     */
    private void initConversation() {
        EMMessage lastMessage = null;
        if (conversation != null) {
            conversation.markAllMessagesAsRead();
            count = conversation.getAllMsgCount();
            lastMessage = conversation.getLastMessage();
        }
        if (lastMessage != null) {
            String lastMsgId = lastMessage.getMsgId();
            int page = count > 9 ? 9 : count;
            List<EMMessage> emMessages = conversation.loadMoreMsgFromDB(lastMsgId, page);
            if (emMessages.size() > 0) {
                indexMsgId = emMessages.get(0).getMsgId();
                for (EMMessage msg : emMessages) {
                    list.add(getMsgBody(msg));
                }
            }
            list.add(getMsgBody(lastMessage));
            count = count - page - 1;
        }
        if (list.size() > 0) {
            recyclerView.smoothScrollToPosition(list.size() - 1);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initToolbar() {
        toolbar.setRightIcon(R.drawable.ic_contact_function)
                .setLeftIcon(R.drawable.ic_left_back)
                .setOnLeftIconClickEvent(v -> finish());
        if (Constants.CHATTYPE_SINGLE.equals(type)) {
            Person findPerson = (Person) DatabaseUtils.queryData(FileType.CONTACT, id);
            toolbar.setTitleText(findPerson != null ? findPerson.getName() : "");
            toolbar.setOnRightIconClickEvent(v -> startActivity(new Intent(ChatActivity.this, ContactInfoActivity.class)
                    .putExtra("userId", id)));
        } else {
            toolbar.setTitleText(EMClient.getInstance().groupManager().getGroup(id).getGroupName());
            toolbar.setOnRightIconClickEvent(v -> startActivity(new Intent(ChatActivity.this, GroupSettingsActivity.class)
                    .putExtra("userId", id)));
        }
    }

    @OnTextChanged(R.id.et_input)
    public void onTextChanged(Editable editable) {
        if (etInput.getText().toString().trim().length() > 0) {
            btnSend.setClickable(true);
            btnSend.setBackground(getResources().getDrawable(R.drawable.shape_btn_corner));
        } else {
            btnSend.setClickable(false);
            btnSend.setBackground(getResources().getDrawable(R.drawable.shape_btn_corner_unclickable));
        }
    }

    @Override
    public String getToolbarTitle() {
        return "";
    }

    @Override
    public boolean isShowHome() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_chat;
    }

    @Override
    public void setPresent(ChatPageContract.Presenter present) {
        this.present = present;
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> list) {

    }

    @Override
    public void onMessageRead(List<EMMessage> list) {

    }

    @Override
    public void onMessageDelivered(List<EMMessage> list) {

    }

    @Override
    public void onMessageRecalled(List<EMMessage> list) {

    }

    @Override
    public void onMessageChanged(EMMessage emMessage, Object o) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        // 添加消息监听
        EMClient.getInstance().chatManager().addMessageListener(this);
        //添加群组事件监听
        EMClient.getInstance().groupManager().addGroupChangeListener(mEMGroupChangeListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 移除消息监听
        EMClient.getInstance().chatManager().removeMessageListener(this);
        //移除群组事件监听
        EMClient.getInstance().groupManager().removeGroupChangeListener(mEMGroupChangeListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //移除Bmob监听
        present.removeRegisterListener();
    }

    @Override
    public void onBackPressed() {
        /**
         * 判断是否拦截返回键操作
         */
        if (!autoInputFragment.isInterceptBackPress()) {
            super.onBackPressed();
        }
    }

    //群组事件监听
    private EMGroupChangeListener mEMGroupChangeListener = new EMGroupChangeListener() {
        @Override
        public void onMemberJoined(String groupId, String userId) {

        }

        @Override
        public void onInvitationReceived(String s, String s1, String s2, String s3) {
            //接收到群组加入邀请
        }

        @Override
        public void onRequestToJoinReceived(String s, String s1, String s2, String s3) {
            //用户申请加入群
        }

        @Override
        public void onRequestToJoinAccepted(String s, String s1, String s2) {
            //加群申请被同意
        }

        @Override
        public void onRequestToJoinDeclined(String s, String s1, String s2, String s3) {
            //加群申请被拒绝
        }

        @Override
        public void onInvitationAccepted(String s, String s1, String s2) {

        }

        @Override
        public void onInvitationDeclined(String s, String s1, String s2) {

        }

        @Override
        public void onUserRemoved(String s, String s1) {

        }

        @Override
        public void onGroupDestroyed(String s, String s1) {

        }

        @Override
        public void onAutoAcceptInvitationFromGroup(String s, String s1, String s2) {

        }

        @Override
        public void onMuteListAdded(String s, List<String> list, long l) {

        }

        @Override
        public void onMuteListRemoved(String s, List<String> list) {

        }

        @Override
        public void onAdminAdded(String s, String s1) {

        }

        @Override
        public void onAdminRemoved(String s, String s1) {

        }

        @Override
        public void onOwnerChanged(String s, String s1, String s2) {

        }

        @Override
        public void onMemberExited(String s, String s1) {

        }

        @Override
        public void onAnnouncementChanged(String s, String s1) {

        }

        @Override
        public void onSharedFileAdded(String s, EMMucSharedFile emMucSharedFile) {

        }

        @Override
        public void onSharedFileDeleted(String s, String s1) {

        }
    };
}
