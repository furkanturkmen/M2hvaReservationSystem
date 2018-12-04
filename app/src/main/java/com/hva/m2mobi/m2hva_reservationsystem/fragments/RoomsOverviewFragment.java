package com.hva.m2mobi.m2hva_reservationsystem.fragments;

import android.content.Intent;
import android.os.AsyncTask;
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
import com.hva.m2mobi.m2hva_reservationsystem.models.Reservation;
import com.hva.m2mobi.m2hva_reservationsystem.models.Room;
import com.hva.m2mobi.m2hva_reservationsystem.utils.CalendarConnection;
import com.hva.m2mobi.m2hva_reservationsystem.utils.DatabaseConnection;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static android.content.ContentValues.TAG;

public class RoomsOverviewFragment extends Fragment {
    private RoomsOverviewAdapter mAdapter;
    public static final String ROOM_EXTRA = "m2_room_extra";
    private List<Room> dbRooms = MainActivity.roomsOutDB;
    private static final String ALL_DAY = "all day";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_rooms_overview, container, false);
        buildRecyclerView(view);
        updateAvailability(view);
        return view;
    }

    public void buildRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new RoomsOverviewAdapter(dbRooms, getContext());

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



    public void updateAvailability(View v){
        for (Room room:dbRooms){
            new CalendarAsyncTask(v).execute(room);
        }
    }

    public class CalendarAsyncTask extends AsyncTask<Room, Void, List<Reservation>> {

        View view;
        public CalendarAsyncTask(View view){
            this.view = view;
        }

        @Override
        protected List<Reservation> doInBackground(Room... rooms) {
            try {
                List<Reservation> reservationList;
                reservationList = CalendarConnection.getInstance(view.getContext()).getRoomEvents(rooms[0], 1);
                if (reservationList.isEmpty()){
                    reservationList.add(new Reservation(0, " ", " ", rooms[0], " ", ALL_DAY, " "));
                }
                return reservationList;
            } catch (IOException | ParseException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Reservation> v) {
            super.onPostExecute(v);
            if (v.get(0).getDate().equals(ALL_DAY)){
                for (Room room:dbRooms){
                    if (room.getCalendarID().equals(v.get(0).getReservationRoom().getCalendarID())){
                        room.setAvailability(true);
                        room.setTime(ALL_DAY);
                    }
                }
                mAdapter.notifyDataSetChanged();
            } else {
                setRoomAvailability(v.get(0));
            }
        }
    }

    public void setRoomAvailability(Reservation reservation){
        SimpleDateFormat dateFormat = new SimpleDateFormat(CalendarConnection.DATE_FORMAT+CalendarConnection.TIME_FORMAT);
        try {
            Date startTime = dateFormat.parse(reservation.getDate()+reservation.getStartTime());
            Date now = new Date();

            boolean available = now.before(startTime);
            String untilTime;
            if (available){
                untilTime = reservation.getStartTime();
            } else {
                untilTime = reservation.getEndTime();
            }

            for (Room room:dbRooms){
                if (room.getCalendarID().equals(reservation.getReservationRoom().getCalendarID())){
                    room.setAvailability(available);
                    room.setTime(untilTime);
                }
            }

            mAdapter.notifyDataSetChanged();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
