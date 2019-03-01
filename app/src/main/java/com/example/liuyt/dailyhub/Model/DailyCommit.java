package com.example.liuyt.dailyhub.Model;

public class DailyCommit {
    public String id = "";
    public String commitTime;
    public String commitContent;
    public DailyCommit(String _id,String time,String content){
        this.id = _id;
        this.commitTime = time;
        this.commitContent = content;
    }
}
