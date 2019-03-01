package com.example.liuyt.dailyhub.MainPage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.liuyt.dailyhub.FragmentAdapter;
import com.example.liuyt.dailyhub.MessageEvent;
import com.example.liuyt.dailyhub.Model.DailyCommit;
import com.example.liuyt.dailyhub.Model.Day;
import com.example.liuyt.dailyhub.Model.RespData;
import com.example.liuyt.dailyhub.Model.StaticObjects;
import com.example.liuyt.dailyhub.Model.Habit;
import com.example.liuyt.dailyhub.MyViewHolder;
import com.example.liuyt.dailyhub.R;
import com.google.android.flexbox.FlexboxLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainFragment extends Fragment {
    static List<List<Habit>> habitsList;
    private String[] titles = {"任意时间", "起床之后", "晨间习惯", "中午时分", "午间习惯", "晚间习惯", "睡觉之前"};
    private FragmentAdapter<List<Habit>> adapter;
    DateFormat format;
    Calendar calendar;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view =inflater.inflate(R.layout.fragment_main,container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycleView);
        adapter = new FragmentAdapter<List<Habit>>(getContext(), R.layout.fragment_main_item, habitsList) {
            @Override
            public int getItemCount() {
                int count = 0;
                for (List<Habit> habits: habitsList) {
                    if (habits.size() > 0) {
                        count = count + 1;
                    }
                }
                return count;
            }

            @Override
            public void convert(MyViewHolder holder, List<Habit> habits1, int position) {
                int count = 0;
                for (int i = 0; i <= position && count < habitsList.size(); ++count) {
                    if (habitsList.get(count).size() > 0) {
                        ++i;
                    }
                }
                final List<Habit> habits = habitsList.get(count - 1);
                TextView timeStr = holder.getView(R.id.timeStr);
                timeStr.setText(titles[habitsList.indexOf(habits)]);
                final TextView hideStr = holder.getView(R.id.hideStr);
                hideStr.setText(String.format(getString(R.string.formatHideInfo), habits.size()));
                hideStr.setVisibility(View.INVISIBLE);
                final ImageView arrow = holder.getView(R.id.icon);
                Glide.with(MainFragment.this).load(R.mipmap.uparrow).into(arrow);
                final FlexboxLayout flexboxLayout = holder.getView(R.id.flexBoxLayout);
                LinearLayout linearLayout = holder.getView(R.id.linearLayout);
                linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (hideStr.getVisibility() == View.INVISIBLE) {
                            hideStr.setVisibility(View.VISIBLE);
                            flexboxLayout.setVisibility(View.GONE);
                            Glide.with(MainFragment.this).load(R.mipmap.downarrow).into(arrow);
                        } else {
                            hideStr.setVisibility(View.INVISIBLE);
                            flexboxLayout.setVisibility(View.VISIBLE);
                            Glide.with(MainFragment.this).load(R.mipmap.uparrow).into(arrow);
                        }
                    }
                });
                if (flexboxLayout.getVisibility() == View.GONE) {
                    hideStr.setVisibility(View.VISIBLE);
                    Glide.with(MainFragment.this).load(R.mipmap.downarrow).into(arrow);
                }
                for (int i = 0; i < habits.size(); ++i) {
                    if (i >= flexboxLayout.getChildCount()) {
                        getLayoutInflater().inflate(R.layout.fragment_main_item_item, flexboxLayout, true);
                    }
                    View view = flexboxLayout.getChildAt(i);
                    ViewGroup.LayoutParams lp = view.getLayoutParams();
                    lp.width = StaticObjects.screenSize.x / 4;
                    view.setLayoutParams(lp);
                    CircleImageView imageView = view.findViewById(R.id.habitIcon);
                    if (habits.get(i).getColor() == null || habits.get(i).getColor().equals("")) {
                        habits.get(i).setColor("#ffffff");
                    }
                    imageView.setCircleBackgroundColor(Color.parseColor(habits.get(i).getColor()));
                    Glide.with(MainFragment.this).load(getResource(habits.get(i).getIcon())).into(imageView);
                    final ImageView imageView2 = view.findViewById(R.id.habitComplete);
                    imageView2.setVisibility(View.INVISIBLE);
                    TextView textView = view.findViewById(R.id.habitName);
                    textView.setText(habits.get(i).getName());
                    final TextView textView1 = view.findViewById(R.id.completeInfo);
                    final Habit habit = habits.get(i);
                    textView1.setText(String.format(getString(R.string.formatCompleteInfo), habits.get(i).getTotalPunch()));
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            punch(v, imageView2, habit, textView1);
                        }
                    });
                    if (habit.getRecentPunchTime().length() > 10 && habit.getRecentPunchTime().substring(0, 10).equals(format.format(calendar.getTime()).substring(0, 10))) {
                        imageView.setAlpha(0.5f);
                        imageView2.setVisibility(View.VISIBLE);
                    }
                }
                for (int i = habits.size(); i < flexboxLayout.getChildCount(); ++i) {
                    flexboxLayout.removeViewAt(i);
                }
            }
        };
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        getHabits();
        EventBus.getDefault().register(this);
        return view;
    }
    public int getResource(String imageName){
        Context ctx = getContext();
        int resId = getResources().getIdentifier(imageName, "mipmap", ctx.getPackageName());
        //如果没有在"mipmap"下找到imageName,将会返回0
        return resId;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        habitsList = new ArrayList<>();
        for (int i = 0; i < titles.length; ++i) {
            habitsList.add(new ArrayList<Habit>());
        }
        calendar = Calendar.getInstance();
        format = new SimpleDateFormat(StaticObjects.formatString, Locale.CHINA);
    }

    private void getHabits() {
        for (List<Habit> l:habitsList) {
            l.clear();
        }
        for (Habit h:StaticObjects.habits) {
            if(h.getFile().equals(true)) continue;
            for (int i = 0; i < titles.length; ++i) {
                if (h.getTimeQuantum() == null || h.getTimeQuantum().equals("")) {
                    habitsList.get(0).add(h);
                    break;
                } else if (titles[i].equals(h.getTimeQuantum())) {
                    habitsList.get(i).add(h);
                    break;
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Subscribe
    public void onEventMainThread(MessageEvent event) {
        getHabits();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        compositeDisposable.clear();
    }

    public void punch(final View imageView1, final ImageView imageView2, final Habit habit, final TextView textView1) {
        DisposableObserver<RespData<Habit>> observer;
        if (habit.getRecentPunchTime().substring(0, 10).equals(format.format(calendar.getTime()).substring(0, 10))) {
            observer = new DisposableObserver<RespData<Habit>>() {
                @Override
                public void onComplete() {}

                @Override
                public void onError(Throwable e) {
                    new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("错误")
                            .setContentText(e.getMessage())
                            .show();
                }

                @Override
                public void onNext(RespData<Habit> dayRespData) {
                    if (dayRespData.status) {
                        new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("操作成功")
                                .show();
                        habit.setTotalPunch(dayRespData.data.getTotalPunch());
                        habit.setRecentPunchTime(dayRespData.data.getLastRecentPunchTime());
                        habit.setCurrcPunch(dayRespData.data.getCurrcPunch());
                        habit.setOncecPunch(dayRespData.data.getOncecPunch());
                        imageView1.setAlpha(1.0f);
                        imageView2.setVisibility(View.INVISIBLE);
                        textView1.setText(String.format(getString(R.string.formatCompleteInfo), habit.getTotalPunch()));
                    } else {
                        new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("错误")
                                .setContentText(dayRespData.msg)
                                .show();
                    }
                }
            };
            commit(observer, false, habit);
        } else {
            observer = new DisposableObserver<RespData<Habit>>() {
                @Override
                public void onComplete() { }

                @Override
                public void onError(Throwable e) {
                    new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("错误")
                            .setContentText(e.getMessage())
                            .show();
                }

                @Override
                public void onNext(RespData<Habit> dayRespData) {
                    if (dayRespData.status) {
                        new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("打卡成功")
                                .setContentText("明天还要再接再厉!")
                                .show();
                        habit.setTotalPunch(dayRespData.data.getTotalPunch());
                        habit.setLastRecentPunchTime(dayRespData.data.getRecentPunchTime());
                        habit.setRecentPunchTime(dayRespData.data.getRecentPunchTime());
                        habit.setCurrcPunch(dayRespData.data.getCurrcPunch());
                        habit.setOncecPunch(dayRespData.data.getOncecPunch());
                        imageView1.setAlpha(0.5f);
                        imageView2.setVisibility(View.VISIBLE);
                        textView1.setText(String.format(getString(R.string.formatCompleteInfo), habit.getTotalPunch()));
                    } else {
                        new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("错误")
                                .setContentText(dayRespData.msg)
                                .show();
                    }
                }
            };
            commit(observer, true, habit);
        }
        compositeDisposable.add(observer);
    }

    private void commit(final Observer<RespData<Habit>> observer, final Boolean isPunch, final Habit habit) {
        final EditText editText = new EditText(getContext());
        String title;
        if (isPunch) {
            title = "输入你打卡时的心情吧";
        } else {
            title = "确定要取消打卡吗?";
            editText.setVisibility(View.GONE);
        }
        new AlertDialog.Builder(getContext()).setTitle(title)
                .setIcon(R.mipmap.add)
                .setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Day day = new Day();
                        day.setTime(format.format(calendar.getTime()));
                        if (isPunch) {
                            day.setLog(editText.getText().toString());
                            StaticObjects.service.punch(StaticObjects.token, day, habit.getId(), day.getTime().substring(0, 7), day.getTime().substring(8, 10))
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(observer);
                        } else {
                            StaticObjects.service.unpunch(StaticObjects.token, habit.getId(), day.getTime().substring(0, 7), day.getTime().substring(8, 10))
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(observer);
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }
}
