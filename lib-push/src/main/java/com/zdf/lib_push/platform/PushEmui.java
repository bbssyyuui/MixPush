package com.zdf.lib_push.platform;

import android.content.Context;

import com.zdf.lib_push.PushCallback;
import com.zdf.lib_push.manager.EmuiPushManager;
import com.zdf.lib_push.receiver.EmuiPushReceiver;
import com.zdf.lib_push.utils.Log;
import com.zdf.lib_push.utils.ProcessUtil;

/**
 * 华为推送服务
 * <p>
 * SDK文档：http://developer.huawei.com/consumer/cn/service/hms/catalog/huaweipush.html
 * <p>
 * Created by xiaofeng on 2017/4/21.
 */

public class PushEmui implements IBasePush {

    private static volatile PushEmui instance = null;
    private PushCallback    mCallback;
    private EmuiPushManager manager;

    private PushEmui() {
        manager = new EmuiPushManager();
    }

    public static PushEmui getInstance() {
        // if already inited, no need to get lock everytime
        if (instance == null) {
            synchronized (PushEmui.class) {
                if (instance == null) {
                    instance = new PushEmui();
                }
            }
        }

        return instance;
    }

    @Override
    public void register(final Context context, PushCallback pushCallback) {
        Log.v("[PushEmui] register");
        mCallback = pushCallback;

        if (ProcessUtil.isMainProcess(context)) {
            EmuiPushReceiver.registerCallback(pushCallback);
            manager.connectAndRequestToken(context);
        }
    }

    @Override
    public void unregister(Context context) {
        mCallback = null;
        EmuiPushReceiver.unregisterCallback();
        manager.disconnectAndReleaseToken(EmuiPushReceiver.getToken());
    }

    @Override
    public void resume(Context context) {
        manager.enableReceiveNormalMsg(true);
        manager.enableReceiveNotifyMsg(true);

        if (mCallback != null) {
            mCallback.onResume(context);
        }
    }

    @Override
    public void pause(Context context) {
        manager.enableReceiveNormalMsg(false);
        manager.enableReceiveNotifyMsg(false);

        if (mCallback != null) {
            mCallback.onPaused(context);
        }
    }

    @Override
    public void setAlias(Context context, String alias) {
//        Map<String, String> map = new HashMap<>();
//        map.put("alias", alias);
//        PushManager.setTags(context, map);
//
//        if (mCallback != null) {
//            mCallback.onAlias(context, alias);
//        }
    }

    @Override
    public void unsetAlias(Context context, String alias) {
//        List<String> list = new ArrayList<>();
//        list.add("alias");
//        PushManager.deleteTags(context, list);
    }

    @Override
    public void setTag(Context context, String tag) {
//        Map<String, String> map = new HashMap<>();
//        map.put(tag, tag);
//        PushManager.setTags(context, map);
    }

    @Override
    public void unsetTag(Context context, String tag) {
//        List<String> list = new ArrayList<>();
//        list.add(tag);
//        PushManager.deleteTags(context, list);
    }
}
