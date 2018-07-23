package com.dexin.eccteacher.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.dexin.eccteacher.R;
import com.dexin.eccteacher.adapter.ContainerViewPagerAdapter;
import com.dexin.eccteacher.application.AppConfig;
import com.dexin.eccteacher.application.CustomApplication;
import com.dexin.eccteacher.bean.MessageEvent;
import com.dexin.eccteacher.fragment.ContFragment;
import com.dexin.eccteacher.fragment.ConversationContainerFragment;
import com.dexin.eccteacher.fragment.HomeFragment;
import com.dexin.eccteacher.fragment.MineFragment;
import com.dexin.eccteacher.service.TeacherService;
import com.dexin.eccteacher.utility.LogUtility;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.vondear.rxtool.RxAppTool;
import com.vondear.rxtool.view.RxToast;
import com.wingsofts.byeburgernavigationview.ByeBurgerBehavior;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.text.MessageFormat;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imkit.manager.IUnReadMessageObserver;
import io.rong.imlib.model.Conversation;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

public class MainActivity extends BaseActivity {
    @BindView(R.id.abl_title)
    AppBarLayout mAblTitle;
    @BindView(R.id.tb_commom_toolbar)
    Toolbar mTbCommomToolbar;
    @BindView(R.id.tv_commom_title)
    TextView mTvCommomTitle;
    @BindView(R.id.vp_container)
    ViewPager mVpContainer;
    @BindView(R.id.bnve_bottom_navigation_view)
    BottomNavigationViewEx mBottomNavigationViewEx;
    @BindView(R.id.fab_top)
    FloatingActionButton mFabTop;
    private IUnReadMessageObserver mUnReadMessageObserver;

    @Override
    protected void onDestroy() {
        RongIM.getInstance().removeUnReadMessageCountChangedObserver(mUnReadMessageObserver);
        unBindTeacherService();
        super.onDestroy();
    }

    @NonNull
    public static Intent createIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected int getLayoutResourceID() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        setSwipeBackEnable(false);
        //初始化Toolbar
        setSupportActionBar(mTbCommomToolbar);
        ActionBar lActionBar;
        if ((lActionBar = getSupportActionBar()) != null) {
            lActionBar.setDisplayHomeAsUpEnabled(false);
            lActionBar.setDisplayShowTitleEnabled(false);
        }
        adjustToolbar(R.string.home_page, false);
        //初始化ContainerViewPager
        ContainerViewPagerAdapter lContainerViewPagerAdapter = new ContainerViewPagerAdapter(mSupportFragmentManager);
        lContainerViewPagerAdapter.clearFragments();
        lContainerViewPagerAdapter.addFragment(HomeFragment.newInstance());
        if (true) {//FIXME 会话列表Activity
            ConversationListFragment conversationListFragment = new ConversationListFragment();//设置私聊会话是否聚合显示   设置系统会话非聚合显示
            conversationListFragment.setUri(Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon().appendPath("conversationlist").appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false").appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "true").build());
            lContainerViewPagerAdapter.addFragment(conversationListFragment);
        } else {
            lContainerViewPagerAdapter.addFragment(ConversationContainerFragment.newInstance());
        }
        lContainerViewPagerAdapter.addFragment(ContFragment.newInstance());
        lContainerViewPagerAdapter.addFragment(MineFragment.newInstance());
        mVpContainer.setAdapter(lContainerViewPagerAdapter);
        mVpContainer.setOffscreenPageLimit(5);//缓存5个页面来解决点击"我的"后"首页"空白的问题
        //初始化BottomNavigationView
        mBottomNavigationViewEx.enableAnimation(false);
        mBottomNavigationViewEx.enableShiftingMode(false);
        mBottomNavigationViewEx.enableItemShiftingMode(false);
        mBottomNavigationViewEx.setupWithViewPager(mVpContainer);//BottomNavigationViewEx与ViewPager关联
        if (RxAppTool.getAppVersionCode(CustomApplication.getContext()) < AppConfig.getSPUtils().getInt(AppConfig.KEY_APP_VERSION_CODE)) addBadgeViewAt("", 3);
    }

