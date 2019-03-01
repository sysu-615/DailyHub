package com.example.liuyt.dailyhub.AllHabits;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.liuyt.dailyhub.MessageEvent;
import com.example.liuyt.dailyhub.Model.RespData;
import com.example.liuyt.dailyhub.Model.StaticObjects;
import com.example.liuyt.dailyhub.Model.Habit;
import com.example.liuyt.dailyhub.MyViewHolder;
import com.example.liuyt.dailyhub.R;
import com.example.liuyt.dailyhub.SettingFragmant.CalendarReminderUtils;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMoveListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


public class AllHabitsFragment extends Fragment {

    private ArrayList<Habit> data = new ArrayList<Habit>();
    private AllHabitsRVAdapter myAdapter;
    private float height = 300;
    private String TAG = "AllHabitFragment";

    /**
     * 菜单创建器。在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int position) {
            SwipeMenuItem endItem = new SwipeMenuItem(getContext())
                    .setImage(R.mipmap.endhabit)
                    .setWidth(130)
                    .setHeight(200);
            swipeRightMenu.addMenuItem(endItem); // 添加一个按钮到右侧菜单。

            SwipeMenuItem editItem = new SwipeMenuItem(getContext())
                    .setImage(R.mipmap.edithabbit)
                    .setWidth(130)
                    .setHeight(200);
            swipeRightMenu.addMenuItem(editItem);// 添加一个按钮到右侧侧菜单。

            SwipeMenuItem deleteItem = new SwipeMenuItem(getContext())
                    .setImage(R.mipmap.deletehabit)
                    .setWidth(130)
                    .setHeight(200);
            swipeRightMenu.addMenuItem(deleteItem);// 添加一个按钮到右侧侧菜单。
        }
    };

    private SwipeMenuItemClickListener mMenuItemClickListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge, int position) {
            // 任何操作必须先关闭菜单，否则可能出现Item菜单打开状态错乱。
            menuBridge.closeMenu();
            // 菜单在Item中的Position：
            int menuPosition = menuBridge.getPosition();
            final int pos = position;

            switch (menuPosition) {
                case 0:
                    // 结束
                    // 设置归档
                    newTreadDeleteNotification(data.get(position));
                    Habit item = data.get(pos);
                    item.setFile(true);
                    StaticObjects.service.updateHabit(StaticObjects.token, item, item.getId())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new DisposableObserver<RespData<Habit>>() {
                                @Override
                                public void onComplete() {
                                }

                                @Override
                                public void onNext(RespData<Habit> repos) {
                                    data.remove(pos);
                                    myAdapter.notifyDataSetChanged();
                                }
                                @Override
                                public void onError(Throwable e) {
                                    e.printStackTrace();
                                }
                            });
                    myAdapter.notifyDataSetChanged();
                    break;
                case 1:
                    // 编辑
                    Intent i = new Intent(getContext(), EditHabitActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("habit", data.get(pos));
                    bundle.putInt("position", StaticObjects.habits.indexOf(data.get(pos)));
                    i.putExtras(bundle);
                    startActivityForResult(i, 2);
                    break;
                case 2:
                    // 删除
                    newTreadDeleteNotification(data.get(position));
                    StaticObjects.service.deleteHabit(StaticObjects.token, data.get(pos).getId())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new DisposableObserver<RespData<Habit>>() {
                                @Override
                                public void onComplete() {
                                }

                                @Override
                                public void onNext(RespData<Habit> repos) {
                                    StaticObjects.habits.remove(data.get(pos));
                                    data.remove(pos);
                                    myAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onError(Throwable e) {
                                    e.printStackTrace();
                                }
                            });
                    break;
            }
        }
    };
    private OnItemMoveListener onItemMoveListener = new OnItemMoveListener() {
        @Override
        public boolean onItemMove(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {
            // 不同的ViewType不能拖拽换位置。
            if (srcHolder.getItemViewType() != targetHolder.getItemViewType()) return false;

            int fromPosition = srcHolder.getAdapterPosition();
            int toPosition = targetHolder.getAdapterPosition();

            Collections.swap(data, fromPosition, toPosition);
            myAdapter.notifyItemMoved(fromPosition, toPosition);
            return true;// 返回true表示处理了并可以换位置，返回false表示你没有处理并不能换位置。
        }

        @Override
        public void onItemDismiss(RecyclerView.ViewHolder srcHolder) {

        }

    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_habits,null);

        SwipeMenuRecyclerView recyclerView = view.findViewById(R.id.allHabits);
        // 设置菜单创建器。
        recyclerView.setSwipeMenuCreator(swipeMenuCreator);
        // 设置菜单Item点击监听。
        recyclerView.setSwipeMenuItemClickListener(mMenuItemClickListener);
        // 监听拖拽，更新UI。
        recyclerView.setOnItemMoveListener(onItemMoveListener);

        myAdapter = new AllHabitsRVAdapter<Habit>(getContext(), R.layout.item_all_habits, (ArrayList<Habit>) data) {
            @Override
            public void convert(MyViewHolder holder, Habit s) {
                ImageView imageView = holder.getView(R.id.habitIcon);
                Glide.with(AllHabitsFragment.this).load(getResource(s.getIcon())).into(imageView);
                TextView textView = holder.getView(R.id.txt_name);
                textView.setText(s.getName());
                TextView textView1 = holder.getView(R.id.txt_describe);
                TextView textView2 = holder.getView(R.id.totalDays);
                textView2.setText(String.format(getString(R.string.formatTotalDays), s.getTotalPunch()));
            }
        };

        myAdapter.setOnItemClickListener(new AllHabitsRVAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Intent myIntent = new Intent(getContext(), HabitsDetailActivity.class);
                // 实例化一个Bundle
                Bundle myBundle = new Bundle();
                // 把item数据放入到bundle中
                myBundle.putSerializable("Habit", data.get(position));
                //把bundle放入intent里
                myIntent.putExtras(myBundle);
                startActivity(myIntent);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLongPressDragEnabled(true); // 拖拽排序
        EventBus.getDefault().register(this);
        return view;
    }
    public int getResource(String imageName){
        Context ctx = getContext();
        int resId = getResources().getIdentifier(imageName, "mipmap", ctx.getPackageName());
        //如果没有在"mipmap"下找到imageName,将会返回0
        return resId;
    }

    /**
     * 提供Fragment实例
     *
     * @return
     */
    public static AllHabitsFragment newInstance() {
        AllHabitsFragment fragment = new AllHabitsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Subscribe
    public void onEventMainThread(MessageEvent event) {
        getUnFileHabit();
        myAdapter.notifyDataSetChanged();
    }

    public void getUnFileHabit() {
        data.clear();
        for (Habit h : StaticObjects.habits) {
            if(h.getFile().equals(false)) {
                data.add(h);
            }
        }
    }

    private void newTreadDeleteNotification(final Habit habit){
        //观察者
        final io.reactivex.Observable<Boolean> observable = io.reactivex.Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                CalendarReminderUtils.deleteCalendarEvent(getContext(),habit.getName());
                e.onComplete();
            }
        });
        //订阅者
        DisposableObserver<Boolean> disposableObserver = new DisposableObserver<Boolean>() {

            @Override
            public void onNext(Boolean value) {
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError = " + e);
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        };
        observable.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver);
    }
}
