package com.example.wyf.classchat.feature.contact.detail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wyf.classchat.weight.ContactInfoItemView;

import java.util.List;

/**
 * Created by Administrator on 2017/10/4.
 */

public class ContactInfoAadpter extends RecyclerView.Adapter<ContactInfoAadpter.ViewHolder> {
    private Context context;
    private int[] imags;
    private List<String> list;

    public ContactInfoAadpter(Context context, int[] imags, List<String> list) {
        this.context = context;
        this.imags = imags;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ContactInfoItemView viewItem = new ContactInfoItemView(context);
        return new ViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position < imags.length) {
            holder.mIvIcon.setImageResource(imags[position]);
        }
        if (list.get(position) == null) {
            holder.mTvTitle.setText("暂未填写此项");
        }else {
            holder.mTvTitle.setText(list.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mIvIcon;
        private TextView mTvTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            ContactInfoItemView view = (ContactInfoItemView) itemView;
            mIvIcon = view.getImageView();
            mTvTitle = view.getTextView();
        }
    }
}
