package com.example.myapplication;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EDTActivity extends AppCompatActivity {
    private EditText date;
    private DatePickerDialog datePickerDialog;
    private Calendar mC;
    int mYear, mMonth, mDay;


    private DBManager m_db;
    int mPromoID;

    private List<TextView> mDisplayEvents;
    private List<EventStruct> mEvents;
    private List<FrameLayout.LayoutParams> mDisplayParams;
    private List<Integer> mMaxParallelEvents;

    private FrameLayout contentEvents;

    private AlertDialog.Builder mDisplayDetails;


    private FloatingActionButton m_refreshButton;

    public NbParsing m_nbParsed;


    private final static String[] colors = new String[]{"#000000", "#222222", "#444444", "#666666"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edt);


        Bundle b = getIntent().getExtras();
        mPromoID = -1;
        if(b != null) mPromoID = b.getInt("promoID");


        mC = Calendar.getInstance();
        mYear = mC.get(Calendar.YEAR); // current year
        mMonth = mC.get(Calendar.MONTH); // current month
        mDay = mC.get(Calendar.DAY_OF_MONTH); // current day

        m_nbParsed = new NbParsing();

        date = findViewById(R.id.date);
        date.setText(mDay+ "/" + (mMonth + 1) + "/" + mYear);

        m_db = new DBManager(this);
        m_db.open();

        datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // set day of month , month and year value in the edit text
                        mYear = year;
                        mMonth = monthOfYear;
                        mDay = dayOfMonth;
                        mC.set(Calendar.DAY_OF_MONTH,mDay);
                        mC.set(Calendar.MONTH, mMonth);
                        mC.set(Calendar.YEAR,mYear);

                        date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                        refreshEDT();

                    }
                }, mYear, mMonth, mDay);

        contentEvents = findViewById(R.id.contentEvents);
        m_refreshButton = findViewById(R.id.refresh);

        mDisplayEvents = new ArrayList<TextView>();
        mEvents = new ArrayList<EventStruct>();
        mDisplayParams = new ArrayList<FrameLayout.LayoutParams>();
        mMaxParallelEvents = new ArrayList<Integer>();


        mDisplayDetails = new AlertDialog.Builder(this);
        mDisplayDetails.setMessage("Empty");
        mDisplayDetails.setCancelable(true);


        mDisplayDetails.setNegativeButton(
                "Fermer",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

    }


    public void refreshClick(View v){
        EDTRefresh refresh = new EDTRefresh(m_db,m_refreshButton,this,m_nbParsed);
        refresh.execute();
    }


    public void clearDisplayedEvents(){

        contentEvents.removeAllViewsInLayout();
        mEvents.clear();
        mDisplayEvents.clear();
        mDisplayParams.clear();
    }

    public void refreshEDT(){
        clearDisplayedEvents();
        EDTGet getMethod = new EDTGet(m_db);
        List<EventStruct> edtEvents = getMethod.getForSpecificDate(mC,mPromoID);

        //Log.d("NB EVENTS",String.valueOf(edtEvents.size()));

        if(edtEvents.size() == 0) return;

        List<EventStruct> sorted_edtEvents = sortByDurationDesc(edtEvents);

        for(int i = 0; i<sorted_edtEvents.size(); i++){
            addEvent(sorted_edtEvents.get(i));
        }

    }



    public List<EventStruct> sortByDurationDesc(List<EventStruct> events){

        List<EventStruct> sorted_events = new ArrayList<EventStruct>();

        sorted_events.add(events.get(0));

        for(int i=1; i<events.size();i++){
            int start_minutes = events.get(i).getHour(false) *60 + events.get(i).getMinute(false);
            int end_minutes = events.get(i).getHour(true) *60 + events.get(i).getMinute(true);
            int duration = end_minutes - start_minutes;

            for(int j=0;j<sorted_events.size();j++){
                int sorted_start_minutes = sorted_events.get(j).getHour(false) *60 + sorted_events.get(j).getMinute(false);
                int sorted_end_minutes = sorted_events.get(j).getHour(true) *60 + sorted_events.get(j).getMinute(true);
                int sorted_duration = sorted_end_minutes - sorted_start_minutes;
                if(duration<sorted_duration){
                    sorted_events.add(j,events.get(i));
                    break;
                }else{
                    if(j == (sorted_events.size() -1)){
                        sorted_events.add(events.get(i));
                        break;
                    }
                }

            }

        }

        Collections.reverse(sorted_events);

        return sorted_events;
    }

    // Function to remove duplicates from an EventStruct ArrayList
    private List<EventStruct> removeDuplicates(List<EventStruct> list)
    {

        // Create a new ArrayList
        List<EventStruct> newList = new ArrayList<EventStruct>();

        // Traverse through the first list
        for (EventStruct element : list) {

            // If this element is not present in newList
            // then add it
            if (!newList.contains(element)) {

                newList.add(element);
            }
        }

        // return the new list
        return newList;
    }

    // Research all events stick together on a specific event
    private List<EventStruct> researchStickyEvents(EventStruct event, List<EventStruct> researchList){

        List<EventStruct> result = new ArrayList<EventStruct>();
        if(researchList.size() ==0 ) return result;

        int start_minute = event.getMinute(false) + event.getHour(false)*60;
        int end_minute = event.getMinute(true) + event.getHour(true)*60;


        List<EventStruct> tempList = new ArrayList<EventStruct>(researchList);


        for(int i=0; i<tempList.size();i++){
            int last_start_minute = tempList.get(i).getHour(false) *60 + tempList.get(i).getMinute(false);
            int last_end_minute = tempList.get(i).getHour(true) *60 + tempList.get(i).getMinute(true);
            if(start_minute<(last_end_minute-5) && end_minute>(last_start_minute+5)){
                result.add(tempList.get(i));
                researchList.remove(tempList.get(i));
            }
        }



        if(result.size() < 1) return result;

        for (int i=0; i<result.size();i++){
            List<EventStruct> newSticky = new ArrayList<>(researchStickyEvents(result.get(i), researchList));
            if(newSticky.size()>0){
                result.addAll(newSticky);
            }
        }

        return removeDuplicates( new ArrayList<EventStruct>(result));
    }




    public void addEvent(EventStruct event){

        //Get all event properties
        String name = event.Name + "\r\n" + event.Room;
        int start_hour = event.getHour(false);
        int start_minute = event.getMinute(false);
        int end_hour = event.getHour(true);
        int end_minute = event.getMinute(true);


        int total_start_minute = start_minute + start_hour*60;
        int total_end_minute = end_minute + end_hour*60;
        int height = 200*(total_end_minute - total_start_minute)/60;

        //Create new textview object that contains the event information
        mDisplayEvents.add(new TextView(this));
        int actualIndex = mDisplayEvents.size()-1;
        TextView text = mDisplayEvents.get(actualIndex);

        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventStruct event = mEvents.get(actualIndex);
                mDisplayDetails.setMessage("Détails:\r\n"
                        + event.Name
                        + "\r\n" + event.Room
                        + "\r\nDe " + event.getHour(false) + ":" + event.getMinute(false) + " à " + event.getHour(true) + ":" + event.getMinute(true)
                        + "\r\n" +  event.Description.replace("\n"," "));
                AlertDialog alert11 = mDisplayDetails.create();
                alert11.show();
            }
        });


        text.setText(name);
        text.setBackgroundColor(Color.parseColor(colors[mEvents.size()%colors.length]));
        text.setTextColor(Color.parseColor("#ffffff"));

        /*
        Log.d("NAME", event.Name);
        Log.d("START", String.valueOf(start_hour)+":"+String.valueOf(start_minute));
        Log.d("END", String.valueOf(end_hour)+":"+String.valueOf(end_minute));
        Log.d("DIFF", String.valueOf(total_end_minute-total_start_minute));
         */

        text.setTranslationY((total_start_minute*200)/60);
        text.setTranslationX(100);

        int width = contentEvents.getWidth();

        FrameLayout.LayoutParams linearPrams = new  FrameLayout.LayoutParams(width-100,height);

        text.setLayoutParams(linearPrams);

        text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);



        mEvents.add(event);
        mDisplayParams.add(linearPrams);


        List<EventStruct> tempEvents = new ArrayList<EventStruct>(mEvents);

        //Get events indexes already displayed in same hours
        List<EventStruct> eventSticky = new ArrayList<EventStruct>();
        tempEvents.remove(event);
        if(tempEvents.size()>0){
            eventSticky =  researchStickyEvents(event,tempEvents);
        }
        eventSticky.add(event);

        eventSticky = sortByDurationDesc(new ArrayList<EventStruct>(eventSticky));


        List<Integer> eventIndexes = new ArrayList<Integer>();

        for(int i=0;i<eventSticky.size();i++){
            eventIndexes.add(mEvents.indexOf(eventSticky.get(i)));
        }

        //Log.d("NB EVENTS RESIZED",String.valueOf(eventIndexes.size()));

        mMaxParallelEvents.add(eventIndexes.size());

        //Calculate the new width size


        int partialParallelNumber = eventIndexes.size();


        int minusWidth = 100;

        for(int i=0; i<eventIndexes.size();i++){

            int eventIndex = eventIndexes.get(i);


            int lastMaxParallel = 1;

            if(i>0) lastMaxParallel =  mMaxParallelEvents.get(eventIndexes.get(i-1));

            if(lastMaxParallel > eventIndexes.size()){
                partialParallelNumber -= 1;
                FrameLayout.LayoutParams lastParam = mDisplayParams.get(eventIndexes.get(i-1));
                minusWidth += lastParam.width;

            }else{
                mMaxParallelEvents.set(eventIndex,eventIndexes.size());
                FrameLayout.LayoutParams param = mDisplayParams.get(eventIndex);
                int new_width = ( width- minusWidth )/partialParallelNumber;
                param.width = new_width;
                mDisplayParams.set(eventIndex, param);
                mDisplayEvents.get(eventIndex).setLayoutParams(param);
            }

            float translateX = 100;

            if(i>0){
                    FrameLayout.LayoutParams lastParam = mDisplayParams.get(eventIndexes.get(i-1));
                    if(mDisplayEvents.get(eventIndexes.get(i-1)).getTranslationX() + lastParam.width >= translateX){
                        translateX = mDisplayEvents.get(eventIndexes.get(i-1)).getTranslationX() + lastParam.width;
                    }
            }
            //Log.d("MAX TRANSLATION",String.valueOf(translateX));

            mDisplayEvents.get(eventIndex).setTranslationX(translateX);


        }


        contentEvents.addView(text);

    }

    public void next(View v){
        mC.add(Calendar.DAY_OF_MONTH, 1); //Adds a day

        mYear = mC.get(Calendar.YEAR); // current year
        mMonth = mC.get(Calendar.MONTH); // current month
        mDay = mC.get(Calendar.DAY_OF_MONTH); // current day

        date = findViewById(R.id.date);
        date.setText(mDay+ "/" + (mMonth + 1) + "/" + mYear);

        refreshEDT();
    }

    public void back(View v){
        mC.add(Calendar.DAY_OF_MONTH, -1); //Goes to previous day

        mYear = mC.get(Calendar.YEAR); // current year
        mMonth = mC.get(Calendar.MONTH); // current month
        mDay = mC.get(Calendar.DAY_OF_MONTH); // current day

        date = findViewById(R.id.date);
        date.setText(mDay+ "/" + (mMonth + 1) + "/" + mYear);

        refreshEDT();
    }


    public void onDateSelectClick(View v) {
        datePickerDialog.show();
    }

}