package com.example.wyf.classchat.weight;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wyf.classchat.R;

/**
 * Created by Administrator on 2017/10/4.
 */

public class MeFragmentItemView extends LinearLayout {

    private Context mContext;
    private View view;
    private ImageView mIvIcon;
    private TextView mTvTitle;

    public MeFragmentItemView(Context context) {
        this(context, null);
    }

    public MeFragmentItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MeFragmentItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    private void initView() {
        view = View.inflate(mContext, R.layout.my_recycleview_item, this);
        mIvIcon = (ImageView) findViewById(R.id.iv_icon);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
    }

    public TextView getTextView() {
        return mTvTitle;
    }

    public ImageView getImageView() {
        return mIvIcon;
    }

    public void setText(String text) {
        mTvTitle.setText(text);
    }

    public void setImageViewIcon(int id) {
        mIvIcon.setImageResource(id);
    }
}
