package com.dexin.eccteacher.fragment;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;

import com.allen.library.SuperButton;
import com.allen.library.SuperTextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.classic.common.MultipleStatusView;
import com.dexin.eccteacher.R;
import com.dexin.eccteacher.activity.PersonInfoActivity;
import com.dexin.eccteacher.activity.SettingActivity;
import com.dexin.eccteacher.activity.ZoomPictureActivity;
import com.dexin.eccteacher.application.AppConfig;
import com.dexin.eccteacher.application.CustomApplication;
import com.dexin.eccteacher.bean.MessageEvent;
import com.dexin.eccteacher.bean.TeacherInfoBean;
import com.dexin.eccteacher.utility.LogUtility;
import com.dexin.eccteacher.utility.OkHttpEngine;
import com.github.chrisbanes.photoview.PhotoView;
import com.vondear.rxtool.RxAppTool;
import com.vondear.rxtool.RxNetTool;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.MessageFormat;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.FormBody;

/**
 * 我的Fragment
 */
public class MineFragment extends BaseFragment {
    @BindView(R.id.srl_swipe_refresh)
    SwipeRefreshLayout mSrlSwipeRefresh;
    @BindView(R.id.refresh_multiple_status_view)
    MultipleStatusView mRefreshMultipleStatusView;

    @NonNull
    public static MineFragment newInstance() {
        return new MineFragment();
    }

    @Override
    public void onSupportInvisible() {
        mSrlSwipeRefresh.setRefreshing(false);
        super.onSupportInvisible();
    }

    @Override
    protected int getLayoutResourceID() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void initView() {
        setSwipeBackEnable(false);
        //初始化RefreshMultipleStatusView
        mRefreshMultipleStatusView.showLoading();
        mSrlSwipeRefresh.setColorSchemeResources(R.color.register_yellow_red);
    }

