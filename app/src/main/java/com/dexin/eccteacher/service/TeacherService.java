package com.dexin.eccteacher.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;

import com.dexin.eccteacher.BuildConfig;
import com.dexin.eccteacher.application.AppConfig;
import com.dexin.eccteacher.application.CustomApplication;
import com.dexin.eccteacher.bean.NewAppBean;
import com.dexin.eccteacher.utility.LogUtility;
import com.dexin.eccteacher.utility.OkHttpEngine;
import com.vondear.rxtool.RxAppTool;
import com.vondear.rxtool.RxNetTool;

import java.text.MessageFormat;
import java.util.Objects;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import okhttp3.Call;
import okhttp3.FormBody;

/**
 * 教师服务
 */
public class TeacherService extends BaseService {
    private TeacherBinder mTeacherBinder = new TeacherBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        initReceiver();
        connect(AppConfig.getSPUtils().getString(AppConfig.KEY_RONG_USER_TOKEN));
        checkToUpdate();
    }

    @NonNull
    @Override
    public IBinder onBind(Intent intent) {
        return mTeacherBinder;
    }

    @Override
    public void onDestroy() {
        destroyReceiver();
        super.onDestroy();
    }

    public class TeacherBinder extends Binder {
        //定义方法...
    }


    //-----------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------TODO 接收广播 逻辑---------------------------------------------------------------
    //-----------------------------------------------------------------------------------------------------------------------------------------
    private TeacherReceiver mTeacherReceiver;
    private IntentFilter mIntentFilter;

    private void initReceiver() {
        if ((mTeacherReceiver == null) || (mIntentFilter == null)) {
            mTeacherReceiver = new TeacherReceiver();
            mIntentFilter = new IntentFilter();
            mIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(mTeacherReceiver, mIntentFilter);
        }
    }

    private void destroyReceiver() {
        unregisterReceiver(mTeacherReceiver);
    }

    private class TeacherReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!AppConfig.isComponentAlive(TeacherService.this)) return;
            switch (Objects.requireNonNull(intent.getAction())) {
                case ConnectivityManager.CONNECTIVITY_ACTION:
                    if (RxNetTool.isAvailable(CustomApplication.getContext())) AppConfig.updateUserInfo(TeacherService.this);
                    break;
                default:
            }
        }
    }


    //-----------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------TODO 连接融云服务器的逻辑----------------------------------------------------------
    //-----------------------------------------------------------------------------------------------------------------------------------------

    /**
     * 连接服务器,在整个应用程序全局,只需要调用一次,需在 {RongIM.init(context)} 之后调用.
     * 如果调用此接口遇到连接失败,SDK 会自动启动重连机制进行最多10次重连,分别是1, 2, 4, 8, 16, 32, 64, 128, 256, 512秒后.
     * 在这之后如果仍没有连接成功,还会在当检测到设备网络状态变化时再次进行重连.
     *
     * @param token 从服务端获取的用户身份令牌(Token)
     */
    private void connect(String token) {
        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            /**
             * Token 错误.可以从下面两点检查:
             * 1.token是否过期,如果过期您需要向App Server重新请求一个 新的token
             * 2.token对应的appKey 和 工程里设置的appKey 是否一致
             */
            @Override
            public void onTokenIncorrect() {
                LogUtility.e(TAG, "onTokenIncorrect: ", null);
            }

            /**
             * 连接融云成功
             * @param userid 当前token 对应的 用户id
             */
            @Override
            public void onSuccess(String userid) {
                LogUtility.i(TAG, "onSuccess: " + MessageFormat.format("onSuccess: {0}", userid));
            }

            /**
             * 连接融云失败
             * @param errorCode 错误码,可到官网 查看错误码对应的注释
             */
            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                LogUtility.e(TAG, "onError: " + MessageFormat.format("onError: {0}:{1}", errorCode.getValue(), errorCode.getMessage()), null);
            }
        });
    }


    //-----------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------TODO 检查是否有新版本逻辑----------------------------------------------------------
    //---------------------------------------------------------↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓----------------------------------------------------------

    /**
     * 检查App更新数据
     */
    private void checkToUpdate() {
        OkHttpEngine.getInstance().sendAsyncPostRequest(TeacherService.this, MessageFormat.format("{0}/appEdition/getAPKInfo", AppConfig.SERVER_ADDRESS), new FormBody.Builder().add("appType", "1").build(), new OkHttpEngine.ResponsedCallback() {
            @Override
            public void onFailure(Call call, Exception e) {
            }

            @Override
            public void onResponseJson(Call call, String responsedJsonStr) {
                NewAppBean lNewAppBean = OkHttpEngine.toObject(responsedJsonStr, NewAppBean.class);
                if (lNewAppBean != null) {
                    switch (lNewAppBean.getCode()) {
                        case 0:
                            NewAppBean.DataBean lLNewAppBeanData = lNewAppBean.getData();
                            if ((lLNewAppBeanData != null) && (RxAppTool.getAppVersionCode(CustomApplication.getContext()) < lLNewAppBeanData.getVersionCode()) && Objects.equals(lLNewAppBeanData.getPackageName(), BuildConfig.APPLICATION_ID)) {
                                AppConfig.getSPUtils().put(AppConfig.KEY_APP_VERSION_CODE, lLNewAppBeanData.getVersionCode());
                            }
                            break;
                        default:
                    }
                }
            }
        });
    }
}
