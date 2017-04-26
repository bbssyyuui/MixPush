package com.zdf.lib_push.platform;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.umeng.message.IUmengCallback;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.common.inter.ITagManager;
import com.umeng.message.entity.UMessage;
import com.umeng.message.tag.TagManager;
import com.zdf.lib_push.PushCallback;
import com.zdf.lib_push.model.Message;
import com.zdf.lib_push.rom.Target;
import com.zdf.lib_push.service.UmengPushService;
import com.zdf.lib_push.utils.Log;
import com.zdf.lib_push.utils.ProcessUtil;

/**
 * Umeng推送服务
 *
 * SDK文档：http://dev.umeng.com/push/android/integration
 *
 * Created by xiaofeng on 2017/4/21.
 */

public class PushUmeng implements IBasePush {

    private static volatile PushUmeng instance = null;
    private PushCallback mCallback;

    // 计时器时间间隔
    private static final int TIMER_INTERVAL = 5000;
    // 最多重连次数
    private static final int LIMIT_RETRY_COUNT = 10;

    private HandlerThread mHandlerThread;
    private Handler mBgHandler;
    private String mDeviceToken;
    private int mRetryCount = 0;

    public static PushUmeng getInstance() {
        // if already inited, no need to get lock everytime
        if (instance == null) {
            synchronized (PushUmeng.class) {
                if (instance == null) {
                    instance = new PushUmeng();
                }
            }
        }

        return instance;
    }

    /**
     * 自定义计时器
     *
     * Umeng注册有个bug，有可能DeviceToken会取不到，
     * 这里做下特殊处理：每隔5s注册一次，知道注册成功或者超过10次。
     */
    private class Timer implements Runnable {
        Context context;
        PushAgent pushAgent;

        public Timer(Context context, PushAgent pushAgent) {
            this.context = context;
            this.pushAgent = pushAgent;
        }

        @Override
        public void run() {
            mDeviceToken = null;
            Log.v("[PushUmeng] timer mRetryCount = " + mRetryCount);
            if (!TextUtils.isEmpty(mDeviceToken) || mRetryCount++ > LIMIT_RETRY_COUNT) {
                stopRegisterTimer();
            } else {
                doRegister(context, pushAgent);
                mBgHandler.postDelayed(this, TIMER_INTERVAL);
            }
        }
    }

