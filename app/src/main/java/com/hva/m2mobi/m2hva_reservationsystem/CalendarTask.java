package com.hva.m2mobi.m2hva_reservationsystem;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Events;
import com.google.api.services.calendar.Calendar.Events.List;
import java.io.IOException;
import java.util.Collections;

//A task to retrieve events from the public calendar
public class CalendarTask extends AsyncTask<CalendarTaskParams,Void,Void>{
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String CALENDAR_ID = "rbvkmi4iflbmllftnd9d12c9g0@group.calendar.google.com";
    static final java.util.List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);

    private GoogleAccountCredential mCredential;
    private CalendarEventListener eventListener;

    //Retrieves events from the calendar and sends them to the eventListener
    private void getEvents(CalendarTaskParams param) throws  IOException{
        Activity activity = param.activity;
       Context context = activity.getBaseContext();

        final NetHttpTransport HTTP_TRANSPORT = new com.google.api.client.http.javanet.NetHttpTransport();
        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, mCredential)
                .setApplicationName(context.getString(R.string.app_name))
                .build();

        // List the next 10 events from the public calendar.
        DateTime now = new DateTime(System.currentTimeMillis());

        try{

            List events = service.events().list(CALENDAR_ID);

            switch(param.calendarAction){
                case CalendarTaskParams.GET_ALL_EVENTS:
                    events.setMaxResults(10)
                        .setTimeMin(now)
                        .setOrderBy("startTime")
                        .setSingleEvents(true);
                break;
                case CalendarTaskParams.GET_ROOM_EVENTS:
                    events.setQ(param.roomName)
                            .setMaxResults(10)
                            .setTimeMin(now)
                            .setOrderBy("startTime")
                            .setSingleEvents(true);
                    break;
                case CalendarTaskParams.GET_CURRENT_EVENT:
                    events.setQ(param.roomName)
                            .setMaxResults(1)
                            .setTimeMin(now)
                            .setOrderBy("startTime")
                            .setSingleEvents(true);
                    break;
                case CalendarTaskParams.GET_MY_EVENTS:
                    events.setQ(mCredential.getSelectedAccountName())
                            .setMaxResults(10)
                            .setTimeMin(now)
                            .setOrderBy("startTime")
                            .setSingleEvents(true);
                    break;
                case CalendarTaskParams.ADD_EVENT:
                    if(param.event != null)
                        param.event.setDescription(param.roomName + "\n" + mCredential.getSelectedAccountName());
                        service.events().insert(CALENDAR_ID,param.event).execute();
                break;
            }

            //eventListener.onCalendarEventsReturned(events.execute());
        } catch (UserRecoverableAuthIOException e) {
            Log.e("Calendar Data Retrieval", "Could not authorise user: " + mCredential.getSelectedAccountName());

            //show unauthorised user ui
        }
    }

    @Override
    protected Void doInBackground(CalendarTaskParams... params) {
        CalendarTaskParams param = params[0];
        eventListener = param.result;
        if(param.accountName.isEmpty()){
            eventListener.onCalendarEventsReturned(null);
        }else {
            mCredential = GoogleAccountCredential.usingOAuth2(param.activity, SCOPES)
                    .setBackOff(new ExponentialBackOff());
            mCredential.setSelectedAccountName(param.accountName);
            try {
                getEvents(param);
            } catch (IOException e) {
                Log.e("Calendar Data Retrieval", "Could not retrieve calendar data");
                Log.e("Calendar Data Retrieval", e.getMessage());

                //make ui error
                params[0].accountName = "";
            }
        }
        return null;
    }
}
