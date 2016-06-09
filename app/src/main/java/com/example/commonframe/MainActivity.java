package com.example.commonframe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import core.base.BaseMultipleFragmentActivity;
import core.util.DLog;

/**
 * Created by Tyrael on 6/9/16.
 */
public class MainActivity extends BaseMultipleFragmentActivity {

    String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (savedInstanceState == null)
        setContentView(R.layout.main);
    }

    @Override
    protected void onInitializeFragments() {
        DLog.d(TAG, "onInitializeFragments");
        User user = new User();
        user.name = "ABC";
        user.age = 25;
        if (getSupportFragmentManager() != null || getSupportFragmentManager().getFragments().size() == 0)
            addFragment(R.id.container, FragmentOne.getInstance(user), FragmentOne.TAG);
    }

    @Override
    protected void onLastFragmentBack(int containerId) {

    }

    @Override
    public void onBaseCreate() {
        DLog.d(TAG, "onBaseCreate");
    }

    @Override
    public void onDeepLinking(Intent data) {

    }

    @Override
    public void onNotification(Intent data) {

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

    }
}
