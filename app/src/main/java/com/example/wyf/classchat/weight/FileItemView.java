package com.example.wyf.classchat.weight;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wyf.classchat.R;

/**
 * Created by Administrator on 2017/10/4.
 */

public class FileItemView extends LinearLayout {

    private Context mContext;
    private View view;
    private CheckBox mCheck;
    private ImageView mIcon;
    private TextView mName;
    private TextView mSize;
    private TextView mTime;

    public FileItemView(Context context) {
        this(context, null);
    }

    public FileItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FileItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    private void initView() {
        view = View.inflate(mContext, R.layout.fragment_file_item, this);
    }
}
