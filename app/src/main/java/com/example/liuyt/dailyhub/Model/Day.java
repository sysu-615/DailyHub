package com.example.liuyt.dailyhub.Model;

import java.io.Serializable;

public class Day implements Serializable {
    String id;
    String time;
    String log;

    public String getId() {
        return id;
    }

    public String getTime() {
        return time;
    }

    public String getLog() {
        return log;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setLog(String log) {
        this.log = log;
    }
}
