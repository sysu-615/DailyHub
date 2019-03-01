package com.example.liuyt.dailyhub;

import com.example.liuyt.dailyhub.Model.Habit;

public class MessageEvent{
    private Habit message;
    public MessageEvent(Habit message){
        this.message = message;
    }

    public Habit getMessage() {
        return message;
    }

    public void setMessage(Habit message) {
        this.message = message;
    }
}