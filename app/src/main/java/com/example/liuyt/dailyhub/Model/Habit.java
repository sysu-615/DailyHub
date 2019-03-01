package com.example.liuyt.dailyhub.Model;

import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Habit implements Serializable, Cloneable {
    String id = "";
    String name = "";
    String icon = "image1";
    Boolean file = false;
    String reminderTime = "";
    String encourage = "";
    Boolean important = false;
    Boolean notification = false;
    String color = "#ffffff";
    int totalPunch = 0;
    int currcPunch = 0;
    int oncecPunch = 0;
    String dateTime = "";
    String timeQuantum = "";
    String createAt = "2000-01-01 00:00";
    String recentPunchTime = "2000-01-01 00:00";
    String lastRecentPunchTime = "";
    ArrayList<String> months;

    public Habit(String name, String icon,String timeQuantum, int totalPunch) {
        this.name = name;
        this.icon = icon;
        this.totalPunch = totalPunch;
        this.timeQuantum = timeQuantum;
    }

    public Habit(){
        // 初始化
        DateFormat format = new SimpleDateFormat(StaticObjects.formatString, Locale.CHINA);
        this.createAt = format.format(new Date());
    }

    @Override
    public Habit clone() {
        Habit habit = null;
        try {
            habit = (Habit) super.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return habit;
    }

    public String getTimeQuantum() {
        return timeQuantum;
    }

    public String getColor() {
        return color;
    }

    public int getCurrcPunch() {
        return currcPunch;
    }

    public int getOncecPunch() {
        return oncecPunch;
    }

    public int getTotalPunch() {
        return totalPunch;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getIcon() {
        return icon;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public ArrayList<String> getMonths() {
        return months;
    }

    public Boolean getFile() {
        return file;
    }

    public Boolean getImportant() {
        return important;
    }

    public Boolean getNotification() {
        return notification;
    }

    public String getEncourage() {
        return encourage;
    }

    public String getReminderTime() {
        return reminderTime;
    }

    public String getRecentPunchTime() {
        return recentPunchTime;
    }

    public String  getLastRecentPunchTime() {
        return lastRecentPunchTime;
    }

    public void setCurrcPunch(int currcPunch) {
        this.currcPunch = currcPunch;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setOncecPunch(int oncecPunch) {
        this.oncecPunch = oncecPunch;
    }

    public void setTotalPunch(int totalPunch) {
        this.totalPunch = totalPunch;
    }

    public void setMonths(ArrayList<String> months) {
        this.months = months;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEncourage(String encourage) {
        this.encourage = encourage;
    }

    public void setFile(Boolean file) {
        this.file = file;
    }

    public void setImportant(Boolean important) {
        this.important = important;
    }

    public void setNotification(Boolean notification) {
        this.notification = notification;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setTimeQuantum(String timeQuantum) {
        this.timeQuantum = timeQuantum;
    }

    public void setReminderTime(String reminderTime) {
        this.reminderTime = reminderTime;
    }

    public void setRecentPunchTime(String recentPunchTime) {
        this.recentPunchTime = recentPunchTime;
    }

    public void setLastRecentPunchTime(String lastRecentPunchTime) {
        this.lastRecentPunchTime = lastRecentPunchTime;
    }
}
