package com.example.myapplication;

//BEGIN CODE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBSyntax extends SQLiteOpenHelper {

    /****************************************
     * Name 	    -> SUMMARY	    -> TEXT *
     * Start 	    -> DTSTART	    -> INT  *
     * End	        -> DTEND	    -> INT  *
     * Room	        -> LOCATION	    -> TEXT *
     * description	-> DESCRIPTION	-> TEXT *
     * resources id -> promo_id	    -> INT  *
     ***************************************/

    /****************************************
     * userid    	-> User ID	    -> INT  *
     * name 	    -> First Name	-> TEXT *
     * last_name 	-> Last Name	-> TEXT *
     * resources id -> promo_id	    -> INT  *
     ***************************************/

    // Table Name
    public static final String TABLE_NAME       = "EDT"     ;
    public static final String TABLE_NAME_USERS = "USERS"   ;

    // Table columns
    public static final String NAME  =      "name"           ;
    public static final String START =      "start"          ;
    public static final String END   =      "endedt"         ;
    public static final String ROOM  =      "room"           ;
    public static final String DESC  =      "description"    ;
    public static final String RID   =      "rid"            ;


    public static final String LAST_NAME =  "lastname"       ;
    public static final String UID       =  "userid"         ;

    // Database Information
    static final String DB_NAME = "POLYAPP.DB";

    // database version
    static final int DB_VERSION = 2;

    // Creating table query
    private static final String CREATE_TABLE = "create table if not exists " + TABLE_NAME + "("
            + NAME  + " TEXT, "
            + START + " INTEGER NOT NULL, "
            + END   + " INTEGER NOT NULL, "
            + ROOM  + " TEXT, "
            + DESC  + " TEXT, "
            + RID   + " INTEGER NOT NULL);";

    private static final String CREATE_TABLE_USERS = "create table if not exists " + TABLE_NAME + "("
            + UID       + " INTEGER NOT NULL, "
            + NAME      + " TEXT, "
            + LAST_NAME + " INTEGER NOT NULL, "
            + RID       + " INTEGER NOT NULL, "
            + "PRIMARY KEY(\"" + UID + "\" AUTOINCREMENT) );";

    public DBSyntax(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // When class is created, create table if not exists
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_TABLE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_USERS);
        onCreate(db);
    }
}




