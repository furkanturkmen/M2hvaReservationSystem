package com.hva.m2mobi.m2hva_reservationsystem;

import android.Manifest;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private CalendarEventListener result = new CalendarEventListener() {
        @Override
        public void onCalendarEventsReturned(Events events) {
            List<Event> items = new ArrayList<>();
            if(events == null){
                Log.e("Calendar Event UI", "Account name could not be found");
            }else {
                items = events.getItems();
            }

            //place holder actions when events are returned - need to sort them out and add them as cards
            if (items.isEmpty()) {
                Log.i("CalendarTask","No upcoming events found.");
            } else {
                Log.i("CalendarTask","Upcoming events");
                for (Event event : items) {
                    DateTime start = event.getStart().getDateTime();
                    if (start == null) {
                        start = event.getStart().getDate();
                    }
                    Log.i("CalendarTask",event.getSummary() + " :: " + start);
                }
            }
        }
    };

    private CalendarTaskParams params = new CalendarTaskParams(result,this);

    private static final int REQUEST_ACCOUNT_AUTHORISATION = 123;
    private static final int REQUEST_PERMISSIONS_INIT = 666;
    private static final int REQUEST_PERMISSIONS_CALENDAR = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        requestPermissions(REQUEST_PERMISSIONS_INIT);
        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.event_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermissions(REQUEST_PERMISSIONS_CALENDAR);
            }
        });
    }

    private void requestPermissions(int requestCode){
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.GET_ACCOUNTS},requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if(requestCode == REQUEST_PERMISSIONS_CALENDAR && grantResults[0] == 0){
            if(params.accountName == null || params.accountName.isEmpty())
                startActivityForResult(new Intent(this,AccountAuthoirsation.class),REQUEST_ACCOUNT_AUTHORISATION);
            else
                getEvents();
        }
    }

    private void getEvents(){
        new CalendarTask().execute(params);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_ACCOUNT_AUTHORISATION && resultCode == RESULT_OK){
            params.accountName = data.getStringExtra("name");
            getEvents();
        }
    }

}
