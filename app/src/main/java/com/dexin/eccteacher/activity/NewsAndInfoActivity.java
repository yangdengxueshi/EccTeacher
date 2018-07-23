package com.dexin.eccteacher.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dexin.eccteacher.R;
import com.dexin.eccteacher.adapter.DividerItemDecoration;
import com.dexin.eccteacher.adapter.NewsAndInfoAdapter;
import com.dexin.eccteacher.application.AppConfig;
import com.dexin.eccteacher.application.CustomApplication;
import com.dexin.eccteacher.bean.NewsAndInfoBean;
import com.dexin.eccteacher.utility.OkHttpEngine;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.vondear.rxtool.view.RxToast;

import java.text.MessageFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.FormBody;

/**
 * 新闻资讯Activity
 */
public class NewsAndInfoActivity extends BaseActivity {
    @BindView(R.id.tb_commom_toolbar)
    Toolbar mTbCommomToolbar;
    @BindView(R.id.tv_commom_title)
    TextView mTvCommomTitle;
    @BindView(R.id.srl_smart_refresh)
    SmartRefreshLayout mSrlSmartRefresh;
    @BindView(R.id.rv_news_and_info_list)
    RecyclerView mRvNewsAndInfo;
    private View mEmptyView, mErrorView, mLoadingView;
    private NewsAndInfoAdapter mNewsAndInfoAdapter;

    @Override
    protected int getLayoutResourceID() {
        return R.layout.activity_news_and_info;
    }

    @Override
    public void initView() {
        //初始化Toolbar
        setSupportActionBar(mTbCommomToolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowTitleEnabled(false);
        mTvCommomTitle.setText("新闻资讯");
        //初始化RecyclerView
        mEmptyView = getLayoutInflater().inflate(R.layout.custom_empty_view, (ViewGroup) mRvNewsAndInfo.getParent(), false);
        mErrorView = getLayoutInflater().inflate(R.layout.custom_error_view, (ViewGroup) mRvNewsAndInfo.getParent(), false);
        mLoadingView = getLayoutInflater().inflate(R.layout.custom_loading_view, (ViewGroup) mRvNewsAndInfo.getParent(), false);
        mNewsAndInfoAdapter = new NewsAndInfoAdapter();
        mNewsAndInfoAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mNewsAndInfoAdapter.isFirstOnly(false);
        mRvNewsAndInfo.setAdapter(mNewsAndInfoAdapter);
        mRvNewsAndInfo.setLayoutManager(new LinearLayoutManager(CustomApplication.getContext()));
        mRvNewsAndInfo.addItemDecoration(new DividerItemDecoration(CustomApplication.getContext(), DividerItemDecoration.VERTICAL));
    }

    @Override
    public void initListener() {
        mTbCommomToolbar.setNavigationOnClickListener(v -> onBackPressedSupport());
        mEmptyView.setOnClickListener(v -> loadMoreNewsAndInfo(true));
        mErrorView.setOnClickListener(v -> loadMoreNewsAndInfo(true));
        mSrlSmartRefresh.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPageIndex = 1;
                loadMoreNewsAndInfo(false);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                loadMoreNewsAndInfo(false);
            }
        });
        mNewsAndInfoAdapter.setOnItemClickListener((baseQuickAdapter, view, position) -> {
            NewsAndInfoBean.DataBean lNewsAndInfoBean = (NewsAndInfoBean.DataBean) baseQuickAdapter.getItem(position);
            if (lNewsAndInfoBean != null) startActivity(H5Activity.createIntent(CustomApplication.getContext(), lNewsAndInfoBean.getHeadline(), lNewsAndInfoBean.getNewsUrl(), lNewsAndInfoBean.getId(), 2));
        });
    }

    @Override
    public void initData() {
        loadMoreNewsAndInfo(true);
    }

    @NonNull
    public static Intent createIntent(Context context) {
        return new Intent(context, NewsAndInfoActivity.class);
    }

    @OnClick({R.id.fab_top})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_top:
                mRvNewsAndInfo.smoothScrollToPosition(0);
                break;
            default:
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------FIXME 获取"新闻资讯列表"模块---------------------------------------------------------
    //---------------------------------------------------------↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓---------------------------------------------------------
    private int mPageIndex = 1;//页码
    private OkHttpEngine.ResponsedCallback mNewsAndInfoResponsedCallback;

    /**
     * 加载更多"新闻资讯"
     */
    private void loadMoreNewsAndInfo(boolean loading) {
        if (loading) mNewsAndInfoAdapter.setEmptyView(mLoadingView);
        if (mNewsAndInfoResponsedCallback == null) {
            mNewsAndInfoResponsedCallback = new OkHttpEngine.ResponsedCallback() {
                @Override
                public void onFailure(Call call, Exception e) {
                    mNewsAndInfoAdapter.setEmptyView(mErrorView);
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
                    NewsAndInfoBean lNewsAndInfoBean = OkHttpEngine.toObject(responsedJsonStr, NewsAndInfoBean.class);
                    if (lNewsAndInfoBean != null) {
                        switch (lNewsAndInfoBean.getCode()) {
                            case 0://成功
                                NewsAndInfoBean.PageObjectBean lPageObjectBean = lNewsAndInfoBean.getPageObject();
                                if ((lPageObjectBean != null) && (mPageIndex <= lPageObjectBean.getTotalPages())) {
                                    List<NewsAndInfoBean.DataBean> lNewsAndInfoDataBeanList = lNewsAndInfoBean.getData();
                                    if ((lNewsAndInfoDataBeanList != null) && !lNewsAndInfoDataBeanList.isEmpty()) {
                                        if (mPageIndex == 1) {
                                            mNewsAndInfoAdapter.replaceData(lNewsAndInfoDataBeanList);
                                        } else {
                                            mNewsAndInfoAdapter.addData(lNewsAndInfoDataBeanList);
                                        }
                                        mPageIndex++;
                                    } else {
                                        if (mPageIndex == 1) {
                                            mNewsAndInfoAdapter.setNewData(null);
                                            mNewsAndInfoAdapter.setEmptyView(mEmptyView);
                                        }
                                    }
                                    mSrlSmartRefresh.finishLoadMore(true);
                                } else {
                                    mSrlSmartRefresh.finishLoadMoreWithNoMoreData();
                                }
                                break;
                            default:
                                RxToast.error(MessageFormat.format("获取新闻资讯失败! {0}:{1}", lNewsAndInfoBean.getCode(), lNewsAndInfoBean.getMessage()));
                        }
                    }
                }
            };
        }
        OkHttpEngine.getInstance().sendAsyncPostRequest(NewsAndInfoActivity.this, MessageFormat.format("{0}/news/getNews", AppConfig.SERVER_ADDRESS), new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).add("pageIndex", String.valueOf(mPageIndex)).add("pageSize", AppConfig.PAGE_SIZE).build(), mNewsAndInfoResponsedCallback);
    }
}
