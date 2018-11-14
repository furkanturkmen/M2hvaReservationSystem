package com.hva.m2mobi.m2hva_reservationsystem.utils;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hva.m2mobi.m2hva_reservationsystem.models.Room;

import java.util.ArrayList;
import java.util.Iterator;

import timber.log.Timber;

import static android.content.ContentValues.TAG;

public class DatabaseConnection {

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference dbRef = database.getReference("rooms");

    private static final ArrayList<Room> roomList = new ArrayList<Room>();

    public static ArrayList<Room> getRooms(){
        if(roomList.isEmpty()){
            dbOphalen();
        }
        System.out.println("ROOMLIST: " + roomList);
        return roomList;
    }

    public static void dbOphalen(){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                while(iterator.hasNext()){
                    DataSnapshot nextDS = iterator.next();
//                    Room room = new Room(nextDS);
                    Room room = nextDS.getValue(Room.class);
                    roomList.add(room);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        dbRef.addValueEventListener(postListener);
    }

}
