package com.zdf.lib_push.platform;

import android.content.Context;

import com.zdf.lib_push.PushCallback;

/**
 * 推送服务的接口定义
 *
 * Created by xiaofeng on 2017/4/21.
 */

public interface IBasePush {

    /**
     * 注册推送服务
     *
     * @param context
     * @param pushCallback
     */
    void register(final Context context, PushCallback pushCallback);

    /**
     * 取消注册推送服务
     *
     * @param context
     */
    void unregister(Context context);

    /**
     * 继续推送服务
     *
     * @param context
     */
    void resume(Context context);

    /**
     * 暂停推送服务
     *
     * @param context
     */
    void pause(Context context);

    /**
     * 设置别名
     *
     * @param context
     * @param alias
     */
    void setAlias(Context context, String alias);

    /**
     * 取消设置别名
     *
     * @param context
     * @param alias
     */
    void unsetAlias(Context context, String alias);

    /**
     * 设置标签
     *
     * @param context
     * @param tag
     */
    void setTag(Context context, String tag);

    /**
     * 取消设置标签
     *
     * @param context
     * @param tag
     */
    void unsetTag(Context context, String tag);
}
