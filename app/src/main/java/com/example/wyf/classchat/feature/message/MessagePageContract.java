package com.example.wyf.classchat.feature.message;

import com.example.wyf.classchat.base.BasePresent;
import com.example.wyf.classchat.base.BaseView;
import com.example.wyf.classchat.bean.ConversationBody;
import com.hyphenate.chat.EMMessage;

import java.util.List;

/**
 * Created by Administrator on 2018/1/22/022.
 */

public interface MessagePageContract {

    interface View extends BaseView<Presenter> {
        void notifyItemInserted(int index);
        void notifyItemLastMsgChanged(int index);
        void deleteConversationSuccess(String id);
        void refresh(List<ConversationBody> list);
    }

    interface Presenter extends BasePresent {
        void onMessageReceived(List<ConversationBody> list, List<EMMessage> msgList);
        void deleteConversation(String id);
        void getNetTime(String user, String groupId);
        void getMessageList();
    }
}
