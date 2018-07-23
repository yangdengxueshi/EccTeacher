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
import android.view.Menu;
import android.view.MenuItem;
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
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.dexin.eccteacher.R;
import com.dexin.eccteacher.application.AppConfig;
import com.dexin.eccteacher.application.CustomApplication;
import com.dexin.eccteacher.bean.FaultRepairBean;
import com.dexin.eccteacher.bean.ReplyFaultBean;
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

public class FaultRepairActivity extends BaseActivity {
    @BindView(R.id.tb_commom_toolbar)
    Toolbar mTbCommomToolbar;
    @BindView(R.id.tv_commom_title)
    TextView mTvCommomTitle;
    @BindView(R.id.srl_smart_refresh)
    SmartRefreshLayout mSrlSmartRefresh;
    @BindView(R.id.rv_fault_list)
    RecyclerView mRvFault;
    private View mEmptyView, mErrorView, mLoadingView;
    private FaultRepairAdapter mFaultRepairAdapter;

    @Override
    protected int getLayoutResourceID() {
        return R.layout.activity_fault_repair;
    }

    @Override
    public void initView() {
        //初始化Toolbar
        setSupportActionBar(mTbCommomToolbar);
        ActionBar lActionBar = getSupportActionBar();
        if (lActionBar != null) lActionBar.setDisplayShowTitleEnabled(false);
        mTvCommomTitle.setText("故障报修");
        //初始化RecyclerView
        mEmptyView = getLayoutInflater().inflate(R.layout.custom_empty_view, (ViewGroup) mRvFault.getParent(), false);
        mErrorView = getLayoutInflater().inflate(R.layout.custom_error_view, (ViewGroup) mRvFault.getParent(), false);
        mLoadingView = getLayoutInflater().inflate(R.layout.custom_loading_view, (ViewGroup) mRvFault.getParent(), false);
        mFaultRepairAdapter = new FaultRepairAdapter();
        mFaultRepairAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mFaultRepairAdapter.isFirstOnly(false);
        mRvFault.setAdapter(mFaultRepairAdapter);
        mRvFault.setLayoutManager(new LinearLayoutManager(CustomApplication.getContext()));
    }

