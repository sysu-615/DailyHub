package com.example.liuyt.dailyhub.Model;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class StaticObjects {
    public static List<Habit> habits = new ArrayList<Habit>();
    public static Service service;
    public static String token;
    public static String baseUrl = "http://129.204.110.49:9090/";
    public static String formatString = "YYYY-MM-DD HH:mm";
    public static String preference_name = "Daily_Hub";
    public static String tokenName = "dh_token";
    public static int mode = MODE_PRIVATE;
    public static Point screenSize;
}
