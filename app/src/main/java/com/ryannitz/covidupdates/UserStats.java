package com.ryannitz.covidupdates;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryannitz.covidupdates.utility.FileHandler;
import com.ryannitz.covidupdates.utility.Logger;
import com.ryannitz.covidupdates.CovidStats.Prov;

import java.io.IOException;

public class UserStats {

    private boolean rawJsonOn;
    private Prov selectedProvince;
    private int totalRequests;
    private long totalDataRetrieved;
    private int totalManualRequests;
    private int totalBackgroundRequests;

    public UserStats(){
        //default constructor;
    }

    public boolean getRawJsonOn(){
        return rawJsonOn;
    }

    public void setRawJsonOn(boolean rawJsonOn){
        this.rawJsonOn = rawJsonOn;
    }

    public void setSelectedProvince(Prov selectedProvince){
        this.selectedProvince = selectedProvince;
    }

    public Prov getSelectedProvince(){
        return selectedProvince;
    }

    public int getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(int totalRequests) {
        this.totalRequests = totalRequests;
    }

    public long getTotalDataRetrieved() {
        return totalDataRetrieved;
    }

    public void setTotalDataRetrieved(long totalDataRetrieved) {
        this.totalDataRetrieved = totalDataRetrieved;
    }

    public int getTotalManualRequests() {
        return totalManualRequests;
    }

    public void setTotalManualRequests(int totalManualRequests) {
        this.totalManualRequests = totalManualRequests;
    }

    public int getTotalBackgroundRequests() {
        return totalBackgroundRequests;
    }

    public void setTotalBackgroundRequests(int totalBackgroundRequests) {
        this.totalBackgroundRequests = totalBackgroundRequests;
    }


    public static UserStats updateSettings(Context ctx, UserStats userStats){
        try{
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(userStats);
            FileHandler.createJsonFile(ctx, FileHandler.USER_SETTINGS, json);
            return userStats;
        }catch (JsonProcessingException jpe){
            jpe.printStackTrace();
        }
        return null;
    }

    public static UserStats loadUserSettings(Context ctx){
        try{
            if(FileHandler.doesFileExist(ctx, FileHandler.USER_SETTINGS)){
                Log.e(Logger.FILE, "Found existing user settingFragment file:");
                String json = FileHandler.getFileContents(ctx, FileHandler.USER_SETTINGS);
                Log.e(Logger.JSON, json);
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(json, UserStats.class);
            }else{
                //load the default from the assets
                return resetUserSettings(ctx);
            }
        }catch(IOException e){
            e.printStackTrace();
            return null;
        }
    }

    public static UserStats resetUserSettings(Context ctx){
        Log.e(Logger.FILE, "Creating new user settingFragment file");
        String json = FileHandler.getAssetContents(ctx, FileHandler.USER_SETTINGS);
        FileHandler.createJsonFile(ctx, FileHandler.USER_SETTINGS, json);
        Log.e(Logger.JSON, json);
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, UserStats.class);
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}

