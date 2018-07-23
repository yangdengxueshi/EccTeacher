package com.dexin.eccteacher.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dexin.eccteacher.R;
import com.dexin.eccteacher.application.AppConfig;
import com.dexin.eccteacher.application.CustomApplication;
import com.dexin.eccteacher.bean.MarkingDetailBean;
import com.dexin.eccteacher.fragment.ModifyMarkFragment;
import com.dexin.eccteacher.utility.OkHttpEngine;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.vondear.rxtool.view.RxToast;

import java.text.MessageFormat;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.FormBody;

public class MarkingDetailActivity extends BaseActivity {
    @BindView(R.id.tb_commom_toolbar)
    Toolbar mTbCommomToolbar;
    @BindView(R.id.tv_commom_title)
    TextView mTvCommomTitle;
    @BindView(R.id.srl_smart_refresh)
    SmartRefreshLayout mSrlSmartRefresh;
    @BindView(R.id.rv_marking_detail_list)
    RecyclerView mRvMarkingDetail;
    private MarkingDetailAdapter mMarkingDetailAdapter;
    private int mClassId;
    private String mTimeToQuery;

    @Override
    protected int getLayoutResourceID() {
        return R.layout.activity_marking_detail;
    }

    @Override
    public void initView() {
        Intent lIntent = getIntent();
        if (lIntent != null) {
            mTvCommomTitle.setText(lIntent.getStringExtra("className"));
            mClassId = lIntent.getIntExtra("classId", -1);
            mTimeToQuery = lIntent.getStringExtra("timeToQuery");
        }
        //初始化Toolbar
        setSupportActionBar(mTbCommomToolbar);
        ActionBar lActionBar = getSupportActionBar();
        if (lActionBar != null) lActionBar.setDisplayShowTitleEnabled(false);
        //初始化RecyclerView
        mMarkingDetailAdapter = new MarkingDetailAdapter();
        mMarkingDetailAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mMarkingDetailAdapter.isFirstOnly(false);
        mRvMarkingDetail.setAdapter(mMarkingDetailAdapter);
        mRvMarkingDetail.setLayoutManager(new LinearLayoutManager(CustomApplication.getContext()));
    }

