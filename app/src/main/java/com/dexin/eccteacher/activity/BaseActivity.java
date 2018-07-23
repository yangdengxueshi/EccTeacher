package com.dexin.eccteacher.activity;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

import com.dexin.eccteacher.R;
import com.dexin.eccteacher.application.AppConfig;
import com.dexin.eccteacher.bean.MessageEvent;
import com.dexin.eccteacher.fragment.DealNonNetFragment;
import com.dexin.eccteacher.fragment.UpdateFragment;
import com.dexin.eccteacher.utility.OkHttpEngine;
import com.gyf.barlibrary.BarHide;
import com.gyf.barlibrary.ImmersionBar;
import com.vondear.rxtool.RxKeyboardTool;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.MessageFormat;

import butterknife.ButterKnife;
import me.yokeyword.fragmentation.ExtraTransaction;
import me.yokeyword.fragmentation.ISupportActivity;
import me.yokeyword.fragmentation.ISupportFragment;
import me.yokeyword.fragmentation.SupportActivityDelegate;
import me.yokeyword.fragmentation.SupportHelper;
import me.yokeyword.fragmentation.SwipeBackLayout;
import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;
import me.yokeyword.fragmentation_swipeback.core.ISwipeBackActivity;
import me.yokeyword.fragmentation_swipeback.core.SwipeBackActivityDelegate;

/**
 * BaseActivity
 */
public abstract class BaseActivity extends AppCompatActivity implements ISupportActivity, ISwipeBackActivity {
    protected final String TAG = MessageFormat.format("TAG_{0}", getClass().getSimpleName());
    public final FragmentManager mSupportFragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSupportActivityDelegate.onCreate(savedInstanceState);
        mSwipeBackActivityDelegate.onCreate(savedInstanceState);
        if (isImmersionBarEnnabled()) initImmersionBar();
        setContentView(getLayoutResourceID());
        ButterKnife.bind(this);
        if (!EventBus.getDefault().isRegistered(this)) EventBus.getDefault().register(this);
        ActivityCollector.addActivity(this);
        initView();
        initListener();
        initData();
    }

    @Override
    protected void onPause() {
        RxKeyboardTool.hideSoftInput(this);//FIXME 必须发在onPause中,放在onDestroy中无效
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        OkHttpEngine.getInstance().cancelAllOkHttpRequest();//TODO 离开页面取消所有后台的网络请求
        if (EventBus.getDefault().isRegistered(this)) EventBus.getDefault().unregister(this);
        if (mImmersionBar != null) mImmersionBar.destroy();
        ActivityCollector.removeActivity(this);
        mSupportActivityDelegate.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mSupportActivityDelegate.onPostCreate(savedInstanceState);
        mSwipeBackActivityDelegate.onPostCreate(savedInstanceState);
    }

    /**
     * 不建议重写该方法,请使用{@link #onBackPressedSupport}代替
     */
    @Override
    final public void onBackPressed() {
        mSupportActivityDelegate.onBackPressed();
    }

    /**
     * 注意   return mSupportActivityDelegate.dispatchTouchEvent(ev) || super.dispatchTouchEvent(ev);
     *
     * @param motionEvent 动作事件
     * @return 消费与否
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        return mSupportActivityDelegate.dispatchTouchEvent(motionEvent) || super.dispatchTouchEvent(motionEvent);
    }


    protected abstract int getLayoutResourceID();

    protected abstract void initView();

    protected abstract void initListener();

    protected abstract void initData();


    //-------------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------FIXME '沉浸式'模块-----------------------------------------------------------------
    //---------------------------------------------------------↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓------------------------------------------------------------------
    protected ImmersionBar mImmersionBar;

    protected void initImmersionBar() {
        mImmersionBar = ImmersionBar.with(this).fitsSystemWindows(true).transparentBar().fullScreen(false).statusBarColor(R.color.register_yellow_red).hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR).keyboardEnable(true);
        mImmersionBar.init();
    }

    protected boolean isImmersionBarEnnabled() {
        return true;
    }


    //-------------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------FIXME 'EventBus'模块--------------------------------------------------------------
    //---------------------------------------------------------↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓------------------------------------------------------------------
    private UpdateFragment mUpdateFragment;
    private DealNonNetFragment mDealNonNetFragment;

    /**
     * 接收到EventBus发布的消息事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(MessageEvent messageEvent) {
        if (!AppConfig.isComponentAlive(this)) return;
        switch (messageEvent.getMessage()) {
            case MessageEvent.EVENT_NEW_APP:
                if (mUpdateFragment == null) mUpdateFragment = UpdateFragment.newInstance();
                mUpdateFragment.show(mSupportFragmentManager, null);
                break;
            case MessageEvent.EVENT_NET_UNAVAILABLE:
                if (mDealNonNetFragment == null) mDealNonNetFragment = DealNonNetFragment.newInstance();
                mDealNonNetFragment.show(mSupportFragmentManager, null);
                break;
            default:
        }
    }


    //-------------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------FIXME 'SupportActivityDelegate'模块-----------------------------------------------
    //---------------------------------------------------------↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓------------------------------------------------
    final SupportActivityDelegate mSupportActivityDelegate = new SupportActivityDelegate(this);

    @Override
    public SupportActivityDelegate getSupportDelegate() {
        return mSupportActivityDelegate;
    }

    /**
     * 额外的事物:自定义Tag,添加SharedElement动画,操作非回退栈Fragment
     *
     * @return 额外的事物
     */
    @Override
    public ExtraTransaction extraTransaction() {
        return mSupportActivityDelegate.extraTransaction();
    }

    /**
     * 获取设置的全局动画Copy
     *
     * @return FragmentAnimator
     */
    @Override
    public FragmentAnimator getFragmentAnimator() {
        return mSupportActivityDelegate.getFragmentAnimator();
    }

    /**
     * 设置Fragment内的全局动画
     *
     * @param fragmentAnimator Fragment内的全局动画
     */
    @Override
    public void setFragmentAnimator(FragmentAnimator fragmentAnimator) {
        mSupportActivityDelegate.setFragmentAnimator(fragmentAnimator);
    }

    /**
     * 设置所有的Fragment转场动画
     * 如果是在Activity内实现,则构建的是Activity内所有Fragment的转场动画;若果是在Frament内实现,则构建的是该Fragment的转场动画,此时优先级>Activity的onCreateFragmentAnimator()
     *
     * @return FragmentAnimator对象
     */
    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return new DefaultHorizontalAnimator();
