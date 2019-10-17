package com.example.wyf.classchat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.wyf.classchat.R;
import com.example.wyf.classchat.bean.NoticeBean;
import com.example.wyf.classchat.bean.ReaderBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/9.
 */

public class NoticeRecycleAdapter extends RecyclerView.Adapter<NoticeRecycleAdapter.ViewHolder> {


    private Context context;
    private List<NoticeBean> datas;
    private ArrayList<ArrayList<ReaderBean>> lists;
    private onItemClickListener listener;

    public NoticeRecycleAdapter(Context context, List<NoticeBean> datas, ArrayList<ArrayList<ReaderBean>> lists) {
        this.context = context;
        this.datas = datas;
        this.lists = lists;
}

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = View.inflate(context, R.layout.notice_recycle_item, null);

        ViewHolder holder = new ViewHolder(view, listener);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NoticeBean bean = datas.get(position);
        holder.mTvTitle.setText(bean.getTitle());
        holder.mTvName.setText(bean.getUser());
        holder.mTvTime.setText(bean.getTime());
        holder.mTvCotent.setText(bean.getContent());
        if (lists!=null&&lists.size()>0){
            int size = lists.get(position).size();
            holder.mTvCount.setText("已读 : "+ size);
        }else {
            holder.mTvCount.setText("已读 : 0");
        }


    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView mTvName;
        private final TextView mTvTitle;
        private final TextView mTvCount;
        private final TextView mTvTime;
        private final TextView mTvCotent;

        public ViewHolder(View itemView, onItemClickListener listener) {
            super(itemView);

            itemView.setOnClickListener(this);
            mTvName = (TextView) itemView.findViewById(R.id.tv_notice_author);
            mTvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            mTvCount = (TextView) itemView.findViewById(R.id.tv_notice_read_count);
            mTvTime = (TextView) itemView.findViewById(R.id.tv_notice_time);
            mTvCotent = (TextView) itemView.findViewById(R.id.tv_notice_content);
        }

        @Override
        public void onClick(View view) {
            if (listener != null) {
                listener.onItemClick(itemView, getPosition());
            }
        }
    }

    public void setOnItemClickListener(onItemClickListener listener) {

        this.listener = listener;
    }

    public interface onItemClickListener {
        void onItemClick(View view, int position);
    }
}
