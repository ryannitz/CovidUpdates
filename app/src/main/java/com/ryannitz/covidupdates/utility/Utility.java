package com.ryannitz.covidupdates.utility;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.ryannitz.covidupdates.MainActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class Utility {

    public static String PACKAGE_NAME = "com.example.covidupdates";

    public static Integer DEFAULT_JSON_FONT_INDEX = 3;
    public static float[] JSON_FONTSIZES = {5f, 10f, 15f, 25f, 30f, 40f, 50f, 60f};

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

    public static String calcDataSize(Long size){
        if(size == null){
            //handle exception gracefully
            Log.e("E", "No size given");
        }else{
            int dataLevel = 0;
            double length = size;
            while(length > 1024){
                length /= 1024;
                dataLevel++;
            }
            length = Math.round(length*100)/100;
            String levelStr = new String[]{"","KB","MB","GB","TB"}[dataLevel];
            return length + levelStr;
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
