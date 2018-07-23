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
import com.dexin.eccteacher.application.AppConfig;
import com.dexin.eccteacher.application.CustomApplication;
import com.dexin.eccteacher.bean.ApplyLeaveBean;
import com.dexin.eccteacher.bean.ApplyLeaveMultipleItemBean;
import com.dexin.eccteacher.bean.ReplyLeaveBean;
import com.dexin.eccteacher.bean.ResponseResultBean;
import com.dexin.eccteacher.utility.OkHttpEngine;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.vondear.rxtool.RxKeyboardTool;
import com.vondear.rxtool.RxTimeTool;
import com.vondear.rxtool.view.RxToast;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.FormBody;

/**
 * 请假事项Activity
 */
public class LeaveThingsActivity extends BaseActivity {
    @BindView(R.id.tb_commom_toolbar)
    Toolbar mTbCommomToolbar;
    @BindView(R.id.tv_commom_title)
    TextView mTvCommomTitle;
    @BindView(R.id.srl_smart_refresh)
    SmartRefreshLayout mSrlSmartRefresh;
    @BindView(R.id.rv_apply_leave_list)
    RecyclerView mRvApplyLeave;
    private View mEmptyView, mErrorView, mLoadingView;
    private ApplyLeaveAdapter mApplyLeaveAdapter;

    @Override
    protected int getLayoutResourceID() {
        return R.layout.activity_leave_things;
    }

    @Override
    public void initView() {
        //初始化Toolbar
        setSupportActionBar(mTbCommomToolbar);
        ActionBar lActionBar = getSupportActionBar();
        if (lActionBar != null) lActionBar.setDisplayShowTitleEnabled(false);
        mTvCommomTitle.setText("请假审核");
        //初始化RecyclerView
        mEmptyView = getLayoutInflater().inflate(R.layout.custom_empty_view, (ViewGroup) mRvApplyLeave.getParent(), false);
        mErrorView = getLayoutInflater().inflate(R.layout.custom_error_view, (ViewGroup) mRvApplyLeave.getParent(), false);
        mLoadingView = getLayoutInflater().inflate(R.layout.custom_loading_view, (ViewGroup) mRvApplyLeave.getParent(), false);
        mApplyLeaveAdapter = new ApplyLeaveAdapter();
        mApplyLeaveAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mApplyLeaveAdapter.isFirstOnly(false);
        mRvApplyLeave.setAdapter(mApplyLeaveAdapter);
        mRvApplyLeave.setLayoutManager(new LinearLayoutManager(CustomApplication.getContext()));
    }

