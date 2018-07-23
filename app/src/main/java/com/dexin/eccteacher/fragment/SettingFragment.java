package com.dexin.eccteacher.fragment;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.allen.library.SuperTextView;
import com.dexin.eccteacher.BuildConfig;
import com.dexin.eccteacher.R;
import com.dexin.eccteacher.activity.ActivityCollector;
import com.dexin.eccteacher.application.AppConfig;
import com.dexin.eccteacher.application.CustomApplication;
import com.dexin.eccteacher.bean.DataBean;
import com.dexin.eccteacher.bean.MessageEvent;
import com.dexin.eccteacher.bean.NewAppBean;
import com.dexin.eccteacher.utility.OkHttpEngine;
import com.just.agentweb.AgentWebConfig;
import com.maning.mndialoglibrary.MProgressDialog;
import com.maning.mndialoglibrary.MStatusDialog;
import com.maning.mndialoglibrary.config.MDialogConfig;
import com.vondear.rxtool.RxAppTool;
import com.vondear.rxtool.RxNetTool;
import com.vondear.rxtool.view.RxToast;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.text.MessageFormat;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import io.rong.imkit.RongIM;
import okhttp3.Call;
import okhttp3.FormBody;

/**
 * 设置Fragment
 */
public class SettingFragment extends BaseFragment {
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.stv_check_to_update)
    SuperTextView mStvCheckToUpdate;

    @NonNull
    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    @Override
    protected int getLayoutResourceID() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void initView() {
        setSwipeBackEnable(false);
        mTvTitle.setText("设置");
        mStvCheckToUpdate.setRightString(RxAppTool.getAppVersionName(CustomApplication.getContext()));
        if (RxAppTool.getAppVersionCode(CustomApplication.getContext()) < AppConfig.getSPUtils().getInt(AppConfig.KEY_APP_VERSION_CODE)) {
            mStvCheckToUpdate.setRightTvDrawableLeft(CustomApplication.getContext().getDrawable(R.drawable.shape_update_badge));
        }
    }

    @Override
    protected void initListener() {
    }

    @Override
    protected void initData() {
    }


    @OnClick({R.id.btn_back, R.id.stv_modify_pwd, R.id.stv_check_to_update, R.id.stv_log_off})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                mFragmentActivity.finish();
                break;
            case R.id.stv_modify_pwd:
                start(ModifyPwdFragment.newInstance());
                break;
            case R.id.stv_check_to_update:
                mStvCheckToUpdate.setRightTvDrawableLeft(null);
                checkToUpdate();
                break;
            case R.id.stv_log_off:
                new MaterialDialog.Builder(mFragmentActivity).content("退出登录将会清空当前用户信息!").negativeText("取消").positiveText("确定").onPositive((dialog, which) -> {
                    AppConfig.getSPUtils().clear();
                    LitePal.deleteAll(DataBean.class);
                    AgentWebConfig.clearDiskCache(CustomApplication.getContext());//清空所有 AgentWeb 硬盘缓存,包括 WebView的缓存,AgentWeb下载的图片,视频,apk 等文件
                    RongIM.getInstance().logout();
                    ActivityCollector.finishAll();
                    android.os.Process.killProcess(android.os.Process.myPid());
//                    startActivity(LoginRegisterActivity.createIntent(CustomApplication.getContext()));
                }).show();
                break;
            default:
        }
    }

    /**
     * 检查App更新数据
     */
    private void checkToUpdate() {
        if (RxNetTool.isAvailable(CustomApplication.getContext())) MProgressDialog.showProgress(Objects.requireNonNull(getActivity()), "检查中...");
        OkHttpEngine.getInstance().sendAsyncPostRequest(SettingFragment.this, MessageFormat.format("{0}/appEdition/getAPKInfo", AppConfig.SERVER_ADDRESS), new FormBody.Builder().add("appType", "1").build(), new OkHttpEngine.ResponsedCallback() {
            @Override
            public void onFailure(Call call, Exception e) {
                MProgressDialog.dismissProgress();
                new MStatusDialog(getActivity(), new MDialogConfig.Builder().setTextColor(getResources().getColor(R.color.operate_failed)).build()).show("操作失败", getResources().getDrawable(R.drawable.ic_failure));
            }

            @Override
            public void onResponseJson(Call call, String responsedJsonStr) {
                MProgressDialog.dismissProgress();
                NewAppBean lNewAppBean = OkHttpEngine.toObject(responsedJsonStr, NewAppBean.class);
                if (lNewAppBean != null) {
                    switch (lNewAppBean.getCode()) {
                        case 0://成功
                            NewAppBean.DataBean lLNewAppBeanData = lNewAppBean.getData();
                            if (lLNewAppBeanData != null) {
                                if (RxAppTool.getAppVersionCode(CustomApplication.getContext()) < lLNewAppBeanData.getVersionCode() && Objects.equals(lLNewAppBeanData.getPackageName(), BuildConfig.APPLICATION_ID) && !TextUtils.isEmpty(lLNewAppBeanData.getMd5Code())) {
                                    AppConfig.getSPUtils().put(AppConfig.KEY_APP_VERSION_CODE, lLNewAppBeanData.getVersionCode());
                                    EventBus.getDefault().post(MessageEvent.EVENT_NEW_APP);//TODO 发送事件总线询问是否更新
                                    EventBus.getDefault().postSticky(lLNewAppBeanData);//FIXME 发送粘滞事件,在弹窗中处理粘滞事件
                                } else {
                                    new MStatusDialog(getActivity()).show("当前已是最新版本", getResources().getDrawable(R.drawable.ic_success));
                                }
                            }
                            break;
                        case 124://不存在最新Apk操作失败
                            new MStatusDialog(getActivity()).show("当前已是最新版本!", getResources().getDrawable(R.drawable.ic_success));
                            break;
                        default:
                            RxToast.error(MessageFormat.format("更新失败! {0}:{1}", lNewAppBean.getCode(), lNewAppBean.getMessage()));
                    }
                }
            }
        });
    }
}
