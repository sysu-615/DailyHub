package com.example.liuyt.dailyhub.SettingFragmant;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.liuyt.dailyhub.FragmentAdapter;
import com.example.liuyt.dailyhub.MessageEvent;
import com.example.liuyt.dailyhub.Model.Habit;
import com.example.liuyt.dailyhub.Model.RespData;
import com.example.liuyt.dailyhub.Model.StaticObjects;
import com.example.liuyt.dailyhub.MyViewHolder;
import com.example.liuyt.dailyhub.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link EndHabitFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EndHabitFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<Habit> mDatas = new ArrayList<>();
    private FragmentAdapter<Habit> adapter;

    public static EndHabitFragment newInstance() {
        EndHabitFragment fragment = new EndHabitFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EndHabitFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EndHabitFragment newInstance(String param1, String param2) {
        EndHabitFragment fragment = new EndHabitFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_end_habit, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.endRecyclerView);
        adapter = initRecyclerViewAdapter();
        //布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        //recyclerView动画
        recyclerView.setItemAnimator(new SlideInLeftAnimator());
        return view;
    }

    private FragmentAdapter<Habit> initRecyclerViewAdapter(){
        mDatas.clear();
        for(Habit habit : StaticObjects.habits){
            if(habit.getFile()){
                mDatas.add(habit);
            }
        }
        return new FragmentAdapter<Habit>(this.getContext(),R.layout.item_all_end_habits,mDatas) {
            @Override
            public void convert(MyViewHolder holder, final Habit habit, final int position) {
                TextView textView1 = holder.getView(R.id.endHabitName),
                        textView2 = holder.getView(R.id.endHabitContinuedTime),
                        textView3 = holder.getView(R.id.endHabitDes);
                textView1.setText(habit.getName());
                textView2.setText(String.valueOf(habit.getTotalPunch()));
                textView3.setText(habit.getEncourage());
                ImageView imageView = holder.getView(R.id.endHabitImage);
                Glide.with(getContext()).load(getResource(habit.getIcon())).into(imageView);
                holder.getView(R.id.resumeHabitBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        habit.setFile(false);
                        //网络访问
                        StaticObjects.service.updateHabit(StaticObjects.token, habit, habit.getId())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new DisposableObserver<RespData<Habit>>() {
                                    @Override
                                    public void onNext(RespData<Habit> respHabit) {
                                        if (respHabit.status) {
                                            mDatas.remove(position);
                                            adapter.notifyDataSetChanged();
                                            Toast.makeText(getContext(), "操作成功", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getContext(), respHabit.msg, Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onComplete() {
                                    }
                                });
                    }
                });
            }
        };
    }

    private int getResource(String imageName){
        Context ctx = getContext();
        int resId = getResources().getIdentifier(imageName, "mipmap", ctx.getPackageName());
        //如果没有在"mipmap"下找到imageName,将会返回0
        return resId;
    }

    @Subscribe
    public void onEventMainThread(MessageEvent event) {
        mDatas.clear();
        for(Habit habit : StaticObjects.habits){
            if(habit.getFile()){
                mDatas.add(habit);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
