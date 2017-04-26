package com.zdf.lib_push;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by xiaofeng on 2017/4/26.
 */

public class Utils {

    /**
     * 判断是否是主进程
     *
     * @param context
     * @return
     */
    public static boolean isMainProcess(Context context) {
        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = context.getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }
}