    @Override
    public void initListener() {
        mTbCommomToolbar.setNavigationOnClickListener(v -> onBackPressedSupport());
        mSrlSmartRefresh.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPageIndex = 1;
                queryMarkingDetail();
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                queryMarkingDetail();
            }
        });
        mMarkingDetailAdapter.setOnItemChildLongClickListener((baseQuickAdapter, view, position) -> {
            switch (view.getId()) {
                case R.id.ll_modify_score:
                    MarkingDetailBean.DataBean curMark = (MarkingDetailBean.DataBean) baseQuickAdapter.getItem(position);
                    if (curMark != null) {
                        MarkingDetailBean.DataBean.GradeSysInfoBean lGradeSysInfo = curMark.getGradeSysInfo();
                        if (lGradeSysInfo != null) ModifyMarkFragment.newInstance(curMark.getId(), curMark.getMark(), curMark.getScore(), lGradeSysInfo.getName(), lGradeSysInfo.getStarScore(), lGradeSysInfo.getTotalStar()).show(mSupportFragmentManager, null);
                    }
                    break;
                default:
            }
            return true;
        });
    }

    @Override
    public void initData() {
        queryMarkingDetail();
    }

    @NonNull
    public static Intent createIntent(Context context, String className, int classId, String timeToQuery) {
        Intent intent = new Intent(context, MarkingDetailActivity.class);
        intent.putExtra("className", className);
        intent.putExtra("classId", classId);
        intent.putExtra("timeToQuery", timeToQuery);
        return intent;
    }


    //-------------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------FIXME 查询"班级评分详情"模块---------------------------------------------------------
    //---------------------------------------------------------↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓---------------------------------------------------------
    private int mPageIndex = 1;
    private OkHttpEngine.ResponsedCallback mMarkDetailResponsedCallback;

    /**
     * 查询班级评分详情
     */
    private void queryMarkingDetail() {
        if (mMarkDetailResponsedCallback == null) {
            mMarkDetailResponsedCallback = new OkHttpEngine.ResponsedCallback() {
                @Override
                public void onFailure(Call call, Exception e) {
                    RxToast.error("服务错误,获取'评分详情'失败!");
                    mSrlSmartRefresh.finishLoadMore(false);//加载失败
                    mSrlSmartRefresh.finishRefresh(false);//刷新失败
                    mSrlSmartRefresh.setNoMoreData(false);//恢复上拉状态
                }

                @Override
                public void onResponseJson(Call call, String responsedJsonStr) {
                    if (mPageIndex == 1) {//刷新成功  或   首次加载成功
                        mSrlSmartRefresh.finishRefresh(true);
                        mSrlSmartRefresh.setNoMoreData(false);//恢复上拉状态
                    }
                    MarkingDetailBean lMarkingDetailBean = OkHttpEngine.toObject(responsedJsonStr, MarkingDetailBean.class);
                    if (lMarkingDetailBean != null) {
                        switch (lMarkingDetailBean.getCode()) {
                            case 0://成功
                                MarkingDetailBean.PageObjectBean lPageObjectBean = lMarkingDetailBean.getPageObject();
                                if ((lPageObjectBean != null) && (mPageIndex <= lPageObjectBean.getTotalPages())) {
                                    List<MarkingDetailBean.DataBean> lMarkingDetailBeanList = lMarkingDetailBean.getData();
                                    if ((lMarkingDetailBeanList != null) && !lMarkingDetailBeanList.isEmpty()) {
                                        if (mPageIndex == 1) {
                                            mMarkingDetailAdapter.replaceData(lMarkingDetailBeanList);
                                        } else {
                                            mMarkingDetailAdapter.addData(lMarkingDetailBeanList);
                                        }
                                        mPageIndex++;
                                    }
                                    mSrlSmartRefresh.finishLoadMore(true);
                                } else {
                                    mSrlSmartRefresh.finishLoadMoreWithNoMoreData();
                                }
                                break;
                            default:
                                RxToast.error(MessageFormat.format("获取''评分详情''失败! {0}:{1}", lMarkingDetailBean.getCode(), lMarkingDetailBean.getMessage()));
                        }
                    }
                }
            };
        }
        OkHttpEngine.getInstance().sendAsyncPostRequest(MarkingDetailActivity.this, MessageFormat.format("{0}/gradePoint/getGradePointAll", AppConfig.SERVER_ADDRESS), new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).add("adminClassId", String.valueOf(mClassId)).add("time", mTimeToQuery).add("pageIndex", String.valueOf(mPageIndex)).add("pageSize", AppConfig.PAGE_SIZE).build(), mMarkDetailResponsedCallback);
    }

    private class MarkingDetailAdapter extends BaseQuickAdapter<MarkingDetailBean.DataBean, BaseViewHolder> {
        MarkingDetailAdapter() {
            super(R.layout.item_marking_detail);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, MarkingDetailBean.DataBean dataBean) {
            MarkingDetailBean.DataBean.GradeSysInfoBean lMarkNameBean = dataBean.getGradeSysInfo();
            if (lMarkNameBean != null) baseViewHolder.setText(R.id.tv_mark_name, lMarkNameBean.getName());
            String creatorName = dataBean.getCreatetorName();
            baseViewHolder.setText(R.id.tv_mark_value, String.valueOf(dataBean.getScore())).setText(R.id.tv_marker_name, "评分教师:" + (!TextUtils.isEmpty(creatorName) ? creatorName : "")).setText(R.id.tv_mark_detail, dataBean.getMark()).setText(R.id.tv_mark_time, dataBean.getTime());
            baseViewHolder.addOnLongClickListener(R.id.ll_modify_score);
        }
    }
}
