package com.dexin.eccteacher.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * 多项Item实体
 */
public class MultipleItemBean implements MultiItemEntity {
    public static final int FEEDBACK_PUBLISH = 0;
    public static final int FEEDBACK_REPLY = 1;
    private int mItemType;
    private FeedbackBean.DataBean mPublishDataBean;
    private FeedbackBean.DataBean.ChildrenBean mReplyDataBean;

    public MultipleItemBean(int itemType, FeedbackBean.DataBean publishDataBean) {
        mItemType = itemType;
        mPublishDataBean = publishDataBean;
    }

    public MultipleItemBean(int itemType, FeedbackBean.DataBean.ChildrenBean replyDataBean) {
        mItemType = itemType;
        mReplyDataBean = replyDataBean;
    }

    public FeedbackBean.DataBean getPublishDataBean() {
        return mPublishDataBean;
    }

    public FeedbackBean.DataBean.ChildrenBean getReplyDataBean() {
        return mReplyDataBean;
    }

    @Override
    public int getItemType() {
        return mItemType;
    }
}
