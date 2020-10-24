package com.ryannitz.covidupdates;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


import com.ryannitz.covidupdates.utility.Logger;

public class MyAlarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        UserStats userStats = UserStats.loadUserSettings(context);
        MainPageDataContainer mainPageDataContainer = null;
        if(MainActivity.active){
            mainPageDataContainer = (MainPageDataContainer) MainActivity.instance.getSupportFragmentManager().findFragmentById(R.id.mainDataContainer);
        }
        Log.e(Logger.ALARM, "ALARM FIRED");
        CasesHTTPRequester casesHTTPRequester = new CasesHTTPRequester(mainPageDataContainer, context, userStats, true, false, true);
        casesHTTPRequester.execute();
    }
}
