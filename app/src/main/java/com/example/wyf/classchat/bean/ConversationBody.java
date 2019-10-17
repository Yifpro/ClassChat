package com.example.wyf.classchat.bean;

import android.support.annotation.NonNull;

import com.hyphenate.chat.EMConversation;

/**
 * Created by WYF on 2017/11/1.
 */

public class ConversationBody implements Comparable {
    private String id;
    private String conversationId;
    private String name;
    private String lastMsg;
    private String lastTime;
    private String msgCount;
    private long timeStamp;
    private boolean isTop;
    private EMConversation.EMConversationType type;

    public ConversationBody()  {

    }

    public ConversationBody(String id) {
        this.id = id;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isTop() {
        return isTop;
    }

    public void setTop(boolean top) {
        isTop = top;
    }

    public EMConversation.EMConversationType getType() {
        return type;
    }

    public void setType(EMConversation.EMConversationType type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public String getMsgCount() {
        return msgCount;
    }

    public void setMsgCount(String msgCount) {
        this.msgCount = msgCount;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ConversationBody && this.getId().equals(((ConversationBody) obj).getId());
    }


    @Override
    public int compareTo(@NonNull Object o) {
        return (int) (((ConversationBody) o).timeStamp - this.timeStamp);
    }
}
