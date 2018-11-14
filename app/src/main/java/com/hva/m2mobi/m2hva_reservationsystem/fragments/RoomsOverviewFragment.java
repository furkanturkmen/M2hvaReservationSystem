package com.hva.m2mobi.m2hva_reservationsystem.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hva.m2mobi.m2hva_reservationsystem.R;
import com.hva.m2mobi.m2hva_reservationsystem.activities.MainActivity;
import com.hva.m2mobi.m2hva_reservationsystem.activities.ReserveRoomActivity;
import com.hva.m2mobi.m2hva_reservationsystem.adapters.RoomsOverviewAdapter;
import com.hva.m2mobi.m2hva_reservationsystem.models.Room;
import com.hva.m2mobi.m2hva_reservationsystem.utils.CalendarConnection;
import com.hva.m2mobi.m2hva_reservationsystem.utils.DatabaseConnection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import static android.content.ContentValues.TAG;

public class RoomsOverviewFragment extends Fragment {
    private RoomsOverviewAdapter mAdapter;
    public static final String ROOM_EXTRA = "m2_room_extra";
    private ArrayList<Room> dbRooms;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        try {
            dbRooms = DatabaseConnection.getRooms();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        View view = inflater.inflate(R.layout.fragment_rooms_overview, container, false);
        buildRecyclerView(view);
        return view;
    }

    public void buildRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new RoomsOverviewAdapter(dbRooms);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new RoomsOverviewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getContext(),ReserveRoomActivity.class);
                intent.putExtra(ROOM_EXTRA,position);
                startActivityForResult(intent,MainActivity.REQUEST_RESERVE_ROOM);
            }
        });
    }
    

}
