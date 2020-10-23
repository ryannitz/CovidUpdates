package com.ryannitz.covidupdates;

import java.util.HashMap;
import java.util.Map;

public class CovidStats{

    enum Prov{
        AB, BC, MT, NB, NL, NT, NS, NV, ON, PE, QC, SK, YK,

    }

    public static int AB = 1;
    public static int BC = 2;
    public static int MT = 3;
    public static int NB = 4;
    public static int NL = 5;
    public static int NT = 6;
    public static int NS = 7;
    public static int NV = 8;
    public static int ON = 9;
    public static int PE = 10;
    public static int QC = 11;
    public static int SK = 12;
    public static int YK = 13;

    //keys for mainobj
    public static String KEY_FILENAME = "filename";
    public static String KEY_PROVINCE_NAME = "provinceName";
    public static String KEY_REQUEST_TIME = "requestTime";
    public static String KEY_REQUEST_DURATION ="requestDuration";
    public static String KEY_DATASIZE = "dataSize";
    public static String KEY_ATTRIBUTES = "attributes";
    public static String KEY_DIFFS = "attributeDiffs";

    //keys for "attributes" objs
    public static Map<String, String> nbKeyLabelMap = new HashMap<String, String>(){{
        put("TotalCases","Total Cases");//get feild[{alias}[n]]
        put("NewToday","New Today");
        put("ActiveCases","Active Cases");
        put("Recovered","Recovered");
        put("Deaths","Deaths");
        put("TotalTests","Total Tests");
        put("Hospitalised","Currently Hospitalised");
        put("ICU","Currenty in ICU");
        put("TltHospitald","Total Hospitalised");
        put("DischHosp","Discharged from Hospital");
        put("TravelRel","Travel Related");
        put("ClsContact","Close Contact Related");
        put("CommTrnsmsn","Commute Transmission");
        put("UnderInves","Under Investigation");
    }};
    public static String KEY_NB_LASTSOURCEUPDATE = "LastUpdate";
    public static String KEY_NB_NEWTODAY = "NewToday";


    public static Map<String, String> getKeyMap(Prov province){
        switch (province){
            case AB:;
            case BC:;
            case MT:;
            case NB: return nbKeyLabelMap;
            case NL:;
            case NT:;
            case NS:;
            case NV:;
            case ON:;
            case PE:;
            case QC:;
            case SK:;
            case YK:;
            default:;
        }
        return null;
    }
}
