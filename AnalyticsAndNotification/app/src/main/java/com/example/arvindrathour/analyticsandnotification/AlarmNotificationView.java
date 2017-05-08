package com.example.arvindrathour.analyticsandnotification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;


/***
 * Notification Class for showing notifications when the time comes for the alarm
 */

public class AlarmNotificationView {

    /**
     * Method for Showing Notification in Status bar
     *
     * @param title            : Title of Task
     * @param contentText      : Detail of Task
     * @param notificationId   : Notification ID of Task
     * @param data             :Uri of Alarm for showing the alarm
     */
    public static void show(String title, String contentText, int notificationId, Uri data) {
        Context context=App.getCotext();
        //Notification Builder for Showing the data in Notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(App.getCotext().getResources().getColor(R.color.colorAccent))
                .setContentTitle(title)
                .setContentText(contentText);
        //Notification Manager For managing the notification
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //Intent for calling the receiver class

//        Intent resultIntent = new Intent(context, AlarmReceiver.class);
//        resultIntent.setAction(StringConstants.ALARM_NOTIFICATION);
//        resultIntent.setData(data);

        //Pending Intent for pending the intent to a particular time

//        PendingIntent resultPendingIntent = PendingIntent.getBroadcast
//                (context, notificationId, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//        builder.setContentIntent(resultPendingIntent);

        //Building notification using NotificationCompat Builder

        Notification buildNotification = builder.build();
        //For clearing the Notification when user touch the notification
//        buildNotification.flags |= Notification.FLAG_AUTO_CANCEL;
//        //For clearing the Notification when user clicks clear all notification
//        buildNotification.flags |= Notification.FLAG_NO_CLEAR;

        //notify the user
        notificationManager.notify(notificationId, buildNotification);
    }
}