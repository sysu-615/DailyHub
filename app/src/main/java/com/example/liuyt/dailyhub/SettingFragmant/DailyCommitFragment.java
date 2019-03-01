package com.example.liuyt.dailyhub.SettingFragmant;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.example.liuyt.dailyhub.DLog;
import com.example.liuyt.dailyhub.FragmentAdapter;
import com.example.liuyt.dailyhub.Model.DailyCommit;
import com.example.liuyt.dailyhub.Model.RespData;
import com.example.liuyt.dailyhub.Model.StaticObjects;
import com.example.liuyt.dailyhub.MyViewHolder;
import com.example.liuyt.dailyhub.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link DailyCommitFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DailyCommitFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String TAG = "DailyCommitFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ArrayList<DailyCommit> mDatas = new ArrayList<>();
    FragmentAdapter<DailyCommit> adapter;
    private com.rey.material.widget.FloatingActionButton floatingActionButton;
    private EditText commentContent;

    public static DailyCommitFragment newInstance() {
        DailyCommitFragment fragment = new DailyCommitFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    public DailyCommitFragment(){

    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DailyCommitFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DailyCommitFragment newInstance(String param1, String param2) {
        DailyCommitFragment fragment = new DailyCommitFragment();
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
        getAllCommit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        DLog.setDebug(true);
        DLog.d(TAG,"onCreateView");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_daily_commit, container, false);
        floatingActionButton = view.findViewById(R.id.addCommit);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editText = new EditText(getContext());
                new AlertDialog.Builder(getContext()).setTitle("添加每日记录")
                        .setIcon(R.mipmap.add)
                        .setView(editText)
                        .setPositiveButton("添加", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String content = editText.getText().toString();
                                if(content.equals("")){
                                    new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("添加失败")
                                        .setContentText("每一天都不会是空白")
                                        .show();
                                } else {
                                    new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                                            .setTitleText("添加成功")
                                            .setContentText("新的一天，新的事物")
                                            .show();
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
                                    String dateString = simpleDateFormat.format(new Date());
                                    addCommit(new DailyCommit("",dateString,content));
                                }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });
        RecyclerView recyclerView = view.findViewById(R.id.commitRecyclerView);
        adapter = initRecyclerViewAdapter();
        //布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        //recyclerView动画
        recyclerView.setItemAnimator(new SlideInLeftAnimator());
        return view;
    }

    private FragmentAdapter<DailyCommit> initRecyclerViewAdapter(){
        return new FragmentAdapter<DailyCommit>(this.getContext(),R.layout.item_daily_commit,mDatas) {
            @Override
            public void convert(MyViewHolder holder,final DailyCommit dailyCommit,final int pos) {
                SwipeLayout swipeLayout = holder.getView(R.id.commitSwipeLayout);
                //set show mode.
                swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
                TextView textView1 = holder.getView(R.id.commitTime),textView2 = holder.getView(R.id.commitContent);
                textView1.setText(dailyCommit.commitTime);
                textView2.setText(dailyCommit.commitContent);
                holder.getView(R.id.deleteCommit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteCommit(pos);
                    }
                });
                holder.getView(R.id.editCommit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    final EditText editText = new EditText(getContext());
                    editText.setText(dailyCommit.commitContent);
                    new AlertDialog.Builder(getContext()).setTitle("编辑每日记录")
                        .setIcon(R.mipmap.add)
                        .setView(editText)
                        .setPositiveButton("修改", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            String content = editText.getText().toString();
                            if(content.equals("")){
                                new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("修改失败")
                                        .setContentText("每一天都不会是空白")
                                        .show();
                            } else {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
                                String dateString = simpleDateFormat.format(new Date());
                                updateCommit(pos,new DailyCommit(dailyCommit.id,dateString,content));
                                new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("修改成功")
                                        .setContentText("新的一天，新的事物")
                                        .show();
                            }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
                    }
                });
            }
        };
    }

    private void getAllCommit(){
        StaticObjects.service.getAllDailyCommit(StaticObjects.token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<RespData<ArrayList<DailyCommit>>>() {
                    @Override
                    public void onNext(RespData<ArrayList<DailyCommit>> resCommits) {
                        mDatas.addAll(resCommits.data);
                        adapter.notifyDataSetChanged();
                        DLog.d(TAG,"get success");
                    }

                    @Override
                    public void onError(Throwable e) {
                        DLog.d(TAG,e.toString());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private void addCommit(DailyCommit commit){
        StaticObjects.service.createCommit(StaticObjects.token,commit)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new DisposableObserver<RespData<DailyCommit>>() {
                @Override
                public void onNext(RespData<DailyCommit> resCommit) {
                    mDatas.add(resCommit.data);
                    adapter.notifyItemInserted(mDatas.size()-1);
                    DLog.d(TAG,"add success");
                }

                @Override
                public void onError(Throwable e) {
                    DLog.d(TAG,e.toString());
                }

                @Override
                public void onComplete() {
                }
            });
    }

    private void deleteCommit(final int pos){
        StaticObjects.service.deleteCommit(StaticObjects.token,mDatas.get(pos).id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new DisposableObserver<RespData<String>>() {
                @Override
                public void onNext(RespData<String> resCommit) {
                    mDatas.remove(pos);
                    adapter.notifyDataSetChanged();
                    DLog.d(TAG,"delete success");
                }

                @Override
                public void onError(Throwable e) {
                    DLog.d(TAG,e.toString());
                }

                @Override
                public void onComplete() {
                }
            });
    }

    private void updateCommit(final int pos,DailyCommit dailyCommit){
        StaticObjects.service.updateCommit(StaticObjects.token,dailyCommit,mDatas.get(pos).id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new DisposableObserver<RespData<DailyCommit>>() {
                @Override
                public void onNext(RespData<DailyCommit> resCommit) {
                    mDatas.set(pos,resCommit.data);
                    adapter.notifyDataSetChanged();
                    DLog.d(TAG,"edit success");
                }

                @Override
                public void onError(Throwable e) {
                    DLog.d(TAG,e.toString());
                }

                @Override
                public void onComplete() {
                }
            });
    }
}
