package com.dexin.eccteacher.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dexin.eccteacher.R;
import com.dexin.eccteacher.bean.GradeBean;

import java.util.List;

/**
 * 年级Adapter
 */
public class GradeAdapter extends ArrayAdapter<GradeBean.DataBean> {
    private int itemLayoutResId;

    public GradeAdapter(@NonNull Context context, int resource, @NonNull List<GradeBean.DataBean> dataBeanList) {
        super(context, resource, dataBeanList);
        itemLayoutResId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        GradeBean.DataBean lGradeBean = getItem(position);
        if (lGradeBean != null) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(itemLayoutResId, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.mTvGrade = convertView.findViewById(R.id.tv_item);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.mTvGrade.setText(lGradeBean.getName());
        }
        return convertView;
    }

    class ViewHolder {
        TextView mTvGrade;
    }
}
