package com.dexin.eccteacher.adapter;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 分类ViewPager Adapter
 */
public final class CategoryViewPagerAdapter extends PagerAdapter {
    private List<View> mViewList;

    public CategoryViewPagerAdapter(List<View> viewList) {
        mViewList = viewList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull @NotNull ViewGroup container, int position) {
        View lView = mViewList.get(position % mViewList.size());
        container.addView(lView);
        return (lView);
    }

    @Contract(pure = true)
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public int getCount() {
        return (mViewList == null) ? 0 : mViewList.size();
    }

    @Override
    public void destroyItem(@NonNull @NotNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(mViewList.get(position));
    }
}
