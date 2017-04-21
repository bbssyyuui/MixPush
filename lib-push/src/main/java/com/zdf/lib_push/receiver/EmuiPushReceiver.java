package com.zdf.lib_push.receiver;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.huawei.android.pushagent.PushReceiver;
import com.zdf.lib_push.PushCallback;

/**
 * Created by xiaofeng on 2017/4/20.
 */

public class EmuiPushReceiver extends PushReceiver {

    private static PushCallback pushCallback;
    private static String mToken = null;

    public static void registerCallback(PushCallback callback) {
        pushCallback = callback;
    }

    public static void unregisterCallback() {
        pushCallback = null;
    }

    public static String getToken() {
        return mToken;
    }

    @Override
    public void onToken(Context context, String token, Bundle extras) {
        String belongId = extras.getString("belongId");
        String content = "获取token和belongId成功，token = " + token + ",belongId = " + belongId;
        Log.d("zdf", "[EmuiPushReceiver] register, " + content);

        mToken = token;

        if (pushCallback != null) {
            pushCallback.onRegister(context, token);
        }
    }

    @Override
    public void onPushState(Context context, boolean pushState) {
        try {
            String content = "查询push通道状态： " + (pushState ? "已连接" : "未连接");
            Log.d("zdf", "onPushState, " + content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onPushMsg(Context context, byte[] msg, Bundle bundle) {
        try {
            String content = "收到一条Push消息： " + new String(msg, "UTF-8");
            Log.d("zdf", "onPushMsg, " + content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onEvent(Context context, Event event, Bundle extras) {
        if (Event.NOTIFICATION_OPENED.equals(event) || Event.NOTIFICATION_CLICK_BTN.equals(event)) {
            int notifyId = extras.getInt(BOUND_KEY.pushNotifyId, 0);
            if (0 != notifyId) {
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(notifyId);
            }
            String content = "收到通知附加消息： " + extras.getString(BOUND_KEY.pushMsgKey);
            Log.d("zdf", "onEvent, " + content);
        }
        super.onEvent(context, event, extras);
    }
}
