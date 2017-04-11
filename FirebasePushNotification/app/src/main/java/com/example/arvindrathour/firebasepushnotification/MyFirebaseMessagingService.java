package com.example.arvindrathour.firebasepushnotification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 *
 * get notification when activity in foreground or background
 * Created by Arvind Rathour on 07-Apr-17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Intent intent=new Intent(this ,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder nBuilder=new NotificationCompat.Builder(this);
        nBuilder.setContentTitle("FCM NOTIFICATION");
        nBuilder.setContentText(remoteMessage.getNotification().getBody());
        nBuilder.setAutoCancel(true);
        nBuilder.setSmallIcon(R.mipmap.ic_launcher);
        nBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0,nBuilder.build());

    }
}
