package com.example.wyf.classchat.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

/**
 * @author WYF on 2017/11/4.
 */
public class ClazzViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
    private ClazzAadpter.OnClazzItemClickListener listener;
    private ClazzAadpter.OnClazzItemLongClickListener longListener;
    public LinearLayout view;

    public ClazzViewHolder(View itemView, ClazzAadpter.OnClazzItemClickListener listener, ClazzAadpter.OnClazzItemLongClickListener longListener) {
        super(itemView);
        this.listener = listener;
        this.longListener = longListener;
        view = (LinearLayout) itemView;
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (listener != null) {
            listener.onItemClick(view, getPosition());
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (longListener != null) {
            longListener.onItemClick(view, getPosition());
        }
        return true;
    }
}
