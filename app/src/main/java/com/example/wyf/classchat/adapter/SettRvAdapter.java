package com.example.wyf.classchat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wyf.classchat.R;
import com.example.wyf.classchat.bean.GroupSettingItemView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/9/26.
 */

public class SettRvAdapter extends RecyclerView.Adapter<SettRvAdapter.SetViewHolder> {

    private Context context;
    private ArrayList<GroupSettingItemView> list;
    private onItemClickListener listener;

    public SettRvAdapter(Context context, ArrayList<GroupSettingItemView> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public SetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.setting_rv_item, null);
        SetViewHolder holder = new SetViewHolder(view,listener);
        return holder;
    }

    public void setOnItemClickListener(onItemClickListener listener){

        this.listener = listener;
    }
    @Override
    public void onBindViewHolder(SetViewHolder holder, int position) {
        GroupSettingItemView kindsBean = list.get(position);
        holder.miV.setImageResource(kindsBean.getImg());
        holder.mtvTitle.setText(kindsBean.getTitle());
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    class SetViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView miV;
        private  TextView mtvTitle;
        private onItemClickListener listener;

        public SetViewHolder(View itemView, onItemClickListener listener) {
            super(itemView);
            this.listener = listener;
            itemView.setOnClickListener(this);
            miV = (ImageView) itemView.findViewById(R.id.iv_kinds);
            mtvTitle = (TextView) itemView.findViewById(R.id.tv_title);
        }

        @Override
        public void onClick(View view) {
            if (listener!=null){
                listener.onClick(view,getPosition());
            }
        }
    }

    public interface onItemClickListener{
        void onClick(View view,int position);
    }
}
