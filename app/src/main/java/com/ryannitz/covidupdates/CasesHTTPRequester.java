package com.ryannitz.covidupdates;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;


import androidx.annotation.Nullable;

import com.ryannitz.covidupdates.utility.FileHandler;
import com.ryannitz.covidupdates.utility.JsonUtility;
import com.ryannitz.covidupdates.utility.NotificationUtility;
import com.ryannitz.covidupdates.utility.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

public class CasesHTTPRequester extends AsyncTask<Void, Void, Void> {


    private String dataSize;
    private long requestTime;
    private long requestDuration;
    private Context ctx;
    private String urlStr;
    private boolean sendNotification;
    private boolean enableFakeResponse;
    private String response;

    private MainPageDataContainer mainPageDataContainer;

    public CasesHTTPRequester(@Nullable MainPageDataContainer mainPageDataContainer, Context ctx, UserSettings userSettings, boolean sendNotification, boolean enableFakeResponse){
        this.mainPageDataContainer = mainPageDataContainer;
        this.ctx = ctx;
        this.urlStr = URIs.getProvinceURI(userSettings.getSelectedProvince());
        this.sendNotification = sendNotification;
        this.enableFakeResponse = enableFakeResponse;
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
            dataSize = Utility.calcStringDataSize(response);
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
            JSONObject newObj = JsonUtility.createJsonObject(this, parsedData, FileHandler.NB_JSON_FILENAME);

            if(FileHandler.provinceHasJsonFile(ctx, FileHandler.NB_JSON_FILENAME)){
                JSONObject oldObj = new JSONObject(FileHandler.getFileContents(ctx, FileHandler.NB_JSON_FILENAME));
                //ArrayList<String> diffKeys = JsonUtility.compareJsonFiles(newObj, oldObj);
                ArrayList<String> diffString = JsonUtility.getStatsDiff(newObj, oldObj);

                if(diffString != null && diffString.size() > 0 && sendNotification){
                    //send notification
                    String tmp = "";
                    for(String str : diffString){
                        tmp += str;
                    }
                    NotificationUtility.sendNotification(ctx, tmp);
                }
            }
            FileHandler.createJsonFile(ctx, FileHandler.NB_JSON_FILENAME, newObj.toString());
            if(MainActivity.active){
                mainPageDataContainer.createDataViews(ctx, newObj);

            }

        }catch (JSONException jse){
            jse.printStackTrace();
        }
    }

    public String getDataSize() {
        return dataSize;
    }

    public void setDataSize(String dataSize) {
        this.dataSize = dataSize;
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
