package com.example.wyf.classchat.feature.home;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.wyf.classchat.feature.contact.ContactFragment;
import com.example.wyf.classchat.feature.me.MeFragment;
import com.example.wyf.classchat.feature.message.MessageFragment;

import java.util.ArrayList;

/**
 * @author WYF on 2017/9/26.
 */
public class HomePageAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> list = new ArrayList<>();

    public HomePageAdapter(FragmentManager fm, ArrayList<Fragment> list) {
        super(fm);
        this.list = list;
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
