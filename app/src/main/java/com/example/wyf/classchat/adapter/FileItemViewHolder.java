package com.example.wyf.classchat.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wyf.classchat.R;
import com.example.wyf.classchat.weight.FileItemView;

/**
 * Created by WYF on 2017/10/28.
 */

public class FileItemViewHolder extends RecyclerView.ViewHolder {
    public CheckBox checkBox;
    public ImageView videoIcon;
    public TextView videoName;
    public TextView videoSize;
    public TextView videoTime;

    public FileItemViewHolder(View itemView) {
        super(itemView);
        FileItemView view = (FileItemView) itemView;
        checkBox = (CheckBox) view.findViewById(R.id.cb_check);
        videoIcon = (ImageView) view.findViewById(R.id.iv_video);
        videoName = (TextView) view.findViewById(R.id.tv_video_name);
        videoSize = (TextView) view.findViewById(R.id.tv_video_size);
        videoTime = (TextView) view.findViewById(R.id.tv_video_time);
    }
}
