package com.example.wyf.classchat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.wyf.classchat.R;

import java.util.List;

/**
 * Created by Administrator on 2017/10/4.
 */

public class ClazzAadpter<T> extends RecyclerView.Adapter<ClazzViewHolder> {
    private Context context;
    private OnClazzItemClickListener listener;
    private OnClazzItemLongClickListener longListener;
    private List<T> list;

    public ClazzAadpter(Context context, List<T> list) {
        this.context = context;
        this.list = list;
    }

    public ClazzAadpter setOnItemClickListener(OnClazzItemClickListener listener) {
        this.listener = listener;
        return this;
    }

    public ClazzAadpter setOnItemLongClickListener(OnClazzItemLongClickListener longListener) {
        this.longListener = longListener;
        return this;
    }

    @Override
    public ClazzViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setBackgroundResource(R.drawable.sel_bg_me_menu);
        ClazzViewHolder holder = new ClazzViewHolder(linearLayout, listener, longListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(ClazzViewHolder holder, int position) {
        View view = (View) list.get(position);
        if (view.getParent() != null) {
            ((LinearLayout)view.getParent()).removeAllViews();
        }
        holder.view.addView(view);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public interface OnClazzItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnClazzItemLongClickListener {
        void onItemClick(View view, int position);
    }
}