    @Override
    protected void initListener() {
        mRefreshMultipleStatusView.setOnRetryClickListener(v -> {
            mRefreshMultipleStatusView.showLoading();
            loadTeacherInfo();
        });
        mSrlSwipeRefresh.setOnRefreshListener(() -> {
            if (RxNetTool.isAvailable(CustomApplication.getContext())) {
                loadTeacherInfo();
            } else {
                mSrlSwipeRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    protected void initData() {
        loadTeacherInfo();//加载教师信息
    }


    /**
     * 接收到EventBus发布的消息事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveEvent(MessageEvent messageEvent) {
        switch (messageEvent.getMessage()) {
            case MessageEvent.EVENT_LOAD_TEACHER_INFO://        if (!isSupportVisible()) return;    不能跳过
                loadTeacherInfo();//本地成功更换教师信息后在当前Fragment中重新加载教师信息
                break;
            default:
        }
    }


    //-------------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------FIXME 获取教师基本信息--------------------------------------------------------------
    //---------------------------------------------------------↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓--------------------------------------------------------------
    @BindView(R.id.pv_teacher_avatar)
    PhotoView mPvTeacherAvatar;
    @BindView(R.id.sb_teacher_name)
    SuperButton mSbTeacherName;
    @BindView(R.id.stv_setting)
    SuperTextView mStvSetting;

    /**
     * 加载"老师基本信息"(先判断本地缓存,再决定是否联网)
     */
    private void loadTeacherInfo() {
        if (RxAppTool.getAppVersionCode(CustomApplication.getContext()) < AppConfig.getSPUtils().getInt(AppConfig.KEY_APP_VERSION_CODE)) mStvSetting.setRightTvDrawableLeft(CustomApplication.getContext().getDrawable(R.drawable.shape_update_badge));

        File lCacheTeacherAvatartFile = new File(AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_USER_AVATAR));
        String lCacheTeacherName = AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_USER_NAME);
        if (lCacheTeacherAvatartFile.exists() && !TextUtils.isEmpty(lCacheTeacherName)) {//每一项本地都有缓存值,直接加载本地缓存
            mRefreshMultipleStatusView.showContent();
            mSrlSwipeRefresh.setRefreshing(false);
            Glide.with(MineFragment.this).load(lCacheTeacherAvatartFile).apply(AppConfig.AVATAR_GLIDE_REQUEST_OPTIONS).into(mPvTeacherAvatar);
            mSbTeacherName.setText(lCacheTeacherName);
        } else {//某一项没有缓存值,就联网请求,请求后缓存至本地
            OkHttpEngine.getInstance().sendAsyncPostRequest(MineFragment.this, MessageFormat.format("{0}/teacherInfo/getTeacherInfo", AppConfig.SERVER_ADDRESS), new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).build(), new OkHttpEngine.ResponsedCallback() {
                @Override
                public void onFailure(Call call, Exception e) {
                    mRefreshMultipleStatusView.showError();
                    mSrlSwipeRefresh.setRefreshing(false);
                    Glide.with(MineFragment.this).load(R.drawable.ic_default_avatar).apply(AppConfig.AVATAR_GLIDE_REQUEST_OPTIONS).into(mPvTeacherAvatar);
                    mSbTeacherName.setText("获取失败");
                }

                @Override
                public void onResponseJson(Call call, String responsedJsonStr) {
                    mRefreshMultipleStatusView.showContent();//要retry所以每次showContent
                    mSrlSwipeRefresh.setRefreshing(false);
                    TeacherInfoBean lTeacherInfoBean = OkHttpEngine.toObject(responsedJsonStr, TeacherInfoBean.class);
                    if (lTeacherInfoBean != null) {
                        switch (lTeacherInfoBean.getCode()) {
                            case 0://成功:
                                TeacherInfoBean.DataBean lTeacherInfoBeanData = lTeacherInfoBean.getData();
                                if (lTeacherInfoBeanData != null) {
                                    Glide.with(MineFragment.this).downloadOnly().load(MessageFormat.format("{0}{1}", AppConfig.SERVER_HOST, lTeacherInfoBeanData.getPhotoPath()).replaceAll("\\\\", "/")).listener(new RequestListener<File>() {// 缓存教师头像
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
                                            LogUtility.e(TAG, MessageFormat.format("onLoadFailed: {0}", isFirstResource), e);
                                            return true;
                                        }

                                        @Override
                                        public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {
                                            Glide.with(MineFragment.this).load(resource).apply(AppConfig.AVATAR_GLIDE_REQUEST_OPTIONS).into(mPvTeacherAvatar);
                                            File cacheFile;
                                            if ((cacheFile = new File(AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_USER_AVATAR))).exists()) cacheFile.delete();
                                            AppConfig.getSPUtils().put(AppConfig.KEY_TEACHER_USER_AVATAR, resource.getAbsolutePath());
                                            return true;
                                        }
                                    }).submit(200, 200);
                                    mSbTeacherName.setText(lTeacherInfoBeanData.getName());
                                    AppConfig.getSPUtils().put(AppConfig.KEY_TEACHER_USER_NAME, lTeacherInfoBeanData.getName());
                                    AppConfig.getSPUtils().put(AppConfig.KEY_TEACHER_ACCOUNT, lTeacherInfoBeanData.getTelephone());
                                    AppConfig.getSPUtils().put(AppConfig.KEY_TEACHER_SEX, lTeacherInfoBeanData.getSex());
                                    AppConfig.getSPUtils().put(AppConfig.KEY_TEACHER_USER_ID, lTeacherInfoBeanData.getNumber());
                                }
                                break;
                            default:
                                mRefreshMultipleStatusView.showError();
                                LogUtility.w(TAG, MessageFormat.format("获取教师信息失败: {0}:{1}", lTeacherInfoBean.getCode(), lTeacherInfoBean.getMessage()));
                        }
                    }
                }
            });
        }
    }

    @OnClick({R.id.pv_teacher_avatar, R.id.stv_person_info, R.id.stv_setting})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.pv_teacher_avatar:
                startActivity(ZoomPictureActivity.createIntent(CustomApplication.getContext()), ActivityOptionsCompat.makeSceneTransitionAnimation(mFragmentActivity, view, getString(R.string.picture_detail)).toBundle());
                break;
            case R.id.stv_person_info:
                startActivity(PersonInfoActivity.createIntent(CustomApplication.getContext()));
                break;
            case R.id.stv_setting:
                mStvSetting.setRightTvDrawableLeft(null);
                startActivity(SettingActivity.createIntent(CustomApplication.getContext()));
                break;
            default:
        }
    }
}
