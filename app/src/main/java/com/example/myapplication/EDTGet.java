package com.example.myapplication;

//BEGIN CODE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

import android.database.Cursor;
import android.icu.util.Calendar;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class EDTGet {

    DBManager m_db;

    public EDTGet(DBManager db){
        m_db = db;
    }

    public List<EventStruct> getForToday(int resourceID){

        Calendar currentTime = Calendar.getInstance();

        currentTime.set(Calendar.HOUR_OF_DAY,0);
        currentTime.set(Calendar.MINUTE,0);
        currentTime.set(Calendar.SECOND,0);
        long startTimestamp = currentTime.getTimeInMillis()/1000;

        currentTime.set(Calendar.HOUR_OF_DAY,23);
        currentTime.set(Calendar.MINUTE,59);
        currentTime.set(Calendar.SECOND,59);
        long endTimestamp = currentTime.getTimeInMillis()/1000;

        //Log.d("START",String.valueOf(startTimestamp));
        //Log.d("END",String.valueOf(endTimestamp));

        String selectionKeys = "(( " + DBSyntax.START   + " > ? ) AND ( "
                                     + DBSyntax.END   + " < ? )";

        if(resourceID >= 0){
           selectionKeys += " AND ( " + DBSyntax.RID + " = ? )";
        }

        selectionKeys += ")";

        String[] selectionArgs = null;

        if(resourceID >= 0) {
            selectionArgs = new String[]{   String.valueOf(startTimestamp),
                                            String.valueOf(endTimestamp),
                                            String.valueOf(resourceID)};
        }else {
            selectionArgs = new String[]{   String.valueOf(startTimestamp),
                                            String.valueOf(endTimestamp)};
        }

        Cursor DBEvents =  m_db.selectFrom(selectionKeys,selectionArgs,null);

        List<EventStruct> eventStructList = new ArrayList<EventStruct>();

        if(!DBEvents.isAfterLast()){
            eventStructList.add(new EventStruct(DBEvents));
            while (DBEvents.moveToNext()) {
                //Log.d("END",String.valueOf(endTimestamp));
                eventStructList.add(new EventStruct(DBEvents));
            }
        }

        DBEvents.close();


        return eventStructList;
    }

    public List<EventStruct> getForSpecificDate(Calendar dateWanted, int resourceID){

        dateWanted.set(Calendar.HOUR_OF_DAY,0);
        dateWanted.set(Calendar.MINUTE,0);
        dateWanted.set(Calendar.SECOND,0);
        long startTimestamp = dateWanted.getTimeInMillis()/1000;

        dateWanted.set(Calendar.HOUR_OF_DAY,23);
        dateWanted.set(Calendar.MINUTE,59);
        dateWanted.set(Calendar.SECOND,59);
        long endTimestamp = dateWanted.getTimeInMillis()/1000;

        //Log.d("START",String.valueOf(startTimestamp));
        //Log.d("END",String.valueOf(endTimestamp));

        String selectionKeys = "(( " + DBSyntax.START   + " > ? ) AND ( "
                + DBSyntax.END   + " < ? )";

        if(resourceID >= 0){
            selectionKeys += " AND ( " + DBSyntax.RID + " = ? )";
        }

        selectionKeys += ")";

        String[] selectionArgs = null;

        if(resourceID >= 0) {
            selectionArgs = new String[]{   String.valueOf(startTimestamp),
                    String.valueOf(endTimestamp),
                    String.valueOf(resourceID)};
        }else {
            selectionArgs = new String[]{   String.valueOf(startTimestamp),
                    String.valueOf(endTimestamp)};
        }

        Cursor DBEvents =  m_db.selectFrom(selectionKeys,selectionArgs,DBSyntax.START + " ASC");

        List<EventStruct> eventStructList = new ArrayList<EventStruct>();

        if(!DBEvents.isAfterLast()){
            eventStructList.add(new EventStruct(DBEvents));
            while (DBEvents.moveToNext()) {
                //Log.d("END",String.valueOf(endTimestamp));
                eventStructList.add(new EventStruct(DBEvents));
            }
        }

        DBEvents.close();


        return eventStructList;
    }

}
