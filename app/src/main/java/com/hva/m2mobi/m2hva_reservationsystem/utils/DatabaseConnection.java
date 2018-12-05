package com.hva.m2mobi.m2hva_reservationsystem.utils;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hva.m2mobi.m2hva_reservationsystem.activities.ReserveRoomActivity;
import com.hva.m2mobi.m2hva_reservationsystem.models.Reservation;
import com.hva.m2mobi.m2hva_reservationsystem.models.Room;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import timber.log.Timber;

public class DatabaseConnection {

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference roomRef = database.getReference("rooms");
    private static DatabaseReference reservationRef = database.getReference("reservations");

    private static final List<Room> roomList = new ArrayList<>();
    public static final List<Reservation> reservationList = new ArrayList<>();

    public static List<Room> getRooms() throws InterruptedException {
        if(roomList.isEmpty()){
            getDbRooms();
            Thread.sleep(2000L);
        }
        return roomList;
    }

    public static List<Reservation> getReservations() throws InterruptedException{
            getDbReservations();
            Thread.sleep(1000L);
        return reservationList;
    }

    public static void getDbRooms(){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                roomList.clear();
                for (DataSnapshot nextDS : dataSnapshot.getChildren()) {
                    Room room = nextDS.getValue(Room.class);
                    roomList.add(room);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Timber.w(databaseError.toException(), "loadPost:onCancelled");
                // ...
            }
        };
        roomRef.addValueEventListener(postListener);
    }

    public static void getDbReservations(){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                reservationList.clear();
                for (DataSnapshot nextDS : dataSnapshot.getChildren()) {
                    Reservation reservation = nextDS.getValue(Reservation.class);
                    reservationList.add(reservation);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Timber.w(databaseError.toException(), "loadPost:onCancelled");
                // ...
            }
        };
        reservationRef.addValueEventListener(postListener);
    }
    public static void deleteReservation(String id) {

        reservationRef.child(id).removeValue();
    }

    public static List<Reservation> filterReservations(List<Reservation> reservations){
        Date now = new Date();
        List<Reservation> reservationList = new ArrayList<>();
        for (Reservation reservation:reservations) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(CalendarConnection.TIME_FORMAT + CalendarConnection.DATE_FORMAT);
            try {
                Date date = simpleDateFormat.parse(reservation.getEndTime() + reservation.getDate());
                if (date.after(now))
                    reservationList.add(reservation);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return reservationList;
    }
}
