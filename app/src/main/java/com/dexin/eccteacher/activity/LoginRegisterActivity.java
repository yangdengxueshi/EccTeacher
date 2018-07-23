package com.dexin.eccteacher.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.dexin.eccteacher.R;
import com.dexin.eccteacher.application.AppConfig;
import com.dexin.eccteacher.application.CustomApplication;
import com.dexin.eccteacher.fragment.LoginFragment;
import com.gyf.barlibrary.ImmersionBar;
import com.vondear.rxtool.view.RxToast;

public class LoginRegisterActivity extends BaseActivity {
    @NonNull
    public static Intent createIntent(Context context) {
        return new Intent(context, LoginRegisterActivity.class);
    }

    @Override
    protected int getLayoutResourceID() {
        return R.layout.activity_login_register;
    }

    @Override
    protected void initImmersionBar() {
        mImmersionBar = ImmersionBar.with(this).transparentBar().statusBarDarkFont(true, 0.2f).keyboardEnable(true);
        mImmersionBar.init();
    }

    @Override
    public void initView() {
        setSwipeBackEnable(false);
        if (!TextUtils.isEmpty(AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN))) {
            startActivity(MainActivity.createIntent(CustomApplication.getContext()));
            finish();
        } else {
            if (findFragment(LoginFragment.class) == null) loadRootFragment(R.id.fl_fragment_container, LoginFragment.newInstance());
        }
    }

    @Override
    public void initListener() {
    }

    @Override
    public void initData() {
    }


    //-----------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------TODO 连续两次点击返回键退出--------------------------------------------------------
    //-----------------------------------------------------------------------------------------------------------------------------------------
    private long TIME_LAST_BACK_PRESSED;//上次Back键被按下的时间

    public void onBackPressedSupport() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            pop();
        } else {
            if (System.currentTimeMillis() - TIME_LAST_BACK_PRESSED < 4 * 500) {
                finish();
            } else {
                RxToast.warning("再次点击返回键退出!");
                TIME_LAST_BACK_PRESSED = System.currentTimeMillis();
            }
        }
    }
}
