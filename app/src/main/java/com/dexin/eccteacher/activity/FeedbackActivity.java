package com.dexin.eccteacher.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dexin.eccteacher.R;
import com.dexin.eccteacher.adapter.DividerItemDecoration;
import com.dexin.eccteacher.application.AppConfig;
import com.dexin.eccteacher.application.CustomApplication;
import com.dexin.eccteacher.bean.FeedbackBean;
import com.dexin.eccteacher.bean.MultipleItemBean;
import com.dexin.eccteacher.bean.ReplyFeedbackBean;
import com.dexin.eccteacher.utility.OkHttpEngine;
import com.maning.mndialoglibrary.MProgressDialog;
import com.maning.mndialoglibrary.MStatusDialog;
import com.maning.mndialoglibrary.config.MDialogConfig;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.vondear.rxtool.RxKeyboardTool;
import com.vondear.rxtool.RxNetTool;
import com.vondear.rxtool.view.RxToast;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.FormBody;

/**
 * 意见反馈 Activity
 */
public class FeedbackActivity extends BaseActivity {
    @BindView(R.id.tb_commom_toolbar)
    Toolbar mTbCommomToolbar;
    @BindView(R.id.tv_commom_title)
    TextView mTvCommomTitle;
    @BindView(R.id.srl_smart_refresh)
    SmartRefreshLayout mSrlSmartRefresh;
    @BindView(R.id.rv_feedback_list)
    RecyclerView mRvFeedback;
    private View mEmptyView, mErrorView, mLoadingView;
    private FeedbackAdapter mFeedbackAdapter;

    @Override
    protected int getLayoutResourceID() {
        return R.layout.activity_feedback;
    }

    @Override
    public void initView() {
        //初始化Toolbar
        setSupportActionBar(mTbCommomToolbar);
        ActionBar lActionBar = getSupportActionBar();
        if (lActionBar != null) lActionBar.setDisplayShowTitleEnabled(false);
        mTvCommomTitle.setText("意见反馈");
        //初始化RecyclerView
        mEmptyView = getLayoutInflater().inflate(R.layout.custom_empty_view, (ViewGroup) mRvFeedback.getParent(), false);
        mErrorView = getLayoutInflater().inflate(R.layout.custom_error_view, (ViewGroup) mRvFeedback.getParent(), false);
        mLoadingView = getLayoutInflater().inflate(R.layout.custom_loading_view, (ViewGroup) mRvFeedback.getParent(), false);
        mFeedbackAdapter = new FeedbackAdapter();
        mFeedbackAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mFeedbackAdapter.isFirstOnly(false);
        mRvFeedback.setAdapter(mFeedbackAdapter);
        mRvFeedback.setLayoutManager(new LinearLayoutManager(CustomApplication.getContext()));
        mRvFeedback.addItemDecoration(new DividerItemDecoration(CustomApplication.getContext(), DividerItemDecoration.VERTICAL));
    }

