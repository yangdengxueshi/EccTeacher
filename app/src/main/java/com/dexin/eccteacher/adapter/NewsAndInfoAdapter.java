package com.dexin.eccteacher.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dexin.eccteacher.R;
import com.dexin.eccteacher.application.AppConfig;
import com.dexin.eccteacher.bean.NewsAndInfoBean;

import java.text.MessageFormat;

public class NewsAndInfoAdapter extends BaseQuickAdapter<NewsAndInfoBean.DataBean, BaseViewHolder> {
    public NewsAndInfoAdapter() {
        super(R.layout.item_news_and_info);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, NewsAndInfoBean.DataBean dataBean) {
        Glide.with(mContext).load(MessageFormat.format("{0}{1}", AppConfig.SERVER_HOST, dataBean.getPhotoUrl())).apply(AppConfig.CORNER_GLIDE_REQUEST_OPTIONS).into((ImageView) baseViewHolder.getView(R.id.iv_news_and_info_cover));
        baseViewHolder.setText(R.id.tv_news_and_info_title, dataBean.getHeadline()).setText(R.id.tv_news_and_info_digist, dataBean.getDigest()).setText(R.id.tv_news_and_info_readed, MessageFormat.format(" {0}", dataBean.getReaded())).setText(R.id.tv_news_and_info_time, MessageFormat.format(" {0}", dataBean.getCreateTime()));
    }
}
