package com.zdf.lib_push.platform;

import android.content.Context;

import com.xiaomi.mipush.sdk.MiPushClient;
import com.zdf.lib_push.Constants;
import com.zdf.lib_push.PushCallback;
import com.zdf.lib_push.Utils;
import com.zdf.lib_push.receiver.MiuiPushReceiver;

/**
 * 小米推送服务
 *
 * SDK文档：http://dev.xiaomi.com/doc/p=6421/index.html
 *
 * Created by xiaofeng on 2017/4/21.
 */

public class PushMiui implements IBasePush {

    private static volatile PushMiui instance = null;
    private PushCallback mCallback;

    public static PushMiui getInstance() {
        // if already inited, no need to get lock everytime
        if (instance == null) {
            synchronized (PushMiui.class) {
                if (instance == null) {
                    instance = new PushMiui();
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
        mCallback = pushCallback;

        if (Utils.isMainProcess(context)) {
            MiuiPushReceiver.registerCallback(pushCallback);
            MiPushClient.registerPush(context, Constants.MIUI_APP_ID, Constants.MIUI_APP_KEY);
        }
    }

    @Override
    public void unregister(Context context) {
        mCallback = null;
        MiuiPushReceiver.unregisterCallback();
        MiPushClient.unregisterPush(context);
    }

    @Override
    public void resume(Context context) {
        MiPushClient.resumePush(context, null);

        if (mCallback != null) {
            mCallback.onResume(context);
        }
    }

    @Override
    public void pause(Context context) {
        MiPushClient.pausePush(context, null);

        if (mCallback != null) {
            mCallback.onPaused(context);
        }
    }

    @Override
    public void setAlias(Context context, String alias) {
        MiPushClient.setAlias(context, alias, null);

        if (mCallback != null) {
            mCallback.onAlias(context, alias);
        }
    }

    @Override
    public void unsetAlias(Context context, String alias) {
        MiPushClient.unsetAlias(context, alias, null);
    }

    @Override
    public void setTag(Context context, String tag) {
        MiPushClient.subscribe(context, tag, null);
    }

    @Override
    public void unsetTag(Context context, String tag) {
        MiPushClient.unsubscribe(context, tag, null);
    }
}
