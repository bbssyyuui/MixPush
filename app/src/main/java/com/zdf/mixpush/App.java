package com.zdf.mixpush;

import android.app.Application;

import com.zdf.lib_push.Push;

/**
 * Created by xiaofeng on 2017/4/19.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Push.getInstance().register(this, new PushMessageProcesser(this));
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Push.getInstance().unregister(this);
    }
}
