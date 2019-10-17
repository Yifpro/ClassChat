package com.example.wyf.classchat.weight;

import android.content.Context;
import android.content.res.TypedArray;
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

public class ContactInfoItemView extends LinearLayout {

    private final int icon;
    private final String desc;
    private Context mContext;
    private TextView tvTitle;
    private ImageView ivIcon;

    public ContactInfoItemView(Context context) {
        this(context,null);
    }

    public ContactInfoItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ContactInfoItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ContactInfoItemView);
        icon = array.getResourceId(R.styleable.ContactInfoItemView_infoIcon, R.mipmap.ic_classchat);
        desc = array.getString(R.styleable.ContactInfoItemView_infoText);
        initViews();
        array.recycle();
    }

    private void initViews() {
        View view = View.inflate(mContext, R.layout.activity_information_recycle_item, this);
        ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
        tvTitle = (TextView)view.findViewById(R.id.tv_notice_content);
    }

    public void setIcon(int icon) {
        ivIcon.setImageResource(icon);
    }

    public void setText(String text) {
        tvTitle.setText(text);
    }

    public ImageView getImageView() {
        return ivIcon;
    }

    public TextView getTextView() {
        return tvTitle;
    }

}
