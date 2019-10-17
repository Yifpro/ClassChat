package com.example.wyf.classchat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wyf.classchat.R;
import com.example.wyf.classchat.file.bean.FileInfo;
import java.util.List;

/**
 * Created by Administrator on 2017/11/6.
 */

public class RecycleFileAdapter extends RecyclerView.Adapter<RecycleFileAdapter.ViewHolder> {


    private final Context context;
    private final List<FileInfo> mList;
    private OnItemClickListener listener;

    public RecycleFileAdapter(Context context, List<FileInfo> fileInfos) {
        this.context = context;
        mList = fileInfos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.fragment_notice_display_file_item, null);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FileInfo fileInfo = mList.get(position);
        holder.mTvname.setText(fileInfo.getName());
        holder.mTvother.setText(fileInfo.getSize());
        String type = fileInfo.getType();
        holder.mIvIcon.setImageResource(type.equals("app")
                ?R.mipmap.ic_launcher:type.equals("img")
                ?R.drawable.ic_file_img :type.equals("file")?R.drawable.ic_file_doc :R.drawable.ic_file_img);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mTvname;
        private TextView mTvother;
        private ImageView mIvIcon;

        public ViewHolder(View itemView) {
            super(itemView);
             mTvname  = (TextView) itemView.findViewById(R.id.tv_rv_name);
            mTvother  = (TextView) itemView.findViewById(R.id.tv_rv_other);
            mIvIcon  = (ImageView) itemView.findViewById(R.id.iv_icon);
            mIvIcon.setVisibility(View.VISIBLE);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (listener!=null){
                listener.onItemClick(getLayoutPosition());
            }
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener =listener;
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }


}
