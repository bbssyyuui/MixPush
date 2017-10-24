package com.zdf.lib_push;

import android.content.Context;

import com.umeng.message.PushAgent;
import com.zdf.lib_push.platform.IBasePush;
import com.zdf.lib_push.platform.PushEmui;
import com.zdf.lib_push.platform.PushJG;
import com.zdf.lib_push.platform.PushMiui;
import com.zdf.lib_push.platform.PushUmeng;
import com.zdf.lib_push.rom.RomUtil;
import com.zdf.lib_push.rom.Target;

/**
 * Created by xiaofeng on 2017/4/19.
 */

public class Push implements IBasePush {

    private static volatile Push instance = null;

    private Push() {

    }

    public static Push getInstance() {
        // if already inited, no need to get lock everytime
        if (instance == null) {
            synchronized (Push.class) {
                if (instance == null) {
                    instance = new Push();
                }
            }
        }

        return instance;
    }

    /**
     * 获取对应平台的推送服务类
     *
     * @return
     */
    public IBasePush getPush() {
        switch (RomUtil.rom()) {
            case JG:
            default:
                return PushJG.getInstance();
            case UMENG:
                return PushUmeng.getInstance();
            case MIUI:
                return PushMiui.getInstance();
            case EMUI:
                return PushEmui.getInstance();
        }
    }

    /**
     * Umeng特有接口，用于统计应用启动数据
     *
     * 可以按照"几天不活跃"条件来推送
     *
     * @param context
     */
    public void onAppStart(Context context) {
        if (RomUtil.rom() == Target.UMENG) {
            PushAgent.getInstance(context).onAppStart();
        }
    }

    @Override
    public void register(Context context, PushCallback pushCallback) {
        // 这里特殊处理：如果Rom是华为，并且华为推送服务不可用，则改用Umeng
        if (RomUtil.rom() == Target.EMUI && !RomUtil.isEmuiServiceEnable(context)) {
            RomUtil.setRom(Target.JG);
        }
        getPush().register(context, pushCallback);
    }

    @Override
    public void unregister(Context context) {
        getPush().unregister(context);
    }

    @Override
    public void resume(Context context) {
        getPush().resume(context);
    }

    @Override
    public void pause(Context context) {
        getPush().pause(context);
    }

    public void setAlias(Context context, String alias) {
        getPush().setAlias(context, alias);
    }

    @Override
    public void unsetAlias(Context context, String alias) {
        getPush().unsetAlias(context, alias);
    }

    @Override
    public void setTag(Context context, String tag) {
        getPush().setTag(context, tag);
    }

    @Override
    public void unsetTag(Context context, String tag) {
        getPush().unsetTag(context, tag);
    }

}
