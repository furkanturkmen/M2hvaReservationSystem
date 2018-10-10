package com.hva.m2mobi.m2hva_reservationsystem.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
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
import com.hva.m2mobi.m2hva_reservationsystem.R;
import com.hva.m2mobi.m2hva_reservationsystem.utils.CalendarConnection;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

//Example activity to show how to use the CalendarConnection model
public class CalendarActivity extends AppCompatActivity {

    //Callback method to list each event
    public static void onCalendarEventsReturned(List<Event> items) {
        if (items == null) {
            Log.e("Calendar Event UI", "Account name could not be found");
            return;
        }
        //place holder actions when events are returned - need to sort them out and add them as cards
        if (items.isEmpty()) {
            Log.i("CalendarTask", "No upcoming events found.");
        } else {
            Log.i("CalendarTask", "Upcoming events");
            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    start = event.getStart().getDate();
                }
                Log.i("CalendarTask", event.getSummary() + " :: " + start);
            }
        }
    }

    private static final int REQUEST_ACCOUNT_AUTHORISATION = 123;
    private static final int REQUEST_PERMISSIONS = 666;

    private static final int GET_ALL_EVENTS = 0;
    private static final int GET_MY_EVENTS = 1;
    private static final int GET_ROOM_EVENTS = 2;
    private static final int GET_CURRENT_EVENT = 3;
    private static final int ADD_EVENT = 4;

    private static CalendarConnection calendarConnection;
    private static Event mEvent;
    private static int mRoom;

    private  int selectedYear,selectedMonth,selectedDay;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;

    public static class CalendarTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... ints) {
            try {
                switch(ints[0]){
                    case GET_ALL_EVENTS:
                        onCalendarEventsReturned(calendarConnection.getAllEvents());
                        break;
                    case GET_ROOM_EVENTS:
                        onCalendarEventsReturned(calendarConnection.getRoomEvents(mRoom));
                        break;
                    case GET_MY_EVENTS:
                        onCalendarEventsReturned(calendarConnection.getMyEvents());
                        break;
                    case GET_CURRENT_EVENT:
                        onCalendarEventsReturned(calendarConnection.getCurrentRoomEvent(mRoom));
                        break;
                    case ADD_EVENT:
                        calendarConnection.addEvent(mEvent,mRoom);
                        break;
                }
            } catch (IOException e) {
                Log.e("Event UI", e.getClass().getName() + ": Cant find calendar, either id is incorrect or user not invited");
            }
            return null;
        }
    }

    //When a date is set show the time picker
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

    //Once the time is set add the event
    private  TimePickerDialog.OnTimeSetListener timePickerDialogListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay,
                              int minuteOfHour) {
            final Calendar c = Calendar.getInstance();
            c.set(selectedYear,selectedMonth,selectedDay,hourOfDay,minuteOfHour);

            String dateString = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(c.getTime());

            Event event = new Event()
                    .setSummary("Test P");

            DateTime startDateTime = new DateTime(dateString);
            EventDateTime start = new EventDateTime()
                    .setDateTime(startDateTime)
                    .setTimeZone("Europe/Amsterdam");
            event.setStart(start);

            c.set(selectedYear,selectedMonth,selectedDay,hourOfDay+1,minuteOfHour);
            dateString = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(c.getTime());

            DateTime endDateTime = new DateTime(dateString);
            EventDateTime end = new EventDateTime()
                    .setDateTime(endDateTime)
                    .setTimeZone("Europe/Amsterdam");
            event.setEnd(end);
            mEvent = event;
            final RadioGroup roomGroup = findViewById(R.id.roomGroup);
            RadioButton rb = findViewById(roomGroup.getCheckedRadioButtonId());
            switch(rb.getText().toString()){
                case "Oreo":
                    mRoom = CalendarConnection.ROOM_OREO;
                    break;
                case "KitKat":
                    mRoom = CalendarConnection.ROOM_KITK;
                    break;
                case "ICS":
                    mRoom = CalendarConnection.ROOM_ICSW;
                    break;
                case "Jelly Bean":
                    mRoom = CalendarConnection.ROOM_JELB;
                    break;
                case "Marshmallow":
                    mRoom = CalendarConnection.ROOM_MARS;
                    break;
            }
            new CalendarTask().execute(ADD_EVENT);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions();
        setContentView(R.layout.activity_calendar);
        Button eventButton = findViewById(R.id.event_button);
        final Calendar c = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, datePickerDialogListener, c.get(Calendar.YEAR),
                c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        timePickerDialog = new TimePickerDialog(this, timePickerDialogListener, c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE), false);
        final RadioGroup roomGroup = findViewById(R.id.roomGroup);
        roomGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rb = findViewById(radioGroup.getCheckedRadioButtonId());
                switch(rb.getText().toString()){
                    case "Oreo":
                        mRoom = CalendarConnection.ROOM_OREO;
                        break;
                    case "KitKat":
                        mRoom = CalendarConnection.ROOM_KITK;
                        break;
                    case "ICS":
                        mRoom = CalendarConnection.ROOM_ICSW;
                        break;
                    case "Jelly Bean":
                        mRoom = CalendarConnection.ROOM_JELB;
                        break;
                    case "Marshmallow":
                        mRoom = CalendarConnection.ROOM_MARS;
                        break;
                }
            }
        });
        eventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CalendarTask().execute(GET_ALL_EVENTS);
            }
        });
        final Button myEventButton = findViewById(R.id.my_event_button);
        myEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CalendarTask().execute(GET_MY_EVENTS);
            }
        });
        final Button roomEventButton = findViewById(R.id.room_event_button);
        roomEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CalendarTask().execute(GET_ROOM_EVENTS);
            }
        });
        final Button addEventButton = findViewById(R.id.add_event_button);
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });
    }


    private void requestPermissions(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALENDAR,Manifest.permission.GET_ACCOUNTS},REQUEST_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if(requestCode == REQUEST_PERMISSIONS && grantResults[0] == 0){
            calendarConnection = new CalendarConnection(this);
        }
    }

}
