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
    private ReservationsOverviewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private DatabaseReference fbRef;
    private FirebaseDatabase fb;

    List<Reservation> reservationList;
    ArrayList<Room> roomList;

    private static final int REQUEST_PERMISSIONS_CALENDAR = 111;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_reservations_overview, container, false);
        Log.d("onCreate", "created successfully");
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
            new CalendarAsyncTask().execute();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){

        Log.d("OnRequest",permissions[0] + " permission name");
        if(requestCode == REQUEST_PERMISSIONS_CALENDAR && grantResults[0] == 0){
            Log.d("OnRequest","passed perms");
            new CalendarAsyncTask().execute();
        }
    }

    /*public void getReservationList() {
        //attendees list
        CalendarConnection con = new CalendarConnection(getContext());
        try {
            reservationList = (ArrayList<Reservation>) con.getMyEvents(20);
            for (Reservation res:reservationList) {
                Log.d("Res List", res.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    public void buildRecylerView() {
        mRecyclerView = view.findViewById(R.id.recyclerView_reservations);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new ReservationsOverviewAdapter(reservationList);
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
                        int position = (viewHolder.getAdapterPosition());
                        mAdapter.notifyItemRemoved(position);
                        Snackbar snackbar = Snackbar
                                .make(view, reservationList.get(position).getReservationRoom().getName() + " reservation has been deleted.", Snackbar.LENGTH_LONG).setAction("UNDO", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Snackbar undoSnackbar = Snackbar.make(view, "Reservation has been restored!", Snackbar.LENGTH_SHORT);
                                        undoSnackbar.show();
                                    }
                                });
                        reservationList.remove(position);
                        snackbar.show();
                    }
                };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);


        fb = FirebaseDatabase.getInstance();
        fbRef = fb.getReference("reservations");
        System.out.println("SOUT: " + fbRef.child("res1"));
    }
    public class CalendarAsyncTask extends AsyncTask<Void, Void, List> {
        public CalendarAsyncTask() {
        }

        @Override
        protected List doInBackground(Void... voids) {
            try {
                return new CalendarConnection(getContext()).getMyEvents(10);
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
            if(list == null){
                list = new ArrayList();
            }
            reservationList = list;
            buildRecylerView();
        }
    }
}
