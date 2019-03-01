package com.example.liuyt.dailyhub.AllHabits;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liuyt.dailyhub.Model.Habit;
import com.example.liuyt.dailyhub.Model.Month;
import com.example.liuyt.dailyhub.Model.RespData;
import com.example.liuyt.dailyhub.Model.StaticObjects;
import com.example.liuyt.dailyhub.R;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class HabitsDetailActivity extends AppCompatActivity {

    public CalendarView calendarView;
    public TextView mTextMonthDay;
    public TextView mTextYear;
    public TextView tv_current_day;
    public TextView mTextLunar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habits_detail);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        final Habit habit = (Habit) bundle.getSerializable("Habit");
        calendarView = findViewById(R.id.calendarView);
        mTextMonthDay = findViewById(R.id.tv_month_day);
        mTextYear = findViewById(R.id.tv_year);
        mTextLunar = findViewById(R.id.tv_lunar);
        tv_current_day = findViewById(R.id.tv_current_day);
        int year = calendarView.getCurYear();
        int month = calendarView.getCurMonth();
        int day = calendarView.getCurDay();
        mTextMonthDay.setText(String.format("%02d",month) + "月" + String.format("%02d",day) + "日");
        mTextYear.setText(String.valueOf(year) + "年");
        tv_current_day.setText(String.valueOf(day));
        getInfo(year, month, habit);
        calendarView.setOnYearChangeListener(new CalendarView.OnYearChangeListener() {
            @Override
            public void onYearChange(int year) {

            }
        });
        calendarView.setOnMonthChangeListener(new CalendarView.OnMonthChangeListener() {
            @Override
            public void onMonthChange(int year, int month) {
                getInfo(year, month, habit);
            }
        });
        calendarView.setOnCalendarSelectListener(new CalendarView.OnCalendarSelectListener() {
            @Override
            public void onCalendarOutOfRange(Calendar calendar) {

            }

            @Override
            public void onCalendarSelect(Calendar calendar, boolean isClick) {
                if(isClick) {
                    int year = calendar.getYear();
                    int month = calendar.getMonth();
                    int day = calendar.getDay();
                    mTextMonthDay.setText(String.format("%02d",month) + "月" + String.format("%02d",day) + "日");
                    mTextYear.setText(String.valueOf(year) + "年");
                }
            }
        });
        initView(habit);
    }


    private Calendar getSchemeCalendar(int year, int month, int day, int color, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
        calendar.setScheme(text);
        return calendar;
    }

    public void initView(Habit habit) {
        TextView totalPunchDays = findViewById(R.id.totalPunchDays);
        totalPunchDays.setText(String.format(getString(R.string.formatTotalDays), habit.getTotalPunch()));

        TextView currentContinuousPunch = findViewById(R.id.currentContinuousPunch);
        currentContinuousPunch.setText(String.format(getString(R.string.formatTotalDays), habit.getCurrcPunch()));

        TextView largestContinuousPunch = findViewById(R.id.largestContinuousPunch);
        largestContinuousPunch.setText(String.format(getString(R.string.formatTotalDays), habit.getOncecPunch()));

        TextView createDate = findViewById(R.id.createDate);
        createDate.setText(habit.getCreateAt());
    }

    public void getInfo(final int year, final int month, Habit habit) {
        String monthId = String.valueOf(year) + "-" + String.format("%02d",month);
        StaticObjects.service.getHabitPunchInfo(StaticObjects.token, habit.getId(),  monthId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<RespData<Month>>() {
                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onNext(RespData<Month> repos) {
                        Map<String, Calendar> map = new HashMap<>();
                        ArrayList<String> days = repos.data.getDays();
                        if(days != null) {
                            for (int i = 0; i < days.size(); ++i) {
                                map.put(getSchemeCalendar(year, month, Integer.valueOf(days.get(i)), 0xFF40db25, "签").toString(),
                                        getSchemeCalendar(year, month, Integer.valueOf(days.get(i)), 0xFF40db25, "签"));
                            }
                            calendarView.setSchemeDate(map);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Toast.makeText(HabitsDetailActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
