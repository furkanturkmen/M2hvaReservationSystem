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
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import com.google.firebase.auth.FirebaseAuth;
import com.hva.m2mobi.m2hva_reservationsystem.R;
import com.hva.m2mobi.m2hva_reservationsystem.models.Reservation;
import com.hva.m2mobi.m2hva_reservationsystem.models.Room;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

//A model to retrieve events from the public calendar - can't be used on the main thread (needs to be called in async task);
//Need to request GET_ACCOUNTS and WRITE_CALENDAR permissions
public class CalendarConnection{
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    //List of calendar ID's (this will be replaced with getting each ID from the database)

    public static final java.util.List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR);
    public static final Room[] ROOMS = {new Room(R.drawable.beach_house,"Mammut","Big Room", "h1omqjoq2o29qs177sb27fleec@group.calendar.google.com",10),
            new Room(R.drawable.beach_house,"Jungle","Medium Room", "nrqltuh2vd42ge4rgfsa909mdo@group.calendar.google.com",8),
            new Room(R.drawable.beach_house,"Elephant","Medium Room", "k26b7a8cvd0rk4oamf58d16ph4@group.calendar.google.com",8),
            new Room(R.drawable.hunting_room,"Hunting Room","Small Room", "l117asict045c4ii2d3da65m54@group.calendar.google.com",6),
            new Room(R.drawable.beach_house,"Beach House 2.0","Small Room", "352a8lc5v10v6qm5prf3sp89gs@group.calendar.google.com",2),
            new Room(R.drawable.beach_house,"Zoo","Auditorium", "abujhftkqu0k3a9h2dbtcm9d5k@group.calendar.google.com",20)
    };

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

    public void addEvent(Reservation reservation) throws IOException, ParseException {
        Event event = new Event();
        java.util.Calendar utilCalendar = java.util.Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        utilCalendar.setTime(sdf.parse(reservation.getStartTime()));
        EventDateTime edt = new EventDateTime().setDate(new DateTime(utilCalendar.getTime()));
        event.setStart(edt);
        utilCalendar.setTime(sdf.parse(reservation.getEndTime()));
        edt.setDate(new DateTime(utilCalendar.getTime()));
        event.setEnd(edt);

        if(event != null)
            calendar.events().insert(reservation.getReservationRoom().getCalendarID(),event).execute();
    }

    private List<Reservation> eventListToReservation(List<Event> events, Room room) throws IOException {
        List <Reservation> res = new ArrayList<>();
        for (Event event:events) {
            if(event != null){
                Reservation newRes = new Reservation(0, event.getStart().toPrettyString(), event.getEnd().toString(),room, event.getCreator().getEmail());
                res.add(newRes);
            }
        }
        return res;
    }

    public List<Reservation> getMyEvents(int noOfEvents) throws IOException {
        List<Reservation> allEvents = getAllEvents(noOfEvents);
        List <Reservation> ownerEvents = filterEventsByOwner(allEvents,accountName);

        return ownerEvents;
    }

    public List<Reservation> getRoomEvents(Room room, int noOfEvents) throws IOException {
        DateTime now = new DateTime(System.currentTimeMillis());
        Calendar.Events.List events = calendar.events().list(room.getCalendarID());
        events.setMaxResults(noOfEvents)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true);
        Events result = events.execute();
        return eventListToReservation(result.getItems(),room);
    }

   /* public List<Event> getCurrentRoomEvent(int room) throws IOException{
        DateTime now = new DateTime(System.currentTimeMillis());
        Calendar.Events.List event = calendar.events().list(CALENDAR_ID[room]);
        event.setMaxResults(1)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true);
        return event.execute().getItems();
    }*/

    public List<Reservation> getAllEvents(int noOfEvents) throws IOException{
        List<Reservation> items = new ArrayList<>();
        for (Room room: ROOMS) {
            List<Reservation> result = getRoomEvents(room,noOfEvents);
            if (!result.isEmpty())
                items.addAll(result);
        }
        return items;
    }

    private List<Reservation> filterEventsByOwner(List<Reservation> items, String accountName){
        if (items.isEmpty())
            return items;

        for (Reservation res : items) {
            String name = res.getCreator();
            if (!name.equals(accountName))
                items.remove(res);
        }
        return items;
    }
}
