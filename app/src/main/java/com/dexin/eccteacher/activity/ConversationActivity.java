package com.dexin.eccteacher.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.dexin.eccteacher.R;

import butterknife.BindView;

public class ConversationActivity extends BaseActivity {
    @BindView(R.id.tb_commom_toolbar)
    Toolbar mTbCommomToolbar;
    @BindView(R.id.tv_commom_title)
    TextView mTvCommomTitle;

    @NonNull
    public static Intent createIntent(Context context) {
        return new Intent(context, ConversationActivity.class);
    }

    @Override
    protected int getLayoutResourceID() {
        return R.layout.activity_conversation;
    }

    @Override
    public void initView() {
        //初始化Toolbar
        setSupportActionBar(mTbCommomToolbar);
        ActionBar lActionBar;
        if ((lActionBar = getSupportActionBar()) != null) lActionBar.setDisplayShowTitleEnabled(false);
        Uri lDataUri = getIntent().getData();
        if (lDataUri != null) mTvCommomTitle.setText(lDataUri.getQueryParameter("title"));
    }

    @Override
    public void initListener() {
        mTbCommomToolbar.setNavigationOnClickListener(v -> onBackPressedSupport());
    }

    @Override
    public void initData() {
    }
}
