package com.dexin.eccteacher.utility;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.dexin.eccteacher.application.AppConfig;
import com.dexin.eccteacher.application.CustomApplication;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.jetbrains.annotations.Contract;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Dispatcher;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 单例OkHttp引擎
 */
public final class OkHttpEngine {
    private static final String TAG = "TAG_OkHttpEngine";
    private OkHttpClient mOkHttpClient;
    private Dispatcher mDispatcher;
    private Handler mHandler;

    private OkHttpEngine() {
        try {
            Context context = CustomApplication.getContext();
            File sdCacheDir = context.getExternalCacheDir();
            mOkHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(4L, TimeUnit.SECONDS).writeTimeout(4L, TimeUnit.SECONDS).readTimeout(4L, TimeUnit.SECONDS)
                    .cache((sdCacheDir != null) ? (new Cache(sdCacheDir.getAbsoluteFile(), 10L * 1024L * 1024L)) : null)
                    .build();
            mDispatcher = mOkHttpClient.dispatcher();
            mHandler = new Handler(context.getMainLooper());
        } catch (RuntimeException e) {
            Log.e(TAG, "OkHttpEngine: ", e);
        }
    }

    @Contract(pure = true)
    public static OkHttpEngine getInstance() {
        return OkHttpEngineHolder.OK_HTTP_ENGINE;//FIXME 推荐使用"静态内部类单例"
    }

    /**
     * 静态内部类单例Holder
     */
    private static final class OkHttpEngineHolder {
        private static final OkHttpEngine OK_HTTP_ENGINE = new OkHttpEngine();
    }

    /**
     * 发送异步GET请求
     *
     * @param component         当前组件
     * @param urlAddress        url地址
     * @param responsedCallback 响应结果回调接口
     */
    public void sendAsyncGetRequest(Object component, String urlAddress, ResponsedCallback responsedCallback) {
        try {
            if (AppConfig.isNetAvailable()) {
                Request request = new Request.Builder().url(urlAddress).build();
                requestNet(component, request, responsedCallback);
            }
        } catch (RuntimeException e) {
            Log.e(TAG, "sendAsyncGetRequest: ", e);
        }
    }

    /**
     * 发送异步POST请求
     *
     * @param component         当前组件
     * @param urlAddress        url地址
     * @param formBody          表单
     * @param responsedCallback 响应结果回调接口
     */
    public void sendAsyncPostRequest(Object component, String urlAddress, RequestBody formBody, ResponsedCallback responsedCallback) {
        try {
            if (AppConfig.isNetAvailable() && formBody != null) {
                Request request = new Request.Builder().url(urlAddress).post(formBody).build();
                requestNet(component, request, responsedCallback);
            }
        } catch (RuntimeException e) {
            Log.e(TAG, "sendAsyncPostRequest: ", e);
        }
    }

    /**
     * 发送异步POST请求,上传文本文件中的内容
     *
     * @param component         当前组件
     * @param urlAddress        url地址
     * @param mediaType         MediaType.parse("text/x-markdown; charset=utf-8")
     * @param file              文件
     * @param responsedCallback 响应结果回调接口
     */
    public void sendAsyncUploadTextRequest(Object component, String urlAddress, MediaType mediaType, File file, ResponsedCallback responsedCallback) {
        try {
            if (AppConfig.isNetAvailable() && (mediaType != null) && (file != null)) {
                Request request = new Request.Builder().url(urlAddress).post(RequestBody.create(mediaType, file)).build();
                requestNet(component, request, responsedCallback);
            }
        } catch (RuntimeException e) {
            Log.e(TAG, "sendAsyncUploadTextRequest: ", e);
        }
    }

    /**
     * 发送异步上传文件请求
     *
     * @param component         当前组件
     * @param urlAddress        url地址
     * @param requestBody       请求体
     * @param responsedCallback 响应结果回调接口
     */
    public void sendAsyncUploadFileRequest(Object component, String urlAddress, RequestBody requestBody, ResponsedCallback responsedCallback) {
        try {
            if (AppConfig.isNetAvailable() && requestBody != null) {
                Request request = new Request.Builder().header("Authorization", "Client-ID ...").url(urlAddress).post(requestBody).build();
                requestNet(component, request, responsedCallback);
            }
        } catch (RuntimeException e) {
            Log.e(TAG, "sendAsyncUploadFileRequest: ", e);
        }
    }

    /**
     * 请求网络
     *
     * @param component         当前组件
     * @param request           请求实体
     * @param responsedCallback 请求结果回调接口
     */
    private void requestNet(Object component, Request request, ResponsedCallback responsedCallback) {
        if ((mOkHttpClient != null) && (mHandler != null) && (request != null) && (responsedCallback != null)) {
            mOkHttpClient.newCall(request).enqueue(new Callback() {//FIXME 这里必须new出不同的Callback对象,才代表发送的是相互无干扰的网络请求
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e(TAG, "onFailure: ", e);
                    if (AppConfig.isComponentAlive(component)) mHandler.post(() -> responsedCallback.onFailure(call, e));
                    call.cancel();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    if (response.isSuccessful()) {
                        try (ResponseBody responseBody = response.body()) {
                            if (responseBody != null) {
                                String responsedJsonStr = responseBody.string();
                                if (!TextUtils.isEmpty(responsedJsonStr) && AppConfig.isComponentAlive(component)) {
                                    LogUtility.d(TAG, "onResponse: " + component.getClass() + "\t\t" + responsedJsonStr);
                                    mHandler.post(() -> responsedCallback.onResponseJson(call, responsedJsonStr));
                                }
                            }
                        } catch (Exception e) {
                            LogUtility.e(TAG, "onResponse: ", e);
                        }
                    }
                    response.close();
                    call.cancel();
                }
            });
        }
    }

    private static final class GsonHolder {
        private static final Gson GSON = new Gson();
    }

    @Nullable
    public static <T> T toObject(String responsedJsonStr, Class<T> classOfT) {
        try {
            return GsonHolder.GSON.fromJson(responsedJsonStr, classOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 取消所有 OkHttp请求
     */
    public void cancelAllOkHttpRequest() {
        try {
            if (mDispatcher != null) mDispatcher.cancelAll();
        } catch (RuntimeException e) {
            LogUtility.e(TAG, "cancelAllOkHttpRequest: ", e);
        }
    }

    /**
     * 请求对应的响应的回调接口
     */
    public interface ResponsedCallback {
        void onFailure(Call call, Exception e);

        void onResponseJson(Call call, String responsedJsonStr);
    }
}
