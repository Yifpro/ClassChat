package com.example.wyf.classchat.adapter.common;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by WYF on 2017/10/21.
 */

public abstract class RvAdapter<T> extends RecyclerView.Adapter<RvViewHolder>
{
    protected Context mContext;
    protected int mLayoutId;
    protected List<T> mDatas;
    protected LayoutInflater mInflater;


    public RvAdapter(Context context, int layoutId, List<T> datas)
    {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mLayoutId = layoutId;
        mDatas = datas;
    }

    @Override
    public RvViewHolder onCreateViewHolder(final ViewGroup parent, int viewType)
    {
        RvViewHolder viewHolder = RvViewHolder.get(mContext, parent, mLayoutId);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RvViewHolder holder, int position) {
        convert(holder, position, mDatas.get(position));
    }

    public abstract void convert(RvViewHolder holder, int position, T t);

    @Override
    public int getItemCount()
    {
        return mDatas == null ? 0 : mDatas.size();
    }

//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
}
