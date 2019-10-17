package com.example.wyf.classchat.feature.message;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.wyf.classchat.R;
import com.example.wyf.classchat.adapter.common.RvAdapter;
import com.example.wyf.classchat.adapter.common.RvViewHolder;
import com.example.wyf.classchat.base.BaseFragment;
import com.example.wyf.classchat.bean.ConversationBody;
import com.example.wyf.classchat.constants.Constants;
import com.example.wyf.classchat.db.Conversation;
import com.example.wyf.classchat.db.FileType;
import com.example.wyf.classchat.feature.contact.create.CreateGroupsActivity;
import com.example.wyf.classchat.feature.group.ScanActivity;
import com.example.wyf.classchat.feature.message.chat.ChatActivity;
import com.example.wyf.classchat.util.BitmapUtils;
import com.example.wyf.classchat.util.HyphenateUtils;
import com.example.wyf.classchat.util.ForbidFastClickUtils;
import com.example.wyf.classchat.weight.AutoToolbar;
import com.example.wyf.classchat.weight.MsgCountUnreadView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @author WYF on 2017/9/26.
 */
public class MessageFragment extends BaseFragment implements MessagePageContract.View {

    @BindView(R.id.toolbar)
    AutoToolbar toolbar;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private static final String TAG = "MessageFragment";
    private static final int RESULT_OK = -1;
    private List<ConversationBody> list = new ArrayList<>();
    private MessagePageContract.Presenter present;
    private PopupWindow popupWindow;

    @Override
    public void onStart() {
        super.onStart();
        present.getMessageList();
    }

    @Override
    public void refresh(List<ConversationBody> list) {
        this.list = list;
        HyphenateUtils.refresh(getActivity(), getAdapter(), recyclerView);
    }

    @NonNull
    private RvAdapter<ConversationBody> getAdapter() {
        return new RvAdapter<ConversationBody>(getActivity(), R.layout.fragment_message_item, list) {

            @Override
            public void convert(final RvViewHolder holder, final int position, final ConversationBody body) {
                int type = FileType.CONTACT;
                if (body.getType() == EMConversation.EMConversationType.Chat) {
                    type = FileType.CONTACT;
                } else if (body.getType() == EMConversation.EMConversationType.GroupChat) {
                    type = FileType.GROUP;
                }
                File file = new File(BitmapUtils.getPath(type, body.getId(), body.getId()));
                if (file.exists()) {
                    Glide.with(MessageFragment.this).load(file).into((ImageView) holder.getView(R.id.msg_icon));
                }
                holder.setText(R.id.tv_msg_name, body.getName());
                holder.setText(R.id.tv_last_msg, body.getLastMsg());
                holder.setText(R.id.tv_last_time, body.getLastTime());
                Log.e(TAG, "convert: " + body.getMsgCount());
                MsgCountUnreadView msgCountUnreadView = holder.getView(R.id.tv_new_msg_count);
                if (body.getMsgCount() != null) {
                    msgCountUnreadView.setText(body.getMsgCount());
                    msgCountUnreadView.setVisibility(View.VISIBLE);
                } else {
                    msgCountUnreadView.setVisibility(View.INVISIBLE);
                }
                //点击事件
                holder.setOnClickListener(R.id.msg_layout, view -> {
                    if (ForbidFastClickUtils.isFastClick()) {
                        if (body.getType() == EMConversation.EMConversationType.GroupChat) {
                            String sendUser = EMClient.getInstance().getCurrentUser();
                            present.getNetTime(sendUser, body.getId());
                        } else if (body.getType() == EMConversation.EMConversationType.Chat) {
                            startActivity(new Intent(getActivity(), ChatActivity.class)
                                    .putExtra("userId", body.getId())
                                    .putExtra("type", body.getType() == EMConversation.EMConversationType.GroupChat
                                            ? Constants.CHATTYPE_GROUP : Constants.CHATTYPE_SINGLE));
                        }
                    }
                });

                holder.getView(R.id.msg_layout).setOnLongClickListener(v -> {
                    showConversationPopupWindow(holder, body);
                    return false;
                });
            }
        };
    }

