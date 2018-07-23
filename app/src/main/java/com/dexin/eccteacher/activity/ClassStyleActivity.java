package com.dexin.eccteacher.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dexin.eccteacher.R;
import com.dexin.eccteacher.application.AppConfig;
import com.dexin.eccteacher.application.CustomApplication;
import com.dexin.eccteacher.bean.ClassStyleBean;
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
 * 班级风采Activity
 */
public class ClassStyleActivity extends BaseActivity {
    @BindView(R.id.tb_commom_toolbar)
    Toolbar mTbCommomToolbar;
    @BindView(R.id.tv_commom_title)
    TextView mTvCommomTitle;
    @BindView(R.id.srl_smart_refresh)
    SmartRefreshLayout mSrlSmartRefresh;
    @BindView(R.id.rv_class_style_list)
    RecyclerView mRvClassStyle;
    private View mEmptyView, mErrorView, mLoadingView;
    private ClassStyleAdapter mClassStyleAdapter;

    @NonNull
    public static Intent createIntent(Context context) {
        return new Intent(context, ClassStyleActivity.class);
    }

    @Override
    protected int getLayoutResourceID() {
        return R.layout.activity_class_style;
    }

    @Override
    public void initView() {
        //初始化Toolbar
        setSupportActionBar(mTbCommomToolbar);
        ActionBar lActionBar = getSupportActionBar();
        if (lActionBar != null) lActionBar.setDisplayShowTitleEnabled(false);
        mTvCommomTitle.setText("班级风采");
        //初始化RecyclerView
        mEmptyView = getLayoutInflater().inflate(R.layout.custom_empty_view, (ViewGroup) mRvClassStyle.getParent(), false);
        mErrorView = getLayoutInflater().inflate(R.layout.custom_error_view, (ViewGroup) mRvClassStyle.getParent(), false);
        mLoadingView = getLayoutInflater().inflate(R.layout.custom_loading_view, (ViewGroup) mRvClassStyle.getParent(), false);
        mClassStyleAdapter = new ClassStyleAdapter();
        mClassStyleAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mClassStyleAdapter.isFirstOnly(false);
        mRvClassStyle.setAdapter(mClassStyleAdapter);
        StaggeredGridLayoutManager lStaggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        lStaggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        mRvClassStyle.setLayoutManager(lStaggeredGridLayoutManager);
    }

    @Override
    public void initListener() {
        mTbCommomToolbar.setNavigationOnClickListener(v -> onBackPressedSupport());
        mEmptyView.setOnClickListener(v -> loadMoreClassStyle(true));
        mErrorView.setOnClickListener(v -> loadMoreClassStyle(true));
        mSrlSmartRefresh.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPageIndex = 1;
                loadMoreClassStyle(false);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                loadMoreClassStyle(false);
            }
        });
        mClassStyleAdapter.setOnItemClickListener((baseQuickAdapter, view, position) -> {
            ClassStyleBean.DataBean lClassStyleBean = (ClassStyleBean.DataBean) baseQuickAdapter.getItem(position);
            if (lClassStyleBean != null) {
                startActivity(ClassStyleDetailActivity.createIntent(CustomApplication.getContext(), lClassStyleBean.getId()), ActivityOptionsCompat.makeSceneTransitionAnimation(ClassStyleActivity.this, view, getString(R.string.picture_detail)).toBundle());
            }
        });
    }

    @Override
    public void initData() {
        loadMoreClassStyle(true);
    }


    @OnClick({R.id.fab_top})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_top:
                mRvClassStyle.smoothScrollToPosition(0);
                break;
            default:
        }
    }


    //-------------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------FIXME 获取"班级风采列表"模块---------------------------------------------------------
    //---------------------------------------------------------↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓---------------------------------------------------------

    private int mPageIndex = 1;//页码
    private OkHttpEngine.ResponsedCallback mClassStyleResponsedCallback;

    /**
     * 加载"班级风采"
     */
    private void loadMoreClassStyle(boolean loading) {
        if (loading) mClassStyleAdapter.setEmptyView(mLoadingView);
        if (mClassStyleResponsedCallback == null) {
            mClassStyleResponsedCallback = new OkHttpEngine.ResponsedCallback() {
                @Override
                public void onFailure(Call call, Exception e) {
                    mClassStyleAdapter.setEmptyView(mErrorView);
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
                    ClassStyleBean lClassStyleBean = OkHttpEngine.toObject(responsedJsonStr, ClassStyleBean.class);
                    if (lClassStyleBean != null) {
                        switch (lClassStyleBean.getCode()) {
                            case 0://成功
                                ClassStyleBean.PageObjectBean lPageObjectBean = lClassStyleBean.getPageObject();
                                if ((lPageObjectBean != null) && (mPageIndex <= lPageObjectBean.getTotalPages())) {
                                    List<ClassStyleBean.DataBean> lClassStyleList = lClassStyleBean.getData();
                                    if ((lClassStyleList != null) && !lClassStyleList.isEmpty()) {
                                        if (mPageIndex == 1) {
                                            mClassStyleAdapter.replaceData(lClassStyleList);
                                        } else {
                                            mClassStyleAdapter.addData(lClassStyleList);

                                        }
                                        mPageIndex++;
                                    } else {
                                        if (mPageIndex == 1) {
                                            mClassStyleAdapter.setNewData(null);
                                            mClassStyleAdapter.setEmptyView(mEmptyView);
                                        }
                                    }
                                    mSrlSmartRefresh.finishLoadMore(true);
                                } else {
                                    mSrlSmartRefresh.finishLoadMoreWithNoMoreData();
                                }
                                break;
                            default:
                                RxToast.error(MessageFormat.format("获取'班级风采列表'失败! {0}:{1}", lClassStyleBean.getCode(), lClassStyleBean.getMessage()));
                        }
                    }
                }
            };
        }
        OkHttpEngine.getInstance().sendAsyncPostRequest(ClassStyleActivity.this, MessageFormat.format("{0}/ClassmienPhotos/getClassmienInfo", AppConfig.SERVER_ADDRESS), new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).add("pageIndex", String.valueOf(mPageIndex)).add("pageSize", AppConfig.PAGE_SIZE).build(), mClassStyleResponsedCallback);
    }

    private class ClassStyleAdapter extends BaseQuickAdapter<ClassStyleBean.DataBean, BaseViewHolder> {
        ClassStyleAdapter() {
            super(R.layout.item_class_style);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, ClassStyleBean.DataBean dataBean) {
            Glide.with(mContext).load(MessageFormat.format("{0}{1}", AppConfig.SERVER_HOST, dataBean.getPhotoUrl())).apply(AppConfig.NORMAL_GLIDE_REQUEST_OPTIONS).into((ImageView) baseViewHolder.getView(R.id.iv_album_cover));
        }
    }
}
