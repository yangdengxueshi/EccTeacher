package com.dexin.eccteacher.fragment;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.classic.common.MultipleStatusView;
import com.dexin.eccteacher.R;
import com.dexin.eccteacher.activity.ApplyClassroomActivity;
import com.dexin.eccteacher.activity.AttendanceStatisticsActivity;
import com.dexin.eccteacher.activity.ClassNoticeActivity;
import com.dexin.eccteacher.activity.ClassStyleActivity;
import com.dexin.eccteacher.activity.FaultRepairActivity;
import com.dexin.eccteacher.activity.FeedbackActivity;
import com.dexin.eccteacher.activity.H5Activity;
import com.dexin.eccteacher.activity.HomeworkActivity;
import com.dexin.eccteacher.activity.LeaveThingsActivity;
import com.dexin.eccteacher.activity.MarkingActivity;
import com.dexin.eccteacher.activity.NewsAndInfoActivity;
import com.dexin.eccteacher.activity.QueryScoreActivity;
import com.dexin.eccteacher.activity.WalkingClassSelectionActivity;
import com.dexin.eccteacher.adapter.CategoryViewPagerAdapter;
import com.dexin.eccteacher.adapter.DividerItemDecoration;
import com.dexin.eccteacher.adapter.HomeEntranceAdapter;
import com.dexin.eccteacher.adapter.NewsAndInfoAdapter;
import com.dexin.eccteacher.application.AppConfig;
import com.dexin.eccteacher.application.CustomApplication;
import com.dexin.eccteacher.bean.BannerDataBean;
import com.dexin.eccteacher.bean.HomeEntranceBean;
import com.dexin.eccteacher.bean.MessageEvent;
import com.dexin.eccteacher.bean.NewsAndInfoBean;
import com.dexin.eccteacher.utility.LogUtility;
import com.dexin.eccteacher.utility.OkHttpEngine;
import com.vondear.rxtool.view.RxToast;
import com.zhouwei.mzbanner.MZBannerView;
import com.zhouwei.mzbanner.holder.MZHolderCreator;
import com.zhouwei.mzbanner.holder.MZViewHolder;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.FormBody;
import top.wefor.circularanim.CircularAnim;

/**
 * 首页Fragment
 */
public class HomeFragment extends BaseFragment {
    @BindView(R.id.nsv_top)
    NestedScrollView mNsvTop;

