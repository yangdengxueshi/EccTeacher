package com.dexin.eccteacher.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dexin.eccteacher.R;
import com.dexin.eccteacher.bean.ClassBean;

import java.util.List;

/**
 * 班级Adapter
 */
public class ClassAdapter extends ArrayAdapter<ClassBean.DataBean> {
    private int itemLayoutResId;

    public ClassAdapter(@NonNull Context context, int resource, @NonNull List<ClassBean.DataBean> dataBeanList) {
        super(context, resource, dataBeanList);
        itemLayoutResId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ClassBean.DataBean lGradeBean = getItem(position);
        if (lGradeBean != null) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(itemLayoutResId, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.mTvClass = convertView.findViewById(R.id.tv_item);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.mTvClass.setText(lGradeBean.getName());
        }
        return convertView;
    }

    class ViewHolder {
        TextView mTvClass;
    }
}
