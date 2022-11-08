package com.example.myapplication;

import android.database.Cursor;

public class UserStruct {
    public String first_name;
    public String last_name;

    public int user_ID;

    private int promo_ID;

    /**
     * Constructor of the UserStruct class
     * @param row The result of fetch or select functions of DBManager class
     */
    public UserStruct(Cursor row){
        user_ID = row.getInt(0);
        first_name = row.getString(1);
        last_name = row.getString(2);
        promo_ID = row.getInt(3);
    }

    /**
     * Get promo ID
     * @return promo ID inside a int format
     */
    public int getPromoID(){
        return promo_ID;
    }

    /**
     * Get promo name (human readable version)
     * @return promo name in String format
     */
    public String getPromoName(){
        Specialities s = new Specialities();
        return s.getSpecialityByID(promo_ID);
    }

}
