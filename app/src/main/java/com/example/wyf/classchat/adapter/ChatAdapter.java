package com.example.wyf.classchat.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.wyf.classchat.R;
import com.example.wyf.classchat.bean.MessageBody;
import com.example.wyf.classchat.bean.PersonBmob;
import com.example.wyf.classchat.db.FileType;
import com.example.wyf.classchat.db.Person;
import com.example.wyf.classchat.listener.BmobCallbackImpl;
import com.example.wyf.classchat.util.BitmapUtils;
import com.example.wyf.classchat.util.BmobUtils;
import com.example.wyf.classchat.db.util.DatabaseUtils;
import com.hyphenate.util.DateUtils;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by WYF on 2017/9/29.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    private static final String TAG = ChatAdapter.class.getSimpleName();
    private String type;
    private Context ctx;
    private List<MessageBody> mDatas;

    public ChatAdapter(Context ctx, List<MessageBody> mDatas, String type, String chatId) {
        this.ctx = ctx;
        this.mDatas = mDatas;
        this.type = type;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.activity_chat_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MessageBody messageBody = mDatas.get(position);
        holder.sendTime.setText(DateUtils.getTimestampString(new Date(messageBody.getTime())));
        if (messageBody.getType() == 0) {
            //显示在右边
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.rightMessage.setText(messageBody.getMessage());
            holder.rightIcon.setTag(messageBody.getId());
            File file = new File(BitmapUtils.getPath(FileType.GROUP, messageBody.getId(), messageBody.getId()));
            if (file.exists()) {
                Glide.with(ctx).load(file).into(holder.rightIcon);
            } else {
                getBitmap(holder.rightIcon, messageBody.getId());
            }
        } else {
            //显示在左边
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.leftMessage.setText(messageBody.getMessage());
            holder.leftIcon.setTag(messageBody.getId());
            getBitmap(holder.leftIcon, messageBody.getId());
        }
    }

    private void getBitmap(final ImageView iv, final String id) {
        Person chatPerson = (Person) DatabaseUtils.queryData(FileType.CONTACT, id);
        if (chatPerson != null) {
            //iv.setImageBitmap(BitmapUtils.stringToBitmap(chatPerson.getIcon()));
        } else {
            HashMap<String, String> map = new HashMap<>();
            map.put("id", id);
            BmobUtils.getInstance().querryPerson(map, new BmobCallbackImpl() {
                @Override
                public void success(Object object) {
                    PersonBmob personBmob = ((List<PersonBmob>) object).get(0);
                    DatabaseUtils.insertPerson(personBmob);
                    if (iv.getTag().equals(id)) {
                        notifyDataSetChanged();
                    }
                }
            });
        }
    }

    public Bitmap querryBitmap(String id) {
        final Bitmap[] bm = {null};
        HashMap<String, String> map = new HashMap<>();
        map.put("id", id);
        BmobUtils.getInstance().querryPerson(map, new BmobCallbackImpl() {
            @Override
            public void success(Object object) {
                PersonBmob personBmob = ((List<PersonBmob>) object).get(0);
                DatabaseUtils.insertPerson(personBmob);
                bm[0] = BitmapUtils.stringToBitmap(personBmob.getIcon());
            }
        });
        return bm[0];
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView sendTime;
        private final LinearLayout leftLayout;
        private final LinearLayout rightLayout;
        private final ImageView leftIcon;
        private final ImageView rightIcon;
        private final TextView leftMessage;
        private final TextView rightMessage;

        MyViewHolder(View view) {
            super(view);
            sendTime = view.findViewById(R.id.tv_send_time);
            leftLayout = view.findViewById(R.id.ll_left);
            rightLayout = view.findViewById(R.id.ll_right);
            leftIcon = view.findViewById(R.id.iv_left_icon);
            rightIcon = view.findViewById(R.id.iv_right_icon);
            leftMessage = view.findViewById(R.id.tv_left_message);
            rightMessage = view.findViewById(R.id.tv_right_message);
        }
    }
}

