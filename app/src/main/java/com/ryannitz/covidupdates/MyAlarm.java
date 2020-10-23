package com.ryannitz.covidupdates;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyAlarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //make uri call
        Log.e("ALARM", "ALARM FIRED");
        UserStats userStats = UserStats.loadUserSettings(context);
        CasesHTTPRequester casesHTTPRequester = new CasesHTTPRequester(null, context, userStats, true, false);
        casesHTTPRequester.execute();
    }
}
