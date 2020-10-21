package com.ryannitz.covidupdates;
import com.ryannitz.covidupdates.CovidStats.Prov;


public class URIs {
    public static String NB_URI = "https://services5.arcgis.com/WO0dQcVbxj7TZHkH/arcgis/rest/services/HealthZones/FeatureServer/0/query?f=json&where=HealthZone%3D%27Province%27&returnGeometry=false&spatialRel=esriSpatialRelIntersects&outFields=*&resultOffset=0&resultRecordCount=50&resultType=standard&cacheHint=true";

    public static String PLACEHOLDER_RESPONSE = "{\"objectIdFieldName\":\"OBJECTID\",\"uniqueIdField\":{\"name\":\"OBJECTID\",\"isSystemMaintained\":true},\"globalIdFieldName\":\"\",\"geometryProperties\":{\"shapeAreaFieldName\":\"Shape__Area\",\"shapeLengthFieldName\":\"Shape__Length\",\"units\":\"esriMeters\"},\"geometryType\":\"esriGeometryPolygon\",\"spatialReference\":{\"wkid\":2036,\"latestWkid\":2953},\"fields\":[{\"name\":\"OBJECTID\",\"type\":\"esriFieldTypeOID\",\"alias\":\"OBJECTID\",\"sqlType\":\"sqlTypeOther\",\"domain\":null,\"defaultValue\":null},{\"name\":\"RecoveryPhase\",\"type\":\"esriFieldTypeString\",\"alias\":\"Recovery Phase\",\"sqlType\":\"sqlTypeOther\",\"length\":256,\"domain\":null,\"defaultValue\":null},{\"name\":\"HealthZnEng\",\"type\":\"esriFieldTypeString\",\"alias\":\"Health Zone\",\"sqlType\":\"sqlTypeOther\",\"length\":255,\"domain\":null,\"defaultValue\":null},{\"name\":\"HealthZnFre\",\"type\":\"esriFieldTypeString\",\"alias\":\"Zone de Santé\",\"sqlType\":\"sqlTypeOther\",\"length\":255,\"domain\":null,\"defaultValue\":null},{\"name\":\"PhasesRétab\",\"type\":\"esriFieldTypeString\",\"alias\":\"Phases de Rétablissement\",\"sqlType\":\"sqlTypeOther\",\"length\":255,\"domain\":null,\"defaultValue\":null},{\"name\":\"HealthZone\",\"type\":\"esriFieldTypeString\",\"alias\":\"Zone Label\",\"sqlType\":\"sqlTypeOther\",\"length\":100,\"domain\":null,\"defaultValue\":null},{\"name\":\"TotalCases\",\"type\":\"esriFieldTypeInteger\",\"alias\":\"Total Cases\",\"sqlType\":\"sqlTypeOther\",\"domain\":null,\"defaultValue\":null},{\"name\":\"NewToday\",\"type\":\"esriFieldTypeInteger\",\"alias\":\"New Today\",\"sqlType\":\"sqlTypeOther\",\"domain\":null,\"defaultValue\":null},{\"name\":\"ActiveCases\",\"type\":\"esriFieldTypeInteger\",\"alias\":\"Active\",\"sqlType\":\"sqlTypeOther\",\"domain\":null,\"defaultValue\":null},{\"name\":\"Recovered\",\"type\":\"esriFieldTypeInteger\",\"alias\":\"Recovered\",\"sqlType\":\"sqlTypeOther\",\"domain\":null,\"defaultValue\":null},{\"name\":\"Deaths\",\"type\":\"esriFieldTypeInteger\",\"alias\":\"Deaths\",\"sqlType\":\"sqlTypeOther\",\"domain\":null,\"defaultValue\":null},{\"name\":\"TotalTests\",\"type\":\"esriFieldTypeInteger\",\"alias\":\"Total Tests\",\"sqlType\":\"sqlTypeOther\",\"domain\":null,\"defaultValue\":null},{\"name\":\"Hospitalised\",\"type\":\"esriFieldTypeInteger\",\"alias\":\"In Hospital\",\"sqlType\":\"sqlTypeOther\",\"domain\":null,\"defaultValue\":null},{\"name\":\"ICU\",\"type\":\"esriFieldTypeInteger\",\"alias\":\"In ICU\",\"sqlType\":\"sqlTypeOther\",\"domain\":null,\"defaultValue\":null},{\"name\":\"TtlHospitald\",\"type\":\"esriFieldTypeInteger\",\"alias\":\"Total Hospitalizations\",\"sqlType\":\"sqlTypeOther\",\"domain\":null,\"defaultValue\":null},{\"name\":\"DischHosp\",\"type\":\"esriFieldTypeInteger\",\"alias\":\"Discharged from Hospital\",\"sqlType\":\"sqlTypeOther\",\"domain\":null,\"defaultValue\":null},{\"name\":\"TravelRel\",\"type\":\"esriFieldTypeInteger\",\"alias\":\"Travel Related\",\"sqlType\":\"sqlTypeOther\",\"domain\":null,\"defaultValue\":null},{\"name\":\"ClsContct\",\"type\":\"esriFieldTypeInteger\",\"alias\":\"Close Contact\",\"sqlType\":\"sqlTypeOther\",\"domain\":null,\"defaultValue\":null},{\"name\":\"CommTrnsmsn\",\"type\":\"esriFieldTypeInteger\",\"alias\":\"Community Transmission\",\"sqlType\":\"sqlTypeOther\",\"domain\":null,\"defaultValue\":null},{\"name\":\"UnderInves\",\"type\":\"esriFieldTypeInteger\",\"alias\":\"Under Investigation\",\"sqlType\":\"sqlTypeOther\",\"domain\":null,\"defaultValue\":null},{\"name\":\"LastUpdate\",\"type\":\"esriFieldTypeDate\",\"alias\":\"Last Update\",\"sqlType\":\"sqlTypeOther\",\"length\":8,\"domain\":null,\"defaultValue\":null},{\"name\":\"HealthZoneNum\",\"type\":\"esriFieldTypeString\",\"alias\":\"Zone Number\",\"sqlType\":\"sqlTypeOther\",\"length\":10,\"domain\":null,\"defaultValue\":null},{\"name\":\"Shape__Area\",\"type\":\"esriFieldTypeDouble\",\"alias\":\"Shape__Area\",\"sqlType\":\"sqlTypeDouble\",\"domain\":null,\"defaultValue\":null},{\"name\":\"Shape__Length\",\"type\":\"esriFieldTypeDouble\",\"alias\":\"Shape__Length\",\"sqlType\":\"sqlTypeDouble\",\"domain\":null,\"defaultValue\":null}],\"features\":[{\"attributes\":{\"OBJECTID\":1,\"RecoveryPhase\":\"\",\"HealthZnEng\":\"New Brunswick\",\"HealthZnFre\":\"Nouveau-Brunswick\",\"PhasesRétab\":\"\",\"HealthZone\":\"Province\",\"TotalCases\":305,\"NewToday\":7,\"ActiveCases\":92,\"Recovered\":203,\"Deaths\":7,\"TotalTests\":89852,\"Hospitalised\":4,\"ICU\":1,\"TtlHospitald\":23,\"DischHosp\":21,\"TravelRel\":110,\"ClsContct\":175,\"CommTrnsmsn\":10,\"UnderInves\":2,\"LastUpdate\":1602856800000,\"HealthZoneNum\":\"\",\"Shape__Area\":72948317901.3564,\"Shape__Length\":4152441.18246912}}]}";

    public static String getProvinceURI(Prov province){
        switch (province){
            case AB:;
            case BC:;
            case MT:;
            case NB: return NB_URI;
            case NL:;
            case NT:;
            case NS:;
            case NV:;
            case ON:;
            case PE:;
            case QC:;
            case SK:;
            case YK:;
            default: return null;
        }
    }

}
