package com.dexin.eccteacher.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;

import com.dexin.eccteacher.utility.OkHttpEngine;

import java.text.MessageFormat;

/**
 * BaseService (不要注册)
 */
@SuppressLint("Registered")
public class BaseService extends Service {
    protected final String TAG = MessageFormat.format("TAG_{0}", getClass().getSimpleName());

    @NonNull
    @Override       // TODO: Return the communication channel to the service.
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        OkHttpEngine.getInstance().cancelAllOkHttpRequest();
        super.onDestroy();
    }
}
