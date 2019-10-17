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

public class ClazzItemView extends LinearLayout {
    private Context mContext;
    private View view;
    private TextView mTvText;
    private ImageView mIvIcon;

    public ClazzItemView(Context context) {
        this(context, null);
    }

    public ClazzItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClazzItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    private void initView() {
        view = View.inflate(mContext, R.layout.activity_clazz_item, this);
        mTvText = (TextView) findViewById(R.id.tv_text);
        mIvIcon = (ImageView) findViewById(R.id.iv_check);
    }

    public void setText(String text) {
        mTvText.setText(text);
    }

    public String getText() {
        return mTvText.getText().toString().trim();
    }

    public void setVisibility(boolean b) {
        if (b) {
            mIvIcon.setVisibility(VISIBLE);
        } else {
            mIvIcon.setVisibility(GONE);
        }
    }

    public boolean isCheck() {
        if (mIvIcon.getTag() == null) {
            mIvIcon.setTag(R.drawable.ic_not_select);
            return false;
        }
        int tag = (int) mIvIcon.getTag();
        if (tag == R.drawable.ic_not_select) {
            return false;
        } else if (tag == R.drawable.ic_select) {
            return true;
        }
        return false;
    }

    public void setCheck(boolean check) {
        if (check) {
            mIvIcon.setTag(R.drawable.ic_select);
            mIvIcon.setImageResource(R.drawable.ic_select);
        } else {
            mIvIcon.setTag(R.drawable.ic_not_select);
            mIvIcon.setImageResource(R.drawable.ic_not_select);
        }
    }
}