    @Override
    public void initListener() {
        mTbCommomToolbar.setNavigationOnClickListener(v -> onBackPressedSupport());
        mEmptyView.setOnClickListener(v -> loadMoreFeedback(true));
        mErrorView.setOnClickListener(v -> loadMoreFeedback(true));
        mSrlSmartRefresh.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPageIndex = 1;
                loadMoreFeedback(false);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                loadMoreFeedback(false);
            }
        });
        mRvFeedback.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                hideSoftInputMethod();
            }
        });
        mEtReplyContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    if (!mBtnReply.isEnabled()) {
                        mBtnReply.setEnabled(true);
                        mBtnReply.setBackgroundResource(R.drawable.bg_page_btn);
                    }
                } else {
                    if (mBtnReply.isEnabled()) {
                        mBtnReply.setEnabled(false);
                        mBtnReply.setBackgroundResource(R.drawable.bg_page_btn_disable);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void initData() {
        loadMoreFeedback(true);
    }

    @NonNull
    public static Intent createIntent(Context context) {
        return new Intent(context, FeedbackActivity.class);
    }


    @OnClick({R.id.fab_top})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_top:
                mRvFeedback.smoothScrollToPosition(0);
                break;
            default:
        }
    }


    //-------------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------FIXME 获取"意见反馈列表"模块---------------------------------------------------------
    //---------------------------------------------------------↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓------------------------------------------------------------------
    private int mPageIndex = 1;//页码
    private OkHttpEngine.ResponsedCallback mFeedbackResponsedCallback;
    private MultipleItemQuickAdapter mMultipleItemQuickAdapter;

    /**
     * 加载更多意见反馈列表
     */
    private void loadMoreFeedback(boolean loading) {
        if (loading) mFeedbackAdapter.setEmptyView(mLoadingView);
        if (mFeedbackResponsedCallback == null) {
            mFeedbackResponsedCallback = new OkHttpEngine.ResponsedCallback() {
                @Override
                public void onFailure(Call call, Exception e) {
                    mFeedbackAdapter.setEmptyView(mErrorView);
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
                    FeedbackBean lFeedbackBean = OkHttpEngine.toObject(responsedJsonStr, FeedbackBean.class);
                    if (lFeedbackBean != null) {
                        switch (lFeedbackBean.getCode()) {
                            case 0:
                                FeedbackBean.PageObjectBean lPageObjectBean = lFeedbackBean.getPageObject();
                                if ((lPageObjectBean != null) && (mPageIndex <= lPageObjectBean.getTotalPages())) {
                                    List<FeedbackBean.DataBean> lFeedbackBeanDataList = lFeedbackBean.getData();
                                    if ((lFeedbackBeanDataList != null) && !lFeedbackBeanDataList.isEmpty()) {
                                        if (mPageIndex == 1) {
                                            mFeedbackAdapter.replaceData(lFeedbackBeanDataList);
                                        } else {
                                            mFeedbackAdapter.addData(lFeedbackBeanDataList);
                                        }
                                        mPageIndex++;
                                    } else {
                                        if (mPageIndex == 1) {
                                            mFeedbackAdapter.setNewData(null);
                                            mFeedbackAdapter.setEmptyView(mEmptyView);
                                        }
                                    }
                                    mSrlSmartRefresh.finishLoadMore(true);
                                } else {
                                    mSrlSmartRefresh.finishLoadMoreWithNoMoreData();
                                }
                                break;
                            default:
                                RxToast.error("获取'意见反馈'失败! " + lFeedbackBean.getCode() + ":" + lFeedbackBean.getMessage());
                        }
                    }
                }
            };
        }
        OkHttpEngine.getInstance().sendAsyncPostRequest(FeedbackActivity.this, MessageFormat.format("{0}/OpinionFeedback/getIdea", AppConfig.SERVER_ADDRESS), new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).add("pageIndex", String.valueOf(mPageIndex)).add("pageSize", AppConfig.PAGE_SIZE).build(), mFeedbackResponsedCallback);
    }

    /**
     * 外层RecyclerView的Adapter
     */
    private class FeedbackAdapter extends BaseQuickAdapter<FeedbackBean.DataBean, BaseViewHolder> {
        FeedbackAdapter() {
            super(R.layout.item_feedback_recyclerview);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, FeedbackBean.DataBean publishDataBean) {
            List<MultipleItemBean> lMultipleItemBeanList = new ArrayList<>();
            if (publishDataBean != null) {
                lMultipleItemBeanList.add(new MultipleItemBean(MultipleItemBean.FEEDBACK_PUBLISH, publishDataBean));
                List<FeedbackBean.DataBean.ChildrenBean> lChildrenBeanList = publishDataBean.getChildren();
                if ((lChildrenBeanList != null) && !lChildrenBeanList.isEmpty()) {
                    for (FeedbackBean.DataBean.ChildrenBean replyDataBean : lChildrenBeanList) {
                        lMultipleItemBeanList.add(new MultipleItemBean(MultipleItemBean.FEEDBACK_REPLY, replyDataBean));
                    }
                }
                RecyclerView lFeedbackItemRecyclerView = baseViewHolder.getView(R.id.rv_item_feedback);
                lFeedbackItemRecyclerView.setLayoutManager(new LinearLayoutManager(CustomApplication.getContext()));
                MultipleItemQuickAdapter lMultipleItemQuickAdapter = new MultipleItemQuickAdapter(lMultipleItemBeanList);
                lMultipleItemQuickAdapter.setOnItemChildClickListener((baseQuickAdapter, view, position) -> {
                    switch (view.getId()) {
                        case R.id.btn_reply:
                            MultipleItemBean lMultipleItemBean = (MultipleItemBean) baseQuickAdapter.getItem(0);
                            if (lMultipleItemBean != null) {
                                FeedbackBean.DataBean lPublishDataBean = lMultipleItemBean.getPublishDataBean();
                                if (lPublishDataBean != null) {
                                    mIdToReply = lPublishDataBean.getId();//需要"被回复的意见"的id
                                    mMultipleItemQuickAdapter = lMultipleItemQuickAdapter;
                                    showSoftInput();
                                }
                            }
                            break;
                        default:
                    }
                });
                lFeedbackItemRecyclerView.setAdapter(lMultipleItemQuickAdapter);
            }
        }
    }

    /**
     * 内层嵌套的RecyclerView的Adapter
     */
    private class MultipleItemQuickAdapter extends BaseMultiItemQuickAdapter<MultipleItemBean, BaseViewHolder> {
        MultipleItemQuickAdapter(List<MultipleItemBean> multipleItemBeanList) {
            super(multipleItemBeanList);
            addItemType(MultipleItemBean.FEEDBACK_PUBLISH, R.layout.item_feedback_publish);
            addItemType(MultipleItemBean.FEEDBACK_REPLY, R.layout.item_feedback_reply);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, MultipleItemBean multipleItemBean) {
            switch (baseViewHolder.getItemViewType()) {
                case MultipleItemBean.FEEDBACK_PUBLISH:
                    FeedbackBean.DataBean publishDataBean = multipleItemBean.getPublishDataBean();
                    if (publishDataBean != null) {
                        String photoUrl;
                        if (!TextUtils.isEmpty(photoUrl = publishDataBean.getPhotoUrl())) Glide.with(mContext).load(MessageFormat.format("{0}{1}", AppConfig.SERVER_HOST, photoUrl.replaceAll("\\\\", "/"))).apply(AppConfig.AVATAR_GLIDE_REQUEST_OPTIONS).into((ImageView) baseViewHolder.getView(R.id.iv_publisher_avatar));
                        baseViewHolder.setText(R.id.tv_publisher_name, publishDataBean.getCreatorName()).setText(R.id.tv_publish_time, publishDataBean.getCreateTime()).setText(R.id.tv_publish_content, publishDataBean.getContent());
                        baseViewHolder.addOnClickListener(R.id.btn_reply);
                    }
                    break;
                case MultipleItemBean.FEEDBACK_REPLY:
                    FeedbackBean.DataBean.ChildrenBean replyDataBean = multipleItemBean.getReplyDataBean();
                    if (replyDataBean != null) {
                        String photoUrl;
                        if (!TextUtils.isEmpty(photoUrl = replyDataBean.getPhotoUrl())) Glide.with(mContext).load(MessageFormat.format("{0}{1}", AppConfig.SERVER_HOST, photoUrl.replaceAll("\\\\", "/"))).apply(AppConfig.AVATAR_GLIDE_REQUEST_OPTIONS).into((ImageView) baseViewHolder.getView(R.id.iv_replyer_avatar));
                        baseViewHolder.setText(R.id.tv_replyer_name, replyDataBean.getCreatorName()).setText(R.id.tv_reply_time, replyDataBean.getCreateTime()).setText(R.id.tv_reply_content, replyDataBean.getContent());
                    }
                    break;
                default:
            }
        }
    }


    //-------------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------FIXME "回复意见"模块----------------------------------------------------------------
    //---------------------------------------------------------↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓----------------------------------------------------------------
    @BindView(R.id.ll_reply)
    LinearLayout mLlReply;
    @BindView(R.id.et_reply_content)
    EditText mEtReplyContent;
    @BindView(R.id.btn_reply)
    Button mBtnReply;
    private int mIdToReply;

    @OnClick({R.id.btn_reply})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_reply:
                replySuggestion(mEtReplyContent.getText().toString());
                break;
            default:
        }
    }

    /**
     * 回复别人发布的意见
     *
     * @param replyContent 回复的内容
     */
    private void replySuggestion(String replyContent) {
        if (RxNetTool.isAvailable(CustomApplication.getContext())) MProgressDialog.showProgress(FeedbackActivity.this, "");
        OkHttpEngine.getInstance().sendAsyncPostRequest(FeedbackActivity.this, MessageFormat.format("{0}/OpinionFeedback/addOpinionFeedbacks", AppConfig.SERVER_ADDRESS), new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).add("id", String.valueOf(mIdToReply)).add("content", replyContent).add("creatorType", "1").build(), new OkHttpEngine.ResponsedCallback() {
            @Override
            public void onFailure(Call call, Exception e) {
                MProgressDialog.dismissProgress();
                new MStatusDialog(FeedbackActivity.this, new MDialogConfig.Builder().setTextColor(getResources().getColor(R.color.operate_failed)).build()).show("操作失败", getResources().getDrawable(R.drawable.ic_failure));
            }

            @Override
            public void onResponseJson(Call call, String responsedJsonStr) {
                hideSoftInputMethod();
                MProgressDialog.dismissProgress();
                ReplyFeedbackBean lReplyFeedbackBean = OkHttpEngine.toObject(responsedJsonStr, ReplyFeedbackBean.class);
                if (lReplyFeedbackBean != null) {
                    switch (lReplyFeedbackBean.getCode()) {
                        case 0:
                            new MStatusDialog(FeedbackActivity.this).show("回复成功", getResources().getDrawable(R.drawable.ic_success));
                            ReplyFeedbackBean.DataBean lServerReplyDataBean = lReplyFeedbackBean.getData();
                            if (lServerReplyDataBean != null) mMultipleItemQuickAdapter.addData(new MultipleItemBean(MultipleItemBean.FEEDBACK_REPLY, new FeedbackBean.DataBean.ChildrenBean(lServerReplyDataBean.getContent(), lServerReplyDataBean.getCountChildren(), lServerReplyDataBean.getCreateTime(), lServerReplyDataBean.getCreatorName(), lServerReplyDataBean.getId(), lServerReplyDataBean.getPhotoUrl(), 0, null)));
                            break;
                        default:
                            RxToast.error(MessageFormat.format("回复意见失败! {0}:{1}", lReplyFeedbackBean.getCode(), lReplyFeedbackBean.getMessage()));
                    }
                }
            }
        });
    }


    //-------------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------FIXME "显示隐藏键盘"模块------------------------------------------------------------
    //---------------------------------------------------------↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓------------------------------------------------------------
    private void showSoftInput() {
        mLlReply.setVisibility(View.VISIBLE);
        RxKeyboardTool.showSoftInput(FeedbackActivity.this, mEtReplyContent);
    }

    private void hideSoftInputMethod() {
        if (mLlReply.getVisibility() == View.VISIBLE) {
            mLlReply.setVisibility(View.GONE);
            RxKeyboardTool.hideSoftInput(FeedbackActivity.this);
            mEtReplyContent.setText("");
        }
    }
}