    @Override
    public void initListener() {
        mBottomNavigationViewEx.setOnNavigationItemSelectedListener(item -> {
            mAblTitle.setExpanded(true, true);
            ByeBurgerBehavior.from(mFabTop).hide();
            ByeBurgerBehavior.from(mBottomNavigationViewEx).show();
            switch (item.getItemId()) {
                case R.id.item_home:
                    adjustToolbar(R.string.home_page, false);
//                    mVpContainer.setPadding(0, 0, 0, ConvertUtils.dp2px(0));
                    return true;
                case R.id.item_chat:
                    adjustToolbar(R.string.chat, false);
//                    mVpContainer.setPadding(0, 0, 0, ConvertUtils.dp2px(56));
                    AppConfig.updateUserInfo(MainActivity.this);
                    return true;
                case R.id.item_cont:
                    adjustToolbar(R.string.contacts, true);
//                    mVpContainer.setPadding(0, 0, 0, ConvertUtils.dp2px(0));
                    return true;
                case R.id.item_mine:
                    adjustToolbar(R.string.person_center, false);
//                    mVpContainer.setPadding(0, 0, 0, ConvertUtils.dp2px(0));
                    return true;
                default:
            }
            return false;
        });
        RongIM.getInstance().addUnReadMessageCountChangedObserver(mUnReadMessageObserver = unReadCount -> {
            if (unReadCount > 0) {
                LogUtility.d(TAG, "initMemberVar: " + MessageFormat.format("{0}未读条消息!", unReadCount));
                addBadgeViewAt((unReadCount < 100) ? String.valueOf(unReadCount) : "99﹢", 1);
            } else {
                hideBadgeView();
            }
        }, Conversation.ConversationType.PRIVATE, Conversation.ConversationType.DISCUSSION, Conversation.ConversationType.GROUP, Conversation.ConversationType.SYSTEM, Conversation.ConversationType.PUBLIC_SERVICE);
    }

    @Override
    public void initData() {
        bindTeacherService();
        LitePal.getDatabase();//创建数据库
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.item_search).setVisible(isSearchVisible);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_search:
                startActivity(SearchContActivity.createIntent(CustomApplication.getContext()));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isSearchVisible;

    private void adjustToolbar(int titleStrResId, boolean searchVisable) {
        String titleStr = getString(titleStrResId);
        if (!Objects.equals(titleStr, mTvCommomTitle.getText().toString())) {
            mTvCommomTitle.setText(titleStr);
        }
        if (isSearchVisible != searchVisable) {
            isSearchVisible = searchVisable;
            invalidateOptionsMenu();
        }
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------TODO 添加BadgeView的逻辑---------------------------------------------------------
    //-----------------------------------------------------------------------------------------------------------------------------------------
    private Badge mQBadgeView;

    private void addBadgeViewAt(String badgeText, int position) {
        if (mQBadgeView == null) mQBadgeView = new QBadgeView(CustomApplication.getContext()).setGravityOffset(25, 5, true);
        mQBadgeView.setBadgeText(badgeText).bindTarget(mBottomNavigationViewEx.getBottomNavigationItemView(position))
                .setOnDragStateChangedListener((dragState, badge, targetView) -> {
//                    if (Badge.OnDragStateChangedListener.STATE_SUCCEED == dragState)
                });
    }

    private void hideBadgeView() {
        if (mQBadgeView != null) mQBadgeView.hide(true);
    }


    //-----------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------TODO 双击返回键退出程序 逻辑-------------------------------------------------------
    //-----------------------------------------------------------------------------------------------------------------------------------------
    private long TIME_LAST_BACK_PRESSED;//上次Back键被按下的时间连续两次点击返回键退出

    @Override
    public void onBackPressedSupport() {
        if (System.currentTimeMillis() - TIME_LAST_BACK_PRESSED < 4 * 500) {
            finish();
        } else {
            RxToast.warning("再次点击返回键退出!");
            TIME_LAST_BACK_PRESSED = System.currentTimeMillis();
            mAblTitle.setExpanded(true, true);
            ByeBurgerBehavior.from(mBottomNavigationViewEx).show();
        }
    }

    @OnClick({R.id.fab_top})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fab_top:
                EventBus.getDefault().post(new MessageEvent(MessageEvent.EVENT_TOP));
                break;
            default:
        }
    }


    //-----------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------TODO 启动服务模块 逻辑------------------------------------------------------------
    //-----------------------------------------------------------------------------------------------------------------------------------------
    private boolean isTeacherServiceBinded;
    private TeacherService.TeacherBinder mTeacherBinder;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mTeacherBinder = (TeacherService.TeacherBinder) service;//之后可调用 mTeacherBinder 的方法
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mTeacherBinder = null;
        }
    };

    private void bindTeacherService() {
        isTeacherServiceBinded = bindService(new Intent(CustomApplication.getContext(), TeacherService.class), mServiceConnection, BIND_AUTO_CREATE);
    }

    private void unBindTeacherService() {
        if (isTeacherServiceBinded) unbindService(mServiceConnection);
    }
}
