package com.ryannitz.covidupdates.utility;

import android.util.Log;


import com.ryannitz.covidupdates.CasesHTTPRequester;
import com.ryannitz.covidupdates.CovidStats;

import org.json.JSONException;
import org.json.JSONObject;

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

    private static JSONObject parseJsonPairs(JSONObject json, Map<String, String> keyVals){
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

    public static JSONObject createJsonObject(CasesHTTPRequester cassesRequest, JSONObject attributes, JSONObject diffs, String filename){
        try {
            JSONObject mainObj = new JSONObject();
            mainObj.put("filename", filename);
            mainObj.put("provinceName", "NB");
            mainObj.put("requestTime", cassesRequest.getRequestTime());
            mainObj.put("requestDuration", cassesRequest.getRequestDuration());
            mainObj.put("dataSize", cassesRequest.getResponseSize());
            mainObj.put("LastUpdate", attributes.getJSONObject(CovidStats.KEY_ATTRIBUTES).getLong(CovidStats.KEY_NB_LASTSOURCEUPDATE));
            JSONObject attrs = parseJsonPairs(attributes, CovidStats.nbKeyLabelMap);
            mainObj.put("attributes", attrs);
            mainObj.put("attributeDiffs", diffs);
            Log.e("JSON", mainObj.toString());
            return mainObj;
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

//    public static boolean compareJsonObject(JSONObject obj1, JSONObject obj2){
//         return obj1.toString().equals(obj2.toString());
//    }
//
    public static String prettyPrintJson(String json){
        try {
            return new JSONObject(json).toString(4);
        }catch (JSONException je){
            je.printStackTrace();
        }
        return "Failure while printing JSON";
    }


    public static ArrayList<String> getStatsDiffStrings(JSONObject newObj, JSONObject oldObj){
        ArrayList<String> diffStrings = new ArrayList<>();
        try {
            JSONObject newAttrObj = newObj.getJSONObject(CovidStats.KEY_ATTRIBUTES);
            JSONObject newDiffObj = newObj.getJSONObject(CovidStats.KEY_DIFFS);

            JSONObject oldAttrObj = oldObj.getJSONObject(CovidStats.KEY_ATTRIBUTES);
            JSONObject oldDiffObj = oldObj.getJSONObject(CovidStats.KEY_DIFFS);

            Iterator<String> keys = newAttrObj.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                //Log.e("DIFF", "NewVal: " + newObj.get(key).toString() + ", OldVal: " + oldObj.get(key).toString());
                //always update if there are new cases (would not update if newcases yesterday == new cases today)
                if(!key.equals(CovidStats.KEY_NB_NEWTODAY)){
                    if(newAttrObj.getLong(key) != oldAttrObj.getLong(key)){
                        String newCases = "- " + CovidStats.nbKeyLabelMap.get(key) + ": " + newAttrObj.getString(key) + " (";
                        if(newDiffObj.getInt(key) >= 0){
                            newCases += "+";
                        }
                        newCases += newDiffObj.getString(key) + ")\n";
                        diffStrings.add(newCases);
                    }
                }
            }
            Calendar curTime = Calendar.getInstance();
            curTime.setTimeInMillis(System.currentTimeMillis());
            Calendar lastUpdate = Calendar.getInstance();
            lastUpdate.setTimeInMillis(newObj.getLong(CovidStats.KEY_NB_LASTSOURCEUPDATE));

            if(curTime.get(Calendar.HOUR_OF_DAY) == lastUpdate.get(Calendar.HOUR_OF_DAY) || diffStrings.size() > 0){
                String newCases = "- New cases: " + newAttrObj.getString(CovidStats.KEY_NB_NEWTODAY) + " (";
                if(newDiffObj.getInt(CovidStats.KEY_NB_NEWTODAY) >= 0){
                    newCases += "+";
                }
                newCases += newDiffObj.getString(CovidStats.KEY_NB_NEWTODAY) +")\n";
                diffStrings.add(newCases);
                diffStrings = swapValues(diffStrings, 0, diffStrings.size()-1);
            }

            return diffStrings;
        }catch(JSONException jse){
            jse.printStackTrace();
        }

        return null;
    }

    public static JSONObject getStatsDiffObject(JSONObject newObj, JSONObject oldObj){

        try {
            newObj = newObj.getJSONObject(CovidStats.KEY_ATTRIBUTES);
            oldObj = oldObj.getJSONObject(CovidStats.KEY_ATTRIBUTES);

            JSONObject diffObj = new JSONObject();
            Iterator<String> keys = oldObj.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                //Log.e("DIFF", "NewVal: " + newObj.get(key).toString() + ", OldVal: " + oldObj.get(key).toString());
                //always update if there are new cases (would not update if newcases yesterday == new cases today)
                if(CovidStats.nbKeyLabelMap.containsKey(key)){
                    diffObj.put(key, oldObj.getLong(key)-newObj.getLong(key));
                }
            }

            return diffObj;
        }catch(JSONException jse){
            jse.printStackTrace();
        }
        return null;
    }


    private static ArrayList<String> swapValues(ArrayList<String> a, int p1, int p2){
        String p2Val = a.get(p2);
        a.set(p2, a.get(p1));
        a.set(p1, p2Val);
        return a;
    }
}
