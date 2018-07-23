package com.dexin.eccteacher.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.dexin.eccteacher.R;
import com.dexin.eccteacher.adapter.TabFragmentAdapter;
import com.dexin.eccteacher.application.AppConfig;
import com.dexin.eccteacher.application.CustomApplication;
import com.dexin.eccteacher.bean.MessageEvent;
import com.dexin.eccteacher.fragment.ApplyClassroomOfAllFragment;
import com.dexin.eccteacher.fragment.ApplyClassroomOfMineFragment;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 申请教室 Activity
 */
public class ApplyClassroomActivity extends BaseActivity {
    @BindView(R.id.tb_commom_toolbar)
    Toolbar mTbCommomToolbar;
    @BindView(R.id.tv_commom_title)
    TextView mTvCommomTitle;
    @BindView(R.id.srf_smart_refresh)
    SmartRefreshLayout mSrfSmartRefresh;
    @BindView(R.id.tl_apply_classroom_classify)
    TabLayout mTlApplyClassroomClassify;
    @BindView(R.id.vp_apply_classroom_classify)
    ViewPager mVpApplyClassroomClassify;
    private List<String> mTabTitleList = new ArrayList<>();
    private List<Fragment> mFragmentList = new ArrayList<>();

    @NonNull
    public static Intent createIntent(Context context) {
        return new Intent(context, ApplyClassroomActivity.class);
    }

    @Override
    protected int getLayoutResourceID() {
        return R.layout.activity_apply_classroom;
    }

    @Override
    public void initView() {
        //初始化Toolbar
        setSupportActionBar(mTbCommomToolbar);
        ActionBar lActionBar = getSupportActionBar();
        if (lActionBar != null) lActionBar.setDisplayShowTitleEnabled(false);
        mTvCommomTitle.setText("教室申请");
        //初始化TabLayout + ViewPager
        mTlApplyClassroomClassify.removeAllTabs();
        mVpApplyClassroomClassify.removeAllViews();
        mTabTitleList.clear();
        mFragmentList.clear();
        mTabTitleList.add("全校申请");
        mTabTitleList.add("我的申请");
        mFragmentList.add(ApplyClassroomOfAllFragment.newInstance());
        mFragmentList.add(ApplyClassroomOfMineFragment.newInstance());
        mVpApplyClassroomClassify.setAdapter(new TabFragmentAdapter(getSupportFragmentManager(), mTabTitleList, mFragmentList));
        mVpApplyClassroomClassify.setOffscreenPageLimit(2);
        mTlApplyClassroomClassify.setupWithViewPager(mVpApplyClassroomClassify);
    }

    @Override
    public void initListener() {
        mTbCommomToolbar.setNavigationOnClickListener(v -> onBackPressedSupport());
        mSrfSmartRefresh.setOnRefreshListener(refreshLayout -> EventBus.getDefault().post(new MessageEvent(MessageEvent.EVENT_REFRESH_CLASSROOM)));//发送事件通知子Fragment刷新
    }

    @Override
    public void initData() {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.apply, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_apply:
                startActivity(DoApplyClassroomActivity.createIntent(CustomApplication.getContext()));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFinishRefreshEvent(MessageEvent messageEvent) {
        if (!AppConfig.isComponentAlive(ApplyClassroomActivity.this)) return;
        switch (messageEvent.getMessage()) {
            case MessageEvent.EVENT_FINISH_REFRESH:
                mSrfSmartRefresh.finishRefresh();
                break;
            default:
        }
    }

    @OnClick({R.id.fab_top})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_top:
                EventBus.getDefault().post(new MessageEvent(MessageEvent.EVENT_TOP));
                break;
            default:
        }
    }
}
