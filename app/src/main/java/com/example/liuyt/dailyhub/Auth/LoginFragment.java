package com.example.liuyt.dailyhub.Auth;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.dd.processbutton.ProcessButton;
import com.dd.processbutton.iml.ActionProcessButton;
import com.example.liuyt.dailyhub.DLog;
import com.example.liuyt.dailyhub.MainActivity;
import com.example.liuyt.dailyhub.Model.Profile;
import com.example.liuyt.dailyhub.Model.RespData;
import com.example.liuyt.dailyhub.Model.StaticObjects;
import com.example.liuyt.dailyhub.Model.Token;
import com.example.liuyt.dailyhub.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        DLog.i("loginFragment: ", "onCreateView");
        View view = inflater.inflate(R.layout.fragment_login, null, false);
        final EditText editUsernameLogin = view.findViewById(R.id.editUsernameLogin);
        final EditText editPasswordLogin = view.findViewById(R.id.editPasswordLogin);
        final CircleImageView avatarImageViewLogin = view.findViewById(R.id.avatarImageViewLogin);
        final com.rey.material.widget.CheckBox rememberMe = view.findViewById(R.id.rememberMeLogin);
        final ActionProcessButton btnLogin = view.findViewById(R.id.btnLogin);

        Bundle bundle = getActivity().getIntent().getExtras();
        editUsernameLogin.setText(bundle.getString("username", null));
        editPasswordLogin.setText(bundle.getString("password", null));

        if(bundle.getString("username", null) != null) {
            StaticObjects.service.getUserInfo(bundle.getString("username", null))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new DisposableObserver<RespData<Profile>>() {
                @Override
                public void onNext(RespData<Profile> resp) {
                    if (resp.status) {
                        avatarImageViewLogin.setImageBitmap(bytesToBitmap(resp.data.avatar));
                    } else {
                        Toast.makeText(getContext(), resp.msg, Toast.LENGTH_SHORT).show();
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
        DLog.i("loginFragment: ", "onCreateView2");

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editUsernameLogin.getText())) {
                    Toast.makeText(getContext(), "请输入用户名", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(editPasswordLogin.getText())) {
                    Toast.makeText(getContext(), "请输入密码", Toast.LENGTH_SHORT).show();
                } else {
                    btnLogin.setProgress(50);
                    btnLogin.setEnabled(false);
                    editUsernameLogin.setEnabled(false);
                    editPasswordLogin.setEnabled(false);

                    final Bundle bundle = new Bundle();
                    bundle.putString("username", editUsernameLogin.getText().toString());
                    bundle.putString("password", null);
                    final Intent intent = new Intent(getContext(), MainActivity.class);
                    if(rememberMe.isChecked()) {
                        bundle.putString("password", editPasswordLogin.getText().toString());
                    }
                    final Profile profile = new Profile();
                    profile.username = editUsernameLogin.getText().toString();
                    profile.password = editPasswordLogin.getText().toString();
                    StaticObjects.service.login(profile)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableObserver<RespData<Token>>() {
                        @Override
                        public void onNext(RespData<Token> resp) {
                            if (resp.status) {
                                btnLogin.setProgress(100);
                                StaticObjects.token = resp.data.dh_token;
                                bundle.putString(StaticObjects.tokenName, StaticObjects.token);
                                Toast.makeText(getContext(), "登录成功", Toast.LENGTH_SHORT).show();
                                intent.putExtras(bundle);
                                getActivity().setResult(getActivity().RESULT_OK, intent);
                                getActivity().finish();
                            } else {
                                btnLogin.setProgress(-1);
                                btnLogin.setEnabled(true);
                                editUsernameLogin.setEnabled(true);
                                editPasswordLogin.setEnabled(true);
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        btnLogin.setProgress(0);
                                    }
                                }, 1000);
                                Toast.makeText(getContext(), resp.msg, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            btnLogin.setProgress(-1);
                            btnLogin.setEnabled(true);
                            editUsernameLogin.setEnabled(true);
                            editPasswordLogin.setEnabled(true);
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    btnLogin.setProgress(0);
                                }
                            }, 1000);
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
                }
            }
        });
        return view;
    }

    public Bitmap bytesToBitmap(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
