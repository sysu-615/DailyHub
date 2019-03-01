package com.example.liuyt.dailyhub.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import io.reactivex.Observable;
import io.reactivex.internal.operators.observable.ObservableError;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Service {
    // 删除habit
    @DELETE("/api/habits/{id}")
    Observable<RespData<Habit>> deleteHabit(@Header("Authorization")  String token, @Path("id") String id);

    // 创建habit
    @POST("/api/habits")
    Observable<RespData<Habit>> createHabit(@Header("Authorization")  String token, @Body Habit habit);

    // 打卡
    @POST("/api/habits/{id}/{monthId}/{dayId}")
    Observable<RespData<Habit>> punch(@Header("Authorization")  String token, @Body Day day, @Path("id") String id, @Path("monthId") String monthId, @Path("dayId") String dayId);

    @DELETE("/api/habits/{id}/{monthId}/{dayId}")
    Observable<RespData<Habit>> unpunch(@Header("Authorization")  String token, @Path("id") String id, @Path("monthId") String monthId, @Path("dayId") String dayId);

    // 更新habit
    @PUT("/api/habits/{id}")
    Observable<RespData<Habit>> updateHabit(@Header("Authorization")  String token, @Body Habit habit, @Path("id") String id);

    // 获取所有habit
    @GET("/api/habits")
    Observable<RespData<List<Habit>>> getAllHabits(@Header("Authorization")  String token);

    // 获取habit该月份的打卡信息
    @GET("/api/habits/{id}/{monthId}")
    Observable<RespData<Month>> getHabitPunchInfo(@Header("Authorization")  String token, @Path("id") String id, @Path("monthId") String monthId);

    // 获取所有DailyCommit
    @GET("/api/dailycommits")
    Observable<RespData<ArrayList<DailyCommit>>> getAllDailyCommit(@Header("Authorization")  String token);

    // 添加commit
    @POST("/api/dailycommits")
    Observable<RespData<DailyCommit>> createCommit(@Header("Authorization")  String token, @Body DailyCommit commit);

    //  删除commit
    @DELETE("/api/dailycommits/{id}")
    Observable<RespData<String>> deleteCommit(@Header("Authorization")  String token, @Path("id") String id);

    //修改commit
    @PUT("/api/dailycommits/{id}")
    Observable<RespData<DailyCommit>> updateCommit(@Header("Authorization")  String token, @Body DailyCommit commit, @Path("id") String id);

    // 注册
    @POST("/api/register")
    Observable<RespData<Token>> register(@Body Profile profile);

    // 登录
    @POST("/api/login")
    Observable<RespData<Token>> login(@Body Profile profile);

    // 退出
    @GET("/api/logout")
    Observable<RespData<Token>> logout(@Header("Authorization")  String token);

    // 获取用户信息
    @GET("/api/users/{user}")
    Observable<RespData<Profile>> getUserInfo(@Path("user") String user);
}
