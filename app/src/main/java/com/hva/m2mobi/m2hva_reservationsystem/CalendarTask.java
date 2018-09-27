package com.hva.m2mobi.m2hva_reservationsystem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.google.android.gms.tasks.Task;
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


public class CalendarTask extends AsyncTask<CalendarTaskParams,Void,Void>{
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR_READONLY);
    private static final String CALENDAR_ID = "rbvkmi4iflbmllftnd9d12c9g0@group.calendar.google.com";
    static final int REQUEST_CODE = 123;
    private GoogleAccountCredential mCredential;
    private TaskIF resultResolver;
    private void authorizeAccount(Activity activity) throws IOException{
        Context context = activity.getBaseContext();
        // Build a new authorized API client service.
         mCredential = GoogleAccountCredential.usingOAuth2(context, SCOPES)
                .setBackOff(new ExponentialBackOff());
         //mCredential.setSelectedAccountName("kylewatson98@gmail.com");
        int noOfAccounts = mCredential.getAllAccounts().length;
        if(noOfAccounts > 1) {
            Intent chooseAccount = mCredential.newChooseAccountIntent();
            activity.startActivityForResult(chooseAccount, REQUEST_CODE);
        }else{
            setAccountName(mCredential.getAllAccounts()[0].name, activity);
        }
    }

    public void getEvents(Activity activity) throws  IOException{
       Context context = activity.getBaseContext();

        final NetHttpTransport HTTP_TRANSPORT = new com.google.api.client.http.javanet.NetHttpTransport();
        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, mCredential)
                .setApplicationName(context.getString(R.string.app_name))
                .build();
        // Do whatever you want with the Drive service


        // List the next 10 events from the primary calendar.
        DateTime now = new DateTime(System.currentTimeMillis());

        try{
            Events events = service.events().list(CALENDAR_ID)
                    .setMaxResults(10)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            resultResolver.onCalendarEventsReturned(events);
        } catch (UserRecoverableAuthIOException e) {
            activity.startActivityForResult(e.getIntent(), 2);
        }
    }

    void setAccountName(String name, Activity activity) throws IOException{
        mCredential.setSelectedAccountName(name);
        getEvents(activity);
    }

    @Override
    protected Void doInBackground(CalendarTaskParams... params) {
        resultResolver = params[0].result;
        try {
            authorizeAccount(params[0].activity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
