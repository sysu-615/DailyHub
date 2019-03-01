package com.example.liuyt.dailyhub.SettingFragmant;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.example.liuyt.dailyhub.Auth.AuthActivity;
import com.example.liuyt.dailyhub.MainActivity;
import com.example.liuyt.dailyhub.Model.DailyCommit;
import com.example.liuyt.dailyhub.Model.Profile;
import com.example.liuyt.dailyhub.Model.RespData;
import com.example.liuyt.dailyhub.Model.StaticObjects;
import com.example.liuyt.dailyhub.Model.Token;
import com.example.liuyt.dailyhub.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class SettingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private PagerSlidingTabStrip pagerSlidingTabStrip;
    private ViewPager viewPager;
    public static SettingFragment newInstance() {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        if (Build.VERSION.SDK_INT >23) {
            if (!(ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.READ_CALENDAR)
                    == PackageManager.PERMISSION_GRANTED)) {
                //未获得权限
                requestPermissions(new String[]{Manifest.permission.READ_CALENDAR},1);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        pagerSlidingTabStrip = view.findViewById(R.id.setting_tab);
        viewPager = view.findViewById(R.id.setting_viewpager);
        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences(StaticObjects.preference_name, StaticObjects.mode);
        String userName = sharedPreferences.getString("username", null);
        final ImageView avatar = view.findViewById(R.id.userAvatar);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            new AlertDialog.Builder(getContext()).setTitle("确认退出")
                .setIcon(R.mipmap.delete)
                .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor edit = sharedPreferences.edit();
                        edit.putString(StaticObjects.tokenName,"");
                        edit.apply();
                        Intent intent = new Intent(getContext(),MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
            }
        });
        final TextView userName_ = view.findViewById(R.id.userName);
        StaticObjects.service.getUserInfo(userName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new DisposableObserver<RespData<Profile>>() {
                @Override
                public void onNext(RespData<Profile> resp) {
                    if (resp.status) {
                        avatar.setImageBitmap(bytesToBitmap(resp.data.avatar));
                        userName_.setText(resp.data.username);
                    } else {
                        Toast.makeText(getContext(), resp.msg, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(Throwable e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onComplete() {
                }
            });
        String[] titles = {"每日记录","提醒设置","已结束习惯"};
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(DailyCommitFragment.newInstance());
        fragments.add(NotificationSettingFragment.newInstance());
        fragments.add(EndHabitFragment.newInstance());
        FragmentManager fragmentManager = this.getFragmentManager();
        viewPager.setAdapter(new MyPageAdapter(fragmentManager,titles,fragments));
        viewPager.setCurrentItem(0);
        //样式
        pagerSlidingTabStrip.setTextSize(50);
        pagerSlidingTabStrip.setIndicatorColor(Color.GRAY);
        pagerSlidingTabStrip.setTextColor(Color.BLACK);
        //设置Tab是自动填充满屏幕的
        pagerSlidingTabStrip.setShouldExpand(true);
        //tab间的分割线
        pagerSlidingTabStrip.setDividerColor(Color.GRAY);
        pagerSlidingTabStrip.setViewPager(viewPager);
        pagerSlidingTabStrip.setDividerWidth(4);
        pagerSlidingTabStrip.setIndicatorHeight(8);
        return view;
    }

    public Bitmap bytesToBitmap(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
