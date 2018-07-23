package com.dexin.eccteacher.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.dexin.eccteacher.R;
import com.dexin.eccteacher.application.AppConfig;
import com.dexin.eccteacher.bean.TeacherInfoBean;
import com.dexin.eccteacher.utility.OkHttpEngine;
import com.github.chrisbanes.photoview.PhotoView;
import com.gyf.barlibrary.BarHide;
import com.gyf.barlibrary.ImmersionBar;

import java.io.File;
import java.text.MessageFormat;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.FormBody;

public class ZoomPictureActivity extends BaseActivity {
    @BindView(R.id.pv_zoom)
    PhotoView mPvZoom;

    @Override
    protected int getLayoutResourceID() {
        return R.layout.activity_zoom_picture;
    }

    @Override
    protected void initImmersionBar() {
        mImmersionBar = ImmersionBar.with(this).hideBar(BarHide.FLAG_HIDE_BAR);
        mImmersionBar.init();
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initListener() {
    }

    @Override
    protected void initData() {
        File lCacheTeacherAvatartFile;
        if ((lCacheTeacherAvatartFile = new File(AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_USER_AVATAR))).exists()) {
            Glide.with(ZoomPictureActivity.this).load(lCacheTeacherAvatartFile).apply(AppConfig.CIRCLE_GLIDE_REQUEST_OPTIONS).into(mPvZoom);
        } else {
            OkHttpEngine.getInstance().sendAsyncPostRequest(ZoomPictureActivity.this, MessageFormat.format("{0}/teacherInfo/getTeacherInfo", AppConfig.SERVER_ADDRESS), new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).build(), new OkHttpEngine.ResponsedCallback() {
                @Override
                public void onFailure(Call call, Exception e) {
                }

                @Override
                public void onResponseJson(Call call, String responsedJsonStr) {
                    TeacherInfoBean lTeacherInfoBean = OkHttpEngine.toObject(responsedJsonStr, TeacherInfoBean.class);
                    if (lTeacherInfoBean != null) {
                        switch (lTeacherInfoBean.getCode()) {
                            case 0://成功:
                                TeacherInfoBean.DataBean lTeacherInfoBeanData = lTeacherInfoBean.getData();
                                if (lTeacherInfoBeanData != null)
                                    Glide.with(ZoomPictureActivity.this).load(MessageFormat.format("{0}{1}", AppConfig.SERVER_HOST, lTeacherInfoBeanData.getPhotoPath()).replaceAll("\\\\", "/")).apply(AppConfig.NORMAL_GLIDE_REQUEST_OPTIONS).into(mPvZoom);
                                break;
                            default:
                        }
                    }
                }
            });
        }
    }

    @NonNull
    public static Intent createIntent(Context context) {
        return new Intent(context, ZoomPictureActivity.class);
    }

    @OnClick(R.id.pv_zoom)
    public void onClick() {
        ActivityCompat.finishAfterTransition(ZoomPictureActivity.this);
    }
}
