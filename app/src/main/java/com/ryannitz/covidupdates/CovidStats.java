package com.ryannitz.covidupdates;

import java.util.HashMap;
import java.util.Map;

public class CovidStats{

    //keys for mainobj
    public static String KEY_FILENAME = "filename";
    public static String KEY_PROVINCE_NAME = "provinceName";
    public static String KEY_REQUEST_TIME = "requestTime";
    public static String KEY_REQUEST_DURATION ="requestDuration";
    public static String KEY_DATASIZE = "dataSize";
    public static String KEY_ATTRIBUTES = "attributes";

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

}
