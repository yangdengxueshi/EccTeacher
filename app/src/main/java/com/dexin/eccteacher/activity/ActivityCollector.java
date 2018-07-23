package com.dexin.eccteacher.activity;

import android.app.Activity;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class ActivityCollector {
    private static List<Activity> activities = new ArrayList<>();

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public static void finishAll() {//如果调用本方法后的目的是为保证程序完全退出，还可以在调用处加上杀掉当前进程的代码：android.os.Process.killProcess(android.os.Process.myPid());
        for (Activity activity : activities) {
            if (!activity.isFinishing()) activity.finish();
        }
        activities.clear();
    }

    @Nullable
    public static Activity getTopActivity() {
        return activities.isEmpty() ? null : activities.get(activities.size() - 1);
    }
}
