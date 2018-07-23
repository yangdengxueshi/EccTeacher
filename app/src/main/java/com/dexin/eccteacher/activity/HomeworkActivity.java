package com.dexin.eccteacher.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.allen.library.SuperButton;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dexin.eccteacher.R;
import com.dexin.eccteacher.adapter.DividerItemDecoration;
import com.dexin.eccteacher.application.AppConfig;
import com.dexin.eccteacher.application.CustomApplication;
import com.dexin.eccteacher.bean.HomeworkBean;
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

public class HomeworkActivity extends BaseActivity {
    @BindView(R.id.tb_commom_toolbar)
    Toolbar mTbCommomToolbar;
    @BindView(R.id.tv_commom_title)
    TextView mTvCommomTitle;
    @BindView(R.id.srl_smart_refresh)
    SmartRefreshLayout mSrlSmartRefresh;
    @BindView(R.id.rv_homework_list)
    RecyclerView mRvHomework;
    private View mEmptyView, mErrorView, mLoadingView;
    private HomeworkAdapter mHomeworkAdapter;

    @NonNull
    public static Intent createIntent(Context context) {
        return new Intent(context, HomeworkActivity.class);
    }

    @Override
    protected int getLayoutResourceID() {
        return R.layout.activity_homework;
    }

    @Override
    public void initView() {
        //初始化Toolbar
        setSupportActionBar(mTbCommomToolbar);
        ActionBar lActionBar = getSupportActionBar();
        if (lActionBar != null) lActionBar.setDisplayShowTitleEnabled(false);
        mTvCommomTitle.setText("家庭作业");
        //初始化RecyclerView
        mEmptyView = getLayoutInflater().inflate(R.layout.custom_empty_view, (ViewGroup) mRvHomework.getParent(), false);
        mErrorView = getLayoutInflater().inflate(R.layout.custom_error_view, (ViewGroup) mRvHomework.getParent(), false);
        mLoadingView = getLayoutInflater().inflate(R.layout.custom_loading_view, (ViewGroup) mRvHomework.getParent(), false);
        mHomeworkAdapter = new HomeworkAdapter();
        mHomeworkAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mHomeworkAdapter.isFirstOnly(false);
        mRvHomework.setAdapter(mHomeworkAdapter);
        mRvHomework.setLayoutManager(new LinearLayoutManager(this));
        mRvHomework.addItemDecoration(new DividerItemDecoration(CustomApplication.getContext(), DividerItemDecoration.VERTICAL));
    }

    @Override
    public void initListener() {
        mTbCommomToolbar.setNavigationOnClickListener(v -> onBackPressedSupport());
        mEmptyView.setOnClickListener(v -> loadMoreHomeworkInfo(true));
        mErrorView.setOnClickListener(v -> loadMoreHomeworkInfo(true));
        mSrlSmartRefresh.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPageIndex = 1;
                loadMoreHomeworkInfo(false);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                loadMoreHomeworkInfo(false);
            }
        });
        mHomeworkAdapter.setOnItemClickListener((baseQuickAdapter, view, position) -> {
            HomeworkBean.DataBean lDataBean = (HomeworkBean.DataBean) baseQuickAdapter.getItem(position);
            if (lDataBean != null) startActivity(H5Activity.createIntent(CustomApplication.getContext(), lDataBean.getHeadline(), lDataBean.getH5Url(), lDataBean.getId(), 3));
        });
    }

    @Override
    public void initData() {
        loadMoreHomeworkInfo(true);
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
                startActivity(PublishHomeworkActivity.createIntent(CustomApplication.getContext()));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.fab_top})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_top:
                mRvHomework.smoothScrollToPosition(0);
                break;
            default:
        }
    }


    //-------------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------FIXME 获取"家庭作业"模块------------------------------------------------------------
    //---------------------------------------------------------↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓---------------------------------------------------------
    private int mPageIndex = 1;//页码
    private OkHttpEngine.ResponsedCallback mHomeworkResponsedCallback;

    /**
     * 加载'家庭作业'信息
     */
    private void loadMoreHomeworkInfo(boolean loading) {
        if (loading) mHomeworkAdapter.setEmptyView(mLoadingView);
        if (mHomeworkResponsedCallback == null) {
            mHomeworkResponsedCallback = new OkHttpEngine.ResponsedCallback() {
                @Override
                public void onFailure(Call call, Exception e) {
                    mHomeworkAdapter.setEmptyView(mErrorView);
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
                    HomeworkBean lHomeworkBean = OkHttpEngine.toObject(responsedJsonStr, HomeworkBean.class);
                    if (lHomeworkBean != null) {
                        switch (lHomeworkBean.getCode()) {
                            case 0://成功:
                                HomeworkBean.PageObjectBean lPageObjectBean = lHomeworkBean.getPageObject();
                                if (lPageObjectBean != null && mPageIndex <= lPageObjectBean.getTotalPages()) {
                                    List<HomeworkBean.DataBean> lHomeworkBeanDataList = lHomeworkBean.getData();
                                    if (lHomeworkBeanDataList != null && !lHomeworkBeanDataList.isEmpty()) {
                                        if (mPageIndex == 1) {
                                            mHomeworkAdapter.replaceData(lHomeworkBeanDataList);
                                        } else {
                                            mHomeworkAdapter.addData(lHomeworkBeanDataList);
                                        }
                                        mPageIndex++;
                                    } else {
                                        if (mPageIndex == 1) {
                                            mHomeworkAdapter.setNewData(null);
                                            mHomeworkAdapter.setEmptyView(mEmptyView);
                                        }
                                    }
                                    mSrlSmartRefresh.finishLoadMore(true);
                                } else {
                                    mSrlSmartRefresh.finishLoadMoreWithNoMoreData();
                                }
                                break;
                            default:
                                RxToast.error("获取'家庭作业'失败! " + lHomeworkBean.getCode() + ":" + lHomeworkBean.getMessage());
                        }
                    }
                }
            };
        }
        OkHttpEngine.getInstance().sendAsyncPostRequest(HomeworkActivity.this, MessageFormat.format("{0}/FamilyTask/getFamilyTasks", AppConfig.SERVER_ADDRESS), new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).add("pageIndex", String.valueOf(mPageIndex)).add("pageSize", AppConfig.PAGE_SIZE).build(), mHomeworkResponsedCallback);
    }

    private class HomeworkAdapter extends BaseQuickAdapter<HomeworkBean.DataBean, BaseViewHolder> {
        HomeworkAdapter() {
            super(R.layout.item_homework_list);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, HomeworkBean.DataBean dataBean) {
            String lSubjectName = dataBean.getHeadline();
            if (!TextUtils.isEmpty(lSubjectName)) {
                int lColor = AppConfig.genRandomColor();
                SuperButton lSbSubjectAbbrName = baseViewHolder.getView(R.id.sb_subject_abbr_name);
                lSbSubjectAbbrName.setShapeStrokeColor(lColor).setUseShape();
                lSbSubjectAbbrName.setTextColor(lColor);
                lSbSubjectAbbrName.setText(lSubjectName.substring(0, 1));
                baseViewHolder.setText(R.id.tv_subject_name, lSubjectName);
            }
            baseViewHolder.setText(R.id.tv_class_name, dataBean.getGradeAdminName()).setText(R.id.tv_homework_digist, dataBean.getDigest()).setText(R.id.tv_publish_time, dataBean.getCreateTime());
        }
    }
}
