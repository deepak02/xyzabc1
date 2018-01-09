package com.sekhontech.singering.Notifications;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sekhontech.singering.Activities.Notifications;
import com.sekhontech.singering.R;
import com.sekhontech.singering.circleMenu.CircleActivity;

import java.util.List;


/**
 * Created by ST_002 on 02-03-2016.
 */
public class FireMsgService extends FirebaseMessagingService {
    public static final String TAG = "FireMsgService";
    private long value = System.currentTimeMillis();
    String check2, check3;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            String title = remoteMessage.getNotification().getTitle();
            String message = remoteMessage.getNotification().getBody();
            if (title!=null)
            {
                sendNotification(message + " - " + title);
            }else {
                sendNotification(message);
            }
           // handleNotification();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void sendNotification(String messageBody) {

        Intent intent = new Intent(this, Notifications.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.mic_icon)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(messageBody)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify((int) value, notificationBuilder.build());
    }


    // Playing notification sound
    public void playNotificationSound() {

        try {
            // Uri alarmSound1 =Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.mba);
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/*   isAppIsInBackground(Settings.this);
    if (!isAppIsInBackground(getApplicationContext()) {
        // play notification sound
        Toast.makeText(Settings.this, "Forground", Toast.LENGTH_SHORT).show();

    } else {
        Toast.makeText(Settings.this, "background", Toast.LENGTH_SHORT).show();

    }*/


 /*   private void handleNotification(*//*String message, String title*//*) {
        if (!isAppIsInBackground(getApplicationContext())) {
            Log.d(TAG, "App_check: " + "Foreground");
            *//*sendNotification(message, title);
            playNotificationSound();*//*
            // play notification sound
        } else {
            Log.d(TAG, "App_check: " + "Background");
           *//* sendNotification(message, title);
            playNotificationSound();// If the app is in background, firebase itself handles the notification*//*
        }

    }*/


    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }


    private void sendNotification(String message, String title) {
        //  Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent intent = new Intent(this, CircleActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent
                = PendingIntent.getActivity(this, 1/* Request code */, intent, PendingIntent.FLAG_NO_CREATE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setSmallIcon(R.drawable.mic_icon)
                .setLargeIcon(BitmapFactory.decodeResource(FireMsgService.this.getResources(), R.drawable.mic_icon))
                .setContentTitle(title)
                .setContentText(message)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setTicker(message)
                .setPriority(Notification.PRIORITY_MAX)
                .setAutoCancel(true)
                // .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) value/* ID of notification */, notificationBuilder.build());
    }
}