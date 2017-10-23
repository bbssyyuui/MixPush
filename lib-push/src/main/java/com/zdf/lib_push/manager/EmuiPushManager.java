package com.zdf.lib_push.manager;

import android.content.Context;
import android.text.TextUtils;

import com.huawei.hms.api.ConnectionResult;
import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.support.api.client.PendingResult;
import com.huawei.hms.support.api.client.ResultCallback;
import com.huawei.hms.support.api.push.HuaweiPush;
import com.huawei.hms.support.api.push.PushException;
import com.huawei.hms.support.api.push.TokenResult;
import com.zdf.lib_push.utils.Log;

/**
 * 华为推送API管理类
 * <p>
 * Created by xiaofeng on 2017/8/3.
 */

public class EmuiPushManager implements HuaweiApiClient.ConnectionCallbacks, HuaweiApiClient.OnConnectionFailedListener {

    private HuaweiApiClient client;

    @Override
    public void onConnected() {
        Log.v("[EmuiPushManager] onConnected");
        getTokenAsyn();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v("[EmuiPushManager] onConnectionSuspended");
        if (client != null) {
            client.connect();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.v("[EmuiPushManager] onConnectionFailed, " + connectionResult.getErrorCode());
    }

    /**
     * 连接并获取token
     *
     * @param context
     */
    public void connectAndRequestToken(Context context) {
        //创建华为移动服务client实例用以使用华为push服务
        //需要指定api为HuaweiPush.PUSH_API
        //连接回调以及连接失败监听
        client = new HuaweiApiClient.Builder(context)
                .addApi(HuaweiPush.PUSH_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        //建议在onCreate的时候连接华为移动服务
        //业务可以根据自己业务的形态来确定client的连接和断开的时机，但是确保connect和disconnect必须成对出现
        client.connect();
    }

    public void disconnectAndReleaseToken(String token) {
        deleteToken(token);

        //建议在onDestroy的时候停止连接华为移动服务
        //业务可以根据自己业务的形态来确定client的连接和断开的时机，但是确保connect和disconnect必须成对出现
        if (client != null) {
            client.disconnect();
        }
    }

    /**
     * 同步获取Token
     */
    public void getTokenSync() {
        if (client == null)
            return;

        if (!client.isConnected()) {
            client.connect();
            return;
        }

        //需要在子线程中调用函数
        new Thread() {
            @Override
            public void run() {
                PendingResult<TokenResult> tokenResult = HuaweiPush.HuaweiPushApi.getToken(client);
                TokenResult result = tokenResult.await();
                Log.v("[EmuiPushManager] getTokenSync, result: " + result.getTokenRes().getRetCode());
            }
        }.start();
    }

    /**
     * 异步获取Token
     */
    public void getTokenAsyn() {
        if (client == null)
            return;

        if (!client.isConnected()) {
            client.connect();
            return;
        }

        PendingResult<TokenResult> tokenResult = HuaweiPush.HuaweiPushApi.getToken(client);
        tokenResult.setResultCallback(new ResultCallback<TokenResult>() {
            @Override
            public void onResult(TokenResult result) {
//                Log.v("[EmuiPushManager] getTokenAsyn, onResult: " + result.getTokenRes().getRetCode());
            }
        });
    }

    /**
     * 注销Token
     */
    private void deleteToken(final String token) {
        if (client == null)
            return;

        if (!client.isConnected()) {
            client.connect();
            return;
        }

        //需要在子线程中执行删除TOKEN操作
        new Thread() {
            @Override
            public void run() {
                //调用删除TOKEN需要传入通过getToken接口获取到TOKEN，并且需要对TOKEN进行非空判断
                if (!TextUtils.isEmpty(token)) {
                    try {
                        HuaweiPush.HuaweiPushApi.deleteToken(client, token);
                    } catch (PushException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    /**
     * 获取华为PUSH服务当前状态的接口。结果通过广播发送。
     */
    public void getPushState() {
        if (client == null)
            return;

        if (!client.isConnected()) {
            client.connect();
            return;
        }

        //需要在子线程中调用函数
        new Thread() {
            @Override
            public void run() {
                HuaweiPush.HuaweiPushApi.getPushState(client);
            }
        }.start();
    }


    /**
     * 开启/关闭普通推送消息
     *
     * @param enable
     */
    public void enableReceiveNormalMsg(final boolean enable) {
        if (client == null)
            return;

        if (!client.isConnected()) {
            client.connect();
            return;
        }

        //需要在子线程中调用函数
        new Thread() {
            @Override
            public void run() {
                //开启自呈现消息
                HuaweiPush.HuaweiPushApi.enableReceiveNormalMsg(client, enable);
            }
        }.start();
    }

    /**
     * 开启/关闭透传消息
     *
     * @param enable
     */
    public void enableReceiveNotifyMsg(final boolean enable) {
        if (client == null)
            return;

        if (!client.isConnected()) {
            client.connect();
            return;
        }

        //需要在子线程中调用函数
        new Thread() {
            @Override
            public void run() {
                //开启透传消息
                HuaweiPush.HuaweiPushApi.enableReceiveNotifyMsg(client, enable);
            }
        }.start();
    }

    /**
     * 查询华为Push用户协议条款
     * <p>
     * 可以在该界面终止华为push用户授权，终止后，将无法收到push消息，需要用户重新授权。
     */
    public void queryAgreement() {
        if (client == null)
            return;

        if (!client.isConnected()) {
            client.connect();
            return;
        }

        //需要在子线程中调用函数
        new Thread() {
            @Override
            public void run() {
                //展示Push协议
                HuaweiPush.HuaweiPushApi.queryAgreement(client);
            }
        }.start();
    }
}
