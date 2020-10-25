package com.ryannitz.covidupdates;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


import androidx.annotation.Nullable;

import com.ryannitz.covidupdates.utility.FileHandler;
import com.ryannitz.covidupdates.utility.JsonUtility;
import com.ryannitz.covidupdates.utility.Logger;
import com.ryannitz.covidupdates.utility.NotificationUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

public class CasesHTTPRequester extends AsyncTask<Void, Void, Void> {


    private long responseSize;
    private long requestTime;
    private long requestDuration;
    private Context ctx;
    private UserStats userStats;
    private String urlStr;
    private boolean sendNotification;
    private boolean enableFakeResponse;
    private boolean fromAlarm;
    private String response;

    private MainPageDataContainer mainPageDataContainer;

    public CasesHTTPRequester(@Nullable MainPageDataContainer mainPageDataContainer, Context ctx, UserStats userStats, boolean sendNotification, boolean enableFakeResponse, boolean fromAlarm){
        this.mainPageDataContainer = mainPageDataContainer;
        this.ctx = ctx;
        this.userStats = userStats;
        this.urlStr = URIs.getProvinceURI(userStats.getSelectedProvince());
        this.sendNotification = sendNotification;
        this.enableFakeResponse = enableFakeResponse;
        this.fromAlarm = fromAlarm;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try{
            long requestStartTime = System.currentTimeMillis();
            if(enableFakeResponse){
                response = URIs.PLACEHOLDER_RESPONSE;
            }else{
                URL uri = new URL(urlStr);
                HttpsURLConnection conn = (HttpsURLConnection) uri.openConnection();
                //conn.setRequestProperty("Content-Type", "application/json");
                InputStream is = conn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));

                String line = "";
                response = "";
                while(line != null){
                    line = br.readLine();
                    response += line;
                }
            }
            requestDuration = System.currentTimeMillis() - requestStartTime;
            responseSize = response.length();
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        requestTime = new Date().getTime();
        try {

            JSONObject parsedData = JsonUtility.parseResponseToJson(response);
            Log.e(Logger.JSON, "Parsed data:" + parsedData.toString());
            JSONObject newObj;
            if(!FileHandler.provinceHasJsonFile(ctx, FileHandler.NB_JSON_FILENAME)){
                //initial open of the app
                JSONObject attrDiffs = JsonUtility.getStatsDiffObject(parsedData, parsedData);
                newObj = JsonUtility.createJsonObject(this, parsedData, attrDiffs, FileHandler.NB_JSON_FILENAME);
            }else{
                //any other time aside from the first open of the app.
                JSONObject oldObj = new JSONObject(FileHandler.getFileContents(ctx, FileHandler.NB_JSON_FILENAME));
                JSONObject attrDiffs = JsonUtility.getStatsDiffObject(oldObj, parsedData);
                newObj = JsonUtility.createJsonObject(this, parsedData, attrDiffs, FileHandler.NB_JSON_FILENAME);

                ArrayList<String> diffString = JsonUtility.getStatsDiffStrings(newObj, oldObj);
                Log.e(Logger.ALARM, diffString.toString());
                if(diffString != null && diffString.size() > 0 && sendNotification){
                    //boolean updated = true
                    String tmp = "";
                    for(String str : diffString){
                        tmp += str;
                    }
                    NotificationUtility.sendNotification(ctx, tmp);
                }
            }

            //handle UI updates and such

            if(newObj != null){
                FileHandler.createJsonFile(ctx, FileHandler.NB_JSON_FILENAME, newObj.toString());
                if(fromAlarm){
                    if(MainActivity.active && mainPageDataContainer != null){
                        mainPageDataContainer.createDataViews(ctx, newObj);
                        //create toast to let user know the ui has updated in the background
                    }
                    userStats.setTotalBackgroundRequests(userStats.getTotalBackgroundRequests()+1);
                }else{
                    //we know it has to be from user update
                    mainPageDataContainer.createDataViews(ctx, newObj);
                }
            }

            if(!enableFakeResponse){
                userStats.setTotalDataRetrieved(userStats.getTotalDataRetrieved()+responseSize);
                userStats.setTotalRequests(userStats.getTotalRequests()+1);
                UserStats.updateSettings(ctx, userStats);
            }

        }catch (JSONException jse){
            jse.printStackTrace();
        }
    }

    public Long getResponseSize() {
        return responseSize;
    }

    public void setResponseSize(Long dataSize) {
        this.responseSize = dataSize;
    }

    public long getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(long requestTime) {
        this.requestTime = requestTime;
    }

    public long getRequestDuration() {
        return requestDuration;
    }

    public void setRequestDuration(long requestDuration) {
        this.requestDuration = requestDuration;
    }

    public Context getCtx() {
        return ctx;
    }

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }

    public String getUrlStr() {
        return urlStr;
    }

    public void setUrlStr(String urlStr) {
        this.urlStr = urlStr;
    }

    public boolean isSendNotification() {
        return sendNotification;
    }

    public void setSendNotification(boolean sendNotification) {
        this.sendNotification = sendNotification;
    }

    public boolean isEnableFakeResponse() {
        return enableFakeResponse;
    }

    public void setEnableFakeResponse(boolean enableFakeResponse) {
        this.enableFakeResponse = enableFakeResponse;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

}
