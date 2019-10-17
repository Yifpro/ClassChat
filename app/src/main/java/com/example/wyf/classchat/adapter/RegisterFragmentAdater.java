package com.example.wyf.classchat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.wyf.classchat.R;
import com.example.wyf.classchat.bean.MemberRegisterBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/14.
 */

public class RegisterFragmentAdater extends RecyclerView.Adapter<RegisterFragmentAdater.ViewHolder> {


    private final Context context;
    private final List<ArrayList<MemberRegisterBean>> beans;
    private OnItemClickLister listener;

    public RegisterFragmentAdater(Context context, List<ArrayList<MemberRegisterBean>> beans) {
        this.context = context;
        this.beans = beans;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.fragment_chat_register_item, null);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//        MemberRegisterBean memberRegisterBean = beans.get(position).get(0);
//        if (memberRegisterBean != null && memberRegisterBean.getCount() > 0) {
//            holder.mTvCount.setText("第" + memberRegisterBean.getCount() + "次点名");
//            holder.mTvTime.setText("时间 ： " + memberRegisterBean.getUpdatedAt());
//        } else {
//            Log.i("你好", "memberRegisterBean 为空");
//        }
//        holder.mTvCount.setText("第" + beans.get(position).get(0).getCount() + "次点名");
//        holder.mTvTime.setText("时间 ： " + beans.get(position).get(0).getUpdatedAt() );
    }

    @Override
    public int getItemCount() {
        return beans == null ? 0 : beans.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTvCount;
        private TextView mTvTime;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mTvCount = (TextView) itemView.findViewById(R.id.tv_notice_read_count);
            mTvTime = (TextView) itemView.findViewById(R.id.tv_notice_time);
        }

        @Override
        public void onClick(View view) {
            if (listener != null) {
                listener.itemClick(view, getPosition());
            }
        }
    }

    public void setOnItemClickListener(OnItemClickLister listener) {
        this.listener = listener;
    }

    public interface OnItemClickLister {
        void itemClick(View view, int position);
    }

}
