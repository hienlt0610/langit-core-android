package com.example.commonframe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import core.base.BaseMultipleFragmentActivity;

public class MainActivity extends BaseMultipleFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onInitializeFragments() {

    }

    @Override
    public void onBaseCreate() {

    }

    @Override
    public void onDeepLinking(Intent data) {
        // do nothing
    }

    @Override
    public void onNotification(Intent data) {
        // do nothing
    }

    @Override
    public void onInitializeViewData() {

    }

    @Override
    public void onBaseResume() {

    }

    @Override
    public void onBaseFree() {

    }

    @Override
    public void onSingleClick(View v) {

    }
}
