package com.example.wyf.classchat.feature.message.chat;

import android.widget.EditText;

import com.example.wyf.classchat.base.BasePresent;
import com.example.wyf.classchat.base.BaseView;
import com.hyphenate.chat.EMMessage;

/**
 * Created by Administrator on 2018/1/22/022.
 */

public interface ChatPageContract {

    interface View extends BaseView<Presenter> {
        void showRollcallView(String registerInfoId, String matchingCode);
        void onSendMessageSuccess(EMMessage message, String content);
    }

    interface Presenter extends BasePresent {
        void addRegisterListener(String friendId);
        void removeRegisterListener();
        void savaRegisterToBmob(String rollcallId);
        void sendMessage(EditText etInput, String type, String userId);
        void querryReaderFromBmob(String noticeId, String userId);
        void initNoticeData(long millsTime, String userId);
    }
}
