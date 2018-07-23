package com.dexin.eccteacher.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.dexin.eccteacher.R;

public class PublishFaultActivity extends BaseActivity {
    @NonNull
    public static Intent createIntent(Context context) {
        return new Intent(context, PublishFaultActivity.class);
    }

    @Override
    protected int getLayoutResourceID() {
        return R.layout.activity_publish_fault;
    }

    @Override
    public void initView() {
    }

    @Override
    public void initListener() {
    }

    @Override
    public void initData() {
    }
}
