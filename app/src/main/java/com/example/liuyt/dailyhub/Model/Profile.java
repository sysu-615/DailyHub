package com.example.liuyt.dailyhub.Model;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;

public class Profile implements Serializable {
    public String username;
    public String password;
    public byte[] avatar;
    public String description;
    public ArrayList<String> habits;
}