    @Override
    public void initListener() {
        mTbCommomToolbar.setNavigationOnClickListener(v -> onBackPressedSupport());
        mEmptyView.setOnClickListener(v -> loadMoreLeaveList(true));
        mErrorView.setOnClickListener(v -> loadMoreLeaveList(true));
        mSrlSmartRefresh.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {//刷新
                mPageIndex = 1;
                loadMoreLeaveList(false);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {//加载
                loadMoreLeaveList(false);
            }
        });
        mRvApplyLeave.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        loadMoreLeaveList(true);
    }


    @NonNull
    public static Intent createIntent(Context context) {
        return new Intent(context, LeaveThingsActivity.class);
    }

    @OnClick({R.id.fab_top})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_top:
                mRvApplyLeave.smoothScrollToPosition(0);
                break;
            default:
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------FIXME 获取"申请请假列表"模块---------------------------------------------------------
    //---------------------------------------------------------↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓------------------------------------------------------------------
    private int mPageIndex = 1;//页码
    private OkHttpEngine.ResponsedCallback mApplyLeaveResponsedCallback;

    /**
     * 加载更多"申请请假列表"
     */
    private void loadMoreLeaveList(boolean loading) {
        if (loading) mApplyLeaveAdapter.setEmptyView(mLoadingView);
        if (mApplyLeaveResponsedCallback == null) {
            mApplyLeaveResponsedCallback = new OkHttpEngine.ResponsedCallback() {
                @Override
                public void onFailure(Call call, Exception e) {
                    mApplyLeaveAdapter.setEmptyView(mErrorView);
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
                    ApplyLeaveBean lApplyLeaveBean = OkHttpEngine.toObject(responsedJsonStr, ApplyLeaveBean.class);
                    if (lApplyLeaveBean != null) {
                        switch (lApplyLeaveBean.getCode()) {
                            case 0:
                                ApplyLeaveBean.PageObjectBean lPageObjectBean = lApplyLeaveBean.getPageObject();
                                if ((lPageObjectBean != null) && (mPageIndex <= lPageObjectBean.getTotalPages())) {
                                    List<ApplyLeaveBean.DataBean> lApplyLeaveList = lApplyLeaveBean.getData();
                                    if ((lApplyLeaveList != null) && !lApplyLeaveList.isEmpty()) {
                                        if (mPageIndex == 1) {
                                            mApplyLeaveAdapter.replaceData(lApplyLeaveList);
                                        } else {
                                            mApplyLeaveAdapter.addData(lApplyLeaveList);
                                        }
                                        mPageIndex++;
                                    } else {
                                        if (mPageIndex == 1) {
                                            mApplyLeaveAdapter.setNewData(null);
                                            mApplyLeaveAdapter.setEmptyView(mEmptyView);
                                        }
                                    }
                                    mSrlSmartRefresh.finishLoadMore(true);
                                } else {
                                    mSrlSmartRefresh.finishLoadMoreWithNoMoreData();
                                }
                                break;
                            default:
                                RxToast.error(MessageFormat.format("获取''申请请假列表''失败! {0}:{1}", lApplyLeaveBean.getCode(), lApplyLeaveBean.getMessage()));
                        }
                    }
                }
            };
        }
        OkHttpEngine.getInstance().sendAsyncPostRequest(LeaveThingsActivity.this, MessageFormat.format("{0}/Vacate/getTeacherVacate", AppConfig.SERVER_ADDRESS), new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).add("pageIndex", String.valueOf(mPageIndex)).add("pageSize", AppConfig.PAGE_SIZE).build(), mApplyLeaveResponsedCallback);
    }

    /**
     * 外层RecyclerView的Adapter
     */
    private class ApplyLeaveAdapter extends BaseQuickAdapter<ApplyLeaveBean.DataBean, BaseViewHolder> {
        ApplyLeaveAdapter() {
            super(R.layout.item_leave_recyclerview);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, ApplyLeaveBean.DataBean publishDataBean) {
            List<ApplyLeaveMultipleItemBean> lApplyLeaveMultipleItemBeanList = new ArrayList<>();
            if (publishDataBean != null) {
                lApplyLeaveMultipleItemBeanList.add(new ApplyLeaveMultipleItemBean(ApplyLeaveMultipleItemBean.APPLY_LEAVE_PUBLISH, publishDataBean));
                List<ApplyLeaveBean.DataBean.ListBean> lReplyBeanList = publishDataBean.getList();
                if ((lReplyBeanList != null) && !lReplyBeanList.isEmpty()) {
                    for (ApplyLeaveBean.DataBean.ListBean replyBean : lReplyBeanList) {
                        lApplyLeaveMultipleItemBeanList.add(new ApplyLeaveMultipleItemBean(ApplyLeaveMultipleItemBean.APPLY_LEAVE_REPLY, replyBean));
                    }
                }
                RecyclerView lApplyLeaveRecyclerView = baseViewHolder.getView(R.id.rv_item_leave);
                lApplyLeaveRecyclerView.setLayoutManager(new LinearLayoutManager(CustomApplication.getContext()));
                MultipleItemQuickAdapter lMultipleItemQuickAdapter = new MultipleItemQuickAdapter(lApplyLeaveMultipleItemBeanList);
                lMultipleItemQuickAdapter.setOnItemChildClickListener((baseQuickAdapter, view, position) -> {
                    ApplyLeaveMultipleItemBean lApplyLeaveMultipleItemBean = (ApplyLeaveMultipleItemBean) baseQuickAdapter.getItem(0);
                    if (lApplyLeaveMultipleItemBean != null) {
                        ApplyLeaveBean.DataBean lPublishDataBean = lApplyLeaveMultipleItemBean.getPublishDataBean();
                        if (lPublishDataBean != null) {
                            mIdToReply = lPublishDataBean.getId();//需要"被审核的意见"的id
                            mApplyLeaveMultipleItemBeanList = lApplyLeaveMultipleItemBeanList;
                            mMultipleItemQuickAdapter = lMultipleItemQuickAdapter;
                            int mType = -500;
                            switch (view.getId()) {
                                case R.id.tv_agree://同意
                                    mType = 2;
                                    break;
                                case R.id.tv_cancel://取消审核
                                    cancelExam(mIdToReply);
                                    break;
                                case R.id.tv_disagree://不同意
                                    mType = 3;
                                    break;
                                case R.id.iv_reply:
                                    showSoftInputMethod();
                                    break;
                                default:
                            }
                            if (mType != -500) examLeave(mType);
                        }
                    }
                });
                lApplyLeaveRecyclerView.setAdapter(lMultipleItemQuickAdapter);
            }
        }

        /**
         * 审核请假
         */
        private void examLeave(int type) {
            OkHttpEngine.getInstance().sendAsyncPostRequest(LeaveThingsActivity.this, MessageFormat.format("{0}/Vacate/addTeacherVacates", AppConfig.SERVER_ADDRESS), new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).add("id", String.valueOf(mIdToReply)).add("type", String.valueOf(type)).build(), new OkHttpEngine.ResponsedCallback() {
                @Override
                public void onFailure(Call call, Exception e) {
                    RxToast.error("服务错误,审核请假失败!");
                }

                @Override
                public void onResponseJson(Call call, String responsedJsonStr) {
                    ReplyLeaveBean lReplyLeaveBean = OkHttpEngine.toObject(responsedJsonStr, ReplyLeaveBean.class);
                    if (lReplyLeaveBean != null) {
                        switch (lReplyLeaveBean.getCode()) {
                            case 0:
                                ApplyLeaveMultipleItemBean lApplyLeaveMultipleItemBean = mApplyLeaveMultipleItemBeanList.get(0);
                                lApplyLeaveMultipleItemBean.getPublishDataBean().setType(type);//2同意 3不同意
                                mMultipleItemQuickAdapter.setData(0, lApplyLeaveMultipleItemBean);
                                break;
                            default:
                                RxToast.error(MessageFormat.format("审核请假失败! {0}:{1}", lReplyLeaveBean.getCode(), lReplyLeaveBean.getMessage()));
                        }
                    }
                }
            });
        }

        /**
         * 取消审核
         *
         * @param idToReply 需要取消审核的请假id
         */
        private void cancelExam(int idToReply) {
            OkHttpEngine.getInstance().sendAsyncPostRequest(LeaveThingsActivity.this, MessageFormat.format("{0}/Vacate/deleteVacates", AppConfig.SERVER_ADDRESS), new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).add("id", String.valueOf(idToReply)).add("type", "1").build(), new OkHttpEngine.ResponsedCallback() {
                @Override
                public void onFailure(Call call, Exception e) {
                    RxToast.error("服务错误,取消审核失败!");
                }

                @Override
                public void onResponseJson(Call call, String responsedJsonStr) {
                    ResponseResultBean lResponseResultBean = OkHttpEngine.toObject(responsedJsonStr, ResponseResultBean.class);
                    if (lResponseResultBean != null) {
                        switch (lResponseResultBean.getCode()) {
                            case 0:
                                ApplyLeaveMultipleItemBean lApplyLeaveMultipleItemBean = mApplyLeaveMultipleItemBeanList.get(0);
                                lApplyLeaveMultipleItemBean.getPublishDataBean().setType(1);//1待批准(待审批) 2同意 3不同意
                                mMultipleItemQuickAdapter.setData(0, lApplyLeaveMultipleItemBean);
                                break;
                            default:
                                RxToast.error(MessageFormat.format("取消审核失败! {0}:{1}", lResponseResultBean.getCode(), lResponseResultBean.getMessage()));
                        }
                    }
                }
            });
        }
    }

    /**
     * 内层嵌套的RecyclerView的Adapter
     */
    private class MultipleItemQuickAdapter extends BaseMultiItemQuickAdapter<ApplyLeaveMultipleItemBean, BaseViewHolder> {
        MultipleItemQuickAdapter(List<ApplyLeaveMultipleItemBean> applyLeaveMultipleItemBeanList) {
            super(applyLeaveMultipleItemBeanList);
            addItemType(ApplyLeaveMultipleItemBean.APPLY_LEAVE_PUBLISH, R.layout.item_apply_leave);
            addItemType(ApplyLeaveMultipleItemBean.APPLY_LEAVE_REPLY, R.layout.item_reply_leave);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, ApplyLeaveMultipleItemBean applyLeaveMultipleItemBean) {
            switch (baseViewHolder.getItemViewType()) {
                case ApplyLeaveMultipleItemBean.APPLY_LEAVE_PUBLISH:
                    ApplyLeaveBean.DataBean publishDataBean = applyLeaveMultipleItemBean.getPublishDataBean();
                    if (publishDataBean != null) {
                        Glide.with(mContext).load(MessageFormat.format("{0}{1}", AppConfig.SERVER_HOST, publishDataBean.getPhotoUrl())).apply(AppConfig.AVATAR_GLIDE_REQUEST_OPTIONS).into((ImageView) baseViewHolder.getView(R.id.iv_applyer_avatar));
                        int type = publishDataBean.getType();
                        switch (type) {
                            case 1://待批准:
                                type = R.drawable.ic_pending_exam;
                                baseViewHolder.setVisible(R.id.tv_agree, true).setGone(R.id.tv_cancel, false).setVisible(R.id.tv_disagree, true);
                                break;
                            case 2://已同意:
                                type = R.drawable.ic_allow;
                                baseViewHolder.setGone(R.id.tv_agree, false).setVisible(R.id.tv_cancel, true).setGone(R.id.tv_disagree, false);
                                break;
                            case 3://不同意:未批准:
                                type = R.drawable.ic_not_allow;
                                baseViewHolder.setGone(R.id.tv_agree, false).setVisible(R.id.tv_cancel, true).setGone(R.id.tv_disagree, false);
                                break;
                            default:
                        }
                        baseViewHolder.setText(R.id.tv_applyer_name, publishDataBean.getName()).setText(R.id.tv_leave_class_and_time, MessageFormat.format("{0} {1}~{2}", publishDataBean.getReason(), publishDataBean.getStartTime(), publishDataBean.getEndTime())).setText(R.id.tv_leave_digist, publishDataBean.getComment()).setText(R.id.tv_apply_time, MessageFormat.format("提交于 {0}", publishDataBean.getSubmitTime()));
                        Glide.with(mContext).load(type).apply(AppConfig.NORMAL_GLIDE_REQUEST_OPTIONS).into((ImageView) baseViewHolder.getView(R.id.iv_apply_status));
                        baseViewHolder.addOnClickListener(R.id.tv_agree).addOnClickListener(R.id.tv_cancel).addOnClickListener(R.id.tv_disagree).addOnClickListener(R.id.iv_reply);
                    }
                    break;
                case ApplyLeaveMultipleItemBean.APPLY_LEAVE_REPLY:
                    ApplyLeaveBean.DataBean.ListBean replyDataBean = applyLeaveMultipleItemBean.getReplyDataBean();
                    if (replyDataBean != null) {
                        Glide.with(mContext).load(MessageFormat.format("{0}{1}", AppConfig.SERVER_HOST, replyDataBean.getPhotoUrl())).apply(AppConfig.AVATAR_GLIDE_REQUEST_OPTIONS).into((ImageView) baseViewHolder.getView(R.id.iv_replyer_avatar));
                        baseViewHolder.setText(R.id.tv_replyer_name, MessageFormat.format("{0} 回复:", replyDataBean.getName())).setText(R.id.tv_reply_time, MessageFormat.format("回复于 {0}", replyDataBean.getSubmitTime())).setText(R.id.tv_reply_digist, replyDataBean.getComment());
                    }
                    break;
                default:
            }
        }
    }


    //-------------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------FIXME "回复请假申请"模块------------------------------------------------------------
    //---------------------------------------------------------↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓------------------------------------------------------------
    @BindView(R.id.ll_reply)
    LinearLayout mLlReply;
    @BindView(R.id.et_reply_content)
    EditText mEtReplyContent;
    @BindView(R.id.btn_reply)
    Button mBtnReply;
    private int mIdToReply;
    private List<ApplyLeaveMultipleItemBean> mApplyLeaveMultipleItemBeanList;
    private MultipleItemQuickAdapter mMultipleItemQuickAdapter;

    @OnClick({R.id.btn_reply})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_reply:
                replyLeave(mEtReplyContent.getText().toString());
                break;
            default:
        }
    }

    /**
     * 回复请假申请
     *
     * @param replyContent 回复内容
     */
    private void replyLeave(String replyContent) {
        OkHttpEngine.getInstance().sendAsyncPostRequest(LeaveThingsActivity.this, MessageFormat.format("{0}/Vacate/addTeacherVacates", AppConfig.SERVER_ADDRESS), new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).add("id", String.valueOf(mIdToReply)).add("type", "0").add("comment", replyContent).build(), new OkHttpEngine.ResponsedCallback() {
            @Override
            public void onFailure(Call call, Exception e) {
                RxToast.error("服务错误,回复请假失败!");
            }

            @Override
            public void onResponseJson(Call call, String responsedJsonStr) {
                hideSoftInputMethod();
                ReplyLeaveBean lReplyLeaveBean = OkHttpEngine.toObject(responsedJsonStr, ReplyLeaveBean.class);
                if (lReplyLeaveBean != null) {
                    switch (lReplyLeaveBean.getCode()) {
                        case 0:
                            ReplyLeaveBean.DataBean lDataBean = lReplyLeaveBean.getData();
                            if (lDataBean != null) mMultipleItemQuickAdapter.addData(new ApplyLeaveMultipleItemBean(ApplyLeaveMultipleItemBean.APPLY_LEAVE_REPLY, new ApplyLeaveBean.DataBean.ListBean(replyContent, -1, AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_USER_NAME), lDataBean.getPhotoUrl(), RxTimeTool.milliseconds2String(System.currentTimeMillis()))));
                            break;
                        default:
                            RxToast.error(MessageFormat.format("回复请假失败! {0}:{1}", lReplyLeaveBean.getCode(), lReplyLeaveBean.getMessage()));
                    }
                }
            }
        });
    }


    //-------------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------FIXME "显示隐藏键盘"模块------------------------------------------------------------
    //---------------------------------------------------------↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓------------------------------------------------------------
    private void showSoftInputMethod() {
        mLlReply.setVisibility(View.VISIBLE);
        RxKeyboardTool.showSoftInput(LeaveThingsActivity.this, mEtReplyContent);
    }

    private void hideSoftInputMethod() {
        if (mLlReply.getVisibility() == View.VISIBLE) {
            mLlReply.setVisibility(View.GONE);
            RxKeyboardTool.hideSoftInput(LeaveThingsActivity.this);
            mEtReplyContent.setText("");
        }
    }
}
