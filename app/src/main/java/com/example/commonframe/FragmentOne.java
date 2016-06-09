package com.example.commonframe;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import core.base.BaseMultipleFragment;
import core.util.DLog;
import icepick.State;

/**
 * Created by Tyrael on 6/9/16.
 */
public class FragmentOne extends BaseMultipleFragment {

    public static final String TAG = FragmentOne.class.getName();
    private static final String USER_KEY = "user";

    @State
    int count = 0;

    @State
    User user;

    @BindView(R.id.button)
    Button button;

    @BindView(R.id.go)
    Button go;

    public static FragmentOne getInstance(User user) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(USER_KEY, user);
        FragmentOne screen = new FragmentOne();
        screen.setArguments(bundle);
        return screen;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.f1, container, false);
    }

    @Override
    public void onBaseCreate() {
        DLog.d(TAG, "onBaseCreate");
        Bundle bundle = getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable(USER_KEY);
        } else {
            return;
        }
    }

    @Override
    public void onDeepLinking(Intent data) {

    }

    @Override
    public void onNotification(Intent data) {

    }

    @Override
    public void onBindView() {
        super.onBindView();
        registerSingleAction(button, go);
        DLog.d(TAG, "onBindView");
    }

    @Override
    public void onInitializeViewData() {
        DLog.d(TAG, "onInitializeViewData");
    }

    @Override
    public void onBaseResume() {
        DLog.d(TAG, "onBaseResume");
    }

    @Override
    public void onBaseFree() {

    }

    @Override
    public void onSingleClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                Device d = new Device();
                d.number = count++;
                user.devices.add(d);
                break;
            case R.id.go:
                addFragment(R.id.container, FragmentTwo.getInstance(user), FragmentTwo.TAG);
                break;
        }
    }
}
