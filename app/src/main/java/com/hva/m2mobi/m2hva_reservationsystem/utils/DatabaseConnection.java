package com.hva.m2mobi.m2hva_reservationsystem.utils;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hva.m2mobi.m2hva_reservationsystem.models.Reservation;
import com.hva.m2mobi.m2hva_reservationsystem.models.Room;

import java.util.ArrayList;
import java.util.Iterator;
import timber.log.Timber;

public class DatabaseConnection {

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference roomRef = database.getReference("rooms");
    private static DatabaseReference reservationRef = database.getReference("reservations");

    private static final ArrayList<Room> roomList = new ArrayList<>();
    public static final ArrayList<Reservation> reservationList = new ArrayList<>();

    public static ArrayList<Room> getRooms() throws InterruptedException {
        if(roomList.isEmpty()){
            getDbRooms();
            Thread.sleep(2000L);
        }
        return roomList;
    }

    public static ArrayList<Reservation> getReservations() throws InterruptedException{
            getDbReservations();
            Thread.sleep(1000L);
        return reservationList;
    }

    public static void getDbRooms(){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
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
}
