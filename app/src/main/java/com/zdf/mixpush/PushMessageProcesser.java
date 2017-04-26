package com.zdf.mixpush;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.zdf.lib_push.PushCallback;
import com.zdf.lib_push.model.Message;
import com.zdf.lib_push.utils.Log;

import org.json.JSONObject;

import java.util.Random;

/**
 * Created by xiaofeng on 2017/4/24.
 */

public class PushMessageProcesser implements PushCallback {

    private Context context;

    public PushMessageProcesser(Context context) {
        this.context = context;
    }

    @Override
    public void onRegister(Context context, String registerID) {
        Log.v("onRegister, registerID = " + registerID);
    }

    @Override
    public void onUnRegister(Context context) {
        Log.v("onUnRegister");
    }

    @Override
    public void onPaused(Context context) {

    }

    @Override
    public void onResume(Context context) {

    }

    @Override
    public void onMessage(Context context, Message message) {
        Log.v("onMessage, message = " + message);
    }

    @Override
    public void onMessageClicked(Context context, Message message) {
        Log.v("onMessageClicked, message = " + message);
    }

    @Override
    public void onCustomMessage(Context context, Message message) {
        Log.v("onCustomMessage, message = " + message);
        try {
            JSONObject object = new JSONObject(message.getCustom());
            String title = object.getString("title");
            String content = object.getString("text");
            showNotify(title, content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAlias(Context context, String alias) {

    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void showNotify(String title, String content) {
        PendingIntent pendingIntent = getPendingIntent();
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(icon)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setTicker(content)
                .setContentTitle(title)
                .setContentText(content)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setVisibility(Notification.VISIBILITY_PUBLIC);

        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notification = builder.build();
        } else {
            notification = builder.getNotification();
        }
        int dummyuniqueInt = new Random().nextInt();
        notificationManager.notify(dummyuniqueInt, notification);
    }
}
