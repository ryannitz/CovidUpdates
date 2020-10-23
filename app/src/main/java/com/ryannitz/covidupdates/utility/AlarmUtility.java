package com.ryannitz.covidupdates.utility;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ryannitz.covidupdates.MyAlarm;

import java.util.Calendar;

public class AlarmUtility {
    public static void createNewAlarm(Context ctx){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, 11);
        cal.set(Calendar.MINUTE, 30);
        Log.e("ALARM TIME:", cal.getTime().toString());

        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ctx, MyAlarm.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 0, intent, 0);
        //real call
        alarmManager.setInexactRepeating(AlarmManager.RTC, cal.getTimeInMillis(), AlarmManager.INTERVAL_HOUR, pendingIntent);
        //dev call
        //alarmManager.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis(), 60000, pendingIntent);
    }

}