    /**
     * 设置置顶会话和删除会话
     *
     * @param holder
     * @param conversationBody
     */
    private void showConversationPopupWindow(RvViewHolder holder, ConversationBody conversationBody) {
        String id = conversationBody.getId();
        popupWindow = new PopupWindow(mActivity);
        popupWindow.setAnimationStyle(R.style.popwin_anim_style);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        View view = LayoutInflater.from(mActivity).inflate(R.layout.layout_popupwindow_conversation, null);
        TextView tvStickToTheTop = (TextView) view.findViewById(R.id.tv_stick_to_the_top);
        if (conversationBody.isTop()) {
            tvStickToTheTop.setText("取消置顶");
        } else {
            tvStickToTheTop.setText("置顶会话");
        }
        tvStickToTheTop.setOnClickListener(v -> {
            stickToTheTopOrCancal(id, conversationBody.isTop());
        });
        view.findViewById(R.id.tv_del_conversation).setOnClickListener(v1 -> {
            present.deleteConversation(id);
        });
        popupWindow.setContentView(view);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.showAsDropDown(holder.getView(R.id.tv_last_time), 0, 12);
    }

    private void stickToTheTopOrCancal(String id, boolean isTop) {
        Conversation c = new Conversation(id);
        int index = list.indexOf(new ConversationBody(id));
        ConversationBody body = list.get(index);
        list.remove(index);
        recyclerView.getAdapter().notifyItemRemoved(index);
        if (!isTop) {
            body.setTop(true);
            c.save();
            list.add(0, body);
            notifyItemInserted(0);
        } else {
            body.setTop(false);
            c.delete();
            long timeStamp = body.getTimeStamp();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).isTop()) {
                    continue;
                }
                if (timeStamp > list.get(i).getTimeStamp()) {
                    list.add(i, body);
                    notifyItemInserted(i);
                    break;
                } else if (timeStamp < list.get(list.size() - 1).getTimeStamp()) {
                    list.add(list.size(), body);
                    notifyItemInserted(list.size());
                    break;
                }
            }
        }
        popupWindow.dismiss();
    }

    @Override
    public void deleteConversationSuccess(String id) {
        int i = list.indexOf(new ConversationBody(id));
        list.remove(i);
        recyclerView.getAdapter().notifyItemRemoved(i);
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            String contents = intentResult.getContents();
            if (contents != null) {
                if (contents.length() == 10) {
                    HyphenateUtils.addFriend(getActivity(), contents);
                } else if (contents.length() > 10) {
                    HyphenateUtils.addGroup(getActivity(), contents);
                } else {
                    Toast.makeText(mActivity, contents, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void initToolbar() {
        toolbar.inflateMenu(R.menu.toolbar_message);
        toolbar.setTitleText("消息")
                .setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.action_sweek:
                            scanQr();
                            break;
                        case R.id.action_create_group:
                            startActivity(new Intent(getActivity(), CreateGroupsActivity.class));
                            break;
                        case R.id.action_add_friend:

                            break;
                    }
                    return false;
                });
    }

    private void scanQr() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(MessageFragment.this);
        // 设置要扫描的条码类型，ONE_D_CODE_TYPES：一维码，QR_CODE_TYPES-二维码
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setCaptureActivity(ScanActivity.class);
        integrator.setPrompt(""); //底部的提示文字，设为""可以置空
        integrator.setCameraId(0); //前置或者后置摄像头
        integrator.setBeepEnabled(false); //扫描成功的「哔哔」声，默认开启
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }

    @Override
    public void initData() {
        MessagePagePresenter p = new MessagePagePresenter(getActivity(), this);
        setPresent(p);
        p.setListener(id -> {
            //refresh(list);
            recyclerView.getAdapter().notifyItemChanged(list.indexOf(new ConversationBody(id)));
        });
        EMClient.getInstance().chatManager().addMessageListener(emMessageListener);
        recyclerView.setItemAnimator(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(getAdapter());
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(true);
            present.getMessageList();
            refreshLayout.setRefreshing(false);
        });
        present.getMessageList();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_message;
    }

    @Override
    public void onDestroy() {
        EMClient.getInstance().chatManager().removeMessageListener(emMessageListener);
        super.onDestroy();
    }

    private EMMessageListener emMessageListener = new EMMessageListener() {
        @Override
        public void onMessageReceived(List<EMMessage> msgList) {
            present.onMessageReceived(list, msgList);
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
    };

    @Override
    public void notifyItemLastMsgChanged(int index) {
        recyclerView.getAdapter().notifyItemChanged(index);
    }

    @Override
    public void notifyItemInserted(int index) {
        recyclerView.getAdapter().notifyItemInserted(index);
    }

    @Override
    public void setPresent(MessagePageContract.Presenter present) {
        this.present = present;
    }
}