    /**
     * 注意：
     *
     * 请勿在调用register方法时做进程判断处理，
     * 主进程和channel进程均需要调用register方法才能保证长连接的正确建立。
     *
     * 若有需要，可以在Application的onCreate方法中创建一个子线程，
     * 并把mPushAgent.register这一行代码放到该子线程中去执行
     * （请勿将PushAgent.getInstance(this)放到子线程中）。
     */
    @Override
    public void register(final Context context, PushCallback pushCallback) {
        mCallback = pushCallback;

        PushAgent pushAgent = PushAgent.getInstance(context);
//        pushAgent.setDebugMode(true);

        // 完全自定义推送消息
        // 若要取消：
//        pushAgent.setPushIntentServiceClass(null);
        pushAgent.setPushIntentServiceClass(UmengPushService.class);
        UmengPushService.registerCallback(pushCallback);

        // 注册推送服务，每次调用register方法都会回调该接口
        if (ProcessUtil.isMainProcess(context)) {
            startRegisterTimer(context, pushAgent);
        } else {
            doRegister(context, pushAgent);
        }

        // 自定义通知点击事件
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                Log.v("[PushUmeng] dealWithCustomAction, msg.custom = " + msg.custom);
                if (mCallback != null) {
                    Message message = new Message();
                    message.setTitle(msg.title); // 通知标题
                    message.setMessage(msg.text); // 通知内容
                    message.setCustom(msg.custom); // 自定义点击行为
                    message.setExtra(new Gson().toJson(msg.extra)); // 自定义参数
                    message.setTarget(Target.UMENG); // 消息平台类型
                    mCallback.onMessageClicked(context, message);
                }
            }
        };
        pushAgent.setNotificationClickHandler(notificationClickHandler);

        // 自定义消息（透传消息）
        UmengMessageHandler messageHandler = new UmengMessageHandler(){
            @Override
            public void dealWithCustomMessage(final Context context, final UMessage msg) {
                Log.v("[PushUmeng] dealWithCustomMessage, msg.custom = " + msg.custom);
                if (mCallback != null) {
                    Message message = new Message();
                    message.setCustom(msg.custom); // 自定义消息
                    message.setExtra(new Gson().toJson(msg.extra)); // 自定义参数
                    message.setTarget(Target.UMENG); // 消息平台类型
                    mCallback.onCustomMessage(context, message);
                }
            }
        };
        pushAgent.setMessageHandler(messageHandler);
    }

    /**
     * 启动计时器
     *
     * @param context
     */
    private void startRegisterTimer(Context context, PushAgent pushAgent) {
        stopRegisterTimer();
        mRetryCount = 0;
        mHandlerThread = new HandlerThread("register_retry");
        mHandlerThread.start();
        mBgHandler = new Handler(mHandlerThread.getLooper());
        mBgHandler.post(new Timer(context, pushAgent));
    }

    /**
     * 停止计时器
     */
    private void stopRegisterTimer() {
        mRetryCount = 0;
        if (mHandlerThread != null) {
            mHandlerThread.quit();
            mHandlerThread = null;
        }
        if (mBgHandler != null) {
            mBgHandler.removeCallbacksAndMessages(null);
            mBgHandler = null;
        }
    }

    public void doRegister(final Context context, final PushAgent pushAgent) {
        pushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                Log.v("[PushUmeng] register, deviceToken = " + deviceToken);
                if (mCallback != null) {
                    // 这里Umeng有个bug，deviceToken有可能是null，所以用下面接口再取一次deviceToken
                    mDeviceToken = pushAgent.getRegistrationId();
                    mCallback.onRegister(context, mDeviceToken);
                }
            }

            @Override
            public void onFailure(String s, String s1) {

            }
        });
    }

    @Override
    public void unregister(Context context) {
        mCallback = null;
        stopRegisterTimer();
        UmengPushService.unregisterCallback();
        pause(context);
    }

    @Override
    public void resume(final Context context) {
        PushAgent.getInstance(context).enable(new IUmengCallback() {
            @Override
            public void onSuccess() {
                if (mCallback != null) {
                    mCallback.onResume(context);
                }
            }

            @Override
            public void onFailure(String s, String s1) {

            }
        });
    }

    @Override
    public void pause(final Context context) {
        PushAgent.getInstance(context).disable(new IUmengCallback() {
            @Override
            public void onSuccess() {
                if (mCallback != null) {
                    mCallback.onPaused(context);
                }
            }

            @Override
            public void onFailure(String s, String s1) {

            }
        });
    }

    @Override
    public void setAlias(final Context context, final String alias) {
        String aliasType = "dongjia"; // 别名类型表明是那个渠道的用户，暂时先写死
        PushAgent.getInstance(context).addAlias(alias, aliasType, new UTrack.ICallBack() {
            @Override
            public void onMessage(boolean isSuccess, String message) {
                if (isSuccess && mCallback != null) {
                    mCallback.onAlias(context, alias);
                }
            }
        });
    }

    @Override
    public void unsetAlias(Context context, String alias) {
        String aliasType = "dongjia"; // 别名类型表明是那个渠道的用户，暂时先写死
        PushAgent.getInstance(context).removeAlias(alias, aliasType, new UTrack.ICallBack(){
            @Override
            public void onMessage(boolean isSuccess, String message) {

            }
        });
    }

    @Override
    public void setTag(Context context, String tag) {
        PushAgent.getInstance(context).getTagManager().add(new TagManager.TCallBack() {
            @Override
            public void onMessage(final boolean isSuccess, final ITagManager.Result result) {
                //isSuccess表示操作是否成功
            }
        }, tag);
    }

    @Override
    public void unsetTag(Context context, String tag) {
        PushAgent.getInstance(context).getTagManager().delete(new TagManager.TCallBack() {
            @Override
            public void onMessage(final boolean isSuccess, final ITagManager.Result result) {

            }
        }, tag);
    }
}
