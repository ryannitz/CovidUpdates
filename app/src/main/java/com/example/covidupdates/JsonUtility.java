package com.example.covidupdates;

import android.content.Context;
import android.util.Log;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

public class JsonUtility {

    public static JSONObject parseResponseToJson(String rawResponseData){
        if(rawResponseData != null){
            try {
                JSONObject responseObj = new JSONObject(rawResponseData);
                return responseObj.getJSONArray("features").getJSONObject(0);
            }catch(JSONException jse){
                jse.printStackTrace();
            }
        }

        return null;
    }

    public static JSONObject parseJsonPairs(JSONObject json, Map<String, String> keyVals){
        try {
            JSONObject keyPairs = json.getJSONObject(CovidStats.KEY_ATTRIBUTES);
            ArrayList<String> removedKeys = new ArrayList<>();
            Iterator<String> keys = keyPairs.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                if (!keyVals.containsKey(key)) {
                    removedKeys.add(key);
                }
            }
            for(String key : removedKeys){
                keyPairs.remove(key);
            }
            return keyPairs;
        }catch(JSONException jse){
            jse.printStackTrace();
        }
        return null;
    }

    public static JSONObject createJsonObject(CasesHTTPRequester cassesRequest, JSONObject attributes, String filename){
        try {
            JSONObject attrKeyPairs = attributes.getJSONObject(CovidStats.KEY_ATTRIBUTES);
            JSONObject mainObj = new JSONObject();
            mainObj.put("filename", filename);
            mainObj.put("provinceName", "NB");
            mainObj.put("requestTime", cassesRequest.getRequestTime());
            mainObj.put("requestDuration", cassesRequest.getRequestDuration());
            mainObj.put("dataSize", cassesRequest.getDataSize());
            mainObj.put("LastUpdate", attributes.getJSONObject(CovidStats.KEY_ATTRIBUTES).getLong(CovidStats.KEY_NB_LASTSOURCEUPDATE));
            JSONObject attrs = parseJsonPairs(attributes, CovidStats.nbKeyLabelMap);
            mainObj.put("attributes", attrs);
            Log.e("JSON", mainObj.toString());
            return mainObj;
        }catch(JSONException jse){
            jse.printStackTrace();
        }
        return null;
    }

    public static JSONObject stringTOJson(String str){
        try{
            return new JSONObject(str);
        }catch(JSONException jse){
            jse.printStackTrace();
        }
        return null;
    }

//    public static ArrayList<String> compareJsonFiles(JSONObject newJson, JSONObject oldJson){
////        Log.e("JSON-new", newJson.toString());
////        Log.e("JSON-old", oldJson.toString());
//        boolean diff = false;
//        ArrayList<String> diffKeys= new ArrayList<>();
//        try {
//            JSONObject newKeyPairs = newJson.getJSONObject(CovidStats.KEY_ATTRIBUTES);
//            JSONObject oldKeyPairs = oldJson.getJSONObject(CovidStats.KEY_ATTRIBUTES);
//            Iterator<String> keys = newKeyPairs.keys();
//            while (keys.hasNext()) {
//                String key = keys.next();
//                Log.e("DIFF", "NewVal: " + newKeyPairs.get(key).toString() + ", OldVal: " + oldKeyPairs.get(key).toString());
//                //always update if there are new cases (would not update if newcases yesterday == new cases today)
//                if(key.equals(CovidStats.KEY_NB_NEWTODAY)){
//                    if(newKeyPairs.getInt(key) > 0){
//                        diff = true;
//                        diffKeys.add(key);
//                    }
//                }else if(!newKeyPairs.get(key).toString().equals(oldKeyPairs.get(key).toString())){
//                    diff = true;
//                    diffKeys.add(key);
//                    Log.e("DIFF", "Key with different value:" + key);
//                    Log.e("DIFF", "\tValues of key:" + newKeyPairs.get(key) + ":" +oldKeyPairs.get(key));
//                }
//            }
//            return diffKeys;
//        }catch(JSONException jse){
//            jse.printStackTrace();
//        }
//
//        return null;
//    }

    public static boolean compareJsonObject(JSONObject obj1, JSONObject obj2){
         return obj1.toString().equals(obj2.toString());
    }

    public static String prettyPrintJson(String json){
        try {
            return new JSONObject(json).toString(4);
        }catch (JSONException je){
            je.printStackTrace();
        }
        return "Failure while printing JSON";
    }

    public static ArrayList<String> getStatsDiff(JSONObject newObj, JSONObject oldObj){
        ArrayList<String> diffStrings = new ArrayList<>();
        try {
            JSONObject newObjAttrs = newObj.getJSONObject(CovidStats.KEY_ATTRIBUTES);
            JSONObject oldObjAttrs = oldObj.getJSONObject(CovidStats.KEY_ATTRIBUTES);
            Iterator<String> keys = oldObjAttrs.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                //Log.e("DIFF", "NewVal: " + newObj.get(key).toString() + ", OldVal: " + oldObj.get(key).toString());
                //always update if there are new cases (would not update if newcases yesterday == new cases today)
                if(key.equals(CovidStats.KEY_NB_NEWTODAY)){

                }else if(!newObjAttrs.get(key).toString().equals(oldObjAttrs.get(key).toString())){
                    String newCases = "- " + CovidStats.nbKeyLabelMap.get(key) + ": " + newObjAttrs.getString(key) + " (";
                    newCases += newObjAttrs.getInt(key) >= oldObjAttrs.getInt(key)?"+":"-";
                    newCases += Math.abs(oldObjAttrs.getInt(key) - newObjAttrs.getInt(key)) +")\n";
                    diffStrings.add(newCases);
                    Log.e("DIFF", "Key with different value:" + key);
                    Log.e("DIFF", "\tValues of key:" + newObjAttrs.get(key) + ":" +oldObjAttrs.get(key));
                }
            }
                Calendar curTime = Calendar.getInstance();
                curTime.setTimeInMillis(System.currentTimeMillis());
                Calendar lastUpdate = Calendar.getInstance();
                lastUpdate.setTimeInMillis(oldObj.getLong(CovidStats.KEY_NB_LASTSOURCEUPDATE));
                if(curTime.get(Calendar.HOUR_OF_DAY) == lastUpdate.get(Calendar.HOUR_OF_DAY) || diffStrings.size() > 0){
                    String newCases = "- New cases: " + newObjAttrs.getString(CovidStats.KEY_NB_NEWTODAY) + " (";
                    newCases += newObjAttrs.getInt(CovidStats.KEY_NB_NEWTODAY) >= oldObjAttrs.getInt(CovidStats.KEY_NB_NEWTODAY)?"+":"-";
                    newCases += Math.abs(oldObjAttrs.getInt(CovidStats.KEY_NB_NEWTODAY) - newObjAttrs.getInt(CovidStats.KEY_NB_NEWTODAY)) +")\n";
                    diffStrings.add(newCases);
                    diffStrings = swapValues(diffStrings, 0, diffStrings.size()-1);
                }

            return diffStrings;
        }catch(JSONException jse){
            jse.printStackTrace();
        }

        return null;
    }

    public static ArrayList<String> swapValues(ArrayList<String> a, int p1, int p2){
        String p2Val = a.get(p2);
        a.set(p2, a.get(p1));
        a.set(p1, p2Val);
        return a;
    }

}
