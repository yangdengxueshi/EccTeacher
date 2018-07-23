package com.dexin.eccteacher.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dexin.eccteacher.bean.MessageEvent;
import com.dexin.eccteacher.utility.OkHttpEngine;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.MessageFormat;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * BaseDialogFragment
 */
public abstract class BaseDialogFragment extends DialogFragment {
    protected final String TAG = MessageFormat.format("TAG_{0}", getClass().getSimpleName());
    private Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutResourceID(), container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        OkHttpEngine.getInstance().cancelAllOkHttpRequest();
        if (EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * 接收到EventBus发布的消息事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(MessageEvent messageEvent) {
    }

    @Override
    public void show(FragmentManager fragmentManager, String tag) {
        if (isAdded()) fragmentManager.beginTransaction().remove(this).commit();
        super.show(fragmentManager, tag);
    }

    protected abstract int getLayoutResourceID();
}
