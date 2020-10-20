package com.example.covidupdates;

import android.content.Context;
import android.util.Log;

public class Utility {

    public static String PACKAGE_NAME = "com.example.covidupdates";

    public static String calcStringDataSize(String str){
        if(str == null){
            //handle exception gracefully
            Log.e("E", "String was null");
        }else{
            int dataLevel = 0;
            double strLen = str.length();
            while(strLen > 1024){
                strLen /= 1024;
                dataLevel++;
            }
            strLen = Math.round(strLen*100)/100;
            String levelStr = new String[]{"","KB","MB","GB","TB"}[dataLevel];
            return strLen + levelStr;
        }
        return null;
    }

    public static String millisToTime(long millis){
        String rtnTime = "(";
        int milliseconds = (int) (millis/1000) / 60;
        int seconds = (int) (millis/1000) % 60;
        int minutes = (int) (millis/1000/60) % 60;
        int hours = (int) (millis/1000/60/60) % 24;
        if(hours > 0){
            rtnTime += hours+"h ";
        }
        if(minutes >= 0){
            rtnTime += minutes+"m ";
        }
        if(seconds > 0){
            rtnTime += seconds+"s ";
        }
        return rtnTime + "ago)";
    }

    public static boolean isAppRunning(Context ctx, String packageName){
        return MainActivity.active;
    }

}
