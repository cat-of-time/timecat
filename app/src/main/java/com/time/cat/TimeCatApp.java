package com.time.cat;

import android.app.Application;
import android.content.Intent;
import android.os.Looper;
import android.os.MessageQueue;

import com.shang.commonjar.contentProvider.Global;
import com.time.cat.component.service.ListenClipboardService;
import com.time.cat.component.service.TimeCatMonitorService;
import com.time.cat.util.KeepAliveWatcher;
import com.time.cat.util.onestep.AppManager;

/**
 * Created by penglu on 2016/10/26.
 */
public class TimeCatApp extends Application {
    private static TimeCatApp instance;

    public static TimeCatApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Global.init(this);
        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
            @Override
            public boolean queueIdle() {

                KeepAliveWatcher.keepAlive(TimeCatApp.this);
                startService(new Intent(TimeCatApp.this, ListenClipboardService.class));
                startService(new Intent(TimeCatApp.this, TimeCatMonitorService.class));
                return false;
            }
        });
        AppManager.getInstance(this);
    }
}
