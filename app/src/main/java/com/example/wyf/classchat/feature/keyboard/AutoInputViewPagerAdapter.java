package com.example.wyf.classchat.feature.keyboard;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * @author Administrator on 2018/2/24/024.
 */
public class AutoInputViewPagerAdapter  extends FragmentPagerAdapter {

    private List<Fragment> datas = null;

    public AutoInputViewPagerAdapter(FragmentManager fm, List<Fragment> datas) {
        super(fm);
        this.datas = datas;

    }

    @Override
    public Fragment getItem(int position) {
        return datas.get(position);
    }

    @Override
    public int getCount() {
        return datas.size();
    }
}
