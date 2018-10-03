package com.hva.m2mobi.m2hva_reservationsystem.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.hva.m2mobi.m2hva_reservationsystem.utils.CalendarEventListener;
import com.hva.m2mobi.m2hva_reservationsystem.utils.CalendarTask;
import com.hva.m2mobi.m2hva_reservationsystem.R;
import com.hva.m2mobi.m2hva_reservationsystem.utils.CalendarTaskParams;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarActivity extends AppCompatActivity {

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
    private int mYear, mMonth, mDay, mHour, mMinute;
    private  int selectedYear,selectedMonth,selectedDay, selectedHour,selectedMinute;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;

    private DatePickerDialog.OnDateSetListener datePickerDialogListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year,
        int monthOfYear, int dayOfMonth) {

            selectedYear = year;
            selectedMonth = monthOfYear;
            selectedDay = dayOfMonth;
            timePickerDialog.show();
        }
    };

    private  TimePickerDialog.OnTimeSetListener timePickerDialogListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay,
                              int minuteOfHour) {
            selectedHour = hourOfDay;
            selectedMinute = minuteOfHour;
            final Calendar c = Calendar.getInstance();
            c.set(selectedYear,selectedMonth,selectedDay,selectedHour,selectedMinute);

            String dateString = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(c.getTime());

            Event event = new Event()
                    .setSummary("Test P");

            DateTime startDateTime = new DateTime(dateString);
            EventDateTime start = new EventDateTime()
                    .setDateTime(startDateTime)
                    .setTimeZone("Europe/Amsterdam");
            event.setStart(start);

            c.set(selectedYear,selectedMonth,selectedDay,selectedHour+1,selectedMinute);
            dateString = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(c.getTime());

            DateTime endDateTime = new DateTime(dateString);
            EventDateTime end = new EventDateTime()
                    .setDateTime(endDateTime)
                    .setTimeZone("Europe/Amsterdam");
            event.setEnd(end);

            params.event = event;
            requestPermissions(REQUEST_PERMISSIONS_CALENDAR);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions(REQUEST_PERMISSIONS_INIT);
        setContentView(R.layout.activity_calendar);
        Button eventButton = findViewById(R.id.event_button);
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        datePickerDialog = new DatePickerDialog(this, datePickerDialogListener, mYear, mMonth, mDay);
        timePickerDialog = new TimePickerDialog(this, timePickerDialogListener, mHour, mMinute, false);
        eventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                params.calendarAction = params.GET_ALL_EVENTS;
                requestPermissions(REQUEST_PERMISSIONS_CALENDAR);
            }
        });
        Button myEventButton = findViewById(R.id.my_event_button);
        myEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                params.calendarAction = params.GET_MY_EVENTS;
                requestPermissions(REQUEST_PERMISSIONS_CALENDAR);
            }
        });
        final Button roomEventButton = findViewById(R.id.room_event_button);
        final RadioGroup roomGroup = findViewById(R.id.roomGroup);
        roomEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                params.calendarAction = params.GET_ROOM_EVENTS;
                RadioButton rb = findViewById(roomGroup.getCheckedRadioButtonId());
                switch(rb.getText().toString()){
                    case "Oreo":
                        params.room = CalendarTaskParams.ROOM_OREO;
                        break;
                    case "KitKat":
                        params.room = CalendarTaskParams.ROOM_KITK;
                        break;
                    case "ICS":
                        params.room = CalendarTaskParams.ROOM_ICSW;
                        break;
                    case "Jelly Bean":
                        params.room = CalendarTaskParams.ROOM_JELB;
                        break;
                    case "Marshmallow":
                        params.room = CalendarTaskParams.ROOM_MARS;
                        break;
                }
                requestPermissions(REQUEST_PERMISSIONS_CALENDAR);
            }
        });
        Button addEventButton = findViewById(R.id.add_event_button);
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                params.calendarAction = params.ADD_EVENT;
                RadioButton rb = findViewById(roomGroup.getCheckedRadioButtonId());
                switch(rb.getText().toString()){
                    case "Oreo":
                        params.room = CalendarTaskParams.ROOM_OREO;
                        break;
                    case "KitKat":
                        params.room = CalendarTaskParams.ROOM_KITK;
                        break;
                    case "ICS":
                        params.room = CalendarTaskParams.ROOM_ICSW;
                        break;
                    case "Jelly Bean":
                        params.room = CalendarTaskParams.ROOM_JELB;
                        break;
                    case "Marshmallow":
                        params.room = CalendarTaskParams.ROOM_MARS;
                        break;
                }
                datePickerDialog.show();

            }
        });
    }

    private void requestPermissions(int requestCode){
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.GET_ACCOUNTS, Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR},requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if(requestCode == REQUEST_PERMISSIONS_CALENDAR && grantResults[0] == 0){
            if(params.accountName == null || params.accountName.isEmpty())
                startActivityForResult(new Intent(this,AccountAuthorisationActivity.class),REQUEST_ACCOUNT_AUTHORISATION);
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
        }else if(requestCode == REQUEST_ACCOUNT_AUTHORISATION && resultCode == RESULT_CANCELED){
            //show auth error
        }
    }

}
