package com.example.liuyt.dailyhub.AllHabits;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.liuyt.dailyhub.FragmentAdapter;
import com.example.liuyt.dailyhub.MainActivity;
import com.example.liuyt.dailyhub.Model.Habit;
import com.example.liuyt.dailyhub.Model.StaticObjects;
import com.example.liuyt.dailyhub.MyViewHolder;
import com.example.liuyt.dailyhub.R;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class EditHabitActivity extends AppCompatActivity {

    private int pos = -1;
    private Habit habit = new Habit();
    private String timeQuantum;
    private Time timeValue;
    private LinearLayout timeLinerLayout;
    private TextView choose;
    private String chooseColor;
    private String[] color = {"#F0E68C","#F08080","#D15FEE","#AB82FF","#8C8C8C","#98FB98","#76EE00","#6A5ACD","#4EEE94","#1E90FF","#00FFFF","#CD3700","#CAFF70","#BF3EFF","#FFBBFF","#FFE1FF","#FFFF00","#FFF8DC"};
    private List<String> dateOp = new ArrayList<String>(Arrays.asList("任意时间", "起床之后", "晨间习惯", "中午时分", "午间习惯", "晚间习惯", "睡觉之前"));
    private int imageId;
    private ImageView add;
    private String imageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_habits);
        final EditText habitName = findViewById(R.id.habitName);
        final EditText enInput = findViewById(R.id.enInput);
        final CircleImageView icon = findViewById(R.id.EditHabitIcon);
        timeLinerLayout = findViewById(R.id.timeLinerLayout);
        final TextView timeText = findViewById(R.id.curTime);
        add = findViewById(R.id.chooseTime);
        try {
            // 获取数据
            final Intent myIntend = getIntent();
            //从intent取出bundle
            Bundle myBundle = myIntend.getExtras();
            // 获取bundle中数据
            Habit temp = (Habit)myBundle.getSerializable("habit");
            if(temp != null) {
                habit = temp;
                habitName.setText(habit.getName());
                enInput.setText(habit.getEncourage());
                timeQuantum = habit.getTimeQuantum();
                icon.setImageResource(getResource(habit.getIcon()));
                icon.setBackgroundColor(Color.parseColor(habit.getColor()));
                if(habit.getReminderTime() != null && !habit.getReminderTime().equals("")) {
                    timeLinerLayout.setVisibility(View.VISIBLE);
                    timeText.setText(habit.getReminderTime());
                }
                pos = myBundle.getInt("position");
                imageName = habit.getIcon();
            }
        } catch (Exception e) {
            TextView headerText = findViewById(R.id.headerText);
            headerText.setText("Create habit");
        }

        // 数据初始化
        List<List<String>> allColor = new ArrayList<>();
        for(int i = 0; i < color.length; i+=2) {
            ArrayList<String> temp = new ArrayList<>();
            temp.add(color[i]);
            temp.add(color[i+1]);
            allColor.add(temp);
        }

        List<List<String>> iconIndex = new ArrayList<>();
        for(int i = 0; i < 60; i += 3) {
            ArrayList<String> temp = new ArrayList<>();
            temp.add(String.valueOf(i));
            temp.add(String.valueOf(i+1));
            temp.add(String.valueOf(i+2));
            iconIndex.add(temp);
        }

        // 提醒时间选择相关
        final Date curDate = new Date();
        timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeLinerLayout.setVisibility(View.INVISIBLE);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timeLinerLayout.getVisibility() == View.VISIBLE) {
                    Toast.makeText(EditHabitActivity.this, "暂时只支持提醒一次", Toast.LENGTH_LONG).show();
                    return;
                }
                TimePickerDialog timePickerDialog = new TimePickerDialog(EditHabitActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        try {
                            String dtStart = String.valueOf(hourOfDay)  + ":" +  String.valueOf(minute);
                            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                            timeValue = new java.sql.Time(format.parse(dtStart).getTime());
                            timeLinerLayout.setVisibility(View.VISIBLE);
                            timeText.setText(timeValue.toString());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }, 0, 0, true);
                timePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        timeLinerLayout.setVisibility(View.INVISIBLE);
                        return;
                    }
                });
                timePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                timePickerDialog.show();
            }
        });

        // 背景色选择
        RecyclerView colorRecyclerView = findViewById(R.id.colorRecyclerView);
        FragmentAdapter<List<String>> myColorAdapter = new FragmentAdapter<List<String>>(EditHabitActivity.this, R.layout.item_color, allColor) {
            @Override
            public void convert(MyViewHolder holder, List<String> s, int t) {
                final  List<String> _s = s;
                final CircleImageView color0 = holder.getView(R.id.color1);
                final CircleImageView color1 = holder.getView(R.id.color2);
                color0.setBackgroundColor(Color.parseColor(s.get(0)));
                color1.setBackgroundColor(Color.parseColor(s.get(1)));
                color0.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        icon.setBackgroundColor(Color.parseColor(_s.get(0)));
                        chooseColor = _s.get(0);
                    }
                });
                color1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        icon.setBackgroundColor(Color.parseColor(_s.get(1)));
                        chooseColor = _s.get(1);
                    }
                });
            }
        };
        LinearLayoutManager colorMs = new LinearLayoutManager(this);
        colorMs.setOrientation(LinearLayoutManager.HORIZONTAL);// 设置 recyclerview 布局方式为横向布局
        colorRecyclerView.setLayoutManager(colorMs); // 类似ListView
        colorRecyclerView.setAdapter(myColorAdapter);

        // 图标选择
        RecyclerView iconRecyclerView = findViewById(R.id.iconRecyclerView);
        FragmentAdapter<List<String>> myIconAdapter = new FragmentAdapter<List<String>>(EditHabitActivity.this, R.layout.item_icon, iconIndex) {
            @Override
            public void convert(MyViewHolder holder, List<String> s, int t) {
                final  List<String> _s = s;
                final CircleImageView icon1 = holder.getView(R.id.icon1);
                final CircleImageView icon2 = holder.getView(R.id.icon2);
                final CircleImageView icon3 = holder.getView(R.id.icon3);
                icon1.setImageResource(getResource("image" + s.get(0)));
                icon2.setImageResource(getResource("image" + s.get(1)));
                icon3.setImageResource(getResource("image" + s.get(2)));
                icon1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageId = getResource("image" + _s.get(0));
                        icon.setImageResource(imageId);
                        imageName = "image" + _s.get(0);
                    }
                });
                icon2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageId = getResource("image" + _s.get(1));
                        icon.setImageResource(imageId);
                        imageName = "image" + _s.get(1);
                    }
                });
                icon3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageId = getResource("image" + _s.get(2));
                        icon.setImageResource(imageId);
                        imageName = "image" + _s.get(2);
                    }
                });
            }
        };
        LinearLayoutManager iconMs = new LinearLayoutManager(this);
        iconMs.setOrientation(LinearLayoutManager.HORIZONTAL);// 设置 recyclerview 布局方式为横向布局
        iconRecyclerView.setLayoutManager(iconMs); // 类似ListView
        iconRecyclerView.setAdapter(myIconAdapter);


        // 提醒时间选择
        RecyclerView dateRecyclerView = findViewById(R.id.dateRecyclerView);
        FragmentAdapter<String> myDateAdapter = new FragmentAdapter<String>(EditHabitActivity.this, R.layout.item_habit_time, dateOp) {
            @Override
            public void convert(MyViewHolder holder, String s, int t) {
                final TextView text = holder.getView(R.id.dateChoose);
                text.setText(s);
                if(timeQuantum != null && s.equals(timeQuantum)) {
                    choose = text;
                    text.setBackgroundColor(Color.parseColor("#1E90FF"));
                }
                text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(choose != null) {
                            choose.setBackgroundColor(Color.parseColor("#ffffff"));
                        }
                        if(choose == text) {
                            text.setBackgroundColor(Color.parseColor("#ffffff"));
                            choose = null;
                        } else {
                            text.setBackgroundColor(Color.parseColor("#1E90FF"));
                            choose = text;
                        }
                    }
                });
            }
        };
        LinearLayoutManager dateMs = new LinearLayoutManager(this);
        dateMs.setOrientation(LinearLayoutManager.HORIZONTAL);// 设置 recyclerview 布局方式为横向布局
        dateRecyclerView.setLayoutManager(dateMs); // 类似ListView
        dateRecyclerView.setAdapter(myDateAdapter);

        // 取消按钮
        Button cancelBtn = findViewById(R.id.canlBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //数据是使用Intent返回
                Intent intent = new Intent(EditHabitActivity.this, MainActivity.class);
                //设置返回数据
                setResult(RESULT_CANCELED, intent);
                // 关闭页面
                finish();
            }
        });

        // 确定按钮
        Button okButn = findViewById(R.id.postBtn);
        okButn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(habitName.getText().toString().equals("")) {
                    Toast.makeText(EditHabitActivity.this, "名字不能为空", Toast.LENGTH_LONG).show();
                } else {
                    if(chooseColor != null) {
                        habit.setColor(chooseColor);
                    }
                    if(imageName != null) {
                        habit.setIcon(imageName);
                    }
                    habit.setEncourage(enInput.getText().toString());
                    habit.setName(habitName.getText().toString());
                    if(choose != null) {
                        habit.setTimeQuantum(choose.getText().toString());
                    }
                    if(timeLinerLayout.getVisibility() == View.VISIBLE && timeValue != null) {
                        habit.setReminderTime(timeValue.toString());
                    }
                    //数据是使用Intent返回
                    Intent intent = new Intent(EditHabitActivity.this, MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("habit", habit);
                    if(pos != -1)
                        bundle.putInt("position", pos);
                    intent.putExtras(bundle);
                    //设置返回数据
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    // 通过图片名获得相应资源的id
    public int getResource(String imageName){
        Context ctx=getBaseContext();
        int resId = getResources().getIdentifier(imageName, "mipmap", ctx.getPackageName());
        //如果没有在"mipmap"下找到imageName,将会返回0
        return resId;
    }

    @Override
    public void onBackPressed() {
        // 关闭页面
        finish();
    }
}
