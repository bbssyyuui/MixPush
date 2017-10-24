package com.zdf.lib_push.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.zdf.lib_push.PushCallback;
import com.zdf.lib_push.model.Message;
import com.zdf.lib_push.rom.Target;
import com.zdf.lib_push.utils.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class JPushReceiver extends BroadcastReceiver {

    // Token前缀
    private static final String TOKEN_PREFIX = "jiguang";
    private static PushCallback mCallback;

    public static void registerCallback(Context context, PushCallback callback) {
        mCallback = callback;

        String regId = JPushInterface.getRegistrationID(context);
        if (!TextUtils.isEmpty(regId)) {
            if (mCallback != null) {
                mCallback.onRegister(context, TOKEN_PREFIX + regId);
            }
        }
    }

    public static void unregisterCallback() {
        mCallback = null;
    }

    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                    Log.i("This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    Log.e("Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
            Log.d("[JPushReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

            if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
                String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
                Log.d("[JPushReceiver] 接收Registration Id : " + regId);
                //send the Registration Id to your server...
                if (mCallback != null) {
                    mCallback.onRegister(context, TOKEN_PREFIX + regId);
                }
            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
                Log.d("[JPushReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
                processCustomMessage(context, bundle);
            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
                Log.d("[JPushReceiver] 接收到推送下来的通知");
                int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
                Log.d("[JPushReceiver] 接收到推送下来的通知的ID: " + notifactionId);
                receivingNotification(context, bundle);
            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
                Log.d("[JPushReceiver] 用户点击打开了通知");
                openNotification(context, bundle);
            } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
                Log.d("[JPushReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
                //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..
            } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
                boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
                Log.w("[JPushReceiver]" + intent.getAction() + " connected state change to " + connected);
            } else {
                Log.d("[JPushReceiver] Unhandled intent - " + intent.getAction());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }    // 打印所有的 intent extra 数据

    /**
     * 收到通知
     *
     * @param context
     * @param bundle
     */
    private void receivingNotification(Context context, Bundle bundle) {
        String msgId = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_ID);
        String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
        // Logger.d(TAG, " title : " + title);
        String message = bundle.getString(JPushInterface.EXTRA_ALERT);
        // Logger.d(TAG, "message : " + message);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        // Logger.d(TAG, "extras : " + extras);

        if (mCallback != null) {
            final Message result = new Message();
            result.setMessageID(msgId);
            result.setTitle(title);
            result.setMessage(message);
            result.setCustom(message);
            result.setTarget(Target.JG);
            result.setExtra(new Gson().toJson(extras));
            mCallback.onMessage(context, result);
        }
    }

    /**
     * 打开通知
     *
     * @param context
     * @param bundle
     */
    private void openNotification(Context context, Bundle bundle) {
        // String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        // String myValue = "";
        // try {
        // 	JSONObject extrasJson = new JSONObject(extras);
        // 	myValue = extrasJson.optString("myKey");
        // } catch (Exception e) {
        // 	Logger.w(TAG, "Unexpected: extras is not a valid json", e);
        // 	return;
        // }
        // if (TYPE_THIS.equals(myValue)) {
        // 	Intent mIntent = new Intent(context, ThisActivity.class);
        // 	mIntent.putExtras(bundle);
        // 	mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // 	context.startActivity(mIntent);
        // } else if (TYPE_ANOTHER.equals(myValue)){
        // 	Intent mIntent = new Intent(context, AnotherActivity.class);
        // 	mIntent.putExtras(bundle);
        // 	mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // 	context.startActivity(mIntent);
        // }


        // //打开自定义的Activity
        // Intent i = new Intent(context, TestActivity.class);
        // i.putExtras(bundle);
        // //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
        // context.startActivity(i);


        String msgId = String.valueOf(bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID, 0));
        String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
        // Logger.d(TAG, " title : " + title);
        String message = bundle.getString(JPushInterface.EXTRA_ALERT);
        // Logger.d(TAG, "message : " + message);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        // Logger.d(TAG, "extras : " + extras);

        if (mCallback != null) {
            final Message result = new Message();
            result.setMessageID(msgId);
            result.setTitle(title);
            result.setMessage(message);
            result.setCustom(message);
            result.setTarget(Target.JG);
            result.setExtra(new Gson().toJson(extras));
            mCallback.onMessageClicked(context, result);
        }
    }

    /**
     * 处理自定义消息（透传消息）
     *
     * @param context
     * @param bundle
     */
    private void processCustomMessage(Context context, Bundle bundle) {
        // if (MainActivity.isForeground) {
        //     String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        //     String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        //     Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
        //     msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
        //     if (!ExampleUtil.isEmpty(extras)) {
        //         try {
        //             JSONObject extraJson = new JSONObject(extras);
        //             if (extraJson.length() > 0) {
        //                 msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
        //             }
        //         } catch (JSONException e) {
        //
        //         }
        //
        //     }
        //     LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);
        // }

        String msgId = String.valueOf(bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID, 0));
        String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
        // Logger.d(TAG, " title : " + title);
        String message = bundle.getString(JPushInterface.EXTRA_ALERT);
        // Logger.d(TAG, "message : " + message);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        // Logger.d(TAG, "extras : " + extras);

        if (mCallback != null) {
            final Message result = new Message();
            result.setMessageID(msgId);
            result.setCustom(message);
            result.setTarget(Target.JG);
            result.setExtra(new Gson().toJson(extras));
            mCallback.onCustomMessage(context, result);
        }
    }
}
