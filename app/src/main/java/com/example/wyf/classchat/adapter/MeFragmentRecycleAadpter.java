package com.example.wyf.classchat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wyf.classchat.R;
import com.example.wyf.classchat.weight.MeFragmentItemView;

/**
 * Created by Administrator on 2017/10/4.
 */

public class MeFragmentRecycleAadpter extends RecyclerView.Adapter<MeFragmentRecycleAadpter.ViewHolder> {
    private Context context;
    private String[] arr;
    private int[] imags;
    private MyItemOnClickListener listener;

    public MeFragmentRecycleAadpter(Context context, String[] arr, int[] imags) {
        this.context = context;
        this.arr = arr;
        this.imags = imags;
    }

    public void setOnItemClickListener(MyItemOnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MeFragmentItemView viewItem = new MeFragmentItemView(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        viewItem.setLayoutParams(layoutParams);
        viewItem.setBackgroundResource(R.drawable.sel_bg_me_menu);
        ViewHolder holder = new ViewHolder(viewItem, listener);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTvTitle.setText(arr[position]);
        holder.mIvIcon.setImageResource(imags[position]);
    }

    @Override
    public int getItemCount() {
        return arr.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mIvIcon;
        private TextView mTvTitle;
        private MyItemOnClickListener listener;

        public ViewHolder(View itemView, MyItemOnClickListener listener) {
            super(itemView);
            this.listener = listener;
            MeFragmentItemView view = (MeFragmentItemView) itemView;
            mTvTitle = view.getTextView();
            mIvIcon = view.getImageView();
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (listener != null) {
                listener.onItemClick(view, getPosition());
            }
        }
    }

    public interface MyItemOnClickListener {
        void onItemClick(View view, int position);
    }
}
