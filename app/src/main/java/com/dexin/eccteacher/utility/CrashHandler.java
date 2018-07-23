package com.dexin.eccteacher.utility;

import android.content.Context;
import android.os.Build;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.dexin.eccteacher.application.AppConfig;
import com.dexin.eccteacher.application.CustomApplication;
import com.vondear.rxtool.RxAppTool;

import org.jetbrains.annotations.Contract;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * UncaughtExceptionHandler 未捕获异常处理类,当程序发生未捕获的异常时,由该类来接管程序,并记录发送错误报告.
 */
public final class CrashHandler implements Thread.UncaughtExceptionHandler {
    /**
     * 私有化构造方法
     */
    private CrashHandler() {
        Thread.setDefaultUncaughtExceptionHandler(this);//设置该CrashHandler为程序的默认处理器
    }

    /**
     * 获取CrashHandler实例,"静态内部类单例模式"
     */
    @Contract(pure = true)
    public static CrashHandler getInstance() {
        return CrashHandlerHolder.CRASH_HANDLER;
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if (e != null) {
            Map<String, String> deviceAndExceptionInfoMap = new HashMap<>();//用来存储 设备信息 和 异常信息
            Context context = CustomApplication.getContext();
            try {//TODO 1.收集设备参数信息
                deviceAndExceptionInfoMap.put("crashTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss EEEE", Locale.CHINA).format(new Date()));
                deviceAndExceptionInfoMap.put("versionCode", "" + RxAppTool.getAppVersionCode(context));
                deviceAndExceptionInfoMap.put("versionName", RxAppTool.getAppVersionName(context));
                deviceAndExceptionInfoMap.put("ipAddress", NetworkUtils.getIPAddress(true));
//                deviceAndExceptionInfoMap.put("macAddress", DeviceUtility.getMacAddress(false));
                Field[] declaredFields = Build.class.getDeclaredFields();//TODO 收集系统 Build类 声明的参数成员变量
                if ((declaredFields != null) && (declaredFields.length > 0)) {
                    for (Field field : declaredFields) {
                        if (field != null) {
                            field.setAccessible(true);
                            deviceAndExceptionInfoMap.put(field.getName(), field.get(null).toString());
                        }
                    }
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            {//TODO 2.保存 设备参数信息 和 错误日志 到磁盘文件
                Set<Map.Entry<String, String>> entrySet = deviceAndExceptionInfoMap.entrySet();
                if (!entrySet.isEmpty()) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (Map.Entry<String, String> stringStringEntry : entrySet) {
                        String key = stringStringEntry.getKey();
                        String value = stringStringEntry.getValue();
                        stringBuilder.append(key).append("\n\t").append(value).append("\n");
                    }
                    StringWriter writer = new StringWriter();
                    PrintWriter printWriter = new PrintWriter(writer);
                    e.printStackTrace(printWriter);
                    printWriter.close();
                    stringBuilder.append(writer).append("\n\n\n\n\n");

                    LogUtils.e(stringBuilder.toString());
                    AppConfig.saveStringToFile(stringBuilder.toString());//TODO 文件存储
                }
            }

            try {//TODO 自定义异常出现后的逻辑
                Thread.sleep(2 * 500);//TODO 必须睡3秒,否则日志文件不能记录下来
                AppConfig.rebootAPP(context, 0);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    /**
     * CrashHandlerHolder
     */
    private static final class CrashHandlerHolder {
        private static final CrashHandler CRASH_HANDLER = new CrashHandler();//TODO 推荐使用"静态内部类模式 单例"
    }
}
