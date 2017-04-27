package com.zdf.lib_push.receiver;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;
import com.zdf.lib_push.PushCallback;
import com.zdf.lib_push.model.Message;
import com.zdf.lib_push.rom.Target;
import com.zdf.lib_push.utils.Log;

import java.util.List;

/**
 * 1、PushMessageReceiver 是个抽象类，该类继承了 BroadcastReceiver。<br/>
 * 2、需要将自定义的 MiuiPushReceiver 注册在 AndroidManifest.xml 文件中：
 * <pre>
 * {@code
 *  <receiver
 *      android:name="com.zdf.miuipush.MiuiPushReceiver"
 *      android:exported="true">
 *      <intent-filter>
 *          <action android:name="com.xiaomi.mipush.RECEIVE_MESSAGE" />
 *      </intent-filter>
 *      <intent-filter>
 *          <action android:name="com.xiaomi.mipush.MESSAGE_ARRIVED" />
 *      </intent-filter>
 *      <intent-filter>
 *          <action android:name="com.xiaomi.mipush.ERROR" />
 *      </intent-filter>
 *  </receiver>
 *  }</pre>
 * 3、MiuiPushReceiver 的 onReceivePassThroughMessage 方法用来接收服务器向客户端发送的透传消息。<br/>
 * 4、MiuiPushReceiver 的 onNotificationMessageClicked 方法用来接收服务器向客户端发送的通知消息，
 * 这个回调方法会在用户手动点击通知后触发。<br/>
 * 5、MiuiPushReceiver 的 onNotificationMessageArrived 方法用来接收服务器向客户端发送的通知消息，
 * 这个回调方法是在通知消息到达客户端时触发。另外应用在前台时不弹出通知的通知消息到达客户端也会触发这个回调函数。<br/>
 * 6、MiuiPushReceiver 的 onCommandResult 方法用来接收客户端向服务器发送命令后的响应结果。<br/>
 * 7、MiuiPushReceiver 的 onReceiveRegisterResult 方法用来接收客户端向服务器发送注册命令后的响应结果。<br/>
 * 8、以上这些方法运行在非 UI 线程中。
 * <p>
 * Created by xiaofeng on 2017/4/20.
 */
public class MiuiPushReceiver extends PushMessageReceiver {

    private static PushCallback mCallback;

    // Token前缀
    private static final String TOKEN_PREFIX = "xiaomi";

    private String mRegId;
    private String mMessage;
    private String mTopic;
    private String mAlias;
    private String mAccount;
    private String mStartTime;
    private String mEndTime;

    public static void registerCallback(PushCallback callback) {
        mCallback = callback;
    }

    public static void unregisterCallback() {
        mCallback = null;
    }

    /**
     * 透传消息
     *
     * @param context
     * @param message
     */
    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
        Log.v("[MiuiPushReceiver] onReceivePassThroughMessage, " + message.toString());
        if (mCallback != null) {
            final Message result = new Message();
            result.setMessageID(message.getMessageId());
            result.setCustom(message.getContent());
            result.setTarget(Target.MIUI);
            result.setExtra(new Gson().toJson(message.getExtra()));
            mCallback.onCustomMessage(context, result);
        }
    }

    /**
     * 通知消息
     *
     * @param context
     * @param message
     */
    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage message) {
        Log.v("[MiuiPushReceiver] onNotificationMessageArrived, " + message.toString());
        if (mCallback != null) {
            final Message result = new Message();
            result.setMessageID(message.getMessageId());
            result.setTitle(message.getTitle());
            result.setMessage(message.getDescription());
            result.setCustom(message.getContent());
            result.setTarget(Target.MIUI);
            result.setExtra(new Gson().toJson(message.getExtra()));
            mCallback.onMessage(context, result);
        }
    }

    /**
     * 通知消息点击事件
     *
     * @param context
     * @param message
     */
    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage message) {
        Log.v("[MiuiPushReceiver] onNotificationMessageClicked, " + message.toString());
        if (mCallback != null) {
            final Message result = new Message();
            result.setMessageID(message.getMessageId());
            result.setTitle(message.getTitle());
            result.setMessage(message.getDescription());
            result.setCustom(message.getContent());
            result.setTarget(Target.MIUI);
            result.setExtra(new Gson().toJson(message.getExtra()));
            mCallback.onMessageClicked(context, result);
        }
    }

    /**
     * 给服务器发送命令后的返回结果
     *
     * @param context
     * @param message
     */
    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        Log.v("[MiuiPushReceiver] onCommandResult, " + message.toString());
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                if (!TextUtils.isEmpty(cmdArg1)) {
                    mRegId = TOKEN_PREFIX + cmdArg1;
                    if (mCallback != null) {
                        mCallback.onRegister(context, mRegId);
                    }
                }
            }
        } else if (MiPushClient.COMMAND_SET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
                if (mCallback != null) {
                    mCallback.onAlias(context, mAlias);
                }
            }
        } else if (MiPushClient.COMMAND_UNSET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_SUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
            }
        } else if (MiPushClient.COMMAND_SET_ACCEPT_TIME.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mStartTime = cmdArg1;
                mEndTime = cmdArg2;
            }
        }
    }

    /**
     * 给服务器发送注册命令的返回结果
     * （跟上面onCommandResult#MiPushClient.COMMAND_REGISTER是重复的）
     *
     * @param context
     * @param message
     */
    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
        super.onReceiveRegisterResult(context, message);
    }

}