package com.hva.m2mobi.m2hva_reservationsystem.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hva.m2mobi.m2hva_reservationsystem.R;
import com.hva.m2mobi.m2hva_reservationsystem.models.Reservation;
import com.hva.m2mobi.m2hva_reservationsystem.models.Room;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;

//A model to retrieve events from the public calendar - can't be used on the main thread (needs to be called in async task);
//Need to request GET_ACCOUNTS and WRITE_CALENDAR permissions
public class CalendarConnection{
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    //List of calendar ID's (this will be replaced with getting each ID from the database)

    private static final java.util.List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);

    public static final String DATE_FORMAT = "dd-MM-yyyy";
    public static final String TIME_FORMAT = "HH:mm";

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

    public void removeEvent(Reservation reservation) throws IOException {
        calendar.events().delete(reservation.getReservationRoom().getCalendarID(), reservation.getID()).execute();
    }

    public String addEvent(Reservation reservation) throws IOException, ParseException {
        Event event = new Event();
        java.util.Calendar utilCalendar = java.util.Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT+TIME_FORMAT);
        utilCalendar.setTime(sdf.parse(reservation.getDate()+reservation.getStartTime()));
        EventDateTime edt = new EventDateTime().setDateTime(new DateTime(utilCalendar.getTime()));
        event.setStart(edt);
        utilCalendar.setTime(sdf.parse(reservation.getDate()+reservation.getEndTime()));
        edt = new EventDateTime().setDateTime(new DateTime(utilCalendar.getTime()));
        event.setEnd(edt);
        event.setSummary("Meeting with " + accountName);
        event.setDescription(reservation.getAttendees() + " people attending.");
        return calendar.events().insert(reservation.getReservationRoom().getCalendarID(),event).execute().getId();
    }

    private List<Reservation> eventListToReservation(List<Event> events, Room room) throws ParseException {
        List <Reservation> res = new ArrayList<>();
        for (Event event:events) {
            if(event != null) {
                DateTime startDateTime = event.getStart().getDateTime();
                DateTime endDateTime = event.getEnd().getDateTime();
                if(startDateTime != null && endDateTime != null) {
                    SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
                    SimpleDateFormat tf = new SimpleDateFormat(TIME_FORMAT);
                    SimpleDateFormat pf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    Date startDate = pf.parse(startDateTime.toString());
                    Date endDate = pf.parse(endDateTime.toString());

                    String startTime = tf.format(startDate);
                    String endTime = tf.format(endDate);
                    String date = df.format(startDate);

                    Reservation newRes = new Reservation(0, startTime, endTime, room,
                            event.getCreator().getEmail(), date, event.getId());

                    res.add(newRes);
                }
            }
        }
        return res;
    }

    public List<Reservation> getMyEvents(int noOfEvents) throws IOException, ParseException, InterruptedException {
        List<Reservation> allEvents = getAllEvents(noOfEvents);
        return filterEventsByOwner(allEvents,accountName);
    }

    private List<Reservation> getRoomEvents(Room room, int noOfEvents) throws IOException, ParseException {
        DateTime now = new DateTime(System.currentTimeMillis());
        Calendar.Events.List events = calendar.events().list(room.getCalendarID());
        events.setMaxResults(noOfEvents)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true);
        Events result = events.execute();
        return eventListToReservation(result.getItems(),room);
    }

    private List<Reservation> getAllEvents(int noOfEvents) throws IOException, ParseException, InterruptedException {
        List<Reservation> items = new ArrayList<>();
        for (Room room: DatabaseConnection.getRooms()) {
            List<Reservation> result = getRoomEvents(room,noOfEvents);
            if (!result.isEmpty())
                items.addAll(result);
        }
        return items;
    }

    public List<Reservation> filterEventsByOwner(List<Reservation> items, String accountName){
        if (items.isEmpty())
            return items;
        List<Reservation> newItems = new ArrayList<>();
        for (Reservation res : items) {
            String name = res.getCreator();
            if (name.equals(accountName))
                newItems.add(res);
        }
        return newItems;
    }
}
