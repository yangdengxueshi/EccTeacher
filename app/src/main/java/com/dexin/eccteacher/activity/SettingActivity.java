package com.dexin.eccteacher.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.dexin.eccteacher.R;
import com.dexin.eccteacher.fragment.SettingFragment;

public class SettingActivity extends BaseActivity {
    @NonNull
    public static Intent createIntent(Context context) {
        return new Intent(context, SettingActivity.class);
    }

    @Override
    protected int getLayoutResourceID() {
        return R.layout.activity_setting;
    }

    @Override
    public void initView() {
        if (findFragment(SettingFragment.class) == null) loadRootFragment(R.id.fl_fragment_container, SettingFragment.newInstance());
    }

    @Override
    public void initListener() {
    }

    @Override
    public void initData() {
    }

    @Override
    public void onBackPressedSupport() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            pop();
        } else {
            finish();
        }
    }
}
