package com.example.myapplication;

//BEGIN CODE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

import android.database.Cursor;

import android.icu.util.Calendar;
import android.icu.util.TimeZone;

public class EventStruct {

    public String Name;
    public String Room;
    public String Description;

    private int Promo_ID;

    private Calendar dStart;
    private Calendar dEnd;

    /**
     * Constructor of the EventStruct class
     * @param row The result of fetch or select functions of DBManager class
     */
    public EventStruct(Cursor row){

        Name = row.getString(0);
        Room = row.getString(3);
        Description = row.getString(4);

        Promo_ID = row.getInt(5);
        dStart  = Calendar.getInstance(TimeZone.getDefault());
        dStart.setTimeInMillis(row.getLong(1) * 1000);
        dEnd    = Calendar.getInstance(TimeZone.getDefault());
        dEnd.setTimeInMillis(row.getLong(2)*1000);

        //dStart.setTimeZone(TimeZone.getDefault());
        //dEnd.setTimeZone(TimeZone.getDefault());

    }

    /**
     * Constructor of the EventStruct class (USED FOR DEBUG ONLY)
     * @param name Name of the event
     * @param room Room of the event
     * @param description Description of the event
     * @param date Day, month and year of the event in Calendar object
     * @param start_hour Start hour of the event
     * @param start_minute Start minute of the
     * @param end_hour End hour of the event
     * @param end_minute End minute of the event
     */
    public EventStruct(String name, String room, String description, Calendar date, int start_hour,int start_minute,int end_hour, int end_minute){
        Name = name;
        Room = room;
        Description = description;

        dStart = Calendar.getInstance();
        dEnd = Calendar.getInstance();

        dStart.setTimeInMillis(date.getTimeInMillis());
        dStart.set(Calendar.HOUR_OF_DAY,start_hour);
        dStart.set(Calendar.MINUTE,start_minute);
        dEnd.setTimeInMillis(date.getTimeInMillis());
        dEnd.set(Calendar.HOUR_OF_DAY,end_hour);
        dEnd.set(Calendar.MINUTE,end_minute);
    }

    /**
     * Get promo ID
     * @return promo ID inside a int format
     */
    public int getPromoID(){
        return Promo_ID;
    }

    /**
     * Get promo name (human readable version)
     * @return promo name in String format
     */
    public String getPromoName(){
        Specialities s = new Specialities();
        return s.getSpecialityByID(Promo_ID);
    }


    /**
     * Get calendar format
     * @param isEnd if isEnd equals 0, it returns the start date, else it returns the end date
     * @return the corresponding date of the event in Calendar format
     */
    public Calendar getDate(boolean isEnd){
        if(isEnd){
            return this.dEnd;
        }else {
            return this.dStart;
        }
    }


    /**
     * Get the day of the event
     * @param isEnd if isEnd equals 0, it returns values of the start date, else it returns values of the end date
     * @return the day in int format
     */
    public int getDay(boolean isEnd){
        if(isEnd){
            return this.dEnd.get(Calendar.DATE);
        }else {
            return this.dStart.get(Calendar.DATE);
        }
    }

    /**
     * Get the month (between 1 and 12) of the event
     * @param isEnd if isEnd equals 0, it returns values of the start date, else it returns values of the end date
     * @return the month in int format
     */
    public int getMonth(boolean isEnd){
        if(isEnd){
            return this.dEnd.get(Calendar.MONTH) + 1;
        }else {
            return this.dStart.get(Calendar.MONTH) + 1;
        }
    }

    /**
     * Get the year of the event
     * @param isEnd if isEnd equals 0, it returns values of the start date, else it returns values of the end date
     * @return the year in int format
     */
    public int getYear(boolean isEnd){
        if(isEnd){
            return this.dEnd.get(Calendar.YEAR);
        }else {
            return this.dStart.get(Calendar.YEAR);
        }
    }

    /**
     * Get the hour (in 24h format) of the event
     * @param isEnd if isEnd equals 0, it returns values of the start date, else it returns values of the end date
     * @return the hour in int format
     */
    public int getHour(boolean isEnd){
        if(isEnd){
            return this.dEnd.get(Calendar.HOUR_OF_DAY);
        }else {
            return this.dStart.get(Calendar.HOUR_OF_DAY);
        }
    }

    /**
     * Get the minute of the event
     * @param isEnd if isEnd equals 0, it returns values of the start date, else it returns values of the end date
     * @return the minute in int format
     */
    public int getMinute(boolean isEnd){
        if(isEnd){
            return this.dEnd.get(Calendar.MINUTE);
        }else {
            return this.dStart.get(Calendar.MINUTE);
        }
    }


}
