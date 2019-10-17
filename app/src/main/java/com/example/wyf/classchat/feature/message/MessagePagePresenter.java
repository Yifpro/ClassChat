package com.example.wyf.classchat.feature.message;

import android.app.Activity;
import android.util.Log;
import android.util.Pair;

import com.example.wyf.classchat.bean.ConversationBody;
import com.example.wyf.classchat.bean.GroupBmob;
import com.example.wyf.classchat.bean.PersonBmob;
import com.example.wyf.classchat.db.Conversation;
import com.example.wyf.classchat.db.FileType;
import com.example.wyf.classchat.db.Group;
import com.example.wyf.classchat.db.Person;
import com.example.wyf.classchat.db.util.DatabaseUtils;
import com.example.wyf.classchat.listener.BmobCallbackImpl;
import com.example.wyf.classchat.util.BitmapUtils;
import com.example.wyf.classchat.util.BmobUtils;
import com.example.wyf.classchat.util.HyphenateUtils;
import com.example.wyf.classchat.util.RxSchedulersHelper;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.util.DateUtils;
import com.raizlabs.android.dbflow.sql.language.SQLite;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.exception.BmobException;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * @author Administrator on 2018/1/21/021.
 */
public class MessagePagePresenter implements MessagePageContract.Presenter {

    private final Activity activity;
    private final MessagePageContract.View view;
    private OnGroupInfoLoadListener listener;

    public MessagePagePresenter(Activity activity, MessagePageContract.View view) {
        this.activity = activity;
        this.view = view;
    }

    @Override
    public void onMessageReceived(List<ConversationBody> list, List<EMMessage> msgList) {
        Observable.defer(() -> Observable.just(onMsgReceived(list, msgList)))
                .compose(RxSchedulersHelper.ioToMain())
                .subscribe(index -> {
                    Log.e(TAG, "onMessageReceived: " + isChange);
                    if (isChange) {
                        view.notifyItemLastMsgChanged(index);
                    } else {
                        view.notifyItemInserted(index);
                    }
                });
    }

    private String TAG = "test msg";
    private boolean isChange;

