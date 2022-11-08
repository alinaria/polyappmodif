package com.example.myapplication;

//BEGIN CODE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.widget.Button;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EDTRefresh extends AsyncTask<Void,Void,Void>{

    // Class variables
    private DBManager m_db;
    private Specialities m_resources;
    private FloatingActionButton m_requestButton;
    private Context m_mainActivity;

    private NbParsing m_nbParsed;


    // Class constructor
    public EDTRefresh(DBManager db, FloatingActionButton requestButton, Context mainActivity,NbParsing nbParsed){

        // Create var to get the number of specialities parsed
        m_nbParsed = nbParsed;

        m_mainActivity = mainActivity;

        // Assign the database and open connection if not already connected
        m_db = db;
        if(!m_db.isOpen()){
            m_db.open();
        }

        // Assign class which contains specialities with their resource IDs
        m_resources = new Specialities();

        // Assign the button for the feedback
        m_requestButton = requestButton;

        m_nbParsed.value = 0;

    }

    // Executed before execute task in parallel
    @Override
    protected void onPreExecute(){
        // Disable button
        m_requestButton.setEnabled(false);
    }

    // Executed in parallel
    @Override
    protected Void doInBackground(Void ... params){
        // Get all resources IDs
        int[] resources = m_resources.listResourcesIDs();

        // Create EDTParser object to process data
        EDTParser edt = new EDTParser(m_db);

        // Create a list to store all downloads
        List<EDTDownload> downs = new ArrayList<EDTDownload>();

        // Setup download directory. If not exists, create the directory. If already exists, remove all content inside.
        File file = new File(Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DOWNLOADS + "/PolyApp/");
        if (!file.exists()) {
            // Create the folder
            file.mkdirs();
        }else{
            // Try is used to avoid crashes when the folder is already empty
            try {
                for (File tempFile : file.listFiles()) {
                    tempFile.delete();
                }
            }catch (Exception e){}
        }

        // Clear database before insert new content
        m_db.deleteAll();

        // For each resource
        for(int i=0; i<resources.length; i++){

            // Add new download object
            downs.add(new EDTDownload(m_mainActivity,edt,m_nbParsed,m_requestButton));

            // Start download
            downs.get(i).process(String.valueOf(resources[i]));
        }

        return null;
    }

    // Executed after the task in parallel
    @Override
    protected void onPostExecute(Void result){

        // Enable button
        //m_requestButton.setEnabled(true);

    }

}

