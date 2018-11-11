package com.hva.m2mobi.m2hva_reservationsystem.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hva.m2mobi.m2hva_reservationsystem.R;
import com.hva.m2mobi.m2hva_reservationsystem.activities.MainActivity;
import com.hva.m2mobi.m2hva_reservationsystem.models.Reservation;
import com.hva.m2mobi.m2hva_reservationsystem.adapters.ReservationsOverviewAdapter;
import com.hva.m2mobi.m2hva_reservationsystem.models.Room;
import com.hva.m2mobi.m2hva_reservationsystem.utils.CalendarConnection;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;


public class ReservationOverviewFragment extends Fragment {
    View view;
    private RecyclerView mRecyclerView;
    private ArrayList attendees;
    private List<Reservation> reservationList = new ArrayList<>();
    private Reservation lastRemoved;
    private final ReservationsOverviewAdapter mAdapter = new ReservationsOverviewAdapter(reservationList);
    private RecyclerView.LayoutManager mLayoutManager;
    private DatabaseReference fbRef;
    private FirebaseDatabase fb;
    private RelativeLayout loader;
    private RelativeLayout noBooking;


    private static final int GET_RESERVATIONS = 0;
    private static final int REMOVE_RESERVATION = 1;
    private static final int ADD_RESERVATION = 2;

    private static final int REQUEST_PERMISSIONS_CALENDAR = 111;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_reservations_overview, container, false);

        loader = view.findViewById(R.id.loadingPanel);
        loader.setVisibility(View.GONE);
        noBooking = view.findViewById(R.id.noBooking);
        inflater.inflate(R.layout.no_bookings,noBooking);
        noBooking.setVisibility(View.GONE);
        Log.d("onCreate", "created successfully");
        buildRecylerView();
        requestPermissions(REQUEST_PERMISSIONS_CALENDAR);

        return view;
    }

    private void requestPermissions(int requestCode){
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.WRITE_CALENDAR},requestCode);
        }else if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.GET_ACCOUNTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.GET_ACCOUNTS},requestCode);
        }else{
            new CalendarAsyncTask(GET_RESERVATIONS).execute();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){

        Log.d("OnRequest",permissions[0] + " permission name");
        if(requestCode == REQUEST_PERMISSIONS_CALENDAR && grantResults[0] == 0){
            Log.d("OnRequest","passed perms");
            new CalendarAsyncTask(GET_RESERVATIONS).execute();
        }
    }

    public void buildRecylerView() {
        mRecyclerView = view.findViewById(R.id.recyclerView_reservations);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

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
                        final int position = (viewHolder.getAdapterPosition());
                        Snackbar snackbar = Snackbar
                                .make(view, reservationList.get(position).getReservationRoom().getName() + " reservation has been deleted.", Snackbar.LENGTH_LONG);//setAction("UNDO", new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View view) {
////                                        Snackbar undoSnackbar = Snackbar.make(view, "Reservation has been restored!", Snackbar.LENGTH_SHORT);
////                                        undoSnackbar.show();
////                                        new CalendarAsyncTask(ADD_RESERVATION).execute(lastRemoved);
//                                    }
//                                });
                        lastRemoved = reservationList.get(position);
                        new CalendarAsyncTask(REMOVE_RESERVATION).execute(reservationList.get(position));
                        snackbar.show();
                    }
                };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);


        fb = FirebaseDatabase.getInstance();
        fbRef = fb.getReference("reservations");
        System.out.println("SOUT: " + fbRef.child("res1"));
    }
    public class CalendarAsyncTask extends AsyncTask<Reservation, Void, List> {
        private int task;
        public CalendarAsyncTask(int task) {
            this.task = task;
            loader.setVisibility(View.VISIBLE);
            noBooking.setVisibility(View.GONE);
        }

        @Override
        protected List doInBackground(Reservation... reservations) {
            try {
                CalendarConnection con = new CalendarConnection(getContext());
                switch(task){
                    case REMOVE_RESERVATION:
                        con.removeEvent(reservations[0]);
                        break;
                    case ADD_RESERVATION:
                        con.addEvent(reservations[0]);
                }
                return con.getMyEvents(10);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List list) {
            super.onPostExecute(list);

            loader.setVisibility(View.GONE);
            if(list != null) {
                Log.d("async post",list.size()+"");
                reservationList = list;
                mAdapter.mResevationsList = list;
                mAdapter.notifyDataSetChanged();
                if(list.isEmpty()){
                    noBooking.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
