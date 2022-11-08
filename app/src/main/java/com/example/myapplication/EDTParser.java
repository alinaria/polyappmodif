package com.example.myapplication;

//BEGIN CODE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Scanner;
import java.util.TimeZone;

public class EDTParser {

    private final DBManager m_db;

    private String nameEvent;
    private long startEvent;
    private long endEvent;
    private String roomEvent;
    private String descriptionEvent;

    FileInputStream inputStream = null;
    Scanner sc = null;

    public EDTParser(DBManager db){
        // Database
        m_db = db;

        // Event informations
        nameEvent = "";
        startEvent = 0;
        endEvent = 0;
        roomEvent = "";
        descriptionEvent = "";

    }

    private long dateToTimestamp(String date){

        // Convert String to Date format
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(Integer.parseInt(date.substring(0,4)),      // Year
                Integer.parseInt(date.substring(4,6))-1,    // Month
                Integer.parseInt(date.substring(6,8)),      // Day
                Integer.parseInt(date.substring(9,11)),     // Hour
                Integer.parseInt(date.substring(11,13))     // Minute
        );

        //Log.d("EVENT", nameEvent + " " + date.substring(6,8) + "/" + date.substring(4,6) + "/" + date.substring(0,4) + " " + date.substring(9,11) + ":" + date.substring(11,13) );
        //Log.d("TIMESTAMP", String.valueOf(cal.getTimeInMillis()/1000));

        // Return Timestamp in seconds
        return cal.getTimeInMillis()/1000;
    }

    public void parse(String path, int resourceID) throws IOException {

        inputStream = new FileInputStream(path);
        sc = new Scanner(inputStream, "UTF-8");

        // Process each line of the response
        String line;
        while (sc.hasNextLine()) {

            // Read the next line
            line = sc.nextLine();

            // Get separator index
            int indexSeparator = line.indexOf(":");
            if(indexSeparator < 0) continue;

            // Get key and value with separation index
            String key = line.substring(0,indexSeparator);
            String value = line.substring(indexSeparator).substring(1);

            // Execute the right function depending of the key value
            switch (key){
                // Name of the event
                case "SUMMARY":
                    nameEvent = value;
                    break;
                // Start date of the event
                case "DTSTART":
                    startEvent = dateToTimestamp(value);
                    break;
                // End date of the event
                case "DTEND":
                    endEvent = dateToTimestamp(value);
                    break;
                // Room of the event
                case "LOCATION":
                    roomEvent = value;
                    break;
                // Details of the event
                case "DESCRIPTION":
                    descriptionEvent = value;
                    break;
                // Add the event to the database
                case  "END":
                     m_db.insert(nameEvent,startEvent,endEvent,roomEvent,descriptionEvent,resourceID);
                    break;
                default:
                    break;
            }

        }

        // After parsing, close properly all pipes
        sc.close();
        inputStream.close();

    }

}
