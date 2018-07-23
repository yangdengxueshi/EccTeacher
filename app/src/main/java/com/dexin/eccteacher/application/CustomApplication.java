package com.dexin.eccteacher.application;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.dexin.eccteacher.BuildConfig;
import com.dexin.eccteacher.bean.DataBean;
import com.dexin.eccteacher.listener.ActivityLifecycleListener;
import com.dexin.eccteacher.receiver.RongCloudEvent;
import com.dexin.eccteacher.utility.CrashHandler;
import com.dexin.eccteacher.utility.LogUtility;
import com.github.anrwatchdog.ANRWatchDog;
import com.liulishuo.filedownloader.FileDownloader;
import com.squareup.leakcanary.LeakCanary;
import com.vondear.rxtool.RxTool;

import org.jetbrains.annotations.Contract;
import org.litepal.LitePal;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Objects;

import io.rong.imkit.RongIM;
import me.yokeyword.fragmentation.Fragmentation;

/**
 * 自定义Application
 */
public class CustomApplication extends Application {
    private static final String TAG = "TAG_CustomApplication";
    private static Context sContext;//FIXME 这里static所修饰的context并不会引起内存泄漏,因为static数据和单例context拥有相同的生命周期

    @Contract(pure = true)      //全局获取 Context 对象
    public static Context getContext() {
        return sContext;
    }


    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Objects.equals(getCurrentProcessName(), BuildConfig.APPLICATION_ID) && !LeakCanary.isInAnalyzerProcess(this)) {//如果不在分析器进程中:此进程专注于LeakCanary堆分析,你不应该在此进程中初始化App
            LeakCanary.install(this);
            initMemberVar();//初始化成员变量
        }
    }

    /**
     * 初始化成员变量
     */
    private void initMemberVar() {
        sContext = getApplicationContext();
        handleExceptionAndANRGlobally();
        Fragmentation.builder().stackViewMode(Fragmentation.BUBBLE).debug(BuildConfig.DEBUG).handleException(e -> LogUtility.e(TAG, "onException: ", e)).install();//建议在Application里初始化

        LitePal.initialize(this);//LitePal数据库初始化
        RxTool.init(this);
        Utils.init(this);
        FileDownloader.setup(this);
        registerActivityLifecycleCallbacks(new ActivityLifecycleListener());

        RongIM.init(this);//IMKit SDK调用第一步 初始化,只有两个进程需要初始化,主进程 和 push进程
        RongCloudEvent.init(this);
        RongIM.setUserInfoProvider(userId -> {//注册融云用户信息提供者
            AppConfig.getImUserInfo(CustomApplication.this, new DataBean("", "", "", userId));
            return null;
        }, true);
    }

    /**
     * 全局处理Exception和ANR
     */
    private static void handleExceptionAndANRGlobally() {
        try {
            CrashHandler.getInstance();//TODO 全局捕获异常
            new ANRWatchDog().setANRListener(error -> {
                StringWriter lStringWriter = null;
                PrintWriter lPrintWriter = null;
                try {
                    lStringWriter = new StringWriter();
                    lPrintWriter = new PrintWriter(lStringWriter);
                    error.printStackTrace(lPrintWriter);
                    String errorStr = lStringWriter + "\n\n\n\n\n";
                    lStringWriter.flush();
                    lPrintWriter.flush();
                    LogUtils.e(errorStr);
                    AppConfig.saveStringToFile(errorStr);//TODO 文件存储
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (lStringWriter != null) {
                            lStringWriter.close();
                        }
                        if (lPrintWriter != null) {
                            lPrintWriter.close();
                        }
                        //TODO 自定义异常出现后的逻辑
                        Thread.sleep(3 * 500);//TODO 必须睡,否则日志文件不能记录下来
                        AppConfig.rebootAPP(getContext(), 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取当前进程的名字
     *
     * @return 当前进程名
     */
    private String getCurrentProcessName() {
        String currentProcessName = "";
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfoList = activityManager.getRunningAppProcesses();
            if ((runningAppProcessInfoList != null) && !runningAppProcessInfoList.isEmpty()) {
                for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcessInfoList) {
                    if ((runningAppProcessInfo != null) && (runningAppProcessInfo.pid == android.os.Process.myPid())) {
                        currentProcessName = runningAppProcessInfo.processName;
                        break;
                    }
                }
            }
        }
        return currentProcessName;
    }
}
