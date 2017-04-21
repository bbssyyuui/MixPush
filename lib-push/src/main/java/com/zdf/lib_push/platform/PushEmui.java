package com.zdf.lib_push.platform;

import android.content.Context;

import com.huawei.android.pushagent.api.PushManager;
import com.zdf.lib_push.PushCallback;
import com.zdf.lib_push.receiver.EmuiPushReceiver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 华为推送服务
 *
 * SDK文档：http://developer.huawei.com/consumer/cn/wiki/index.php?title=HMS%E5%BC%80%E5%8F%91%E6%8C%87%E5%AF%BC%E4%B9%A6-PUSH%E6%9C%8D%E5%8A%A1%E6%8E%A5%E5%8F%A3
 * （以下载后的SDK中的Demo和doc为准，PushSDK开发指导书.pdf）
 *
 * Created by xiaofeng on 2017/4/21.
 */

public class PushEmui implements IBasePush {

    private static volatile PushEmui instance = null;
    private PushCallback mCallback;

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
        mCallback = pushCallback;
        EmuiPushReceiver.registerCallback(pushCallback);
        PushManager.requestToken(context);
        resume(context);
    }

    @Override
    public void unregister(Context context) {
        mCallback = null;
        EmuiPushReceiver.unregisterCallback();
        pause(context);
//        PushManager.deregisterToken(context, EmuiPushReceiver.getToken());
    }

    @Override
    public void resume(Context context) {
        PushManager.enableReceiveNormalMsg(context, true);
        PushManager.enableReceiveNotifyMsg(context, true);
    }

    @Override
    public void pause(Context context) {
        PushManager.enableReceiveNormalMsg(context, false);
        PushManager.enableReceiveNotifyMsg(context, false);
    }

    @Override
    public void setAlias(Context context, String alias) {
        Map<String, String> map = new HashMap<>();
        map.put("alias", alias);
        PushManager.setTags(context, map);
    }

    @Override
    public void unsetAlias(Context context, String alias) {
        List<String> list = new ArrayList<>();
        list.add("alias");
        PushManager.deleteTags(context, list);
    }

    @Override
    public void setTag(Context context, String tag) {
        Map<String, String> map = new HashMap<>();
        map.put(tag, tag);
        PushManager.setTags(context, map);
    }

    @Override
    public void unsetTag(Context context, String tag) {
        List<String> list = new ArrayList<>();
        list.add(tag);
        PushManager.deleteTags(context, list);
    }
}
