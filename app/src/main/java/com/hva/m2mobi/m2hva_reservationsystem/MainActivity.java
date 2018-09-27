package com.hva.m2mobi.m2hva_reservationsystem;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private CalendarTask calendar;
    private TaskIF result = new TaskIF() {
        @Override
        public void onCalendarEventsReturned(Events events) {
            List<Event> items = events.getItems();
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        requestPermissions(2);
        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.event_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getEvents();
            }
        });
    }

    private void requestPermissions(int requestCode){
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.GET_ACCOUNTS},requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        calendar = new CalendarTask();
        if(requestCode == 1 && grantResults[0] == 0)
            calendar.execute(params);
    }

    private void getEvents(){
        requestPermissions(1);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode,
                                    final Intent data) {

        if (requestCode == CalendarTask.REQUEST_CODE && resultCode == RESULT_OK) {
            String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            try {
                calendar.setAccountName(accountName, this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
