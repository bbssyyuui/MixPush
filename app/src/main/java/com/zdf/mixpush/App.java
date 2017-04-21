package com.zdf.mixpush;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.zdf.lib_push.model.Message;
import com.zdf.lib_push.PushCallback;
import com.zdf.lib_push.Push;

/**
 * Created by xiaofeng on 2017/4/19.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Push.getInstance().register(this, pushCallback);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    private final PushCallback pushCallback = new PushCallback() {
        @Override
        public void onRegister(Context context, String registerID) {
            Log.v("zdf", "onRegister, registerID = " + registerID);
        }

        @Override
        public void onUnRegister(Context context) {

        }

        @Override
        public void onPaused(Context context) {

        }

        @Override
        public void onResume(Context context) {

        }

        @Override
        public void onMessage(Context context, Message message) {

        }

        @Override
        public void onMessageClicked(Context context, Message message) {

        }

        @Override
        public void onCustomMessage(Context context, Message message) {

        }

        @Override
        public void onAlias(Context context, String alias) {

        }
    };
}
