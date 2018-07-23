package com.dexin.eccteacher.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * 申请请假多项Item实体
 */
public class ApplyLeaveMultipleItemBean implements MultiItemEntity {
    public static final int APPLY_LEAVE_PUBLISH = 0;
    public static final int APPLY_LEAVE_REPLY = 1;
    private int mItemType;
    private ApplyLeaveBean.DataBean mPublishDataBean;
    private ApplyLeaveBean.DataBean.ListBean mReplyDataBean;

    public ApplyLeaveMultipleItemBean(int itemType, ApplyLeaveBean.DataBean publishDataBean) {
        mItemType = itemType;
        mPublishDataBean = publishDataBean;
    }

    public ApplyLeaveMultipleItemBean(int itemType, ApplyLeaveBean.DataBean.ListBean replyDataBean) {
        mItemType = itemType;
        mReplyDataBean = replyDataBean;
    }

    public ApplyLeaveBean.DataBean getPublishDataBean() {
        return mPublishDataBean;
    }

    public ApplyLeaveBean.DataBean.ListBean getReplyDataBean() {
        return mReplyDataBean;
    }

    @Override
    public int getItemType() {
        return mItemType;
    }
}
