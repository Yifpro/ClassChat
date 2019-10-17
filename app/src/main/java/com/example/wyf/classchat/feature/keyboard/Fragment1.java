package com.example.wyf.classchat.feature.keyboard;

import android.os.Bundle;
import android.widget.TextView;

import com.example.wyf.classchat.R;
import com.example.wyf.classchat.base.BaseFragment;

/**
 * @author Administrator on 2018/2/24/024.
 */
public class Fragment1 extends BaseFragment {

    public static Fragment1 newInstance(Bundle bundle) {
        Fragment1 f = new Fragment1();
        f.setArguments(bundle);
        return f;
    }

    @Override
    public void initData() {
        String going = mBundle.getString("going");
        TextView tv = mView.findViewById(R.id.tv);
        tv.setText(going);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_a;
    }
}
