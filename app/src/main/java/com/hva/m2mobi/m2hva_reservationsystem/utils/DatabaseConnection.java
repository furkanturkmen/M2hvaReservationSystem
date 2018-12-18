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
    private static RoomListReturner mRoomListReturner;
    private static ReservationListReturner mReservationListReturner;


    public interface RoomListReturner{
        void onReturnList(List<Room> list);
    }
    public interface ReservationListReturner{
        void onReturnList(List<Reservation> list);
    }

    public static void getRooms(RoomListReturner listReturner){
        mRoomListReturner = listReturner;
        if(roomList.isEmpty()){
            getDbRooms();
        } else{
            mRoomListReturner.onReturnList(roomList);
        }
    }

    public static void getReservations(ReservationListReturner listReturner){
        mReservationListReturner = listReturner;
            getDbReservations();
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
                mRoomListReturner.onReturnList(roomList);
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
                mReservationListReturner.onReturnList(reservationList);

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
