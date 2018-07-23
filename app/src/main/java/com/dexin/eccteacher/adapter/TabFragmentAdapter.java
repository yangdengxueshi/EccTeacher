package com.dexin.eccteacher.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import org.jetbrains.annotations.Contract;

import java.util.List;

/**
 * 联系人Tab FragmentAdapter
 */
public final class TabFragmentAdapter extends FragmentStatePagerAdapter {
    private final List<String> mTabTitleList;
    private final List<Fragment> mFragmentList;

    public TabFragmentAdapter(FragmentManager fragmentManager, List<String> tabTitleList, List<Fragment> fragmentList) {
        super(fragmentManager);
        mTabTitleList = tabTitleList;
        mFragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position % mFragmentList.size());
    }

    @Contract(pure = true)
    @Override
    public int getItemPosition(@NonNull Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public int getCount() {
        return (mFragmentList != null) ? mFragmentList.size() : 0;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mTabTitleList.get(position % mTabTitleList.size());
    }
}
