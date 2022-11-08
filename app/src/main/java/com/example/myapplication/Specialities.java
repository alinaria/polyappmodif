package com.example.myapplication;

//BEGIN CODE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

/*
{
    "A1":           4810,
    "A1 STI2D":     13152,
    "A2":           3164,
    "A2 STI2D":     799,
    "A3 GC":        28501,
    "A3 GPSE":      28550,
    "A3 ICM":       8018,
    "A3 PROD":      6516,
    "A3 SB":        1924,
    "A3 TEAM":      28498,
    "A3 GI FISA":   74966,
    "A3 GI FISE":   9104,
    "A4 GC":        28452,
    "A4 GPSE":      28471,
    "A4 ICM":       2849,
    "A4 PROD":      2022,
    "A4 SB":        8164,
    "A4 TEAM":      2890,
    "A4 GI FISA":   32693,
    "A4 GI FISE":   12939,
    "A5 GC":        1890,
    "A5 GPSE":      1339,
    "A5 ICM":       2903,
    "A5 PROD":      6521,
    "A5 SB":        10326,
    "A5 TEAM":      3176,
    "A5 GI FISA":   1868,
    "A5 GI FISE":   548,
    "DU IoT":       23,
    "Master AESM":  1656
}
 */


public class Specialities {
    private static final String[] keys = new String[]{
            "A1", "A1 STI2D", "A2", "A2 STI2D",
            "A3 GC", "A3 GPSE", "A3 ICM", "A3 PROD", "A3 SB", "A3 TEAM", "A3 GI FISA", "A3 GI FISE",
            "A4 GC", "A4 GPSE", "A4 ICM", "A4 PROD", "A4 SB", "A4 TEAM", "A4 GI FISA", "A4 GI FISE",
            "A3 GC", "A5 GPSE", "A5 ICM", "A5 PROD", "A5 SB", "A5 TEAM", "A5 GI FISA", "A5 GI FISE",
            "DU IoT" , "Master AESM"
    };
    private static final int[] values = new int[]{
            4810, 13152, 3164, 799,
            28501, 28550, 8018, 6516, 1924, 28498, 74966, 9104,
            28452, 28471, 2849, 2022, 8164, 2890, 32693, 12939,
            1890, 1339, 2903, 6521, 10326, 3176, 1868, 548,
            23, 1656
    };

    public String[] listSpecialities(){
        return keys;
    }

    public int[] listResourcesIDs(){
        return values;
    }

    public String getSpecialityByID(int ID){
        int index = -1;
        for(int i=0; i<values.length;i++){
            if(ID == values[i]){
                index = i;
                break;
            }
        }
        if(index >= 0){
            return keys[index];
        }
        return null;
    }

    public int getIDBySpeciality(String speciality){
        int index = -1;
        for(int i=0; i<keys.length;i++){
            if(speciality == keys[i]){
                index = i;
                break;
            }
        }
        if(index >= 0){
            return values[index];
        }
        return -1;
    }

}


// Class that permits to get parsing status
class NbParsing {
    public int value;
    // default constructor
    public NbParsing()
    {
        value = 0;
    }
}
