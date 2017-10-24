package com.zdf.lib_push.utils;

/**
 * Created by xiaofeng on 16/4/26.
 */
public class Log {

    public static boolean DEBUG = true;
    public static final String TAG = "lib-push";

    public static void v(String message) {
        if (DEBUG)
            android.util.Log.v(TAG, message);
    }

    public static void d(String message) {
        if (DEBUG)
            android.util.Log.d(TAG, message);
    }

    public static void i(String message) {
        if (DEBUG)
            android.util.Log.i(TAG, message);
    }

    public static void w(String message) {
        if (DEBUG)
            android.util.Log.w(TAG, message);
    }

    public static void e(String message) {
        if (DEBUG)
            android.util.Log.e(TAG, message);
    }

    public static void e(Throwable e) {
        if (DEBUG)
            android.util.Log.e(TAG, "", e);
    }

    public static void e(String message, Exception e) {
        if (DEBUG)
            android.util.Log.e(TAG, message, e);
    }

    public static void e(String tag, String message, Throwable e) {
        if (DEBUG) {
            android.util.Log.e(tag, message, e);
        }
    }
}
