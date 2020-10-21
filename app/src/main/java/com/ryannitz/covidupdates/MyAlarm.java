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
        UserSettings userSettings = UserSettings.loadUserSettings(context);
        CasesHTTPRequester casesHTTPRequester = new CasesHTTPRequester(context, userSettings, true, false);
        casesHTTPRequester.execute();
    }
}
