package com.hva.m2mobi.m2hva_reservationsystem.utils;

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
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.api.services.calendar.Calendar.Events.List;
import com.hva.m2mobi.m2hva_reservationsystem.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

//A task to retrieve events from the public calendar
public class CalendarTask extends AsyncTask<CalendarTaskParams,Void,Void>{
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static final String[] CALENDAR_ID = {"352a8lc5v10v6qm5prf3sp89gs@group.calendar.google.com","nrqltuh2vd42ge4rgfsa909mdo@group.calendar.google.com","l117asict045c4ii2d3da65m54@group.calendar.google.com", "k26b7a8cvd0rk4oamf58d16ph4@group.calendar.google.com", "h1omqjoq2o29qs177sb27fleec@group.calendar.google.com"};
    public static final java.util.List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    public static final int ACCOUNT_AUTH_REQUEST = 22;

    private GoogleAccountCredential mCredential;
    private CalendarEventListener eventListener;

    //Retrieves events from the calendar and sends them to the eventListener
    private void getEvents(CalendarTaskParams param) throws IOException {
        Activity activity = param.activity;
        Context context = activity.getBaseContext();

        final NetHttpTransport HTTP_TRANSPORT = new com.google.api.client.http.javanet.NetHttpTransport();
        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, mCredential)
                .setApplicationName(context.getString(R.string.app_name))
                .build();

        // List the next 10 events from the public calendar.
        DateTime now = new DateTime(System.currentTimeMillis());

        try{
            switch(param.calendarAction){
                case CalendarTaskParams.GET_ALL_EVENTS:
                    eventListener.onCalendarEventsReturned(getAllEvents(service));
                break;

                case CalendarTaskParams.GET_ROOM_EVENTS:
                    List events = service.events().list(CALENDAR_ID[param.room]);
                    events.setMaxResults(10)
                            .setTimeMin(now)
                            .setOrderBy("startTime")
                            .setSingleEvents(true);
                    eventListener.onCalendarEventsReturned(events.execute());
                    break;
                case CalendarTaskParams.GET_CURRENT_EVENT:
                    List event = service.events().list(CALENDAR_ID[param.room]);
                    event.setMaxResults(1)
                            .setTimeMin(now)
                            .setOrderBy("startTime")
                            .setSingleEvents(true);
                    eventListener.onCalendarEventsReturned(event.execute());
                    break;
                case CalendarTaskParams.GET_MY_EVENTS:
                    eventListener.onCalendarEventsReturned(filterEventsByOwner(getAllEvents(service),mCredential.getSelectedAccountName()));
                    break;
                case CalendarTaskParams.ADD_EVENT:
                    if(param.event != null)
                        service.events().insert(CALENDAR_ID[param.room],param.event).execute();
                break;
            }
        } catch (UserRecoverableAuthIOException e) {
            Log.e("Calendar Data Retrieval", "Could not authorise user: " + mCredential.getSelectedAccountName());
            e.printStackTrace();
            activity.startActivityForResult(e.getIntent(), ACCOUNT_AUTH_REQUEST);
            //Log.e("Calendar Data Retrieval",e.getMessage());
            //show unauthorised user ui
        }
    }

    private Events getAllEvents(Calendar calendar) throws IOException{

        DateTime now = new DateTime(System.currentTimeMillis());
        java.util.List<Event> items = new ArrayList<>();
        List events;
        for (String id : CALENDAR_ID) {
            events = calendar.events().list(id);
            events.setMaxResults(10)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true);
            Events result = events.execute();
            if (result == null)
                break;
            java.util.List<Event> newItems = result.getItems();
            if (!newItems.isEmpty())
                items.addAll(newItems);
        }
        Events newEvents = new Events();
        newEvents.setItems(items);
        return newEvents;
    }

    private Events filterEventsByOwner(Events events, String accountName){
        java.util.List<Event> items;
        java.util.List<Event> keptItems = new ArrayList<>();
        if(events == null)
            return null;

        items = events.getItems();
        events = new Events();

        if (items.isEmpty())
            return events;
        for (Event event : items) {
            String name = event.getCreator().getEmail();
            if (name.equals(accountName))
                keptItems.add(event);
        }

        if(!keptItems.isEmpty())
            events.setItems(keptItems);
        return events;
    }

    @Override
    protected Void doInBackground(CalendarTaskParams... params) {
        CalendarTaskParams param = params[0];
        eventListener = param.result;
        if (param.accountName.isEmpty()) {
            eventListener.onCalendarEventsReturned(null);
        } else {
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