    @SuppressWarnings("unchecked")
    private int onMsgReceived(List<ConversationBody> list, List<EMMessage> msgList) {
        int n = 0;
        Log.e(TAG, "onMsgReceived: " + list.size() + ", " + msgList.size());
        for (EMMessage emMessage : msgList) {
            isChange = false;
            Log.e(TAG, "onMsgReceived: msg is " + emMessage.getBody().toString());
            for (int i = 0; i < list.size(); i++) {
                Log.e(TAG, "onMsgReceived: current id " + list.get(i).getId());
                ConversationBody conversationBody = list.get(i);
                if (conversationBody.getConversationId().equals(emMessage.conversationId())) {
                    Log.e(TAG, "onMsgReceived: id equals from: " + conversationBody.getId() + ", " + emMessage.getFrom());
                    conversationBody.setLastTime(DateUtils.getTimestampString(new Date(emMessage.getMsgTime())));
                    conversationBody.setTimeStamp(emMessage.getMsgTime());
                    conversationBody.setLastMsg(emMessage.getBody().toString().substring(5, emMessage.getBody().toString().length() - 1));
                    if (conversationBody.getMsgCount() != null) {
                        conversationBody.setMsgCount(String.valueOf(Integer.parseInt(conversationBody.getMsgCount()) + 1));
                    } else {
                        conversationBody.setMsgCount("1");
                    }
                    Collections.sort(list);
                    isChange = true;
                    return i;
                }
            }
            /**
             * 当前会话列表无此会话
             */
            if (!isChange) {
                Log.e(TAG, "onMsgReceived: not found conversation");
                ConversationBody body = new ConversationBody();
                body.setConversationId(emMessage.conversationId());
                body.setTimeStamp(emMessage.getMsgTime());
                body.setLastMsg(emMessage.getBody().toString().substring(5, emMessage.getBody().toString().length() - 1));
                body.setLastTime(DateUtils.getTimestampString(new Date(emMessage.getMsgTime())));
                body.setMsgCount(1 + "");
                Log.e(TAG, "onMsgReceived: 封装完毕");
                if (emMessage.getChatType() == EMMessage.ChatType.Chat) {
                    body.setId(emMessage.getFrom());
                    Log.e(TAG, "onMsgReceived: 单聊");
                    body.setType(EMConversation.EMConversationType.Chat);
                    Person findPerson = (Person) DatabaseUtils.queryData(FileType.CONTACT, emMessage.getFrom());
                    if (findPerson != null) {
                        body.setName(findPerson.getName());
                    } else {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("id", emMessage.getFrom());
                        BmobUtils.getInstance().querryPerson(map, new BmobCallbackImpl() {
                            @Override
                            public void success(Object object) {
                                PersonBmob personBmob = ((List<PersonBmob>) object).get(0);
                                if (personBmob.getId().equals(body.getId())) {
                                    body.setName(personBmob.getName());
                                    DatabaseUtils.insertPerson(personBmob);
                                }
                            }
                        });
                    }
                } else if (emMessage.getChatType() == EMMessage.ChatType.GroupChat) {
                    body.setId(emMessage.getTo());
                    Log.e(TAG, "onMsgReceived: 群聊");
                    body.setType(EMConversation.EMConversationType.GroupChat);
                    Group findGroup = (Group) DatabaseUtils.queryData(FileType.GROUP, emMessage.getFrom());
                    if (findGroup != null) {
                        Log.e(TAG, "onMsgReceived: to db " + findGroup.getName());
                        body.setName(findGroup.getName());
                    } else {
                        Log.e(TAG, "onMsgReceived: to server");
                        HashMap<String, String> map = new HashMap<>();
                        map.put("id", emMessage.getTo());
                        BmobUtils.getInstance().querryGroup(map, new BmobCallbackImpl() {
                            @Override
                            public void success(Object object) {
                                GroupBmob groupBmob = ((List<GroupBmob>) object).get(0);
                                Log.e(TAG, "onMsgReceived: not equals server group id");
                                if (groupBmob.getId().equals(body.getId())) {
                                    Log.e(TAG, "onMsgReceived: equals server group id");
                                    body.setName(groupBmob.getName());
                                    listener.onGroupInfoLoad(groupBmob.getId());
                                    DatabaseUtils.insertGroup(groupBmob);
                                }
                            }

                            @Override
                            public void fail() {
                                Log.e(TAG, "onMsgReceived: fail server group id");
                                super.fail();
                            }

                            @Override
                            public void Error(BmobException e) {
                                Log.e(TAG, "onMsgReceived: Error server group id");
                                super.Error(e);
                            }
                        });
                    }
                }
                Log.e(TAG, "onMsgReceived: 能不能走到这里");
                /**
                 * 排序会话
                 */
                if (list.size() != 0) {
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).isTop()) {
                            continue;
                        }
                        if (body.getTimeStamp() > list.get(i).getTimeStamp()) {
                            list.add(i, body);
                            n = i;
                            break;
                        } else if (body.getTimeStamp() < list.get(list.size() - 1).getTimeStamp()) {
                            list.add(list.size(), body);
                            n = list.size();
                            break;
                        }
                    }
                } else {
                    list.add(body);
                }
                for (ConversationBody b : list) {
                    Log.e(TAG, "onMsgReceived: " + b.getId());
                }
            }
        }
        return n;
    }

    @Override
    public void deleteConversation(String id) {
        Observable.defer(() -> Observable.just(deleteConversationFromServer(id)))
                .compose(RxSchedulersHelper.ioToMain())
                .subscribe(view::deleteConversationSuccess);
    }

    private String deleteConversationFromServer(String id) {
        EMClient.getInstance().chatManager().deleteConversation(id, false);
        return id;
    }

    @Override
    public void getNetTime(String user, String groupId) {
        Observable.defer(() -> Observable.just(HyphenateUtils.loadNetTime(user, groupId)))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(event -> HyphenateUtils.getDateFromServer(activity, event));
    }

    @Override
    public void getMessageList() {
        Observable.defer(() -> Observable.just(loadMessageList()))
                .compose(RxSchedulersHelper.ioToMain())
                .subscribe(view::refresh);
    }

    @SuppressWarnings("unchecked")
    private List<ConversationBody> loadMessageList() {
        List<ConversationBody> list = new ArrayList<>();
        for (EMConversation conversation : loadConversationFromServer()) {
            String userId = conversation.conversationId();
            final ConversationBody body = new ConversationBody();
            body.setId(userId);
            body.setType(conversation.getType());
            body.setConversationId(conversation.conversationId());
            if (conversation.getType() == EMConversation.EMConversationType.Chat) {
                Person findPerson = (Person) DatabaseUtils.queryData(FileType.CONTACT, userId);
                if (findPerson != null) {
                    body.setName(findPerson.getName());
                } else {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("id", userId);
                    BmobUtils.getInstance().querryPerson(map, new BmobCallbackImpl() {
                        @Override
                        public void success(Object object) {
                            PersonBmob personBmob = ((List<PersonBmob>) object).get(0);
                            if (personBmob.getId().equals(body.getId())) {
                                body.setName(personBmob.getName());
                                DatabaseUtils.insertPerson(personBmob);
                                BitmapUtils.saveIconToLocal(FileType.CONTACT, userId, userId, personBmob.getIcon());
                            }
                        }
                    });
                }
            } else if (conversation.getType() == EMConversation.EMConversationType.GroupChat) {
                Group findGroup = (Group) DatabaseUtils.queryData(FileType.GROUP, userId);
                if (findGroup != null) {
                    body.setName(findGroup.getName());
                } else {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("id", userId);
                    BmobUtils.getInstance().querryGroup(map, new BmobCallbackImpl() {
                        @Override
                        public void success(Object object) {
                            GroupBmob groupBmob = ((List<GroupBmob>) object).get(0);
                            if (groupBmob.getId().equals(body.getId())) {
                                body.setName(groupBmob.getName());
                                DatabaseUtils.insertGroup(groupBmob);
                                listener.onGroupInfoLoad(groupBmob.getId());
                            }
                        }
                    });
                }
            }
            //最后一条消息
            if (conversation.getAllMsgCount() != 0) {
                EMMessage lastMessage = conversation.getLastMessage();
                body.setLastMsg(((EMTextMessageBody) conversation.getLastMessage().getBody()).getMessage());
                body.setLastTime(DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));
                body.setTimeStamp(lastMessage.getMsgTime());
            }
            //未读数量
            int unreadMsgCount = conversation.getUnreadMsgCount();
            if (unreadMsgCount >= 99) {
                body.setMsgCount("99+");
            } else if (unreadMsgCount > 0) {
                body.setMsgCount(String.valueOf(unreadMsgCount));
            }
            list.add(body);
        }
        //时间排序
        Collections.sort(list);
        //置顶排序
        List<Conversation> conversations = SQLite.select().from(Conversation.class).queryList();
        for (Conversation c : conversations) {
            if (list.contains(new ConversationBody(c.getId()))) {
                int index = list.indexOf(new ConversationBody(c.getId()));
                ConversationBody body = list.get(index);
                body.setTop(true);
                list.remove(index);
                list.add(0, body);
            } else {
                c.delete();
            }
        }

        return list;
    }

    public void setListener(OnGroupInfoLoadListener listener) {
        this.listener = listener;
    }

    public interface OnGroupInfoLoadListener {
        void onGroupInfoLoad(String id);
    }

    //获取会话列表
    private List<EMConversation> loadConversationFromServer() {
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        List<Pair<Long, EMConversation>> sortList = new ArrayList<>();
        for (EMConversation conversation : conversations.values()) {
            if (conversation.getAllMessages().size() != 0) {
                sortList.add(new Pair<>(conversation.getLastMessage().getMsgTime(), conversation));
            }
        }
        Collections.sort(sortList, (con1, con2) -> {
            if (con1.first.equals(con2.first)) {
                return 0;
            } else if (con2.first > con1.first) {
                return 1;
            } else {
                return -1;
            }
        });
        List<EMConversation> list = new ArrayList<>();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }
        return list;
    }
}
