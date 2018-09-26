package com.hva.m2mobi.m2hva_reservationsystem;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
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
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CalendarTask extends AsyncTask<Activity,Void,Void>{
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_READONLY);
    private static final int CALLBACK_WHEN_PERMISSION_ACCEPTED = 0;
    private Context context;
    private Activity activity;

    public void main() throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = new com.google.api.client.http.javanet.NetHttpTransport();
         GoogleAccountCredential mCredential = GoogleAccountCredential.usingOAuth2(context, SCOPES)
                .setBackOff(new ExponentialBackOff());
         mCredential.setSelectedAccountName("kylewatson98@gmail.com");


            Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, mCredential)
                    .setApplicationName(context.getString(R.string.app_name))
                    .build();
            // Do whatever you want with the Drive service


        // List the next 10 events from the primary calendar.
        DateTime now = new DateTime(System.currentTimeMillis());

        List<Event> items = new ArrayList<>();
        try{
            Events events = service.events().list("primary")
                    .setMaxResults(10)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            items = events.getItems();
        } catch (UserRecoverableAuthIOException e) {
            activity.startActivityForResult(e.getIntent(), 2);
        }
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

    @Override
    protected Void doInBackground(Activity... activities) {
        activity = activities[0];
        context = activity.getBaseContext();

        try {
            main();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return null;
    }
}
