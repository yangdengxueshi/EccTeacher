package com.dexin.eccteacher.custom_view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.dexin.eccteacher.R;
import com.dexin.eccteacher.application.CustomApplication;
import com.dexin.eccteacher.utility.LogUtility;

public class RxAutoImageView extends FrameLayout {
    private static final String TAG = "TAG_RxAutoImageView";
    private ImageView mImageView;
    private Animation mAnimation;

    public RxAutoImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        try {
            LayoutInflater.from(context).inflate(R.layout.customview_auto_image, this);
            mImageView = findViewById(R.id.img_backgroud);
            TypedArray lTypedArray = context.obtainStyledAttributes(attrs, R.styleable.RxAutoImageView);
            int lDefaultResId = -1, lResId = lTypedArray.getResourceId(R.styleable.RxAutoImageView_ImageSrc, lDefaultResId);
            lTypedArray.recycle();
            if (lResId != lDefaultResId) mImageView.setImageResource(lResId);

            mAnimation = AnimationUtils.loadAnimation(CustomApplication.getContext(), R.anim.auto_image);
            mAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mImageView != null) mImageView.clearAnimation();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        } catch (RuntimeException e) {
            LogUtility.e(TAG, "RxAutoImageView: ", e);
        }
    }

    public void startAutoImageView() {
        mImageView.startAnimation(mAnimation);
    }

    /**
     * 释放ImageView的相关资源
     */
    public void releaseAutoImageView() {
        mImageView.clearAnimation();
    }
}
