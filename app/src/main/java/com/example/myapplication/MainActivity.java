package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.TextView;

import android.icu.util.Calendar;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private DBManager m_db;
    private Spinner spin;
    private String[] list_s;

    private static final int REQUEST_WRITE_STORAGE = 112;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        m_db = new DBManager(this);
        m_db.open();

        spin = (Spinner) findViewById(R.id.spinner);

        Specialities s = new Specialities();
        list_s = new String[s.listSpecialities().length +1];
        list_s[0] = "Tous";
        for (int i=0; i<s.listSpecialities().length;i++) list_s[i+1] = s.listSpecialities()[i];

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,list_s);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);



        // IMPORTANT
        requestAppPermissions();


    }


    public void displayDB(View v){

        String resourceName = spin.getSelectedItem().toString();
        Specialities s = new Specialities();
        int resourceID = s.getIDBySpeciality(resourceName);

        Intent intent = new Intent(this, EDTActivity.class);
        Bundle b = new Bundle();
        b.putInt("promoID", resourceID); //Your id
        intent.putExtras(b); //Put your id to your next Intent
        startActivity(intent);
        //finish();

        /*
        Intent switchActivityIntent = new Intent(this, EDTActivity.class);
        startActivity(switchActivityIntent);

         */
    }



    /*
    PERMISSIONS !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     */

    private void requestAppPermissions() {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        if (hasReadPermissions() && hasWritePermissions()) {
            return;
        }

        ActivityCompat.requestPermissions(this,
                new String[] {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, REQUEST_WRITE_STORAGE); // your request code
    }

    private boolean hasReadPermissions() {
        return (ContextCompat.checkSelfPermission(this.getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private boolean hasWritePermissions() {
        return (ContextCompat.checkSelfPermission(this.getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

}

