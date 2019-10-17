package com.example.wyf.classchat.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;




import java.util.ArrayList;

/**
 * Created by WYF on 2017/9/26.
 */

public class WeatherAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> list = new ArrayList<Fragment>();

    public WeatherAdapter(FragmentManager fm) {
        super(fm);
    }

    public WeatherAdapter(FragmentManager fm, ArrayList<Fragment> list) {
        super(fm);
        this.list = list;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment ft = null;
        switch (position) {
            case 0:

                break;
            case 1:

                break;
            case 2:

                break;
        }
        return ft;
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
