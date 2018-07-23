package com.dexin.eccteacher.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.classic.common.MultipleStatusView;
import com.dexin.eccteacher.R;
import com.dexin.eccteacher.application.AppConfig;
import com.dexin.eccteacher.bean.ClassStyleDetailBean;
import com.dexin.eccteacher.utility.OkHttpEngine;
import com.github.chrisbanes.photoview.PhotoView;
import com.vondear.rxtool.view.RxToast;
import com.zhouwei.mzbanner.MZBannerView;
import com.zhouwei.mzbanner.holder.MZHolderCreator;
import com.zhouwei.mzbanner.holder.MZViewHolder;

import java.text.MessageFormat;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.FormBody;

/**
 * 班级风采详情 Activity
 */
public class ClassStyleDetailActivity extends BaseActivity {
    @BindView(R.id.tb_commom_toolbar)
    Toolbar mTbCommomToolbar;
    @BindView(R.id.tv_commom_title)
    TextView mTvCommomTitle;
    @BindView(R.id.refresh_multiple_status_view)
    MultipleStatusView mRefreshMultipleStatusView;
    @BindView(R.id.mzbv_class_style_detail)
    MZBannerView<ClassStyleDetailBean.DataBean> mMzbvClassStyleDetail;
    @BindView(R.id.tv_class_style_detail_index)
    TextView mTvClassStyleDetailIndex;
    @BindView(R.id.sv_scroll)
    ScrollView mSvScroll;
    @BindView(R.id.tv_class_style_detail_content)
    TextView mTvClassStyleDetailContent;

    public static Intent createIntent(Context context, int classStyleId) {
        Intent intent = new Intent(context, ClassStyleDetailActivity.class);
        intent.putExtra("classStyleId", classStyleId);
        return intent;
    }

    @Override
    protected int getLayoutResourceID() {
        return R.layout.activity_class_style_detail;
    }

    @Override
    public void initView() {
        //初始化Toolbar
        setSupportActionBar(mTbCommomToolbar);
        ActionBar lActionBar = getSupportActionBar();
        if (lActionBar != null) lActionBar.setDisplayShowTitleEnabled(false);
        mTvCommomTitle.setText("班级风采");
        mRefreshMultipleStatusView.showLoading();//初始化RefreshMultipleStatusView
        mMzbvClassStyleDetail.setIndicatorVisible(false);
    }

    @Override
    public void initListener() {
        mTbCommomToolbar.setNavigationOnClickListener(v -> ActivityCompat.finishAfterTransition(ClassStyleDetailActivity.this));
        mRefreshMultipleStatusView.setOnRetryClickListener(v -> loadClassStyleDetail(getIntent().getIntExtra("classStyleId", -1)));
    }

    @Override
    public void initData() {
        loadClassStyleDetail(getIntent().getIntExtra("classStyleId", -1));
    }


    /**
     * 加载"班级风采详情"
     */
    private void loadClassStyleDetail(int classStyleId) {
        if (classStyleId < 0) {
            RxToast.error("班级风采详情ID有误!");
            finish();
            return;
        }
        OkHttpEngine.getInstance().sendAsyncPostRequest(ClassStyleDetailActivity.this, MessageFormat.format("{0}/ClassmienPhotos/getClassmienPhotosAll", AppConfig.SERVER_ADDRESS), new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).add("classmienId", String.valueOf(classStyleId)).build(), new OkHttpEngine.ResponsedCallback() {
            private MZHolderCreator mMZHolderCreator = MZBannerViewHolder::new;

            @Override
            public void onFailure(Call call, Exception e) {
                mRefreshMultipleStatusView.showError();
            }

            @Override
            public void onResponseJson(Call call, String responsedJsonStr) {
                ClassStyleDetailBean lClassStyleDetailBean = OkHttpEngine.toObject(responsedJsonStr, ClassStyleDetailBean.class);
                if (lClassStyleDetailBean != null) {
                    switch (lClassStyleDetailBean.getCode()) {
                        case 0://成功
                            List<ClassStyleDetailBean.DataBean> lDataBeanList = lClassStyleDetailBean.getData();
                            if ((lDataBeanList != null) && !lDataBeanList.isEmpty()) {
                                mRefreshMultipleStatusView.showContent();
                                mMzbvClassStyleDetail.setPages(lDataBeanList, mMZHolderCreator);
                                ViewPager.OnPageChangeListener lOnPageChangeListener = new ViewPager.OnPageChangeListener() {
                                    @Override
                                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                                    }

                                    @Override
                                    public void onPageSelected(int position) {
                                        ClassStyleDetailBean.DataBean lDataBean = lDataBeanList.get(position % lDataBeanList.size());
                                        if (lDataBean != null) {
                                            mTvClassStyleDetailIndex.setText(MessageFormat.format("{0}/{1}", position + 1, lDataBeanList.size()));
                                            mSvScroll.fullScroll(View.FOCUS_UP);
                                            mSvScroll.smoothScrollTo(0, 0);
                                            mTvClassStyleDetailContent.setText(lDataBean.getContent());
                                        }
                                    }

                                    @Override
                                    public void onPageScrollStateChanged(int state) {
                                    }
                                };
                                lOnPageChangeListener.onPageSelected(0);
                                mMzbvClassStyleDetail.addPageChangeListener(lOnPageChangeListener);
                            } else {
                                mRefreshMultipleStatusView.showEmpty();
                            }
                            break;
                        default:
                            RxToast.error(MessageFormat.format("获取'班级风采详情'失败! {0}:{1}", lClassStyleDetailBean.getCode(), lClassStyleDetailBean.getMessage()));
                    }
                }
            }

            class MZBannerViewHolder implements MZViewHolder<ClassStyleDetailBean.DataBean> {
                private PhotoView mPhotoView;

                @Override
                public View createView(Context context) {
                    mPhotoView = new PhotoView(context);
                    return mPhotoView;
                }

                @Override
                public void onBind(Context context, int position, ClassStyleDetailBean.DataBean dataBean) {
                    Glide.with(context).load(MessageFormat.format("{0}{1}", AppConfig.SERVER_HOST, dataBean.getPhotoUrl())).apply(AppConfig.NORMAL_GLIDE_REQUEST_OPTIONS).into(mPhotoView);
                }
            }
        });
    }
}
