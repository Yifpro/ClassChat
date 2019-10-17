package com.example.wyf.classchat.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.wyf.classchat.feature.group.file.FileFragment;

import java.util.List;

/**
 * Created by WYF on 2017/10/28.
 */

public class FileAdapter extends FragmentPagerAdapter {
    private final int flag;
    private  String groupId = "";
    private  String noticeId = "";
    private String userId;
    private List<String> list;

    public FileAdapter(FragmentManager fm, List<String> list, String userId, int flag) {
        super(fm);
        this.list = list;
        this.userId = userId;
        this.flag =flag;
    }

    public FileAdapter(FragmentManager fm, List<String> list, String userId, String groupId, String noticeId, int flag) {
        super(fm);
        this.list = list;
        this.userId = userId;
        this.groupId =groupId;
        this.noticeId =noticeId;
        this.flag =flag;
    }

    @Override
    public Fragment getItem(int position) {
        return FileFragment.newInstance(position, userId,groupId,noticeId,flag);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return list.get(position);
    }
}
