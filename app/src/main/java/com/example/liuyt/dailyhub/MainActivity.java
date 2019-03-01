package com.example.liuyt.dailyhub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.example.liuyt.dailyhub.AllHabits.AllHabitsFragment;
import com.example.liuyt.dailyhub.AllHabits.EditHabitActivity;
import com.example.liuyt.dailyhub.Auth.AuthActivity;
import com.example.liuyt.dailyhub.MainPage.MainFragment;
import com.example.liuyt.dailyhub.Model.RespData;
import com.example.liuyt.dailyhub.Model.StaticObjects;
import com.example.liuyt.dailyhub.Model.Habit;
import com.example.liuyt.dailyhub.Model.Service;
import com.example.liuyt.dailyhub.SettingFragmant.SettingFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    // Fragment管理器，和执行器
    private FragmentManager mManager;
    private FragmentTransaction mTransaction;
    private AllHabitsFragment allHabitsFragment;  // 全部
    private MainFragment mainFragment;
    private SettingFragment settingFragment;
    private BottomNavigationBar bottomNavigationBar;
    private ImageView addHabit;

    private void handleAuth() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(StaticObjects.preference_name, StaticObjects.mode);
        String dh_token = sharedPreferences.getString(StaticObjects.tokenName, null);
        if(dh_token == null || dh_token.equals("")) {
            Intent intent = new Intent(MainActivity.this, AuthActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("username", sharedPreferences.getString("username", null));
            bundle.putString("password", sharedPreferences.getString("password", null));
            intent.putExtras(bundle);
            startActivityForResult(intent, 2);
        } else {
            StaticObjects.token = "Bearer " + sharedPreferences.getString(StaticObjects.tokenName, null);
            initHabits();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationBar = findViewById(R.id.bottom_navigation_bar);
        StaticObjects.screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(StaticObjects.screenSize);
        addHabit = findViewById(R.id.addHabit);

        addHabit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(MainActivity.this, EditHabitActivity.class);
                startActivityForResult(newIntent, 1);
            }
        });

        // 初始化列表
        OkHttpClient build = new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.SECONDS)
                .readTimeout(2, TimeUnit.SECONDS)
                .writeTimeout(2, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(StaticObjects.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(build)
                .build();
        StaticObjects.service = retrofit.create(Service.class);
        handleAuth();
    }

    void initHabits() {
        StaticObjects.service.getAllHabits(StaticObjects.token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new DisposableObserver<RespData<List<Habit>>>() {
                @Override
                public void onComplete() {
                }

                @Override
                public void onNext(RespData<List<Habit>> repos) {
                    StaticObjects.habits.clear();
                    if(repos.data != null) {
                        StaticObjects.habits.addAll(repos.data);
                        EventBus.getDefault().post(new MessageEvent(new Habit()));
                    }
                    setDefaultFragment();

                    final TextView title = findViewById(R.id.title);
                    bottomNavigationBar
                            .addItem(new BottomNavigationItem(R.mipmap.image38, "今日"))
                            .addItem(new BottomNavigationItem(R.mipmap.image30, "全部"))
                            .addItem(new BottomNavigationItem(R.mipmap.image45, "我的"))
                            .setActiveColor(R.color.header)
                            .setFirstSelectedPosition(0)
                            .initialise();

                    bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
                        @Override
                        public void onTabSelected(int position) {//未选中 -> 选中
                            //开启事务
                            mTransaction = mManager.beginTransaction();
                            hideFragment(mTransaction);

                            /**
                             * fragment 用 add + show + hide 方式
                             * 只有第一次切换会创建fragment，再次切换不创建
                             *
                             * fragment 用 replace 方式
                             * 每次切换都会重新创建
                             *
                             */
                            switch (position){
                                case 0:   // 今日
                                    title.setText(getString(R.string.main_fragment_title));
                                    if (mainFragment == null) {
                                        mainFragment = MainFragment.newInstance();
                                        mTransaction.add(R.id.viewPage, mainFragment);
                                    } else {
                                        mTransaction.show(mainFragment);
                                    }
                                    if (allHabitsFragment != null){
                                        mTransaction.hide(settingFragment);
                                    }
                                    if(settingFragment != null) {
                                        mTransaction.hide(allHabitsFragment);
                                    }
                                    EventBus.getDefault().post(new MessageEvent(new Habit()));
                                    addHabit.setVisibility(View.VISIBLE);
                                    break;
                                case 1:    // 全部
                                    title.setText(getString(R.string.all_habits_fragment_title));
                                    if (allHabitsFragment == null) {
                                        allHabitsFragment =  AllHabitsFragment.newInstance();
                                        mTransaction.add(R.id.viewPage, allHabitsFragment);
                                    } else {
                                        mTransaction.show(allHabitsFragment);
                                    }
                                    if (allHabitsFragment != null){
                                        mTransaction.hide(mainFragment);
                                    }
                                    if(settingFragment != null) {
                                        mTransaction.hide(settingFragment);
                                    }
                                    addHabit.setVisibility(View.INVISIBLE);
                                    EventBus.getDefault().post(new MessageEvent(new Habit()));
                                    break;
                                case 2:   // 我的
                                    title.setText(getString(R.string.setting_fragment_title));
                                    if (settingFragment == null) {
                                        settingFragment =  settingFragment.newInstance();
                                        mTransaction.add(R.id.viewPage, settingFragment);
                                    } else {
                                        mTransaction.show(settingFragment);
                                    }
                                    if (allHabitsFragment != null){
                                        mTransaction.hide(allHabitsFragment);
                                    }
                                    if(settingFragment != null) {
                                        mTransaction.hide(mainFragment);
                                    }
                                    addHabit.setVisibility(View.INVISIBLE);
                                    EventBus.getDefault().post(new MessageEvent(new Habit()));
                                    break;
                            }
                            // 事务提交
                            mTransaction.commit();
                        }

                        @Override
                        public void onTabUnselected(int position) {//选中 -> 未选中
                        }

                        @Override
                        public void onTabReselected(int position) {//选中 -> 选中
                        }
                    });
                }

                @Override
                public void onError(Throwable e) {
                    SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(StaticObjects.preference_name, StaticObjects.mode);
                    Intent intent = new Intent(MainActivity.this, AuthActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("username", sharedPreferences.getString("username", null));
                    bundle.putString("password", sharedPreferences.getString("password", null));
                    intent.putExtras(bundle);
                    MainActivity.this.startActivityForResult(intent, 2);
                    e.printStackTrace();
                }
            });
    }
    /**
     * 隐藏当前fragment
     * @param transaction
     */
    private void hideFragment(FragmentTransaction transaction){
        if (allHabitsFragment != null){
            transaction.hide(allHabitsFragment);
        }
        if(settingFragment != null) {
            transaction.hide(settingFragment);
        }
    }

    private void setDefaultFragment() {
        mainFragment = new MainFragment();
        mManager = getSupportFragmentManager();
        mTransaction = mManager.beginTransaction();
        settingFragment =  settingFragment.newInstance();
        mTransaction.add(R.id.viewPage, settingFragment);
        mTransaction.hide(settingFragment);
        allHabitsFragment =  AllHabitsFragment.newInstance();
        mTransaction.add(R.id.viewPage, allHabitsFragment);
        mTransaction.hide(allHabitsFragment);
        mTransaction.add(R.id.viewPage, mainFragment);
        mTransaction.commit();
    }

    /**
     * requestCode 请求码，即调用startActivityForResult()传递过去的值
     * resultCode 结果码，结果码用于标识返回数据来自哪个新Activity
     * data 为返回的intent
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 得到返回的数据
        if(resultCode == RESULT_OK && requestCode == 2) {
            try {
                Bundle bundle = data.getExtras();
                SharedPreferences sharedPreferences = this.getSharedPreferences(StaticObjects.preference_name, StaticObjects.mode);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("username", bundle.getString("username"));
                editor.putString("password", bundle.getString("password"));
                editor.putString(StaticObjects.tokenName, bundle.getString(StaticObjects.tokenName));
                editor.apply();
                StaticObjects.token = "Bearer " + bundle.getString(StaticObjects.tokenName);
                initHabits();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if(resultCode == RESULT_OK && requestCode == 1) {
            try {
                //从intent取出bundle
                Bundle myBundle = data.getExtras();
                // 获取bundle中数据
                Habit item = (Habit) myBundle.getSerializable("habit");
                // 新建
                StaticObjects.service.createHabit(StaticObjects.token, item)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new DisposableObserver<RespData<Habit>>() {
                            @Override
                            public void onComplete() {
                            }

                            @Override
                            public void onNext(RespData<Habit> repos) {
                                StaticObjects.habits.add(repos.data);
                                EventBus.getDefault().post(new MessageEvent(repos.data));
                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (resultCode == RESULT_OK) {
            try {
                //从intent取出bundle
                Bundle myBundle = data.getExtras();
                // 获取bundle中数据
                Habit item = (Habit) myBundle.getSerializable("habit");
                final int pos = myBundle.getInt("position");

                // 更新
                StaticObjects.service.updateHabit(StaticObjects.token, item, item.getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new DisposableObserver<RespData<Habit>>() {
                            @Override
                            public void onComplete() {
                            }

                            @Override
                            public void onNext(RespData<Habit> repos) {
                                StaticObjects.habits.set(pos, repos.data);
                                EventBus.getDefault().post(new MessageEvent(repos.data));
                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
