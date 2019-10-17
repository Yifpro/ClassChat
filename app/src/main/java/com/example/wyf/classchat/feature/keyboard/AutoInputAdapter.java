package com.example.wyf.classchat.feature.keyboard;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.wyf.classchat.R;
import com.example.wyf.classchat.util.DisplayUtils;

import java.util.List;

/**
 * Created by Administrator on 2018/2/24/024.
 */

public class AutoInputAdapter extends RecyclerView.Adapter<AutoInputAdapter.MyViewHolder> {

    private Context ctx;
    private List<ImageModel> list;
    private OnClickItemListener listener;

    public AutoInputAdapter(Context ctx, List<ImageModel> list) {
        this.ctx = ctx;
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.fragment_auto_input_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ImageModel model = list.get(position);
        holder.iv.setImageResource(model.getRes());
        holder.iv.setOnClickListener(v -> listener.onClickItem(holder.iv, holder.getLayoutPosition(), list));
        ViewGroup.LayoutParams lp = holder.iv.getLayoutParams();
        lp.width = getScreenWidth() / 6;
        if (model.isSelected()) {
            holder.iv.setBackgroundColor(ctx.getResources().getColor(R.color.darker_gray));
        } else {
            holder.iv.setBackgroundColor(ctx.getResources().getColor(R.color.bg_horizontal_btn_normal));
        }
    }

    private int getScreenWidth() {
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);//将当前窗口的信息放在DisplayMetrics类中
        return outMetrics.widthPixels;
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView iv;

        MyViewHolder(View view) {
            super(view);
            iv = view.findViewById(R.id.iv_image_model);
        }
    }

    public void setOnClickItemListener(OnClickItemListener listener) {
        this.listener = listener;
    }

    public interface OnClickItemListener {

        void onClickItem(View view, int position, List<ImageModel> datas);
    }
}
