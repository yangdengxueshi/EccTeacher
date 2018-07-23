package com.dexin.eccteacher.adapter;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.dexin.eccteacher.R;
import com.dexin.eccteacher.bean.ExamClassBean;

/**
 * 考试类型Adapter
 */
public class ExamClassAdapter extends BaseQuickAdapter<ExamClassBean.DataBean, BaseViewHolder> {
    private int mFocusPosition = -500;

    public ExamClassAdapter() {
        super(R.layout.item_drop_down_menu);
    }

    public void setFocusPosition(int focusPosition) {
        mFocusPosition = focusPosition;
        notifyDataSetChanged();
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, ExamClassBean.DataBean dataBean) {
        TextView lTvExamClassName = baseViewHolder.getView(R.id.tv_item);
        lTvExamClassName.setTextColor((mFocusPosition == baseViewHolder.getAdapterPosition()) ? mContext.getResources().getColor(R.color.register_yellow_red) : mContext.getResources().getColor(R.color.light_black));
        lTvExamClassName.setText(dataBean.getExamName());
    }
}
