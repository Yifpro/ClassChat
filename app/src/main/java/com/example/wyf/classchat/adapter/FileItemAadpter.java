package com.example.wyf.classchat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.wyf.classchat.R;
import com.example.wyf.classchat.weight.FileItemView;

import java.util.List;

/**
 * Created by Administrator on 2017/10/4.
 */

public abstract class FileItemAadpter<T> extends RecyclerView.Adapter<FileItemViewHolder> {
    private Context context;
    private List<T> mDatas;

    public FileItemAadpter(Context context, List<T> mDatas) {
        this.context = context;
        this.mDatas = mDatas;
    }

    @Override
    public FileItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FileItemView viewItem = new FileItemView(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        viewItem.setLayoutParams(layoutParams);
        viewItem.setBackgroundResource(R.drawable.sel_bg_me_menu);
        FileItemViewHolder holder = new FileItemViewHolder(viewItem);
        return holder;
    }

    @Override
    public void onBindViewHolder(FileItemViewHolder holder, int position) {
        convert(holder, position, mDatas.get(position));
    }

    public abstract void convert(FileItemViewHolder holder, int position, T t);

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

}