//        return mSupportActivityDelegate.onCreateFragmentAnimator();//默认是"自下而上" ↑
    }

    /**
     * 前面的事务全部执行后,执行该Action
     *
     * @param runnable 子线程
     */
    @Override
    public void post(Runnable runnable) {
        mSupportActivityDelegate.post(runnable);
    }

    /**
     * 请尽量复写该方法,避免复写onBackPressed(),以保证SupportFragemnt内的onBackPressedSupport()回退事件正常执行
     * 该方法回调时机为:Activity回退栈内Fragment的数量<=1时,默认finish Activity
     */
    @Override
    public void onBackPressedSupport() {
        mSupportActivityDelegate.onBackPressedSupport();
    }


    //-----------------------------------------------------FIXME 'SupportActivityDelegate'模块可选方法---------------------------------------------
    //-----------------------------------------------------↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓---------------------------------------------

    /**
     * 加载根Fragment, 即Activity内的第一个Fragment 或 Fragment内的第一个子Fragment
     *
     * @param containerId 容器id
     * @param toFragment  目标Fragment
     */
    public void loadRootFragment(int containerId, @NonNull ISupportFragment toFragment) {
        mSupportActivityDelegate.loadRootFragment(containerId, toFragment);
    }


    public void loadRootFragment(int containerId, ISupportFragment toFragment, boolean addToBackStack, boolean allowAnimation) {
        mSupportActivityDelegate.loadRootFragment(containerId, toFragment, addToBackStack, allowAnimation);
    }


    /**
     * 加载多个同级根Fragment,类似Wechat, QQ主页的场景
     */
    public void loadMultipleRootFragment(int containerId, int showPosition, ISupportFragment... toFragments) {
        mSupportActivityDelegate.loadMultipleRootFragment(containerId, showPosition, toFragments);
    }


    /**
     * show一个Fragment,hide其他同栈所有Fragment
     * 使用该方法时，要确保同级栈内无多余的Fragment,(只有通过loadMultipleRootFragment()载入的Fragment)
     * <p>
     * 建议使用更明确的{@link #showHideFragment(ISupportFragment, ISupportFragment)}
     *
     * @param showFragment 需要show的Fragment
     */
    public void showHideFragment(ISupportFragment showFragment) {
        mSupportActivityDelegate.showHideFragment(showFragment);
    }

    /**
     * show一个Fragment,hide一个Fragment ; 主要用于类似微信主页那种 切换tab的情况
     */
    public void showHideFragment(ISupportFragment showFragment, ISupportFragment hideFragment) {
        mSupportActivityDelegate.showHideFragment(showFragment, hideFragment);
    }

    /**
     * It is recommended to use {SupportFragment#start(ISupportFragment)}.
     */
    public void start(ISupportFragment toFragment) {
        mSupportActivityDelegate.start(toFragment);
    }

    /**
     * It is recommended to use {SupportFragment#start(ISupportFragment, int)}.
     *
     * @param launchMode Similar to Activity's LaunchMode.
     */
    public void start(ISupportFragment toFragment, @ISupportFragment.LaunchMode int launchMode) {
        mSupportActivityDelegate.start(toFragment, launchMode);
    }

    /**
     * It is recommended to use {SupportFragment#startForResult(ISupportFragment, int)}.
     * Launch an fragment for which you would like a result when it poped.
     */
    public void startForResult(ISupportFragment toFragment, int requestCode) {
        mSupportActivityDelegate.startForResult(toFragment, requestCode);
    }

    /**
     * It is recommended to use {SupportFragment#startWithPop(ISupportFragment)}.
     * Start the target Fragment and pop itself
     */
    public void startWithPop(ISupportFragment toFragment) {
        mSupportActivityDelegate.startWithPop(toFragment);
    }

    /**
     * It is recommended to use {SupportFragment#startWithPopTo(ISupportFragment, Class, boolean)}.
     *
     * @see #popTo(Class, boolean)
     * +
     * @see #start(ISupportFragment)
     */
    public void startWithPopTo(ISupportFragment toFragment, Class<?> targetFragmentClass, boolean includeTargetFragment) {
        mSupportActivityDelegate.startWithPopTo(toFragment, targetFragmentClass, includeTargetFragment);
    }

    /**
     * It is recommended to use {SupportFragment#replaceFragment(ISupportFragment, boolean)}.
     */
    public void replaceFragment(ISupportFragment toFragment, boolean addToBackStack) {
        mSupportActivityDelegate.replaceFragment(toFragment, addToBackStack);
    }

    /**
     * Pop the fragment.
     */
    public void pop() {
        mSupportActivityDelegate.pop();
    }

    /**
     * Pop the last fragment transition from the manager's fragment
     * back stack.
     * <p>
     * 出栈到目标fragment
     *
     * @param targetFragmentClass   目标fragment
     * @param includeTargetFragment 是否包含该fragment
     */
    public void popTo(Class<?> targetFragmentClass, boolean includeTargetFragment) {
        mSupportActivityDelegate.popTo(targetFragmentClass, includeTargetFragment);
    }

    /**
     * If you want to begin another FragmentTransaction immediately after popTo(), use this method.
     * 如果你想在出栈后, 立刻进行FragmentTransaction操作，请使用该方法
     */
    public void popTo(Class<?> targetFragmentClass, boolean includeTargetFragment, Runnable afterPopTransactionRunnable) {
        mSupportActivityDelegate.popTo(targetFragmentClass, includeTargetFragment, afterPopTransactionRunnable);
    }

    public void popTo(Class<?> targetFragmentClass, boolean includeTargetFragment, Runnable afterPopTransactionRunnable, int popAnim) {
        mSupportActivityDelegate.popTo(targetFragmentClass, includeTargetFragment, afterPopTransactionRunnable, popAnim);
    }

    /**
     * 当Fragment根布局 没有 设定background属性时,
     * Fragmentation默认使用Theme的android:windowbackground作为Fragment的背景,
     * 可以通过该方法改变其内所有Fragment的默认背景。
     */
    public void setDefaultFragmentBackground(@DrawableRes int backgroundRes) {
        mSupportActivityDelegate.setDefaultFragmentBackground(backgroundRes);
    }

    /**
     * 得到位于栈顶Fragment
     */
    public ISupportFragment getTopFragment() {
        return SupportHelper.getTopFragment(getSupportFragmentManager());
    }

    /**
     * 获取栈内的fragment对象
     */
    public <T extends ISupportFragment> T findFragment(Class<T> fragmentClass) {
        return SupportHelper.findFragment(getSupportFragmentManager(), fragmentClass);
    }


    //-------------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------FIXME '滑动返回'模块----------------------------------------------------------------
    //---------------------------------------------------------↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓------------------------------------------------------------------
    final SwipeBackActivityDelegate mSwipeBackActivityDelegate = new SwipeBackActivityDelegate(this);

    @Override
    public SwipeBackLayout getSwipeBackLayout() {
        SwipeBackLayout lSwipeBackLayout = mSwipeBackActivityDelegate.getSwipeBackLayout();
        lSwipeBackLayout.setEdgeOrientation(SwipeBackLayout.EDGE_LEFT);
        return lSwipeBackLayout;
    }

    /**
     * 设置是否可以滑动
     *
     * @param enable 能否
     */
    @Override
    public void setSwipeBackEnable(boolean enable) {
        mSwipeBackActivityDelegate.setSwipeBackEnable(enable);
    }

    @Override
    public void setEdgeLevel(SwipeBackLayout.EdgeLevel edgeLevel) {
        mSwipeBackActivityDelegate.setEdgeLevel(edgeLevel);
    }

    @Override
    public void setEdgeLevel(int widthPixel) {
        mSwipeBackActivityDelegate.setEdgeLevel(widthPixel);
    }

    /**
     * 限制SwipeBack的条件,默认栈内Fragment数<=1时,优先滑动退出Activity
     *
     * @return true:Activity优先滑动退出    false:Fragment优先滑动退出
     */
    @Override
    public boolean swipeBackPriority() {
        return mSwipeBackActivityDelegate.swipeBackPriority();
    }
}
