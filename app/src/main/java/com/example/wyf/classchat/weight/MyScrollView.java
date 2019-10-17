package com.example.wyf.classchat.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by Administrator on 2017/9/30.
 */

public class MyScrollView extends ScrollView {
    private ScrollViewListener listener;

    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setScrollViewListener(ScrollViewListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (listener!=null){
            listener.onScrollViewChanged(this,l,t,oldl,oldt);
        }
    }

    public interface ScrollViewListener {
        void onScrollViewChanged(MyScrollView scrollView, int x, int y, int oldx, int oldy);

    }
}
