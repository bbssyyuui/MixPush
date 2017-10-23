package com.zdf.lib_push.receiver;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.huawei.hms.support.api.push.PushReceiver;
import com.zdf.lib_push.PushCallback;
import com.zdf.lib_push.model.Message;
import com.zdf.lib_push.rom.Target;
import com.zdf.lib_push.utils.Log;

/**
 * Created by xiaofeng on 2017/4/20.
 */

public class EmuiPushReceiver extends PushReceiver {

    private static PushCallback mCallback;
    private static String mToken = null;

    // Token前缀
    private static final String TOKEN_PREFIX = "huawei";

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
        Log.v("[EmuiPushReceiver] register, token = " + token);
        if (!TextUtils.isEmpty(token)) {
            mToken = TOKEN_PREFIX + token;
            if (mCallback != null) {
                mCallback.onRegister(context, mToken);
            }
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
        Log.v("[EmuiPushReceiver] onPushState, " + pushState);
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
            Log.v("[EmuiPushReceiver] onPushMsg, " + content);
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
        Log.v("[EmuiPushReceiver] onEvent, " + new Gson().toJson(extras));
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
