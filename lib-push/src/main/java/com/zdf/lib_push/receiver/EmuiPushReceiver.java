package com.zdf.lib_push.receiver;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.huawei.android.pushagent.PushReceiver;
import com.zdf.lib_push.PushCallback;
import com.zdf.lib_push.model.Message;
import com.zdf.lib_push.rom.Target;

/**
 * Created by xiaofeng on 2017/4/20.
 */

public class EmuiPushReceiver extends PushReceiver {

    private static PushCallback mCallback;
    private static String mToken = null;

    public static void registerCallback(PushCallback callback) {
        mCallback = callback;
    }

    public static void unregisterCallback() {
        mCallback = null;
    }

    public static String getToken() {
        return mToken;
    }

    /**
     * 服务器返回的token结果（注册之后）
     *
     * @param context
     * @param token
     * @param extras
     */
    @Override
    public void onToken(Context context, String token, Bundle extras) {
        Log.d("zdf", "[EmuiPushReceiver] register, token = " + token);
        mToken = token;

        if (mCallback != null) {
            mCallback.onRegister(context, token);
        }
    }

    /**
     * push连接状态的查询结果
     *
     * @param context
     * @param pushState
     */
    @Override
    public void onPushState(Context context, boolean pushState) {
        Log.d("zdf", "[EmuiPushReceiver] onPushState, " + pushState);
    }

    /**
     * 透传消息
     *
     * @param context
     * @param msg
     * @param bundle
     * @return
     */
    @Override
    public boolean onPushMsg(Context context, byte[] msg, Bundle bundle) {
        try {
            String content = new String(msg, "UTF-8");
            Log.d("zdf", "[EmuiPushReceiver] onPushMsg, " + content);
            if (mCallback != null) {
                Message message = new Message();
                message.setCustom(content);
                message.setTarget(Target.EMUI);
                mCallback.onCustomMessage(context, message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 发给服务器命令后的返回结果（通知点击事件，只有填了Extra[key-value]才能收到消息）
     *
     * @param context
     * @param event
     * @param extras
     */
    @Override
    public void onEvent(Context context, Event event, Bundle extras) {
        Log.d("zdf", "[EmuiPushReceiver] onEvent, " + new Gson().toJson(extras));
        if (Event.NOTIFICATION_OPENED.equals(event) || Event.NOTIFICATION_CLICK_BTN.equals(event)) {
            int notifyId = extras.getInt(BOUND_KEY.pushNotifyId, 0);
            if (0 != notifyId) {
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(notifyId);
            }
            if (mCallback != null) {
                Message message = new Message();
                message.setNotifyID(notifyId);
                message.setExtra(extras.getString(BOUND_KEY.pushMsgKey)); // 自定义参数
                message.setTarget(Target.EMUI);
                mCallback.onMessageClicked(context, message);
            }
        }
    }
}
