package com.dexin.eccteacher.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dexin.eccteacher.R;
import com.dexin.eccteacher.application.AppConfig;
import com.dexin.eccteacher.bean.HomeEntranceBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 首页入口菜单Adapter
 */
public class HomeEntranceAdapter extends RecyclerView.Adapter<HomeEntranceAdapter.ViewHolder> {
    private Context mContext;
    private List<HomeEntranceBean> mHomeEntranceBeanList;
    private OnItemClickedListener mOnItemClickedListener;
    private int mPageIndex;//页数下标
    private int mPageMaxSize;//每页显示最大条目个数

    public HomeEntranceAdapter(List<HomeEntranceBean> homeEntranceBeanList, int pageIndex, int pageMaxSize) {
        mHomeEntranceBeanList = homeEntranceBeanList;
        mPageIndex = pageIndex;
        mPageMaxSize = pageMaxSize;
    }

    /**
     * 设置 Item点击监听器
     *
     * @param onItemClickedListener item被点击的监听器
     */
    public void setOnItemClickedListener(OnItemClickedListener onItemClickedListener) {
        mOnItemClickedListener = onItemClickedListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_home_entrance, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if ((mHomeEntranceBeanList != null) && (mHomeEntranceBeanList.size() > (position + mPageIndex * mPageMaxSize))) {
            HomeEntranceBean lHomeEntranceBean = mHomeEntranceBeanList.get(position + mPageIndex * mPageMaxSize);
            if (lHomeEntranceBean != null) {
                Glide.with(mContext).load(lHomeEntranceBean.getImageId()).apply(AppConfig.NORMAL_GLIDE_REQUEST_OPTIONS).into(holder.mIvEntranceImage);
                holder.mTvEntranceName.setText(lHomeEntranceBean.getName());
                if ((mOnItemClickedListener != null) && ((0 <= (holder.getAdapterPosition() + mPageIndex * mPageMaxSize)) && ((holder.getAdapterPosition() + mPageIndex * mPageMaxSize) < mHomeEntranceBeanList.size()))) {
                    holder.itemView.setOnClickListener(v -> mOnItemClickedListener.onItemClick(holder.itemView, holder.getAdapterPosition() + mPageIndex * mPageMaxSize));
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return (mHomeEntranceBeanList.size() > ((mPageIndex + 1) * mPageMaxSize)) ? mPageMaxSize : (mHomeEntranceBeanList.size() - (mPageIndex * mPageMaxSize));
    }

    @Override
    public long getItemId(int position) {
        return position + mPageIndex * mPageMaxSize;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_entrance_image)
        ImageView mIvEntranceImage;
        @BindView(R.id.tv_entrance_name)
        TextView mTvEntranceName;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
