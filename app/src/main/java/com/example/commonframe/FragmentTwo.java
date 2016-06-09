package com.example.commonframe;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import core.base.BaseMultipleFragment;
import core.util.DLog;
import icepick.State;

/**
 * Created by Tyrael on 6/9/16.
 */
public class FragmentTwo extends BaseMultipleFragment {

    public static final String TAG = FragmentTwo.class.getName();
    private static final String USER_KEY = "user";

    @State
    User user;

    @BindView(R.id.textView2)
    TextView textView2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.f2, container, false);
    }

    public static FragmentTwo getInstance(User user) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(USER_KEY, user);
        FragmentTwo screen = new FragmentTwo();
        screen.setArguments(bundle);
        return screen;
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
        DLog.d(TAG, "onBindView");
    }

    @Override
    public void onInitializeViewData() {
        DLog.d(TAG, "onInitializeViewData");
        textView2.setText(user.toString());
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

    }
}