    @NonNull
    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        startMZBannerView();
    }

    @Override
    public void onSupportInvisible() {
        mSrlSwipeRefresh.setRefreshing(false);
        pauseMZBannerView();
        super.onSupportInvisible();
    }

    @Override
    protected int getLayoutResourceID() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView() {
        setSwipeBackEnable(false);
        initSwipeRefreshView();
        initBannerView();
        initHomeEntranceView();
        initNewsAndInfoView();
    }

    @Override
    protected void initListener() {
    }

    @Override
    protected void initData() {
        getBannerDataInOnOnActivityCreated();
        getNewsAndInfo(true);
    }


    //-------------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------FIXME 下拉刷新模块-----------------------------------------------------------------
    //---------------------------------------------------------↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓------------------------------------------------------------------
    @BindView(R.id.srl_swipe_refresh)
    SwipeRefreshLayout mSrlSwipeRefresh;

    private void initSwipeRefreshView() {
        mSrlSwipeRefresh.setColorSchemeResources(R.color.register_yellow_red);
        mSrlSwipeRefresh.setOnRefreshListener(() -> {
            if (AppConfig.isNetAvailable()) {
                //region Banner
                mBannerMultipleStatusView.showLoading();
                getBannerDataInOnOnActivityCreated();
                //endregion
                getNewsAndInfo(false);
            } else {
                mSrlSwipeRefresh.setRefreshing(false);
            }
        });
    }


    //-----------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------TODO 首页Banner设计逻辑-----------------------------------------------------------
    //-----------------------------------------------------------------------------------------------------------------------------------------
    @BindView(R.id.banner_multiple_status_view)
    MultipleStatusView mBannerMultipleStatusView;
    @BindView(R.id.mz_banner_view)
    MZBannerView<BannerDataBean.DataBean> mMzBannerView;
    private List<BannerDataBean.DataBean> mDataBeanList;
    private Intent[] distinationClassArr = new Intent[]{
            null,
            LeaveThingsActivity.createIntent(CustomApplication.getContext()),
            ClassNoticeActivity.createIntent(CustomApplication.getContext()),
            MarkingActivity.createIntent(CustomApplication.getContext()),
            HomeworkActivity.createIntent(CustomApplication.getContext()),
            QueryScoreActivity.createIntent(CustomApplication.getContext()),
            AttendanceStatisticsActivity.createIntent(CustomApplication.getContext()),
            ClassStyleActivity.createIntent(CustomApplication.getContext()),
            ApplyClassroomActivity.createIntent(CustomApplication.getContext()),
            FaultRepairActivity.createIntent(CustomApplication.getContext()),
            FeedbackActivity.createIntent(CustomApplication.getContext()),
            H5Activity.createIntent(CustomApplication.getContext(), "教师课表", "/student_app_cloud/teacherLessonTable.html", 0, 0)
    };

    private void initBannerView() {
        mBannerMultipleStatusView.showLoading();
        mMzBannerView.setBannerPageClickListener((view, position) -> {
            if (mDataBeanList == null) return;
            BannerDataBean.DataBean lDataBean = mDataBeanList.get(position % mDataBeanList.size());
            if (lDataBean != null) {
                switch (lDataBean.getDoStatus()) {
                    case 0://不能跳转
                        RxToast.info("无跳转逻辑!");
                        break;
                    case 1://H5跳转+token
                        startActivity(H5Activity.createIntent(CustomApplication.getContext(), lDataBean.getName(), lDataBean.getDoUrl(), lDataBean.getId(), 2));
                        break;
                    case 2://app功能页面跳转
                        int turnIndex = lDataBean.getDoType();
                        if ((0 <= turnIndex) && (turnIndex < distinationClassArr.length)) {
                            Intent intent = distinationClassArr[turnIndex];
                            if (intent != null) {
                                startActivity(intent);
                            } else {
                                RxToast.info("无法完成跳转!");
                            }
                        } else {
                            RxToast.info(MessageFormat.format("跳转索引越界:{0}!", turnIndex));
                        }
                        break;
                    case 3://外网跳转
                        startActivity(H5Activity.createIntent(CustomApplication.getContext(), true, lDataBean.getName(), lDataBean.getDoUrl()));
                        break;
                    default:
                }
            }
        });
    }

    private boolean isBannerAutoPlay;

    private void startMZBannerView() {
        pauseMZBannerView();
        if (!isBannerAutoPlay) {
            mMzBannerView.start();
            isBannerAutoPlay = true;
        }
    }

    private void pauseMZBannerView() {
        if (isBannerAutoPlay) {
            mMzBannerView.pause();
            isBannerAutoPlay = false;
        }
    }

    private OkHttpEngine.ResponsedCallback mBannerDataResponsedCallback;

    /**
     * 获取Banner数据
     */
    private void getBannerDataInOnOnActivityCreated() {
        if (mBannerDataResponsedCallback == null) {
            mBannerDataResponsedCallback = new OkHttpEngine.ResponsedCallback() {
                private MZHolderCreator mMZHolderCreator = MZBannerViewHolder::new;

                @Override
                public void onFailure(Call call, Exception e) {
                    mSrlSwipeRefresh.setRefreshing(false);
                    mBannerMultipleStatusView.showError();
                    getBannerDataInOnOnActivityCreated();
                }

                @Override
                public void onResponseJson(Call call, String responsedJsonStr) {
                    LogUtility.d(TAG, "onResponseJson: " + responsedJsonStr);
                    mSrlSwipeRefresh.setRefreshing(false);
                    BannerDataBean lBannerDataBean = OkHttpEngine.toObject(responsedJsonStr, BannerDataBean.class);
                    if (lBannerDataBean != null) {
                        switch (lBannerDataBean.getCode()) {
                            case 0://成功
                                mDataBeanList = lBannerDataBean.getData();
                                if ((mDataBeanList != null) && !mDataBeanList.isEmpty()) {
                                    mBannerMultipleStatusView.showContent();
                                    mMzBannerView.setPages(mDataBeanList, mMZHolderCreator);
                                    startMZBannerView();
                                } else {
                                    mBannerMultipleStatusView.showEmpty();
                                }
                                break;
                            default:
                                RxToast.error(MessageFormat.format("获取Banner图片失败! {0}:{1}", lBannerDataBean.getCode(), lBannerDataBean.getMessage()));
                                mBannerMultipleStatusView.showError();
                        }
                    }
                }

                /**
                 * MZBannerViewHolder
                 */
                class MZBannerViewHolder implements MZViewHolder<BannerDataBean.DataBean> {
                    private ImageView mBannerItemImageView;

                    @Override   //TODO 返回页面布局
                    public View createView(Context context) {
                        View itemView = LayoutInflater.from(context).inflate(R.layout.item_arc_view, null, false);
                        mBannerItemImageView = itemView.findViewById(R.id.iv_banner_item);
                        return itemView;
                    }

                    @Override   //TODO 数据绑定
                    public void onBind(Context context, int position, BannerDataBean.DataBean dataBean) {
                        Glide.with(context).load(MessageFormat.format("{0}{1}", AppConfig.SERVER_HOST, dataBean.getPhotoUrl())).apply(AppConfig.NORMAL_GLIDE_REQUEST_OPTIONS).into(mBannerItemImageView);
                    }
                }
            };
        }
        OkHttpEngine.getInstance().sendAsyncPostRequest(HomeFragment.this, MessageFormat.format("{0}/Photos/getBannerPhoto", AppConfig.SERVER_ADDRESS), new FormBody.Builder().add("userType", "1").add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).build(), mBannerDataResponsedCallback);
    }


    //-----------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------TODO 首页"入口"设计逻辑-----------------------------------------------------------
    //-----------------------------------------------------------------------------------------------------------------------------------------
    public static final int HOME_ENTRANCE_PAGE_MAX_SIZE = 8;//菜单单页显示数量
    @BindView(R.id.vp_home_menu)
    ViewPager mVpHomeMenu;
    private List<View> mItemViewList = new ArrayList<>();
    private List<HomeEntranceBean> mHomeEntranceBeanList = new ArrayList<>();

    private void initHomeEntranceView() {
        mItemViewList.clear();
        mHomeEntranceBeanList.clear();
        mHomeEntranceBeanList.add(new HomeEntranceBean(R.drawable.ic_attendance_statistics, "考勤统计"));
        mHomeEntranceBeanList.add(new HomeEntranceBean(R.drawable.ic_class_card_statistics, "走班选课"));
        mHomeEntranceBeanList.add(new HomeEntranceBean(R.drawable.ic_class_notice, "班级通知"));
        mHomeEntranceBeanList.add(new HomeEntranceBean(R.drawable.ic_class_rating, "班级评分"));
        mHomeEntranceBeanList.add(new HomeEntranceBean(R.drawable.ic_course_table, "教师课表"));
        mHomeEntranceBeanList.add(new HomeEntranceBean(R.drawable.ic_publish_exersice, "发布作业"));
        mHomeEntranceBeanList.add(new HomeEntranceBean(R.drawable.ic_query_score, "分数查询"));
        mHomeEntranceBeanList.add(new HomeEntranceBean(R.drawable.ic_class_mien, "班级风采"));
        mHomeEntranceBeanList.add(new HomeEntranceBean(R.drawable.ic_apply_classroom, "教室申请"));
        mHomeEntranceBeanList.add(new HomeEntranceBean(R.drawable.ic_ask_for_leave, "请假审核"));
        mHomeEntranceBeanList.add(new HomeEntranceBean(R.drawable.ic_brokendown, "故障报修"));
        mHomeEntranceBeanList.add(new HomeEntranceBean(R.drawable.ic_feedback, "意见反馈"));
        int lPageCount = (int) Math.ceil(mHomeEntranceBeanList.size() * 1.0 / HOME_ENTRANCE_PAGE_MAX_SIZE);
        for (int pageIndex = 0; pageIndex < lPageCount; pageIndex++) {
            RecyclerView lRecyclerView = (RecyclerView) LayoutInflater.from(CustomApplication.getContext()).inflate(R.layout.item_home_menu_recyclerview, mVpHomeMenu, false);
            lRecyclerView.setLayoutManager(new GridLayoutManager(CustomApplication.getContext(), 4));
            HomeEntranceAdapter lHomeEntranceAdapter = new HomeEntranceAdapter(mHomeEntranceBeanList, pageIndex, HOME_ENTRANCE_PAGE_MAX_SIZE);
            lHomeEntranceAdapter.setOnItemClickedListener((itemView, position) -> {
                if (!AppConfig.isNetAvailable()) return;
                switch (position) {
                    case 0://考勤统计
                        CircularAnim.fullActivity(mFragmentActivity, itemView).colorOrImageRes(R.color.colorEntrance_00).go(() -> startActivity(AttendanceStatisticsActivity.createIntent(CustomApplication.getContext())));
                        break;
                    case 1://班牌统计
                        CircularAnim.fullActivity(mFragmentActivity, itemView).colorOrImageRes(R.color.colorEntrance_01).go(() -> startActivity(WalkingClassSelectionActivity.createIntent(CustomApplication.getContext())));
                        break;
                    case 2://班级通知:
                        CircularAnim.fullActivity(mFragmentActivity, itemView).colorOrImageRes(R.color.colorEntrance_02).go(() -> startActivity(ClassNoticeActivity.createIntent(CustomApplication.getContext())));
                        break;
                    case 3://班级评分
                        CircularAnim.fullActivity(mFragmentActivity, itemView).colorOrImageRes(R.color.colorEntrance_03).go(() -> startActivity(MarkingActivity.createIntent(CustomApplication.getContext())));
                        break;
                    case 4://教师课表
                        CircularAnim.fullActivity(mFragmentActivity, itemView).colorOrImageRes(R.color.colorEntrance_04).go(() -> startActivity(H5Activity.createIntent(CustomApplication.getContext(), "教师课表", "/student_app_cloud/teacherLessonTable.html", 0, 0)));
                        break;
                    case 5://发布作业
                        CircularAnim.fullActivity(mFragmentActivity, itemView).colorOrImageRes(R.color.colorEntrance_05).go(() -> startActivity(HomeworkActivity.createIntent(CustomApplication.getContext())));
                        break;
                    case 6://分数查询 :
                        CircularAnim.fullActivity(mFragmentActivity, itemView).colorOrImageRes(R.color.colorEntrance_06).go(() -> startActivity(QueryScoreActivity.createIntent(CustomApplication.getContext())));
                        break;
                    case 7://班级风采
                        CircularAnim.fullActivity(mFragmentActivity, itemView).colorOrImageRes(R.color.colorEntrance_07).go(() -> startActivity(ClassStyleActivity.createIntent(CustomApplication.getContext())));
                        break;
                    case 8://教室申请
                        CircularAnim.fullActivity(mFragmentActivity, itemView).colorOrImageRes(R.color.colorEntrance_08).go(() -> startActivity(ApplyClassroomActivity.createIntent(CustomApplication.getContext())));
                        break;
                    case 9://请假审核
                        CircularAnim.fullActivity(mFragmentActivity, itemView).colorOrImageRes(R.color.colorEntrance_09).go(() -> startActivity(LeaveThingsActivity.createIntent(CustomApplication.getContext())));
                        break;
                    case 10://故障报修:
                        CircularAnim.fullActivity(mFragmentActivity, itemView).colorOrImageRes(R.color.colorEntrance_10).go(() -> startActivity(FaultRepairActivity.createIntent(CustomApplication.getContext())));
                        break;
                    case 11://意见反馈
                        CircularAnim.fullActivity(mFragmentActivity, itemView).colorOrImageRes(R.color.colorEntrance_11).go(() -> startActivity(FeedbackActivity.createIntent(CustomApplication.getContext())));
                        break;
                    default:
                }
            });
            lRecyclerView.setAdapter(lHomeEntranceAdapter);
            mItemViewList.add(lRecyclerView);
        }
        mVpHomeMenu.removeAllViews();
        mVpHomeMenu.setAdapter(new CategoryViewPagerAdapter(mItemViewList));
    }


    //-----------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------TODO 首页"新闻资讯"逻辑-----------------------------------------------------------
    //-----------------------------------------------------------------------------------------------------------------------------------------
    @BindView(R.id.rv_news_and_info_list)
    RecyclerView mRvNewsAndInfo;
    private NewsAndInfoAdapter mNewsAndInfoAdapter;
    private View mEmptyView, mErrorView, mLoadingView;
    private OkHttpEngine.ResponsedCallback mNewsAndInfoResponsedCallback;

    private void initNewsAndInfoView() {
        mEmptyView = getLayoutInflater().inflate(R.layout.custom_empty_view, (ViewGroup) mRvNewsAndInfo.getParent(), false);
        mErrorView = getLayoutInflater().inflate(R.layout.custom_error_view, (ViewGroup) mRvNewsAndInfo.getParent(), false);
        mLoadingView = getLayoutInflater().inflate(R.layout.custom_loading_view, (ViewGroup) mRvNewsAndInfo.getParent(), false);
        mEmptyView.setOnClickListener(v -> getNewsAndInfo(true));
        mErrorView.setOnClickListener(v -> getNewsAndInfo(true));

        mNewsAndInfoAdapter = new NewsAndInfoAdapter();
        mNewsAndInfoAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mNewsAndInfoAdapter.isFirstOnly(false);
        mNewsAndInfoAdapter.setOnItemClickListener((baseQuickAdapter, view, position) -> {
            NewsAndInfoBean.DataBean lNewsAndInfoBean = (NewsAndInfoBean.DataBean) baseQuickAdapter.getItem(position);
            if (lNewsAndInfoBean != null)
                startActivity(H5Activity.createIntent(CustomApplication.getContext(), lNewsAndInfoBean.getHeadline(), ((NewsAndInfoBean.DataBean) Objects.requireNonNull(baseQuickAdapter.getItem(position))).getNewsUrl(), lNewsAndInfoBean.getId(), 2));
        });
        mRvNewsAndInfo.setAdapter(mNewsAndInfoAdapter);
        mRvNewsAndInfo.addItemDecoration(new DividerItemDecoration(CustomApplication.getContext(), DividerItemDecoration.VERTICAL));
        mRvNewsAndInfo.setLayoutManager(new LinearLayoutManager(CustomApplication.getContext()));
    }

    /**
     * 获取"首页新闻资讯"
     */
    private void getNewsAndInfo(boolean loading) {
        if (loading) mNewsAndInfoAdapter.setEmptyView(mLoadingView);
        if (mNewsAndInfoResponsedCallback == null) {
            mNewsAndInfoResponsedCallback = new OkHttpEngine.ResponsedCallback() {
                @Override
                public void onFailure(Call call, Exception e) {
                    mSrlSwipeRefresh.setRefreshing(false);
                    getNewsAndInfo(true);
                }

                @Override
                public void onResponseJson(Call call, String responsedJsonStr) {
                    mSrlSwipeRefresh.setRefreshing(false);
                    NewsAndInfoBean lNewsAndInfoBean = OkHttpEngine.toObject(responsedJsonStr, NewsAndInfoBean.class);
                    if (lNewsAndInfoBean != null) {
                        switch (lNewsAndInfoBean.getCode()) {
                            case 0://成功
                                List<NewsAndInfoBean.DataBean> lDataBeanList = lNewsAndInfoBean.getData();
                                if ((lDataBeanList != null) && !lDataBeanList.isEmpty()) {
                                    mNewsAndInfoAdapter.replaceData(lDataBeanList);
                                } else {
                                    mNewsAndInfoAdapter.setEmptyView(mEmptyView);
                                }
                                break;
                            default:
                                mNewsAndInfoAdapter.setEmptyView(mErrorView);
                                RxToast.error(MessageFormat.format("获取新闻资讯失败! {0}:{1}", lNewsAndInfoBean.getCode(), lNewsAndInfoBean.getMessage()));
                        }
                    }
                }
            };
        }
        OkHttpEngine.getInstance().sendAsyncPostRequest(HomeFragment.this, MessageFormat.format("{0}/news/getNews", AppConfig.SERVER_ADDRESS), new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).add("pageIndex", "1").add("pageSize", "5").build(), mNewsAndInfoResponsedCallback);
    }


    //-----------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------TODO 点击事件模块-----------------------------------------------------------------
    //-----------------------------------------------------------------------------------------------------------------------------------------
    @OnClick({R.id.btn_more_news_and_info})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_more_news_and_info:
                startActivity(NewsAndInfoActivity.createIntent(CustomApplication.getContext()));
                break;
            default:
        }
    }


    /**
     * 接收到EventBus发布的消息事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(MessageEvent messageEvent) {
        if (!isSupportVisible()) return;
        switch (messageEvent.getMessage()) {
            case MessageEvent.EVENT_TOP://滑动置顶功能
                mNsvTop.fullScroll(View.FOCUS_UP);
                mNsvTop.smoothScrollTo(0, 0);
                break;
            default:
        }
    }
}
