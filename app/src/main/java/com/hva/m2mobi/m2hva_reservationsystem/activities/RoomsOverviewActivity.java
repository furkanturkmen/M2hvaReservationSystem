package com.hva.m2mobi.m2hva_reservationsystem.activities;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hva.m2mobi.m2hva_reservationsystem.R;
import com.hva.m2mobi.m2hva_reservationsystem.adapters.RoomsOverviewAdapter;
import com.hva.m2mobi.m2hva_reservationsystem.adapters.Room;

import java.util.ArrayList;
import java.util.List;

public class RoomsOverviewActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RoomsOverviewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private  List<Room> mListData;

    FirebaseDatabase mDatabase;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms_overview);



        mListData = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance();



        mRecyclerView = findViewById(R.id.recyclerView);

        // changes in content do not change layout of recyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter
         mAdapter = new RoomsOverviewAdapter(this, mListData);
         getDataFirebase();
    }

    private void getDataFirebase(){
        myRef = mDatabase.getReference("rooms");

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Room data = new Room();
                data = dataSnapshot.getValue(Room.class);
                mListData.add(data);

                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
