package com.dexin.eccteacher.adapter;

import android.text.TextUtils;

import com.allen.library.SuperButton;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dexin.eccteacher.R;
import com.dexin.eccteacher.application.AppConfig;
import com.dexin.eccteacher.bean.DataBean;

/**
 * 联系人Adapter
 */
public class ContAdapter extends BaseQuickAdapter<DataBean, BaseViewHolder> {
    public ContAdapter() {
        super(R.layout.item_cont);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, DataBean dataBean) {
        String teachername = dataBean.getName();
        if (!TextUtils.isEmpty(teachername)) {
            int lColor = AppConfig.genRandomColor();
            SuperButton mSbSurName = baseViewHolder.getView(R.id.sb_surname);
            mSbSurName.setShapeStrokeColor(lColor).setUseShape();
            mSbSurName.setTextColor(lColor);
            mSbSurName.setText(teachername.substring(0, 1));
            baseViewHolder.setText(R.id.tv_cont_name, teachername);
        }
        baseViewHolder.setText(R.id.tv_cont_tel, dataBean.getPhone())
                .addOnClickListener(R.id.iv_cont_chat).addOnClickListener(R.id.iv_cont_dail);
    }
}
