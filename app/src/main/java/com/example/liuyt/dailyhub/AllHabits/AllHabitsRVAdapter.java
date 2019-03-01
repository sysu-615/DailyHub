package com.example.liuyt.dailyhub.AllHabits;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.example.liuyt.dailyhub.MyViewHolder;

import java.util.ArrayList;

abstract class AllHabitsRVAdapter<Habit> extends RecyclerView.Adapter<MyViewHolder> {
    private Context context;
    private int layoutId;
    private ArrayList<Habit> data;
    private OnItemClickListener onItemClickListener;

    // 构造函数
    public AllHabitsRVAdapter(Context _context, int _layoutId, ArrayList<Habit> _data) {
        context = _context;
        layoutId = _layoutId;
        data = _data;
    }

    // 点击事件
    public interface OnItemClickListener{
        void onClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener _onItemClickListener) {
        this.onItemClickListener = _onItemClickListener;
    }

    // 声明抽象方法convert
    public abstract void convert(MyViewHolder holder, Habit t);

    // 创建Item视图，并返回相应的ViewHolder
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = MyViewHolder.get(context, parent, layoutId);
        return holder;
    }

    // 绑定数据到正确的Item视图上
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        convert(holder, data.get(position)); // convert函数需要重写，在创建Adapter时重载
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onClick(holder.getAdapterPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}