package com.example.liuyt.dailyhub.SettingFragmant;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.liuyt.dailyhub.FragmentAdapter;
import com.example.liuyt.dailyhub.MessageEvent;
import com.example.liuyt.dailyhub.Model.Habit;
import com.example.liuyt.dailyhub.Model.RespData;
import com.example.liuyt.dailyhub.Model.StaticObjects;
import com.example.liuyt.dailyhub.MyViewHolder;
import com.example.liuyt.dailyhub.R;
import com.rey.material.widget.Switch;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

import static com.example.liuyt.dailyhub.SettingFragmant.CalendarReminderUtils.findCalendarEvent;

public class NotificationSettingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String TAG = "Notification";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<Habit> mDatas = new ArrayList<>();
    private FragmentAdapter<Habit> adapter;
    private Switch aSwitch;
    public static NotificationSettingFragment newInstance() {
        NotificationSettingFragment fragment = new NotificationSettingFragment();
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
     * @return A new instance of fragment NotificationSettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationSettingFragment newInstance(String param1, String param2) {
        NotificationSettingFragment fragment = new NotificationSettingFragment();
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
        EventBus.getDefault().register(this);
        if (Build.VERSION.SDK_INT >23) {
            if(!(ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.WRITE_CALENDAR)
                    == PackageManager.PERMISSION_GRANTED )){
                requestPermissions(new String[]{Manifest.permission.WRITE_CALENDAR},1);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification_setting, container, false);
        aSwitch=view.findViewById(R.id.notificationAllBtn);
        RecyclerView recyclerView = view.findViewById(R.id.notificationRecyclerView);
        adapter = initRecyclerViewAdapter();
        initNotification();
        //布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        //recyclerView动画
        recyclerView.setItemAnimator(new SlideInLeftAnimator());
        return view;
    }

    private void initNotification(){
        aSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                if(checked){
                    //将所有的提醒设为checked
                    for(Habit mHabit : mDatas){
                        mHabit.setNotification(true);
                    }
                } else {
                    //将所有提醒设置为unChecked
                    for(Habit mHabit : mDatas){
                        mHabit.setNotification(false);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
        aSwitch.setChecked(false);
    }

    private FragmentAdapter<Habit> initRecyclerViewAdapter(){
        mDatas.clear();
        for(Habit habit : StaticObjects.habits){
            if(!habit.getFile() && !habit.getReminderTime().isEmpty()){
                mDatas.add(habit);
                if(habit.getNotification() && !findCalendarEvent(getContext(),habit.getName())){
                    newThreadAddNotification(habit);
                }
            }
        }
        return new FragmentAdapter<Habit>(this.getContext(),R.layout.item_notification_setting,mDatas) {
            @Override
            public void convert(final MyViewHolder holder, final Habit habit, final int position) {
                TextView textView1 = holder.getView(R.id.notificationName),textView2 = holder.getView(R.id.notificationTime);
                textView1.setText(habit.getName());
                textView2.setText(habit.getDateTime());
                Switch nSwitch = holder.getView(R.id.notificationBtn);
                nSwitch.setChecked(habit.getNotification());
                CircleImageView circleImageView = holder.getView(R.id.notificationIcon);
                Glide.with(getContext()).load(getResource(habit.getIcon())).into(circleImageView);
                if (habit.getColor() == null || habit.getColor().equals("")) {
                    habit.setColor("#ffffff");
                }
                circleImageView.setCircleBackgroundColor(Color.parseColor(habit.getColor()));
                nSwitch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(Switch view,final boolean checked) {
                        habit.setNotification(checked);
                        //网络访问
                        StaticObjects.service.updateHabit(StaticObjects.token, habit, habit.getId())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new DisposableObserver<RespData<Habit>>() {
                                    @Override
                                    public void onNext(RespData<Habit> respHabit) {
                                        if (respHabit.status) {
                                            newTreadDeleteNotification(habit);
                                            if(checked){
                                                if(!findCalendarEvent(getContext(),habit.getName())){
                                                    newThreadAddNotification(habit);
                                                }
                                            }
                                            Toast.makeText(getContext(), "操作成功", Toast.LENGTH_SHORT).show();

                                        } else {
                                            Toast.makeText(getContext(), respHabit.msg, Toast.LENGTH_SHORT).show();
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
                    }
                });
            }
        };
    }

    private void newThreadAddNotification(final Habit habit){
        //观察者
        final io.reactivex.Observable<Boolean> observable = io.reactivex.Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                String dateString = simpleDateFormat.format(new Date()).substring(0,11);
                Date date = simpleDateFormat.parse(dateString+habit.getReminderTime());
                long mill = date.getTime();
                CalendarReminderUtils.addCalendarEvent(getContext(), habit.getName(), habit.getEncourage(), mill, 5);
                for(long i = 1;i<30;i++) {
                    CalendarReminderUtils.addCalendarEvent(getContext(), habit.getName(), habit.getEncourage(), mill+i*1000*60*60*24, 5);
                }
                e.onComplete();
            }
        });
        //订阅者
        DisposableObserver<Boolean> disposableObserver = new DisposableObserver<Boolean>() {

            @Override
            public void onNext(Boolean value) {
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError = " + e);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        };
        observable.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver);
    }

    private void newTreadDeleteNotification(final Habit habit){
        //观察者
        final io.reactivex.Observable<Boolean> observable = io.reactivex.Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                CalendarReminderUtils.deleteCalendarEvent(getContext(),habit.getName());
                e.onComplete();
            }
        });
        //订阅者
        DisposableObserver<Boolean> disposableObserver = new DisposableObserver<Boolean>() {

            @Override
            public void onNext(Boolean value) {
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError = " + e);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        };
        observable.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver);
    }

    @Subscribe
    public void onEventMainThread(MessageEvent event) {
        mDatas.clear();
        for(Habit habit : StaticObjects.habits){
            if(!habit.getFile() && !habit.getReminderTime().isEmpty()){
                mDatas.add(habit);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private int getResource(String imageName){
        Context ctx = getContext();
        int resId = getResources().getIdentifier(imageName, "mipmap", ctx.getPackageName());
        //如果没有在"mipmap"下找到imageName,将会返回0
        return resId;
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
