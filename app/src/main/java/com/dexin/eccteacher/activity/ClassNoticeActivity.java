package com.dexin.eccteacher.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blankj.utilcode.util.TimeUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dexin.eccteacher.R;
import com.dexin.eccteacher.adapter.DividerItemDecoration;
import com.dexin.eccteacher.application.AppConfig;
import com.dexin.eccteacher.application.CustomApplication;
import com.dexin.eccteacher.bean.ClassNoticeBean;
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

public class ClassNoticeActivity extends BaseActivity {
    @BindView(R.id.tb_commom_toolbar)
    Toolbar mTbCommomToolbar;
    @BindView(R.id.tv_commom_title)
    TextView mTvCommomTitle;
    @BindView(R.id.srl_smart_refresh)
    SmartRefreshLayout mSrlSmartRefresh;
    @BindView(R.id.rv_class_notice_list)
    RecyclerView mRvClassNotice;
    private View mEmptyView, mErrorView, mLoadingView;
    private ClassNoticeAdapter mClassNoticeAdapter;

    @Override
    protected int getLayoutResourceID() {
        return R.layout.activity_class_notice;
    }

    @Override
    public void initView() {
        //初始化Toolbar
        setSupportActionBar(mTbCommomToolbar);
        ActionBar lActionBar = getSupportActionBar();
        if (lActionBar != null) lActionBar.setDisplayShowTitleEnabled(false);
        mTvCommomTitle.setText("班级通知");
        //初始化RecyclerView
        mEmptyView = getLayoutInflater().inflate(R.layout.custom_empty_view, (ViewGroup) mRvClassNotice.getParent(), false);
        mErrorView = getLayoutInflater().inflate(R.layout.custom_error_view, (ViewGroup) mRvClassNotice.getParent(), false);
        mLoadingView = getLayoutInflater().inflate(R.layout.custom_loading_view, (ViewGroup) mRvClassNotice.getParent(), false);
        mClassNoticeAdapter = new ClassNoticeAdapter();
        mClassNoticeAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mClassNoticeAdapter.isFirstOnly(false);
        mRvClassNotice.setAdapter(mClassNoticeAdapter);
        mRvClassNotice.setLayoutManager(new LinearLayoutManager(CustomApplication.getContext()));
        mRvClassNotice.addItemDecoration(new DividerItemDecoration(CustomApplication.getContext(), DividerItemDecoration.VERTICAL));
    }

    @Override
    public void initListener() {
        mTbCommomToolbar.setNavigationOnClickListener(v -> onBackPressedSupport());
        mEmptyView.setOnClickListener(v -> loadClassNoticeList(true));
        mErrorView.setOnClickListener(v -> loadClassNoticeList(true));
        mSrlSmartRefresh.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPageIndex = 1;
                loadClassNoticeList(false);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                loadClassNoticeList(false);
            }
        });
        mClassNoticeAdapter.setOnItemClickListener((baseQuickAdapter, view, position) -> {
            ClassNoticeBean.DataBean lClassNoticeBean = (ClassNoticeBean.DataBean) baseQuickAdapter.getItem(position);
            if (lClassNoticeBean != null) startActivity(H5Activity.createIntent(CustomApplication.getContext(), lClassNoticeBean.getTheme(), lClassNoticeBean.getInformUrl(), lClassNoticeBean.getId(), 1));
        });
    }

    @Override
    public void initData() {
        loadClassNoticeList(true);
    }

    @NonNull
    public static Intent createIntent(Context context) {
        return new Intent(context, ClassNoticeActivity.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.publish, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_publish:
                startActivity(PublishClassNoticeActivity.createIntent(CustomApplication.getContext()));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.fab_top})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_top:
                mRvClassNotice.smoothScrollToPosition(0);
                break;
            default:
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------FIXME 获取"班级通知列表"模块---------------------------------------------------------
    //---------------------------------------------------------↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓---------------------------------------------------------
    private int mPageIndex = 1;//页码
    private OkHttpEngine.ResponsedCallback mClassNoticeListResponsedCallback;

    /**
     * 加载"班级通知列表"
     */
    private void loadClassNoticeList(boolean loading) {
        if (loading) mClassNoticeAdapter.setEmptyView(mLoadingView);
        if (mClassNoticeListResponsedCallback == null) {
            mClassNoticeListResponsedCallback = new OkHttpEngine.ResponsedCallback() {
                @Override
                public void onFailure(Call call, Exception e) {
                    mClassNoticeAdapter.setEmptyView(mErrorView);
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
                    ClassNoticeBean lClassNoticeBean = OkHttpEngine.toObject(responsedJsonStr, ClassNoticeBean.class);
                    if (lClassNoticeBean != null) {
                        switch (lClassNoticeBean.getCode()) {
                            case 0://成功:
                                ClassNoticeBean.PageObjectBean lPageObjectBean = lClassNoticeBean.getPageObject();
                                if ((lPageObjectBean != null) && (mPageIndex <= lPageObjectBean.getTotalPages())) {
                                    List<ClassNoticeBean.DataBean> lClassNoticeDataBeanList = lClassNoticeBean.getData();
                                    if ((lClassNoticeDataBeanList != null) && !lClassNoticeDataBeanList.isEmpty()) {
                                        if (mPageIndex == 1) {
                                            mClassNoticeAdapter.replaceData(lClassNoticeDataBeanList);
                                        } else {
                                            mClassNoticeAdapter.addData(lClassNoticeDataBeanList);
                                        }
                                        mPageIndex++;
                                    } else {
                                        if (mPageIndex == 1) {
                                            mClassNoticeAdapter.setNewData(null);
                                            mClassNoticeAdapter.setEmptyView(mEmptyView);
                                        }
                                    }
                                    mSrlSmartRefresh.finishLoadMore(true);
                                } else {
                                    mSrlSmartRefresh.finishLoadMoreWithNoMoreData();
                                }
                                break;
                            default:
                                RxToast.error(MessageFormat.format("获取'班级通知列表'失败! {0}:{1}", lClassNoticeBean.getCode(), lClassNoticeBean.getMessage()));
                        }
                    }
                }
            };
        }
        OkHttpEngine.getInstance().sendAsyncPostRequest(ClassNoticeActivity.this, MessageFormat.format("{0}/ClassInform/getClassInformAll", AppConfig.SERVER_ADDRESS), new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).add("pageIndex", String.valueOf(mPageIndex)).add("pageSize", AppConfig.PAGE_SIZE).build(), mClassNoticeListResponsedCallback);
    }

    private class ClassNoticeAdapter extends BaseQuickAdapter<ClassNoticeBean.DataBean, BaseViewHolder> {
        ClassNoticeAdapter() {
            super(R.layout.item_class_notice_list);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, ClassNoticeBean.DataBean dataBean) {
            baseViewHolder.setText(R.id.tv_subject_name, dataBean.getTheme()).setText(R.id.tv_class_name, dataBean.getGradeAdminName()).setText(R.id.tv_homework_digist, dataBean.getDigest()).setText(R.id.tv_publish_time, TimeUtils.millis2String(dataBean.getCreateTime()));
        }
    }
}
