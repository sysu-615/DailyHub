package com.example.liuyt.dailyhub.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class Month implements Serializable {
    String id;
    int planPunch;
    int actualPunch;
    int missPunch;
    ArrayList<String> days;

    public String getId() {
        return id;
    }

    public ArrayList<String> getDays() {
        return days;
    }

    public int getActualPunch() {
        return actualPunch;
    }

    public int getMissPunch() {
        return missPunch;
    }

    public int getPlanPunch() {
        return planPunch;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setActualPunch(int actualPunch) {
        this.actualPunch = actualPunch;
    }

    public void setDays(ArrayList<String> days) {
        this.days = days;
    }

    public void setMissPunch(int missPunch) {
        this.missPunch = missPunch;
    }

    public void setPlanPunch(int planPunch) {
        this.planPunch = planPunch;
    }
}
