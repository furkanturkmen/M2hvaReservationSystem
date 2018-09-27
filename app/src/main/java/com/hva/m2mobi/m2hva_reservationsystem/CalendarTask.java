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
import java.io.IOException;
import java.util.Collections;
import java.util.List;

//A task to retrieve events from the public calendar
public class CalendarTask extends AsyncTask<CalendarTaskParams,Void,Void>{
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String CALENDAR_ID = "rbvkmi4iflbmllftnd9d12c9g0@group.calendar.google.com";
    static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_READONLY);

    private GoogleAccountCredential mCredential;
    private CalendarEventListener eventListener;

    //Retrieves events from the calendar and sends them to the eventListener
    private void getEvents(Activity activity) throws  IOException{
       Context context = activity.getBaseContext();

        final NetHttpTransport HTTP_TRANSPORT = new com.google.api.client.http.javanet.NetHttpTransport();
        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, mCredential)
                .setApplicationName(context.getString(R.string.app_name))
                .build();

        // List the next 10 events from the public calendar.
        DateTime now = new DateTime(System.currentTimeMillis());

        try{
            Events events = service.events().list(CALENDAR_ID)
                    .setMaxResults(10)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            eventListener.onCalendarEventsReturned(events);
        } catch (UserRecoverableAuthIOException e) {
            Log.e("Calendar Data Retrieval", "Could not authorise user: " + mCredential.getSelectedAccountName());

            //show unauthorised user ui
        }
    }

    @Override
    protected Void doInBackground(CalendarTaskParams... params) {
        eventListener = params[0].result;
        if(params[0].accountName.isEmpty()){
            eventListener.onCalendarEventsReturned(null);
        }else {
            mCredential = GoogleAccountCredential.usingOAuth2(params[0].activity, SCOPES)
                    .setBackOff(new ExponentialBackOff());
            mCredential.setSelectedAccountName(params[0].accountName);
            try {
                getEvents(params[0].activity);
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
