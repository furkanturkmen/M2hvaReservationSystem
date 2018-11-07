package com.hva.m2mobi.m2hva_reservationsystem.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hva.m2mobi.m2hva_reservationsystem.R;
import com.hva.m2mobi.m2hva_reservationsystem.models.Reservation;
import com.hva.m2mobi.m2hva_reservationsystem.adapters.ReservationsOverviewAdapter;
import com.hva.m2mobi.m2hva_reservationsystem.models.Room;
import java.util.ArrayList;



public class ReservationOverviewFragment extends Fragment {
    View view;
    private RecyclerView mRecyclerView;
    private ArrayList attendees;
    private ReservationsOverviewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DatabaseReference fbRef;
    private FirebaseDatabase fb;

    ArrayList<Reservation> exampleList;
    ArrayList<Room> roomList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_reservations_overview, container, false);
        createRoomList();
        createExampleList();
        buildRecylerView();
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder
                            target) {
                        return false;
                    }

                    //Called when a user swipes left or right on a ViewHolder
                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                        //Get the index corresponding to the selected position
                        int position = (viewHolder.getAdapterPosition());
                        mAdapter.notifyItemRemoved(position);
                        Snackbar snackbar = Snackbar
                                .make(view, exampleList.get(position).reservationRoom.getName() + " reservation has been deleted.", Snackbar.LENGTH_LONG).setAction("UNDO", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Snackbar undoSnackbar = Snackbar.make(view, "Reservation has been restored!", Snackbar.LENGTH_SHORT);
                                        undoSnackbar.show();
                                    }
                                });
                        exampleList.remove(position);
                        snackbar.show();
                    }
                };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        mAdapter.setOnItemClickListener(new ReservationsOverviewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Snackbar snackbar = Snackbar
                        .make(view, exampleList.get(position).reservationRoom.getName() + " has been clicked.", Snackbar.LENGTH_LONG).setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Snackbar snackbar1 = Snackbar.make(view, "Message is restored!", Snackbar.LENGTH_SHORT);
                                snackbar1.show();
                            }
                        });

                snackbar.show();
            }
        });

        fb = FirebaseDatabase.getInstance();
        fbRef = fb.getReference("reservations");
        System.out.println("SOUT: " + fbRef.child("res1"));

        return view;
    }


    public void createExampleList() {
        //attendees list
        attendees = new ArrayList<>();
        String furkan = "furkan";
        String kyle = "kyle";
        attendees.add(furkan);
        attendees.add(kyle);
        exampleList = new ArrayList<>();
        Reservation res;

        //filling reservation list
        for(int i=0; i<roomList.size(); i++) {
            res = new Reservation(attendees, "10-01-2019", "09:00", "10:00", roomList.get(i));
            exampleList.add(res);
        }
    }

    public void createRoomList(){
        roomList = new ArrayList<>();
        roomList.add(new Room(R.drawable.beach_house,"Beach House 2.0", "Plants", "10"));
        roomList.add(new Room(R.drawable.hunting_room, "Hunter Room", "Rifles", "5"));
        roomList.add(new Room(R.drawable.beach_house,"Elephant", "", "20"));
        roomList.add(new Room(R.drawable.hunting_room, "Auditorium", "Place for audits", "16"));
    }

    public void buildRecylerView() {
        mRecyclerView = view.findViewById(R.id.recyclerView_reservations);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        Log.d("size",""+exampleList.size());
        mAdapter = new ReservationsOverviewAdapter(exampleList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }
}
