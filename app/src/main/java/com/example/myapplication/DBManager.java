package com.example.myapplication;

//BEGIN CODE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {

    private DBSyntax dbHelper;

    private final Context context;

    private SQLiteDatabase database;

    private boolean m_open;

    /**
     * DBManager class constructor
     * @param c Context of the MainActivity
     */
    public DBManager(Context c) {
        context = c;
        m_open = false;
    }

    /**
     * Open a new connection with the database
     * @return DBManager class
     * @throws SQLException connection error
     */
    public DBManager open() throws SQLException {
        dbHelper = new DBSyntax(context);
        database = dbHelper.getWritableDatabase();
        m_open = true;
        return this;
    }

    /**
     * Close the connection with the database
     */
    public void close() {
        dbHelper.close();
        m_open = false;
    }

    /**
     * Returns status of the database (if it's open or not)
     * @return true if the database is opened, false if it's closed
     */
    public boolean isOpen(){
        return m_open;
    }

    /**
     * Insert new event inside the table
     * @param name name of the event
     * @param start start date of the event in Timestamp format (in seconds)
     * @param end end date of the event in Timestamp format (in seconds)
     * @param room room name of the event
     * @param description description of the event
     * @param promo_id promo ID of the event
     */
    public void insert(String name, long start, long end, String room, String description, int promo_id) {
        ContentValues contentValue = new ContentValues();

        contentValue.put(DBSyntax.NAME, name);
        contentValue.put(DBSyntax.START, start);
        contentValue.put(DBSyntax.END, end);
        contentValue.put(DBSyntax.ROOM, room);
        contentValue.put(DBSyntax.DESC, description);
        contentValue.put(DBSyntax.RID, promo_id);

        database.insert(DBSyntax.TABLE_NAME, null, contentValue);
    }

    /**
     * Get all rows of database (debug usage only)
     * @return Cursor of database moved to first
     */
    public Cursor fetch() {
        String[] columns = new String[] { DBSyntax.NAME, DBSyntax.START, DBSyntax.END, DBSyntax.ROOM, DBSyntax.DESC, DBSyntax.RID};
        Cursor cursor = database.query(DBSyntax.TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    /**
     * Get content of database
     * @param conditionKeys String of all conditions wanted in SQL language. Replace all values by ?
     * @param conditionValues String array of all values sorted with the same order inside conditionKeys
     * @param order String of order arguments in SQL language
     * @return Cursor of database moved to first
     */
    public Cursor selectFrom(String conditionKeys,String[] conditionValues,String order) {

        String[] columns = new String[] { DBSyntax.NAME, DBSyntax.START, DBSyntax.END, DBSyntax.ROOM, DBSyntax.DESC, DBSyntax.RID};

        Cursor cursor = database.query(DBSyntax.TABLE_NAME, columns, conditionKeys, conditionValues, null, null, order);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        return cursor;
    }

    /**
     * Get content of user table
     * @param conditionKeys String of all conditions wanted in SQL language. Replace all values by ?
     * @param conditionValues String array of all values sorted with the same order inside conditionKeys
     * @param order String of order arguments in SQL language
     * @return Cursor of database moved to first
     */
    public Cursor selectFromUsers(String conditionKeys,String[] conditionValues,String order) {

        String[] columns = new String[] { DBSyntax.UID,  DBSyntax.NAME, DBSyntax.LAST_NAME, DBSyntax.RID};

        Cursor cursor = database.query(DBSyntax.TABLE_NAME_USERS, columns, conditionKeys, conditionValues, null, null, order);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        return cursor;
    }

    /**
     * Delete content inside user table
     * @param conditionKeys String of all conditions wanted in SQL language. Replace all values by ?
     * @param conditionValues String array of all values sorted with the same order inside conditionKeys
     */
    public void deleteUser(String conditionKeys,String[] conditionValues){
        database.delete(DBSyntax.TABLE_NAME_USERS, conditionKeys, conditionValues);
    }

    /**
     * Insert new user inside the table
     * @param first_name    first name of the user
     * @param last_name     last_name of the user
     * @param promo_id      promo_id of the user
     */
    public void insertUser(String first_name, String last_name, int promo_id) {
        ContentValues contentValue = new ContentValues();

        contentValue.put(DBSyntax.NAME, first_name);
        contentValue.put(DBSyntax.LAST_NAME, last_name);
        contentValue.put(DBSyntax.RID, promo_id);

        database.insert(DBSyntax.TABLE_NAME_USERS, null, contentValue);
    }

    /**
     * Clear all content inside table
     */
    public void deleteAll() {
        database.delete(DBSyntax.TABLE_NAME, null, null);
    }

}

