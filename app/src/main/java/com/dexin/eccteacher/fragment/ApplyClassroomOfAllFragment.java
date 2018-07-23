package com.dexin.eccteacher.fragment;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.dexin.eccteacher.R;
import com.dexin.eccteacher.application.AppConfig;
import com.dexin.eccteacher.application.CustomApplication;
import com.dexin.eccteacher.bean.ApplyClassroomBean;
import com.dexin.eccteacher.bean.MessageEvent;
import com.dexin.eccteacher.bean.ReplyApplyClassroomBean;
import com.dexin.eccteacher.bean.ResponseResultBean;
import com.dexin.eccteacher.utility.OkHttpEngine;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.vondear.rxtool.RxTimeTool;
import com.vondear.rxtool.view.RxToast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.FormBody;

/**
 * 申请教室 所有列表 Fragment
 */
public class ApplyClassroomOfAllFragment extends BaseFragment {
    @BindView(R.id.srl_smart_refresh)
    SmartRefreshLayout mSrlSmartRefresh;
    @BindView(R.id.rv_apply_classroom_of_all)
    RecyclerView mRvApplyClassroomOfAll;
    private View mEmptyView, mErrorView, mLoadingView;
    private ApplyClassroomAdapter mApplyClassroomAdapter;

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        mSrlSmartRefresh.finishRefresh(true);
        mSrlSmartRefresh.finishLoadMore(true);
    }

    @NonNull
    public static ApplyClassroomOfAllFragment newInstance() {
        return new ApplyClassroomOfAllFragment();
    }

    @Override
    protected int getLayoutResourceID() {
        return R.layout.fragment_apply_classroom_of_all;
    }

    @Override
    protected void initView() {
        setSwipeBackEnable(false);
        //初始化RecyclerView
        mEmptyView = getLayoutInflater().inflate(R.layout.custom_empty_view, (ViewGroup) mRvApplyClassroomOfAll.getParent(), false);
        mErrorView = getLayoutInflater().inflate(R.layout.custom_error_view, (ViewGroup) mRvApplyClassroomOfAll.getParent(), false);
        mLoadingView = getLayoutInflater().inflate(R.layout.custom_loading_view, (ViewGroup) mRvApplyClassroomOfAll.getParent(), false);
        mApplyClassroomAdapter = new ApplyClassroomAdapter();
        mApplyClassroomAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        mApplyClassroomAdapter.isFirstOnly(false);
        mRvApplyClassroomOfAll.setAdapter(mApplyClassroomAdapter);
        mRvApplyClassroomOfAll.setLayoutManager(new LinearLayoutManager(CustomApplication.getContext()));
    }

    @Override
    protected void initListener() {
        mEmptyView.setOnClickListener(v -> loadMoreApplyClassroomList(true));
        mErrorView.setOnClickListener(v -> loadMoreApplyClassroomList(true));
        mSrlSmartRefresh.setOnLoadMoreListener(refreshLayout -> loadMoreApplyClassroomList(false));
        mRvApplyClassroomOfAll.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
    protected void initData() {
        loadMoreApplyClassroomList(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshEvent(MessageEvent messageEvent) {
        if (!isSupportVisible()) return;
        switch (messageEvent.getMessage()) {
            case MessageEvent.EVENT_REFRESH_CLASSROOM:
                mPageIndex = 1;
                loadMoreApplyClassroomList(false);
                break;
            case MessageEvent.EVENT_TOP:
                mRvApplyClassroomOfAll.smoothScrollToPosition(0);
                break;
            default:
        }
    }


    //-------------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------FIXME 获取"所有教室申请"模块---------------------------------------------------------
    //---------------------------------------------------------↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓---------------------------------------------------------
    private int mPageIndex = 1;//页码
    private OkHttpEngine.ResponsedCallback mApplyClassroomResponsedCallback;

    private void loadMoreApplyClassroomList(boolean loading) {
        if (loading) mApplyClassroomAdapter.setEmptyView(mLoadingView);
        if (mApplyClassroomResponsedCallback == null) {
            mApplyClassroomResponsedCallback = new OkHttpEngine.ResponsedCallback() {
                @Override
                public void onFailure(Call call, Exception e) {
                    mApplyClassroomAdapter.setEmptyView(mErrorView);
                    EventBus.getDefault().post(new MessageEvent(MessageEvent.EVENT_FINISH_REFRESH));//发送刷新失败事件给 ApplyClassroomActivity
                    mSrlSmartRefresh.finishLoadMore(false);//加载失败
                    mSrlSmartRefresh.finishRefresh(false);//刷新失败
                    mSrlSmartRefresh.setNoMoreData(false);//恢复上拉状态
                }

                @Override
                public void onResponseJson(Call call, String responsedJsonStr) {
                    if (mPageIndex == 1) {//刷新成功  或   首次加载成功
                        EventBus.getDefault().post(new MessageEvent(MessageEvent.EVENT_FINISH_REFRESH));//发送刷新成功事件给 ApplyClassroomActivity
                        mSrlSmartRefresh.finishRefresh(true);
                        mSrlSmartRefresh.setNoMoreData(false);//恢复上拉状态
                    }
                    ApplyClassroomBean lApplyClassroomBean = OkHttpEngine.toObject(responsedJsonStr, ApplyClassroomBean.class);
                    if (lApplyClassroomBean != null) {
                        switch (lApplyClassroomBean.getCode()) {
                            case 0://成功:
                                ApplyClassroomBean.PageObjectBean lPageBean = lApplyClassroomBean.getPageObject();
                                if ((lPageBean != null) && (mPageIndex <= lPageBean.getTotalPages())) {
                                    List<ApplyClassroomBean.DataBean> lApplyClassroomBeanList = lApplyClassroomBean.getData();
                                    if ((lApplyClassroomBeanList != null) && !lApplyClassroomBeanList.isEmpty()) {
                                        if (mPageIndex == 1) {
                                            mApplyClassroomAdapter.replaceData(lApplyClassroomBeanList);
                                        } else {
                                            mApplyClassroomAdapter.addData(lApplyClassroomBeanList);
                                        }
                                        mPageIndex++;
                                    } else {
                                        if (mPageIndex == 1) {
                                            mApplyClassroomAdapter.setNewData(null);
                                            mApplyClassroomAdapter.setEmptyView(mEmptyView);
                                        }
                                    }
                                    mSrlSmartRefresh.finishLoadMore(true);
                                } else {
                                    mSrlSmartRefresh.finishLoadMoreWithNoMoreData();
                                }
                                break;
                            default:
                                RxToast.error(MessageFormat.format("获取全校教室申请列表失败! {0}:{1}", lApplyClassroomBean.getCode(), lApplyClassroomBean.getMessage()));
                        }
                    }
                }
            };
        }
        OkHttpEngine.getInstance().sendAsyncPostRequest(ApplyClassroomOfAllFragment.this, MessageFormat.format("{0}/applyClassRoom/getApplyClassRoom", AppConfig.SERVER_ADDRESS), new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).add("pageIndex", String.valueOf(mPageIndex)).add("pageSize", AppConfig.PAGE_SIZE).build(), mApplyClassroomResponsedCallback);
    }

    /**
     * 外层RecyclerView的Adapter
     */
    private class ApplyClassroomAdapter extends BaseQuickAdapter<ApplyClassroomBean.DataBean, BaseViewHolder> {
        ApplyClassroomAdapter() {
            super(R.layout.item_all_apply_classroom_recyclerview);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, ApplyClassroomBean.DataBean publishDataBean) {
            List<AllApplyClassroomMultipleItemBean> lAllApplyClassroomMultipleItemBeanList = new ArrayList<>();
            if (publishDataBean != null) {
                lAllApplyClassroomMultipleItemBeanList.add(new AllApplyClassroomMultipleItemBean(AllApplyClassroomMultipleItemBean.APPLY_CLASSROOM_PUBLISH, publishDataBean));
                List<ApplyClassroomBean.DataBean.ListBean> lReplyBeanList = publishDataBean.getList();
                if ((lReplyBeanList != null) && !lReplyBeanList.isEmpty()) {
                    for (ApplyClassroomBean.DataBean.ListBean replyBean : lReplyBeanList) {
                        lAllApplyClassroomMultipleItemBeanList.add(new AllApplyClassroomMultipleItemBean(AllApplyClassroomMultipleItemBean.APPLY_CLASSROOM_REPLY, replyBean));
                    }
                }
                RecyclerView lApplyClassroomRecyclerView = baseViewHolder.getView(R.id.rv_item_all_apply_classroom);
                lApplyClassroomRecyclerView.setLayoutManager(new LinearLayoutManager(CustomApplication.getContext()));
                MultipleItemQuickAdapter lMultipleItemQuickAdapter = new MultipleItemQuickAdapter(lAllApplyClassroomMultipleItemBeanList);
                lMultipleItemQuickAdapter.setOnItemChildClickListener((baseQuickAdapter, view, position) -> {
                    AllApplyClassroomMultipleItemBean lAllApplyClassroomMultipleItemBean = (AllApplyClassroomMultipleItemBean) baseQuickAdapter.getItem(0);
                    if (lAllApplyClassroomMultipleItemBean != null) {
                        ApplyClassroomBean.DataBean lPublishDataBean = lAllApplyClassroomMultipleItemBean.getPublishDataBean();
                        if (lPublishDataBean != null) {
                            mIdToReply = lPublishDataBean.getId();
                            mAllApplyClassroomMultipleItemBeanList = lAllApplyClassroomMultipleItemBeanList;
                            mMultipleItemQuickAdapter = lMultipleItemQuickAdapter;
                            int mType = -500;
                            switch (view.getId()) {
                                case R.id.tv_agree://同意:
                                    mType = 1;
                                    break;
                                case R.id.tv_cancel://取消审核
                                    mType = 3;
                                    break;
                                case R.id.tv_disagree://不同意
                                    mType = 2;
                                    break;
                                case R.id.iv_reply:
                                    showSoftInputMethod();
                                    break;
                                default:
                            }
                            if (mType != -500) examApplyClassroom(mType);
                        }
                    }
                });
                lApplyClassroomRecyclerView.setAdapter(lMultipleItemQuickAdapter);
            }
        }

        /**
         * 审核教室申请
         *
         * @param type 审核类型(同意,不同意,取消审核)
         */
        private void examApplyClassroom(int type) {
            OkHttpEngine.getInstance().sendAsyncPostRequest(ApplyClassroomOfAllFragment.this, MessageFormat.format("{0}{1}", AppConfig.SERVER_ADDRESS, type == 3 ? "/applyClassRoom/teacherDeleteApplyClassRoom" : "/applyClassRoom/addApplyClassRoom"), new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).add("id", String.valueOf(mIdToReply)).add("type", String.valueOf(type)).build(), new OkHttpEngine.ResponsedCallback() {
                @Override
                public void onFailure(Call call, Exception e) {
                    RxToast.error("服务错误,操作失败!");
                }

                @Override
                public void onResponseJson(Call call, String responsedJsonStr) {
                    ResponseResultBean lResponseResultBean = OkHttpEngine.toObject(responsedJsonStr, ResponseResultBean.class);
                    if (lResponseResultBean != null) {
                        switch (lResponseResultBean.getCode()) {
                            case 0://成功
                                AllApplyClassroomMultipleItemBean lAllApplyClassroomMultipleItemBean = mAllApplyClassroomMultipleItemBeanList.get(0);
                                lAllApplyClassroomMultipleItemBean.getPublishDataBean().setType(type);//1同意 2不同意 3待审批
                                mMultipleItemQuickAdapter.setData(0, lAllApplyClassroomMultipleItemBean);
                                break;
                            default:
                                RxToast.error(MessageFormat.format("操作失败! {0}:{1}", lResponseResultBean.getCode(), lResponseResultBean.getMessage()));
                        }
                    }
                }
            });
        }
    }

    /**
     * 全校申请教室多项Item实体
     */
    private class AllApplyClassroomMultipleItemBean implements MultiItemEntity {
        public static final int APPLY_CLASSROOM_PUBLISH = 0;
        public static final int APPLY_CLASSROOM_REPLY = 1;
        private int mItemType;
        private ApplyClassroomBean.DataBean mPublishDataBean;
        private ApplyClassroomBean.DataBean.ListBean mReplyDataBean;

        AllApplyClassroomMultipleItemBean(int itemType, ApplyClassroomBean.DataBean publishDataBean) {
            mItemType = itemType;
            mPublishDataBean = publishDataBean;
        }

        AllApplyClassroomMultipleItemBean(int itemType, ApplyClassroomBean.DataBean.ListBean replyDataBean) {
            mItemType = itemType;
            mReplyDataBean = replyDataBean;
        }

        public ApplyClassroomBean.DataBean getPublishDataBean() {
            return mPublishDataBean;
        }

        public ApplyClassroomBean.DataBean.ListBean getReplyDataBean() {
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
    private class MultipleItemQuickAdapter extends BaseMultiItemQuickAdapter<AllApplyClassroomMultipleItemBean, BaseViewHolder> {
        MultipleItemQuickAdapter(List<AllApplyClassroomMultipleItemBean> allApplyClassroomMultipleItemBeanList) {
            super(allApplyClassroomMultipleItemBeanList);
            addItemType(AllApplyClassroomMultipleItemBean.APPLY_CLASSROOM_PUBLISH, R.layout.item_apply_all_classroom);
            addItemType(AllApplyClassroomMultipleItemBean.APPLY_CLASSROOM_REPLY, R.layout.item_reply_all_classroom);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, AllApplyClassroomMultipleItemBean allApplyClassroomMultipleItemBean) {
            switch (allApplyClassroomMultipleItemBean.getItemType()) {
                case AllApplyClassroomMultipleItemBean.APPLY_CLASSROOM_PUBLISH:
                    ApplyClassroomBean.DataBean publishDataBean = allApplyClassroomMultipleItemBean.getPublishDataBean();
                    if (publishDataBean != null) {
                        Glide.with(mContext).load(MessageFormat.format("{0}{1}", AppConfig.SERVER_HOST, publishDataBean.getPhotoUrl()).replaceAll("\\\\", "/")).apply(AppConfig.AVATAR_GLIDE_REQUEST_OPTIONS).into((ImageView) baseViewHolder.getView(R.id.iv_applyer_avatar));
                        int type = publishDataBean.getType();
                        switch (type) {
                            case 1://已同意:
                                type = R.drawable.ic_allow;
                                baseViewHolder.setGone(R.id.tv_agree, false);
                                baseViewHolder.setVisible(R.id.tv_cancel, true);
                                baseViewHolder.setGone(R.id.tv_disagree, false);
                                break;
                            case 2://未同意:
                                type = R.drawable.ic_not_allow;
                                baseViewHolder.setGone(R.id.tv_agree, false);
                                baseViewHolder.setVisible(R.id.tv_cancel, true);
                                baseViewHolder.setGone(R.id.tv_disagree, false);
                                break;
                            case 3://待批准:
                                type = R.drawable.ic_pending_exam;
                                baseViewHolder.setVisible(R.id.tv_agree, true);
                                baseViewHolder.setGone(R.id.tv_cancel, false);
                                baseViewHolder.setVisible(R.id.tv_disagree, true);
                                break;
                            default:
                        }
                        baseViewHolder.setText(R.id.tv_applyer_name, publishDataBean.getName()).setText(R.id.tv_apply_place_and_time, MessageFormat.format("{0} {1}~{2}", publishDataBean.getClassRoomName(), RxTimeTool.milliseconds2String(publishDataBean.getStartTime()), RxTimeTool.milliseconds2String(publishDataBean.getEndTime()))).setText(R.id.tv_apply_digist, publishDataBean.getMark()).setText(R.id.tv_submit_time, MessageFormat.format("提交于 {0}", RxTimeTool.milliseconds2String(publishDataBean.getCreaterTime())));
                        Glide.with(mContext).load(type).apply(AppConfig.NORMAL_GLIDE_REQUEST_OPTIONS).into((ImageView) baseViewHolder.getView(R.id.iv_apply_status));
                        baseViewHolder.addOnClickListener(R.id.tv_agree).addOnClickListener(R.id.tv_cancel).addOnClickListener(R.id.tv_disagree).addOnClickListener(R.id.iv_reply);
                    }
                    break;
                case AllApplyClassroomMultipleItemBean.APPLY_CLASSROOM_REPLY:
                    ApplyClassroomBean.DataBean.ListBean replyDataBean = allApplyClassroomMultipleItemBean.getReplyDataBean();
                    if (replyDataBean != null) {
                        Glide.with(mContext).load(MessageFormat.format("{0}{1}", AppConfig.SERVER_HOST, replyDataBean.getPhotoUrl()).replaceAll("\\\\", "/")).apply(AppConfig.AVATAR_GLIDE_REQUEST_OPTIONS).into((ImageView) baseViewHolder.getView(R.id.iv_replyer_avatar));
                        baseViewHolder.setText(R.id.tv_replyer_name, MessageFormat.format("{0} 回复:", replyDataBean.getName())).setText(R.id.tv_reply_time, MessageFormat.format("回复于 {0}", RxTimeTool.milliseconds2String(replyDataBean.getCreaterTime()))).setText(R.id.tv_reply_digist, replyDataBean.getMark());
                    }
                    break;
                default:
            }
        }
    }


    //-------------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------FIXME "回复教室申请"模块------------------------------------------------------------
    //---------------------------------------------------------↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓------------------------------------------------------------
    @BindView(R.id.ll_reply)
    LinearLayout mLlReply;
    @BindView(R.id.et_reply_content)
    EditText mEtReplyContent;
    @BindView(R.id.btn_reply)
    Button mBtnReply;
    private List<AllApplyClassroomMultipleItemBean> mAllApplyClassroomMultipleItemBeanList;
    private MultipleItemQuickAdapter mMultipleItemQuickAdapter;
    private int mIdToReply;

    @OnClick({R.id.btn_reply})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_reply:
                replyApplyClassroom(mEtReplyContent.getText().toString());
                break;
            default:
        }
    }

    /**
     * 回复申请教室
     *
     * @param replyContent 回复内容
     */
    private void replyApplyClassroom(String replyContent) {
        OkHttpEngine.getInstance().sendAsyncPostRequest(ApplyClassroomOfAllFragment.this, MessageFormat.format("{0}/applyClassRoom/addApplyClassRoom", AppConfig.SERVER_ADDRESS), new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).add("mark", String.valueOf(replyContent)).add("type", "0").add("parentId", String.valueOf(mIdToReply)).build(), new OkHttpEngine.ResponsedCallback() {
            @Override
            public void onFailure(Call call, Exception e) {
                RxToast.error("服务错误,回复申请教室失败!");
            }

            @Override
            public void onResponseJson(Call call, String responsedJsonStr) {
                hideSoftInputMethod();
                ReplyApplyClassroomBean lReplyApplyClassroomBean = OkHttpEngine.toObject(responsedJsonStr, ReplyApplyClassroomBean.class);
                if (lReplyApplyClassroomBean != null) {
                    switch (lReplyApplyClassroomBean.getCode()) {
                        case 0:
                            ReplyApplyClassroomBean.DataBean lDataBean = lReplyApplyClassroomBean.getData();
                            if (lDataBean != null) mMultipleItemQuickAdapter.addData(new AllApplyClassroomMultipleItemBean(AllApplyClassroomMultipleItemBean.APPLY_CLASSROOM_REPLY, new ApplyClassroomBean.DataBean.ListBean(lDataBean.getCreaterTime(), -1, replyContent, lDataBean.getName(), lDataBean.getPhotoUrl())));
                            break;
                        default:
                            RxToast.error(MessageFormat.format("回复申请教室失败! {0}:{1}", lReplyApplyClassroomBean.getCode(), lReplyApplyClassroomBean.getMessage()));
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
        showSoftInput(mEtReplyContent);
    }

    private void hideSoftInputMethod() {
        if (mLlReply.getVisibility() == View.VISIBLE) {
            mLlReply.setVisibility(View.GONE);
            hideSoftInput();
            mEtReplyContent.setText("");
        }
    }
}
