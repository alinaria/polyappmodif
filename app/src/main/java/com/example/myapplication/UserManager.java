package com.example.myapplication;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class UserManager {
    DBManager m_db;

    public UserManager(DBManager db){
        m_db = db;
    }

    public List<UserStruct> getUsersByPromoID(int resourceID){

        String selectionKeys = null;
        String[] selectionArgs = null;

        if(resourceID >= 0){
            selectionKeys = "( " + DBSyntax.RID + " = ? )";
            selectionArgs = new String[]{String.valueOf(resourceID)};
        }

        Cursor DBEvents =  m_db.selectFromUsers(selectionKeys,selectionArgs,null);

        List<UserStruct> userStructList = new ArrayList<UserStruct>();

        if(!DBEvents.isAfterLast()){
            userStructList.add(new UserStruct(DBEvents));
            while (DBEvents.moveToNext()) {
                userStructList.add(new UserStruct(DBEvents));
            }
        }

        DBEvents.close();

        return userStructList;
    }

    public List<UserStruct> getFriends(){

        String selectionKeys = null;
        String[] selectionArgs = null;

        Cursor DBEvents =  m_db.selectFromUsers(null,null,null);

        List<UserStruct> userStructList = new ArrayList<UserStruct>();

        if(!DBEvents.isAfterLast()){
            while (DBEvents.moveToNext()) {
                userStructList.add(new UserStruct(DBEvents));
            }
        }

        DBEvents.close();

        return userStructList;
    }

    public UserStruct getUserById(int userID){

        String selectionKeys = "( " + DBSyntax.UID + " = ? )";
        String[] selectionArgs = new String[]{String.valueOf(userID)};

        Cursor DBEvents =  m_db.selectFromUsers(selectionKeys,selectionArgs,null);

        UserStruct userStruct = null;

        if(!DBEvents.isAfterLast()){
            userStruct = new UserStruct(DBEvents);
        }

        DBEvents.close();

        return userStruct;
    }

    public UserStruct getMainUser(){
        return getUserById(0);
    }

    public void createUserByPromoID(String first_name, String last_name, int promoID){
        m_db.insertUser(first_name,last_name,promoID);
    }

    public void createUserByPromoName(String first_name, String last_name, String promoName){
        Specialities s = new Specialities();
        int promoID = s.getIDBySpeciality(promoName);
        createUserByPromoID(first_name,last_name,promoID);
    }

    public void deleteUser(int userID){
        String selectionKeys = "( " + DBSyntax.UID + " = ? )";
        String[] selectionArgs = new String[]{String.valueOf(userID)};

        m_db.deleteUser(selectionKeys,selectionArgs);
    }

}
