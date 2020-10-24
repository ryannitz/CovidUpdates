package com.ryannitz.covidupdates.utility;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.ryannitz.covidupdates.MainActivity;
import com.ryannitz.covidupdates.R;

public class NotificationUtility {

    public static String CHANNEL_DESC = "Notify when covid data is updated and there are changes.";
    public static String CHANNEL_NAME = "Covid Data Updates";

    public static void sendNotification(Context ctx, String text){


        Intent intent = new Intent(ctx, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, NotificationUtility.getChannelID(ctx))
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_error_outline_black_24dp)
                .setContentTitle("Here are some new changes since last update:")
                .setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(), R.drawable.ic_stat_name))
                .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setContentIntent(pendingIntent);




        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ctx);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());
    }


    public static void createNotificationChannel(Context ctx,String chId, String chName, String chDesc, int chImportance, boolean showBadge){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(chId, chName, chImportance);
            channel.setDescription(chDesc);
            channel.setShowBadge(showBadge);
            channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
            channel.enableVibration(true);
            channel.enableLights(true);
            channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            createNotificationChannel(ctx, channel);
        }
    }

    public static void createNotificationChannel(Context ctx, NotificationChannel channel){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = ctx.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            Log.e(Logger.NOTIF, "New Notication channel created: " + channel.toString());
        }
    }

    public static void createDefaultNotificationChannel(Context ctx){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel(ctx, NotificationUtility.getChannelID(ctx), NotificationUtility.CHANNEL_NAME, NotificationUtility.CHANNEL_DESC, NotificationManager.IMPORTANCE_HIGH, true);
        }
    }

    private static String getChannelID(Context ctx){
        return ctx.getPackageName() + "-" + NotificationUtility.CHANNEL_NAME;
    }
}
