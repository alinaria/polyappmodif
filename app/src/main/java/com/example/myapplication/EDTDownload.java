package com.example.myapplication;

//BEGIN CODE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.widget.Button;

import java.util.Calendar;

public class EDTDownload {

    /*

    BE AWARE !!!!!

    Please add inside "AndroidManifest.xml"

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />

     */

    private DownloadManager m_downloadManager;
    private long m_downloadReference;
    private Context m_mainActivity;
    private String m_resourceID;
    private String m_filename;
    private EDTParser m_edt;

    private FloatingActionButton m_requestButton;
    private NbParsing m_nbParsed;


    private BroadcastReceiver onComplete = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            //check if the broadcast message is for our Enqueued download
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            if (m_downloadReference == referenceId){

                Log.d("COMPLETE","DOWNLOAD COMPLETE TRIGGERED");

                try {
                    ParcelFileDescriptor file = m_downloadManager.openDownloadedFile(referenceId);
                    file.close();

                    m_edt.parse(Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DOWNLOADS + "/PolyApp/" + m_filename,Integer.valueOf(m_resourceID));


                } catch (Exception e) {
                    e.printStackTrace();
                }

                m_nbParsed.value += 1;
                Specialities s = new Specialities();
                if(s.listResourcesIDs().length <= m_nbParsed.value){
                    m_requestButton.setEnabled(true);
                    Log.d("PARSING STATUS","PARSING FINISHED");
                }

            }
        }
    };

    private Uri buildURL(String resourceID){

        // Create the root of the URL
        String sURL = "https://www.univ-orleans.fr/EDTWeb/export?type=ical&project=";

        /**
         * GET PARAMETERS
         * project      -> contains the scholar year (ex: 2022-2023)
         * resources    -> contains the id of the calendar ( ex: 4810 for the first year)
         * type         -> must be ical for icalendar format
         */

        // Get current scholar year (for project parameter)
        Calendar calendar = Calendar.getInstance();
        String year = String.valueOf(calendar.get(Calendar.YEAR)) + "-" + String.valueOf(calendar.get(Calendar.YEAR)+1);

        //  Add parameters to the string URL
        sURL += (year + "&resources=" + resourceID);

        // Convert String URL to Uri object
        try{
            Uri url = Uri.parse(sURL);
            return url;
        }catch (Exception e){
            return null;
        }
    }

    public void process(String resourceID){
        Uri Download_Uri = buildURL(resourceID);
        DownloadManager.Request request = new DownloadManager.Request(Download_Uri);

        Specialities spec = new Specialities();
        String spec_name = spec.getSpecialityByID(Integer.valueOf(resourceID));
        m_filename = resourceID + ".ics";

        request.setTitle(spec_name);
        request.setDescription("Télechargement de l'emploi du temps de la spécialité " + spec_name);
        request.allowScanningByMediaScanner();


        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS ,"PolyApp/"+m_filename);


        m_downloadReference = m_downloadManager.enqueue(request);
        m_resourceID = resourceID;
    }

    public EDTDownload(Context mainActivity, EDTParser edt, NbParsing nbParsed, FloatingActionButton requestButton){

        m_requestButton = requestButton;
        m_nbParsed = nbParsed;

        // Initialisation of EDTParser object for parsing after download
        m_edt = edt;

        // Initialisation of downloadManager object
        m_mainActivity = mainActivity;
        m_downloadManager = (DownloadManager)m_mainActivity.getSystemService(Context.DOWNLOAD_SERVICE);

        // Create a new event triggered when the download is finished
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        m_mainActivity.registerReceiver(onComplete, filter);

    }

}
