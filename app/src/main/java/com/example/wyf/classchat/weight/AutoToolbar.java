package com.example.wyf.classchat.weight;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wyf.classchat.R;

/**
 * Created by Administrator on 2018/1/24/024.
 */

public class AutoToolbar extends android.support.v7.widget.Toolbar {

    private TextView tvTitle;
    private TextView tvLeft;
    private ImageView ivLeft;
    private TextView tvRight;
    private ImageView ivRight;

    public AutoToolbar(Context context) {
        this(context, null);
    }

    public AutoToolbar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray array=context.obtainStyledAttributes(attrs, R.styleable.AutoToolbar);
        int leftResId = array.getResourceId(R.styleable.AutoToolbar_leftIcon, 0);
        int rightlResId = array.getResourceId(R.styleable.AutoToolbar_rightIcon, 0);
        String leftText = array.getString(R.styleable.AutoToolbar_leftText);
        String rightText = array.getString(R.styleable.AutoToolbar_rightText);
        String titleText = array.getString(R.styleable.AutoToolbar_titleText);
        Log.e("", "hhhAutoToolbar: " + titleText);
        if (titleText != null) tvTitle.setText(titleText);
        if (leftText != null) tvLeft.setText(leftText);
        if (rightText != null) tvRight.setText(rightText);
        if (leftResId != 0) ivLeft.setBackgroundResource(leftResId);
        if (rightlResId != 0) ivRight.setBackgroundResource(rightlResId);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tvTitle = (TextView) findViewById(R.id.atb_tv_title);
        tvLeft = (TextView) findViewById(R.id.atb_tv_left);
        ivLeft = (ImageView) findViewById(R.id.atb_iv_left);
        tvRight = (TextView) findViewById(R.id.atb_tv_right);
        ivRight = (ImageView) findViewById(R.id.atb_iv_right);
    }

    public AutoToolbar setTitleText(String text) {
        setTitle("");
        tvTitle.setText(text);
        return this;
    }

    public AutoToolbar setTitleColor(int color) {
        tvTitle.setTextColor(color);
        return this;
    }

    public AutoToolbar setLeftText(String text) {
        tvLeft.setVisibility(VISIBLE);
        tvLeft.setText(text);
        return this;
    }

    public AutoToolbar setOnLeftTextClickEvent(OnClickListener listener) {
        tvLeft.setOnClickListener(listener);
        return this;
    }

    public AutoToolbar setLeftIcon(int resId) {
        ivLeft.setVisibility(VISIBLE);
        ivLeft.setBackgroundResource(resId);
        return this;
    }

    public AutoToolbar setOnLeftIconClickEvent(OnClickListener listener) {
        ivLeft.setOnClickListener(listener);
        return this;
    }

    public AutoToolbar setRightText(String text) {
        tvRight.setVisibility(VISIBLE);
        tvRight.setText(text);
        return this;
    }

    public AutoToolbar setOnRightTextClickEvent(OnClickListener listener) {
        tvRight.setOnClickListener(listener);
        return this;
    }

    public AutoToolbar setRightIcon(int resId) {
        ivRight.setVisibility(VISIBLE);
        ivRight.setBackgroundResource(resId);
        return this;
    }

    public AutoToolbar setOnRightIconClickEvent(OnClickListener listener) {
        ivRight.setOnClickListener(listener);
        return this;
    }

    public AutoToolbar setLeftTextVisibility(int visible) {
        tvLeft.setVisibility(visible);
        return this;
    }

    public AutoToolbar setLeftIconVisibility(int visible) {
        ivLeft.setVisibility(visible);
        return this;
    }

    public AutoToolbar setRightTextVisibility(int visible) {
        tvRight.setVisibility(visible);
        return this;
    }

    public AutoToolbar setRightIconVisibility(int visible) {
        ivRight.setVisibility(visible);
        return this;
    }

    public String getTitleText() {
        return tvTitle.getText().toString().trim();
    }

    public String getLeftText() {
        return tvLeft.getText().toString().trim();
    }

    public String getRightText() {
        return tvRight.getText().toString().trim();
    }
}
