package com.example.liuyt.dailyhub.SettingFragmant;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import java.util.ArrayList;

public class MyPageAdapter extends FragmentPagerAdapter {
    private String[] titles;
    ArrayList<Fragment> fragments;
    //根据需求定义构造方法，方便外部调用
    public MyPageAdapter(FragmentManager fm, String[] titles, ArrayList<Fragment> fragments) {
        super(fm);
        this.titles = titles;
        this.fragments = fragments;
    }
    //设置每页的标题
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
    //设置每一页对应的fragment
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }
     //fragment的数量
    @Override
    public int getCount() {
        return fragments.size();
    }
}
