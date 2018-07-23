package com.dexin.eccteacher.custom_view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import org.jetbrains.annotations.NotNull;

/**
 * 自定义PopupWindow       TODO RecyclerView的话，必须采用回调的方式才能设置的上 “背景变暗”
 */
public final class CustomPopupWindow {
    private final Context mContext;                                   //上下文对象
    private final View contentView;                                   //内容View
    private final PopupWindow mPopupWindow;                           //PopupWindow对象 mPopupWindow

    private CustomPopupWindow(@NotNull Builder builder) {
        mContext = builder.mContext;
        contentView = LayoutInflater.from(mContext).inflate(builder.mContentViewId, null, false);
        mPopupWindow = new PopupWindow(contentView, builder.mWidth, builder.mHeight, builder.mFocus);

        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopupWindow.setOutsideTouchable(builder.mOutsideCancelable);

        mPopupWindow.setAnimationStyle(builder.mAnimStyle);
        mPopupWindow.setOnDismissListener(() -> builder.mOnDismissListener.onDismiss());
    }

    /**
     * 判断自定义PopupWindow正在显示
     *
     * @return 正在显示与否
     */
    public boolean isShowing() {
        return mPopupWindow.isShowing();
    }

    /**
     * 设置背景Drawable
     *
     * @param drawable Drawable对象
     */
    public void setBackgroundDrawable(Drawable drawable) {
        mPopupWindow.setBackgroundDrawable(drawable);
    }

    /**
     * 自定义PopupWindow 消失
     */
    public void dismiss() {
        mPopupWindow.dismiss();
    }

    /**
     * 设置 软输入模式
     *
     * @param mode 输入模式
     */
    public void setSoftInputMode(int mode) {
        mPopupWindow.setSoftInputMode(mode);
    }

    /**
     * 根据id获取子view
     *
     * @param subViewId 子View的id
     * @return 子View（可能为null）
     */
    public View findViewById(int subViewId) {
        return contentView.findViewById(subViewId);
    }

    /**
     * 根据父布局，显示位置
     *
     * @param parentViewId 父View的id
     * @param gravity      引力
     * @param xOffset      x轴偏移量
     * @param yOffset      y轴偏移量
     */
    public void showAtLocation(int parentViewId, int gravity, int xOffset, int yOffset) {
        View parentView = LayoutInflater.from(mContext).inflate(parentViewId, null, false);
        mPopupWindow.showAtLocation(parentView, gravity, xOffset, yOffset);
    }

    /**
     * 显示在 目标View 的不同位置
     *
     * @param targetViewId 目标View的id
     * @param gravity      引力
     * @param xOffset      x轴偏移量
     * @param yOffset      y轴偏移量
     */
    public void showAsDropDown(int targetViewId, int gravity, int xOffset, int yOffset) {
        View targetView = LayoutInflater.from(mContext).inflate(targetViewId, null, false);
        mPopupWindow.showAsDropDown(targetView, xOffset, yOffset, gravity);
    }

    /**
     * 显示在 目标View 的不同位置
     *
     * @param targetView 目标View
     * @param gravity    引力
     * @param xOffset    x轴偏移量
     * @param yOffset    y轴偏移量
     */
    public void showAsDropDown(View targetView, int xOffset, int yOffset, int gravity) {
        mPopupWindow.showAsDropDown(targetView, xOffset, yOffset, gravity);
    }

    /**
     * 设置当焦点改变时的监听器
     *
     * @param viewId                view的id
     * @param onFocusChangeListener 当焦点改变时的监听器
     */
    public void setOnFocusChangeListener(int viewId, View.OnFocusChangeListener onFocusChangeListener) {
        View view = findViewById(viewId);
        view.setOnFocusChangeListener(onFocusChangeListener);
    }

    /**
     * 建造者模式 build 类
     */
    public static final class Builder {
        private Context mContext;
        private int mContentViewId;
        private int mWidth = 1080;
        private int mHeight = 1080;
        private boolean mFocus;
        private boolean mOutsideCancelable;
        private int mAnimStyle;
        private OnDismissListener mOnDismissListener = () -> {
        };//自定义的回调接口：当消失时的监听器

        public Builder setContext(Context context) {
            mContext = context;
            return this;
        }

        public Builder setContentView(int contentViewId) {
            mContentViewId = contentViewId;
            return this;
        }

        public Builder setWidth(int width) {
            mWidth = width;
            return this;
        }

        public Builder setHeight(int height) {
            mHeight = height;
            return this;
        }

        public Builder setFocus(boolean focus) {
            mFocus = focus;
            return this;
        }

        public Builder setOutsideCancelable(boolean outsideCancelable) {
            mOutsideCancelable = outsideCancelable;
            return this;
        }

        public Builder setAnimStyle(int animStyle) {
            mAnimStyle = animStyle;
            return this;
        }

        public Builder setOnDismissListener(OnDismissListener onDismissListener) {
            mOnDismissListener = onDismissListener;
            return this;
        }

        @NonNull
        public CustomPopupWindow build() {
            return new CustomPopupWindow(this);
        }

        /**
         * 自定义回调接口 当消失时的监听器
         */
        public interface OnDismissListener {
            void onDismiss();
        }
    }
}
