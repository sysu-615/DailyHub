package com.example.liuyt.dailyhub.Auth;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.example.liuyt.dailyhub.R;

import java.util.ArrayList;
import java.util.List;

public class AuthActivity extends AppCompatActivity {
    private final String[] titles = {"登录","注册"};
    ArrayList<Fragment> fragments;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        PagerSlidingTabStrip tabs = findViewById(R.id.tabs);
        final ViewPager pager = findViewById(R.id.pager);
        if (pager == null){
            Log.i("null","null");
        }

        fragments = new ArrayList<>();
        LoginFragment fragment_login = new LoginFragment();
        RegisterFragment fragment_register = new RegisterFragment();
        //添加fragment到集合中时注意顺序
        fragments.add(fragment_login);
        fragments.add(fragment_register);
        tabs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        tabs.setOnTabReselectedListener(new PagerSlidingTabStrip.OnTabReselectedListener() {
            @Override
            public void onTabReselected(int position) {
                pager.setCurrentItem(position);
                Toast.makeText(AuthActivity.this, "Tab reselected: " + position, Toast.LENGTH_SHORT).show();
            }
        });
        pager.setAdapter(new AuthActivity.TextAdapter(getSupportFragmentManager(), titles, fragments));
        // 设置Tab底部选中的指示器 Indicator的颜色
        tabs.setIndicatorColor(Color.BLACK);
        //设置Tab标题文字的颜色
        tabs.setTextColor(Color.BLACK);
        // 设置Tab标题文字的大小
        tabs.setTextSize(45);
        //设置Tab底部分割线的颜色
        tabs.setUnderlineColor(Color.TRANSPARENT);
        // 设置点击某个Tab时的背景色,设置为0时取消背景色
        tabs.setTabBackground(0);
        // 设置Tab是自动填充满屏幕的
        tabs.setShouldExpand(true);
        //!!!设置选中的Tab文字的颜色!!!
        //tabs.setSelectedTextColor(Color.GREEN);
        //tab间的分割线
        tabs.setDividerColor(Color.GRAY);
        //与ViewPager关联，这样指示器就可以和ViewPager联动
        tabs.setViewPager(pager);
    }
    class TextAdapter extends FragmentPagerAdapter {
        String[] titles;
        List<Fragment> lists = new ArrayList<>();

        public TextAdapter(FragmentManager fm, String[] titles, List<Fragment> list) {
            super(fm);
            this.titles = titles;
            this.lists = list;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return lists.get(position);
        }

        @Override
        public int getCount() {
            return lists.size();
        }
    }

    @Override
    public void onBackPressed() {
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.addCategory(Intent.CATEGORY_HOME);
        startActivity(home);
    }
}
