package com.dexin.eccteacher.fragment;

import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.dexin.eccteacher.R;
import com.dexin.eccteacher.adapter.TabFragmentAdapter;
import com.dexin.eccteacher.application.AppConfig;
import com.dexin.eccteacher.bean.MessageEvent;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 通讯录Fragment
 */
public class ContFragment extends BaseFragment {
    @BindView(R.id.srf_smart_refresh)
    SmartRefreshLayout mSrfSmartRefresh;
    @BindView(R.id.tl_cont_classify)
    TabLayout mTlContClassify;
    @BindView(R.id.vp_cont_classify)
    ViewPager mVpContClassify;
    private List<String> mTabTitleList = new ArrayList<>();
    private List<Fragment> mFragmentList = new ArrayList<>();

    @NonNull
    public static ContFragment newInstance() {
        return new ContFragment();
    }

    @Override
    public void onSupportInvisible() {
        mSrfSmartRefresh.finishRefresh(false);
        super.onSupportInvisible();
    }

    @Override
    protected int getLayoutResourceID() {
        return R.layout.fragment_cont;
    }

    @Override
    protected void initView() {
        setSwipeBackEnable(false);
        mTabTitleList.clear();
        mTabTitleList.add("教师");
        mTabTitleList.add("家长");
        mFragmentList.clear();
        mFragmentList.add(ContTeacherFragment.newInstance());
        mFragmentList.add(ContHouseholderFragment.newInstance());
        mVpContClassify.removeAllViews();
        mVpContClassify.setAdapter(new TabFragmentAdapter(getChildFragmentManager(), mTabTitleList, mFragmentList));
        mTlContClassify.removeAllTabs();
        mTlContClassify.setupWithViewPager(mVpContClassify);
    }

    @Override
    protected void initListener() {
        mSrfSmartRefresh.setOnRefreshListener(refreshLayout -> {
            if (AppConfig.isNetAvailable()) {
                EventBus.getDefault().post(new MessageEvent(MessageEvent.EVENT_REFRESH_CONT));
            } else {
                mSrfSmartRefresh.finishRefresh(false);
            }
        });
    }

    @Override
    protected void initData() {
    }

    /**
     * 接收到EventBus发布的消息事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(MessageEvent messageEvent) {
        if (!isSupportVisible()) return;
        switch (messageEvent.getMessage()) {
            case MessageEvent.EVENT_CANCEL_REFRESH_CONT:
                mSrfSmartRefresh.finishRefresh(true);
                break;
            default:
        }
    }
}
