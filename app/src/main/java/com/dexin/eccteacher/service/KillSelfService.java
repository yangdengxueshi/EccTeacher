package com.dexin.eccteacher.service;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;

public class KillSelfService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            new Handler().postDelayed(() -> {
                PackageManager packageManager = getPackageManager();
                if (packageManager != null) {
                    Intent launcherIntent = packageManager.getLaunchIntentForPackage(intent.getStringExtra("packageName"));
                    if (launcherIntent != null) {
                        startActivity(launcherIntent);
                    }
                    stopSelf();
                }
            }, intent.getLongExtra("delayMillis", 0));
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