    @Override
    public void initListener() {
        mTbCommomToolbar.setNavigationOnClickListener(v -> onBackPressedSupport());
        mEmptyView.setOnClickListener(v -> loadMoreFaultList(true));
        mErrorView.setOnClickListener(v -> loadMoreFaultList(true));
        mSrlSmartRefresh.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPageIndex = 1;
                loadMoreFaultList(false);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                loadMoreFaultList(false);
            }
        });
        mRvFault.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        loadMoreFaultList(true);
    }

    @NonNull
    public static Intent createIntent(Context context) {
        return new Intent(context, FaultRepairActivity.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.repair, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_repair:
                startActivity(SubmitFaultActivity.createIntent(CustomApplication.getContext()));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //-------------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------FIXME 获取"故障报修列表"模块---------------------------------------------------------
    //---------------------------------------------------------↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓---------------------------------------------------------
    private int mPageIndex = 1;
    private OkHttpEngine.ResponsedCallback mFaultRepairResponsedCallback;

    /**
     * 加载更多故障列表
     */
    private void loadMoreFaultList(boolean loading) {
        if (loading) mFaultRepairAdapter.setEmptyView(mLoadingView);
        if (mFaultRepairResponsedCallback == null) {
            mFaultRepairResponsedCallback = new OkHttpEngine.ResponsedCallback() {
                @Override
                public void onFailure(Call call, Exception e) {
                    mFaultRepairAdapter.setEmptyView(mErrorView);
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
                    FaultRepairBean lFaultRepairBean = OkHttpEngine.toObject(responsedJsonStr, FaultRepairBean.class);
                    if (lFaultRepairBean != null) {
                        switch (lFaultRepairBean.getCode()) {
                            case 0://成功:
                                FaultRepairBean.PageObjectBean lPageObjectBean = lFaultRepairBean.getPageObject();
                                if ((lPageObjectBean != null) && (mPageIndex <= lPageObjectBean.getTotalPages())) {
                                    List<FaultRepairBean.DataBean> lFaultBeanList = lFaultRepairBean.getData();
                                    if ((lFaultBeanList != null) && !lFaultBeanList.isEmpty()) {
                                        if (mPageIndex == 1) {
                                            mFaultRepairAdapter.replaceData(lFaultBeanList);
                                        } else {
                                            mFaultRepairAdapter.addData(lFaultBeanList);
                                        }
                                        mPageIndex++;
                                    } else {
                                        if (mPageIndex == 1) {
                                            mFaultRepairAdapter.setNewData(null);
                                            mFaultRepairAdapter.setEmptyView(mEmptyView);
                                        }
                                    }
                                    mSrlSmartRefresh.finishLoadMore(true);
                                } else {
                                    mSrlSmartRefresh.finishLoadMoreWithNoMoreData();
                                }
                                break;
                            default:
                                RxToast.error(MessageFormat.format("获取'故障报修'列表失败! {0}:{1}", lFaultRepairBean.getCode(), lFaultRepairBean.getMessage()));
                        }
                    }
                }
            };
        }
        OkHttpEngine.getInstance().sendAsyncPostRequest(FaultRepairActivity.this, MessageFormat.format("{0}/faultMend/getFaultMend", AppConfig.SERVER_ADDRESS), new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).add("pageIndex", String.valueOf(mPageIndex)).add("pageSize", AppConfig.PAGE_SIZE).build(), mFaultRepairResponsedCallback);
    }

    /**
     * 外层RecyclerView的Adapter
     */
    private class FaultRepairAdapter extends BaseQuickAdapter<FaultRepairBean.DataBean, BaseViewHolder> {
        FaultRepairAdapter() {
            super(R.layout.item_fault_repair_recyclerview);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, FaultRepairBean.DataBean publishDataBean) {
            List<FaultRepairMultipleItemBean> lFaultRepairMultipleItemBeanList = new ArrayList<>();
            if (publishDataBean != null) {
                lFaultRepairMultipleItemBeanList.add(new FaultRepairMultipleItemBean(FaultRepairMultipleItemBean.APPLY_LEAVE_PUBLISH, publishDataBean));
                List<FaultRepairBean.DataBean.ListBean> lReplyBeanList = publishDataBean.getList();
                if ((lReplyBeanList != null) && !lReplyBeanList.isEmpty()) {
                    for (FaultRepairBean.DataBean.ListBean replyBean : lReplyBeanList) {
                        lFaultRepairMultipleItemBeanList.add(new FaultRepairMultipleItemBean(FaultRepairMultipleItemBean.APPLY_LEAVE_REPLY, replyBean));
                    }
                }
                RecyclerView lFaultRepairRecyclerView = baseViewHolder.getView(R.id.rv_item_fault_repair);
                lFaultRepairRecyclerView.setLayoutManager(new LinearLayoutManager(CustomApplication.getContext()));
                MultipleItemQuickAdapter lMultipleItemQuickAdapter = new MultipleItemQuickAdapter(lFaultRepairMultipleItemBeanList);
                lMultipleItemQuickAdapter.setOnItemChildClickListener((baseQuickAdapter, view, position) -> {
                    FaultRepairMultipleItemBean lFaultRepairMultipleItemBean = (FaultRepairMultipleItemBean) baseQuickAdapter.getItem(0);
                    if (lFaultRepairMultipleItemBean != null) {
                        FaultRepairBean.DataBean lPublishDataBean = lFaultRepairMultipleItemBean.getPublishDataBean();
                        if (lPublishDataBean != null) {
                            mIdToReply = lPublishDataBean.getId();//需要"被审核的报修"的id
                            mFaultRepairMultipleItemBeanList = lFaultRepairMultipleItemBeanList;
                            mMultipleItemQuickAdapter = lMultipleItemQuickAdapter;
                            int mType = -500;
                            switch (view.getId()) {
                                case R.id.tv_accepted:
                                    mType = 3;//已受理
                                    break;
                                case R.id.tv_handled:
                                    mType = 1;//已处理
                                    break;
                                case R.id.iv_reply:
                                    showSoftInput();
                                    break;
                                default:
                            }
                            if (mType != -500) examFault(mType);
                        }
                    }
                });
                lFaultRepairRecyclerView.setAdapter(lMultipleItemQuickAdapter);
            }
        }

        /**
         * 审核故障
         *
         * @param type 审核类型
         */
        private void examFault(int type) {
            OkHttpEngine.getInstance().sendAsyncPostRequest(FaultRepairActivity.this, MessageFormat.format("{0}/faultMend/addFaultMend", AppConfig.SERVER_ADDRESS), new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).add("id", String.valueOf(mIdToReply)).add("type", String.valueOf(type)).build(), new OkHttpEngine.ResponsedCallback() {
                @Override
                public void onFailure(Call call, Exception e) {
                    RxToast.error("服务错误,审核故障失败!");
                }

                @Override
                public void onResponseJson(Call call, String responsedJsonStr) {
                    ResponseResultBean lResponseResultBean = OkHttpEngine.toObject(responsedJsonStr, ResponseResultBean.class);
                    if (lResponseResultBean != null) {
                        switch (lResponseResultBean.getCode()) {
                            case 0:
                                FaultRepairMultipleItemBean lApplyLeaveMultipleItemBean = mFaultRepairMultipleItemBeanList.get(0);
                                lApplyLeaveMultipleItemBean.getPublishDataBean().setType(type);//1已处理   3已受理
                                mMultipleItemQuickAdapter.setData(0, lApplyLeaveMultipleItemBean);
                                break;
                            default:
                                RxToast.error(MessageFormat.format("审核故障失败! {0}:{1}", lResponseResultBean.getCode(), lResponseResultBean.getMessage()));
                        }
                    }
                }
            });
        }
    }

    private class FaultRepairMultipleItemBean implements MultiItemEntity {
        public static final int APPLY_LEAVE_PUBLISH = 0;
        public static final int APPLY_LEAVE_REPLY = 1;
        private int mItemType;
        private FaultRepairBean.DataBean mPublishDataBean;
        private FaultRepairBean.DataBean.ListBean mReplyDataBean;

        FaultRepairMultipleItemBean(int itemType, FaultRepairBean.DataBean publishDataBean) {
            mItemType = itemType;
            mPublishDataBean = publishDataBean;
        }

        FaultRepairMultipleItemBean(int itemType, FaultRepairBean.DataBean.ListBean replyDataBean) {
            mItemType = itemType;
            mReplyDataBean = replyDataBean;
        }

        public FaultRepairBean.DataBean getPublishDataBean() {
            return mPublishDataBean;
        }

        public FaultRepairBean.DataBean.ListBean getReplyDataBean() {
            return mReplyDataBean;
        }

        @Override
        public int getItemType() {
            return mItemType;
        }
    }

    /**
     * 内层嵌套的RecyclerView的Adapter
     */
    private class MultipleItemQuickAdapter extends BaseMultiItemQuickAdapter<FaultRepairMultipleItemBean, BaseViewHolder> {
        MultipleItemQuickAdapter(List<FaultRepairMultipleItemBean> faultRepairMultipleItemBeanList) {
            super(faultRepairMultipleItemBeanList);
            addItemType(FaultRepairMultipleItemBean.APPLY_LEAVE_PUBLISH, R.layout.item_submit_fault);
            addItemType(FaultRepairMultipleItemBean.APPLY_LEAVE_REPLY, R.layout.item_reply_fault);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, FaultRepairMultipleItemBean faultRepairMultipleItemBean) {
            switch (baseViewHolder.getItemViewType()) {
                case FaultRepairMultipleItemBean.APPLY_LEAVE_PUBLISH:
                    FaultRepairBean.DataBean publishDataBean = faultRepairMultipleItemBean.getPublishDataBean();
                    if (publishDataBean != null) {
                        Glide.with(mContext).load(MessageFormat.format("{0}{1}", AppConfig.SERVER_HOST, publishDataBean.getPhotoUrl())).apply(AppConfig.AVATAR_GLIDE_REQUEST_OPTIONS).into((ImageView) baseViewHolder.getView(R.id.iv_fault_cover));
                        int type = publishDataBean.getType();
                        switch (type) {
                            case 1://已处理
                                type = R.drawable.ic_handled;
                                baseViewHolder.setGone(R.id.tv_accepted, false).setGone(R.id.tv_handled, false);
                                break;
                            case 2://未处理:
                                type = R.drawable.ic_not_handle;
                                baseViewHolder.setVisible(R.id.tv_accepted, true).setGone(R.id.tv_handled, false);
                                break;
                            case 3://已受理:
                                type = R.drawable.ic_accepted;
                                baseViewHolder.setGone(R.id.tv_accepted, false).setVisible(R.id.tv_handled, true);
                                break;
                            default:
                        }
                        baseViewHolder.setText(R.id.tv_publisher_name, publishDataBean.getName()).setText(R.id.tv_fault_class, publishDataBean.getTypeName()).setText(R.id.tv_fault_digist, publishDataBean.getMark()).setText(R.id.tv_publish_time, MessageFormat.format("提交于 {0}", RxTimeTool.milliseconds2String(publishDataBean.getCreaterTime())));
                        Glide.with(mContext).load(type).apply(AppConfig.NORMAL_GLIDE_REQUEST_OPTIONS).into((ImageView) baseViewHolder.getView(R.id.iv_fault_status));
                        baseViewHolder.addOnClickListener(R.id.tv_accepted).addOnClickListener(R.id.tv_handled).addOnClickListener(R.id.iv_reply);
                    }
                    break;
                case FaultRepairMultipleItemBean.APPLY_LEAVE_REPLY:
                    FaultRepairBean.DataBean.ListBean replyDataBean = faultRepairMultipleItemBean.getReplyDataBean();
                    if (replyDataBean != null) {
                        Glide.with(mContext).load(MessageFormat.format("{0}{1}", AppConfig.SERVER_HOST, replyDataBean.getPhotoUrl())).apply(AppConfig.AVATAR_GLIDE_REQUEST_OPTIONS).into((ImageView) baseViewHolder.getView(R.id.iv_replyer_avatar));
                        baseViewHolder.setText(R.id.tv_replyer_name, MessageFormat.format("{0} 回复:", replyDataBean.getName())).setText(R.id.tv_reply_time, MessageFormat.format("回复于 {0}", RxTimeTool.milliseconds2String(replyDataBean.getCreaterTime()))).setText(R.id.tv_reply_digist, replyDataBean.getMarkInfo());
                    }
                    break;
                default:
            }
        }
    }


    //-------------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------FIXME "回复故障报修"模块------------------------------------------------------------
    //---------------------------------------------------------↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓------------------------------------------------------------
    @BindView(R.id.ll_reply)
    LinearLayout mLlReply;
    @BindView(R.id.et_reply_content)
    EditText mEtReplyContent;
    @BindView(R.id.btn_reply)
    Button mBtnReply;
    private int mIdToReply;
    private List<FaultRepairMultipleItemBean> mFaultRepairMultipleItemBeanList;
    private MultipleItemQuickAdapter mMultipleItemQuickAdapter;

    @OnClick({R.id.btn_reply, R.id.fab_top})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_reply:
                String lReplyContent = mEtReplyContent.getText().toString();
                replyFault(lReplyContent);
                break;
            case R.id.fab_top:
                mRvFault.smoothScrollToPosition(0);
                break;
            default:
        }
    }

    /**
     * 回复故障报修
     *
     * @param replyContent 回复内容
     */
    private void replyFault(String replyContent) {
        OkHttpEngine.getInstance().sendAsyncPostRequest(FaultRepairActivity.this, MessageFormat.format("{0}/faultMend/addFaultMend", AppConfig.SERVER_ADDRESS), new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).add("parentId", String.valueOf(mIdToReply)).add("type", "0").add("mark", replyContent).build(), new OkHttpEngine.ResponsedCallback() {
            @Override
            public void onFailure(Call call, Exception e) {
                RxToast.error("服务错误,回复报修失败!");
            }

            @Override
            public void onResponseJson(Call call, String responsedJsonStr) {
                hideSoftInputMethod();
                ReplyFaultBean lReplyFaultBean = OkHttpEngine.toObject(responsedJsonStr, ReplyFaultBean.class);
                if (lReplyFaultBean != null) {
                    switch (lReplyFaultBean.getCode()) {
                        case 0:
                            ReplyFaultBean.DataBean lDataBean = lReplyFaultBean.getData();
                            if (lDataBean != null) mMultipleItemQuickAdapter.addData(new FaultRepairMultipleItemBean(FaultRepairMultipleItemBean.APPLY_LEAVE_REPLY, new FaultRepairBean.DataBean.ListBean(lDataBean.getCreaterTime(), lDataBean.getId(), lDataBean.getMarkInfo(), lDataBean.getName(), lDataBean.getPhotoUrl())));
                            break;
                        default:
                            RxToast.error(MessageFormat.format("回复失败! {0}:{1}", lReplyFaultBean.getCode(), lReplyFaultBean.getMessage()));
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
        RxKeyboardTool.showSoftInput(FaultRepairActivity.this, mEtReplyContent);
    }

    private void hideSoftInputMethod() {
        if (mLlReply.getVisibility() == View.VISIBLE) {
            mLlReply.setVisibility(View.GONE);
            RxKeyboardTool.hideSoftInput(FaultRepairActivity.this);
            mEtReplyContent.setText("");
        }
    }
}
