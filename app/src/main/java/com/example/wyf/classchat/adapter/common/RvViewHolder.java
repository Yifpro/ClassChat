package com.example.wyf.classchat.adapter.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by WYF on 2017/10/21.
 */

public class RvViewHolder extends RecyclerView.ViewHolder {
    private SparseArray<View> arr;
    private View mView;
    private Context mContext;

    public RvViewHolder(Context context, View v, ViewGroup parent)
    {
        super(v);
        mContext = context;
        mView = v;
        arr = new SparseArray<>();
    }

    public static RvViewHolder get(Context context, ViewGroup parent, int layoutId)
    {

        View itemView = LayoutInflater.from(context).inflate(layoutId, parent,
                false);
        RvViewHolder holder = new RvViewHolder(context, itemView, parent);
        return holder;
    }

    /**
     * 通过viewId获取控件
     *
     * @param viewId
     * @return
     */
    public <T extends View> T getView(int viewId)
    {
        View view = arr.get(viewId);
        if (view == null)
        {
            view = mView.findViewById(viewId);
            arr.put(viewId, view);
        }
        return (T) view;
    }

    public RvViewHolder setText(int viewId, String text)
    {
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }

    public RvViewHolder setBitmap(int viewId, Bitmap bm)
    {
        ImageView view = getView(viewId);
        view.setImageBitmap(bm);
        return this;
    }

    public RvViewHolder setImageResource(int viewId, int resourceId) {
        ImageView view = getView(viewId);
        view.setImageResource(resourceId);
        return this;
    }

    public RvViewHolder setOnClickListener(int viewId, View.OnClickListener listener) {
        View view = getView(viewId);
        view.setOnClickListener(listener);
        return this;
    }
}
