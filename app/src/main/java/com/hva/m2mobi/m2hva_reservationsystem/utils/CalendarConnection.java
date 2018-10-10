package com.hva.m2mobi.m2hva_reservationsystem.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.firebase.auth.FirebaseAuth;
import com.hva.m2mobi.m2hva_reservationsystem.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//A model to retrieve events from the public calendar - can't be used on the main thread (needs to be called in async task);
//Need to request GET_ACCOUNTS and WRITE_CALENDAR permissions
public class CalendarConnection{
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    //List of calendar ID's (this will be replaced with getting each ID from the database)
    private static final String[] CALENDAR_ID = {"352a8lc5v10v6qm5prf3sp89gs@group.calendar.google.com","nrqltuh2vd42ge4rgfsa909mdo@group.calendar.google.com","l117asict045c4ii2d3da65m54@group.calendar.google.com", "k26b7a8cvd0rk4oamf58d16ph4@group.calendar.google.com", "h1omqjoq2o29qs177sb27fleec@group.calendar.google.com"};
    public static final java.util.List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    public static final int ROOM_OREO = 0;
    public static final int ROOM_MARS = 1;
    public static final int ROOM_KITK = 2;
    public static final int ROOM_JELB = 3;
    public static final int ROOM_ICSW = 4;

    private Calendar calendar;
    private String accountName;

    public CalendarConnection(Context context){
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            throw new SecurityException("Calendar Connection requires WRITE_CALENDAR permission");
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS)
                != PackageManager.PERMISSION_GRANTED) {
            throw new SecurityException("Calendar Connection requires GET_ACCOUNTS permission");
        }
        accountName = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        GoogleAccountCredential mCredential = GoogleAccountCredential.usingOAuth2(context, SCOPES)
                .setBackOff(new ExponentialBackOff());
        mCredential.setSelectedAccountName(accountName);
        final NetHttpTransport HTTP_TRANSPORT = new com.google.api.client.http.javanet.NetHttpTransport();
        calendar = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, mCredential)
                .setApplicationName(context.getString(R.string.app_name))
                .build();
    }

    public void addEvent(Event event, int room) throws IOException {
        if(event != null)
            calendar.events().insert(CALENDAR_ID[room],event).execute();
    }

    public List<Event> getMyEvents() throws IOException {
        List<Event> allEvents = getAllEvents();
        return filterEventsByOwner(allEvents,accountName);
    }

    public List<Event> getRoomEvents(int room) throws IOException {
        DateTime now = new DateTime(System.currentTimeMillis());
        Calendar.Events.List events = calendar.events().list(CALENDAR_ID[room]);
        events.setMaxResults(10)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true);
        Events result = events.execute();
        return result.getItems();
    }

    public List<Event> getCurrentRoomEvent(int room) throws IOException{
        DateTime now = new DateTime(System.currentTimeMillis());
        Calendar.Events.List event = calendar.events().list(CALENDAR_ID[room]);
        event.setMaxResults(1)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true);
        return event.execute().getItems();
    }

    public List<Event> getAllEvents() throws IOException{
        DateTime now = new DateTime(System.currentTimeMillis());
        java.util.List<Event> items = new ArrayList<>();
        for (String id : CALENDAR_ID) {
            Calendar.Events.List events = calendar.events().list(id);
            events.setMaxResults(10)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true);
            Events result = events.execute();
            if (result == null)
                return null;
            java.util.List<Event> newItems = result.getItems();
            if (!newItems.isEmpty())
                items.addAll(newItems);
        }
        return items;
    }

    private List<Event> filterEventsByOwner(List<Event> items, String accountName){
        if (items.isEmpty())
            return items;

        for (Event event : items) {
            String name = event.getCreator().getEmail();
            if (!name.equals(accountName))
                items.remove(event);
        }
        return items;
    }
}
