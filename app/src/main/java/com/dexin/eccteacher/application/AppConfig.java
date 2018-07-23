package com.dexin.eccteacher.application;

import android.app.Activity;
import android.app.Application;
import android.app.DialogFragment;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.blankj.utilcode.util.SPUtils;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.dexin.eccteacher.R;
import com.dexin.eccteacher.bean.DataBean;
import com.dexin.eccteacher.bean.MessageEvent;
import com.dexin.eccteacher.bean.UserInfoBean;
import com.dexin.eccteacher.service.KillSelfService;
import com.dexin.eccteacher.utility.LogUtility;
import com.dexin.eccteacher.utility.OkHttpEngine;
import com.vondear.rxtool.RxNetTool;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.Contract;
import org.litepal.LitePal;
import org.litepal.crud.callback.FindMultiCallback;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Call;
import okhttp3.FormBody;

/**
 * App全局配置          未完成模块:发布班级通知传照片
 * <p>
 * H5页面"更多"仿微信      开发点击饼图
 */
public final class AppConfig {
    private static final String TAG = "TAG_AppConfig";
    //Rong_App_Key      登录的默认账号和密码
    public static final String SERVER_HOST = "http://182.150.56.235:8092/";//内网调试:192.168.0.252:8081     192.168.0.106:8080        外网调试:182.150.56.235:8092
    public static final String SERVER_ADDRESS = SERVER_HOST + "witCloud";
    public static final String NEW_APK_PATH = Objects.requireNonNull(CustomApplication.getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)).getAbsolutePath() + "/Teacher.apk";
    public static final String PAGE_SIZE = "10";
    public static final RequestOptions NORMAL_GLIDE_REQUEST_OPTIONS = new RequestOptions().dontAnimate().diskCacheStrategy(DiskCacheStrategy.RESOURCE).skipMemoryCache(true);
    public static final RequestOptions CORNER_GLIDE_REQUEST_OPTIONS = new RequestOptions().dontAnimate().diskCacheStrategy(DiskCacheStrategy.RESOURCE).skipMemoryCache(true).transform(new RoundedCornersTransformation(8, 0, RoundedCornersTransformation.CornerType.ALL)).placeholder(R.drawable.ic_default_squre).error(R.drawable.ic_default_squre);
    public static final RequestOptions CIRCLE_GLIDE_REQUEST_OPTIONS = new RequestOptions().dontAnimate().diskCacheStrategy(DiskCacheStrategy.RESOURCE).skipMemoryCache(true).circleCrop();
    public static final RequestOptions AVATAR_GLIDE_REQUEST_OPTIONS = new RequestOptions().dontAnimate().diskCacheStrategy(DiskCacheStrategy.RESOURCE).skipMemoryCache(true).circleCrop().placeholder(R.drawable.ic_default_avatar).error(R.drawable.ic_default_avatar);


    //FIXME SP_KEY
    public static final String KEY_APP_VERSION_CODE = "KEY_APP_VERSION_CODE";
    public static final String KEY_TEACHER_ACCOUNT_ALL_SIGNED = "KEY_TEACHER_ACCOUNT_ALL_SIGNED";
    public static final String KEY_TEACHER_ACCOUNT = "KEY_TEACHER_ACCOUNT";
    public static final String KEY_TEACHER_PWD = "KEY_TEACHER_PWD";
    public static final String KEY_TEACHER_TOKEN = "KEY_TEACHER_TOKEN";
    public static final String KEY_TEACHER_ID = "KEY_TEACHER_ID";
    public static final String KEY_TEACHER_SCHOOL_ID = "KEY_TEACHER_SCHOOL_ID";
    public static final String KEY_TEACHER_USER_ID = "KEY_TEACHER_USER_ID";
    public static final String KEY_TEACHER_USER_AVATAR = "KEY_TEACHER_USER_AVATAR";
    public static final String KEY_TEACHER_USER_NAME = "KEY_TEACHER_USER_NAME";
    public static final String KEY_TEACHER_SEX = "KEY_TEACHER_SEX";
    public static final String KEY_RONG_USER_ID = "KEY_RONG_USER_ID";
    public static final String KEY_RONG_USER_TOKEN = "KEY_RONG_USER_TOKEN";
    public static final String KEY_SEARCH_CONT_CLASS = "KEY_SEARCH_CONT_CLASS";

    @Contract(pure = true)
    public static SPUtils getSPUtils() {
        return SPUtilsHolder.SP_UTILS;
    }

    private static final class SPUtilsHolder {
        private static final SPUtils SP_UTILS = SPUtils.getInstance();
    }

    public static boolean isNetAvailable() {
        if (RxNetTool.isAvailable(CustomApplication.getContext())) return true;
        EventBus.getDefault().post(new MessageEvent(MessageEvent.EVENT_NET_UNAVAILABLE));//发送网络不可用事件
        return false;
    }

    /**
     * 随机生成 RGB颜色值
     *
     * @return 随机颜色值
     */
    public static int genRandomColor() {
        Random lRandom = new Random();
        return Color.rgb(lRandom.nextInt(256), lRandom.nextInt(256), lRandom.nextInt(256));
    }

    /**
     * 判断组件状态是否 活跃      //FIXME 还可以考虑使用“多态”来进行封装（待续）
     *
     * @return 组件状态
     */
    public static boolean isComponentAlive(Object component) {
        if (component instanceof Application) {
            Application application = (Application) component;
            return !application.isRestricted();
        } else if (component instanceof Activity) {
            Activity activity = (Activity) component;
            return !activity.isFinishing() && !activity.isDestroyed() && !activity.isRestricted();
        } else if (component instanceof Fragment) {
            Fragment fragment = (Fragment) component;
            return !fragment.isHidden() && !fragment.isRemoving() && !fragment.isDetached() && fragment.getUserVisibleHint();
        } else if (component instanceof DialogFragment) {
            DialogFragment fragment = (DialogFragment) component;
            return !fragment.isHidden() && !fragment.isRemoving() && !fragment.isDetached();
        } else if (component instanceof Service) {
            Service service = (Service) component;
            return !service.isRestricted();
        }
        return false;
    }

    /**
     * 验证手机号是否合法
     *
     * @param telPhoneNo 手机号
     * @return 是否合法
     */
    public static boolean isTelphoneNo(String telPhoneNo) {
        return !TextUtils.isEmpty(telPhoneNo) && telPhoneNo.startsWith("1") && telPhoneNo.length() == 11;
    }

    /**
     * 保存数据
     *
     * @param inputText 输入的文本
     */
    public static void saveStringToFile(String inputText) {
        FileOutputStream fileOutputStream = null;
        BufferedWriter bufferedWriter = null;
        try {
            File crashLogfile = new File(MessageFormat.format("{0}/crash.log", Objects.requireNonNull(CustomApplication.getContext().getExternalCacheDir()).getAbsolutePath()));
            fileOutputStream = new FileOutputStream(crashLogfile, true);
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
            bufferedWriter.write(inputText);
            fileOutputStream.flush();
            bufferedWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 重启整个APP
     *
     * @param context     程序上下文对象
     * @param delayMillis 延迟多少毫秒
     */
    public static void rebootAPP(Context context, long delayMillis) {
        Intent intent = new Intent(context, KillSelfService.class);
        intent.putExtra("packageName", context.getPackageName());
        intent.putExtra("delayMillis", delayMillis);
        context.startService(intent);//开启一个新的服务，用来重启本APP

        android.os.Process.killProcess(android.os.Process.myPid());//杀死整个进程
        System.exit(1);//退出Java虚拟机JVM
    }


    //-----------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------TODO App更新聊过天的人物信息 逻辑--------------------------------------------------
    //-----------------------------------------------------------------------------------------------------------------------------------------
    public static void updateUserInfo(Object component) {
        LitePal.findAllAsync(DataBean.class).listen(new FindMultiCallback() {
            @Override
            public <T> void onFinish(List<T> t) {
                List<DataBean> contChatedList = (List<DataBean>) t;
                if ((contChatedList != null) && !contChatedList.isEmpty()) {
                    for (DataBean contChated : contChatedList) {
                        if ((contChated != null) && !contChated.isUpdateStatus()) AppConfig.getImUserInfo(component, contChated);
                    }
                }
            }
        });
    }


    //-----------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------TODO 融云自动获取聊天用户信息的逻辑-------------------------------------------------
    //-----------------------------------------------------------------------------------------------------------------------------------------
    private static OkHttpEngine.ResponsedCallback mUserInfoResponsedCallback;

    public static void getImUserInfo(Object component, DataBean contChated) {
        if (mUserInfoResponsedCallback == null) {
            mUserInfoResponsedCallback = new OkHttpEngine.ResponsedCallback() {
                @Override
                public void onFailure(Call call, Exception e) {
                    LogUtility.e(TAG, "onFailure: 服务错误,获取用户信息失败!", e);
                    if (!TextUtils.isEmpty(contChated.getName())) {
                        contChated.setUpdateStatus(false);
                        contChated.save();
                    }
                }

                @Override
                public void onResponseJson(Call call, String responsedJsonStr) {
                    UserInfoBean lUserInfoBean = OkHttpEngine.toObject(responsedJsonStr, UserInfoBean.class);
                    if (lUserInfoBean != null) {
                        switch (lUserInfoBean.getCode()) {
                            case 0://成功
                                UserInfoBean.DataBean lUserData = lUserInfoBean.getData();
                                if (lUserData != null) {
                                    RongIM.getInstance().refreshUserInfoCache(new UserInfo(lUserData.getRongUserId(), lUserData.getName(), Uri.parse(MessageFormat.format("{0}{1}", AppConfig.SERVER_HOST, lUserData.getPhotoUrl()).replaceAll("\\\\", "/"))));
                                    if (!TextUtils.isEmpty(contChated.getName())) {
                                        contChated.setUpdateStatus(true);
                                        contChated.save();
                                    }
                                } else {
                                    LogUtility.e(TAG, "onResponseJson: 用户为空,获取用户信息失败!", null);
                                }
                                break;
                            default:
                                LogUtility.e(TAG, "onResponseJson: " + MessageFormat.format("获取用户信息失败! {0}:{1}", lUserInfoBean.getCode(), lUserInfoBean.getMessage()), null);
                        }
                    }
                }
            };
        }
        OkHttpEngine.getInstance().sendAsyncPostRequest(component, MessageFormat.format("{0}/teacherInfo/getMessageInfo", AppConfig.SERVER_ADDRESS), new FormBody.Builder().add("token", AppConfig.getSPUtils().getString(AppConfig.KEY_TEACHER_TOKEN)).add("rongYunId", contChated.getRongUserId()).build(), mUserInfoResponsedCallback);
    }
}
/*
LaunchActivity(读取SD卡权限,拨打电话和管理通话,决定是否开启引导页)
GuideActivity(ViewPager引导页)
LoginActivity(正则匹配手机号,手机号有,正确与否,密码错误与否)
RegisterActivity(倒计时获取手机验证码,校验码校验,密码可见和不可见,协议放到WebViewActivity) 忘记密码:ResetPwdActivity
MD加载对话框
MainActivity(GuideView)
*/
