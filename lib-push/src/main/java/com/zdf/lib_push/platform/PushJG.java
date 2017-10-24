package com.zdf.lib_push.platform;

import android.content.Context;

import com.zdf.lib_push.PushCallback;
import com.zdf.lib_push.receiver.JPushReceiver;
import com.zdf.lib_push.utils.Log;
import com.zdf.lib_push.utils.ProcessUtil;

import cn.jpush.android.api.JPushInterface;

/**
 * 极光推送服务
 * <p>
 * SDK文档：https://docs.jiguang.cn/jpush/client/Android/android_sdk/
 * <p>
 * Created by xiaofeng on 2017/10/23.
 */

public class PushJG implements IBasePush {

    private static volatile PushJG instance = null;
    private PushCallback mCallback;

    public static PushJG getInstance() {
        // if already inited, no need to get lock everytime
        if (instance == null) {
            synchronized (PushJG.class) {
                if (instance == null) {
                    instance = new PushJG();
                }
            }
        }

        return instance;
    }

    /**
     * 注意：
     * 因为推送服务XMPushService在AndroidManifest.xml中设置为运行在另外一个进程，
     * 这导致本Application会被实例化两次，所以我们需要让应用的主进程初始化。
     */
    @Override
    public void register(final Context context, PushCallback pushCallback) {
        Log.v("[PushJG] register");
        mCallback = pushCallback;

        if (ProcessUtil.isMainProcess(context)) {
            JPushReceiver.registerCallback(context, pushCallback);
            JPushInterface.setDebugMode(true);
            JPushInterface.init(context);
        }
    }

    @Override
    public void unregister(Context context) {
        mCallback = null;
        JPushReceiver.unregisterCallback();
    }

    @Override
    public void resume(Context context) {
        JPushInterface.resumePush(context);

        if (mCallback != null) {
            mCallback.onResume(context);
        }
    }

    @Override
    public void pause(Context context) {
        JPushInterface.stopPush(context);

        if (mCallback != null) {
            mCallback.onPaused(context);
        }
    }

    @Override
    public void setAlias(Context context, String alias) {
        JPushInterface.setAlias(context, 0, alias);

        if (mCallback != null) {
            mCallback.onAlias(context, alias);
        }
    }

    @Override
    public void unsetAlias(Context context, String alias) {
        JPushInterface.deleteAlias(context, 0);
    }

    @Override
    public void setTag(Context context, String tag) {
        JPushInterface.setTags(context, 0, null);
    }

    @Override
    public void unsetTag(Context context, String tag) {
        JPushInterface.deleteTags(context, 0, null);
    }
}
