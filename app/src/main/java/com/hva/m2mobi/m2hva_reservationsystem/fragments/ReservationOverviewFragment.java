package com.hva.m2mobi.m2hva_reservationsystem.fragments;

import android.Manifest;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.hva.m2mobi.m2hva_reservationsystem.R;
import com.hva.m2mobi.m2hva_reservationsystem.models.Reservation;
import com.hva.m2mobi.m2hva_reservationsystem.adapters.ReservationsOverviewAdapter;
import com.hva.m2mobi.m2hva_reservationsystem.utils.CalendarConnection;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ReservationOverviewFragment extends Fragment {
    View view;
    private List<Reservation> mReservationList = new ArrayList<>();
    private ReservationsOverviewAdapter mAdapter = new ReservationsOverviewAdapter(mReservationList);
    private RelativeLayout mLoaderLayout;
    private RelativeLayout mNoBookingLayout;
    private RecyclerView mRecyclerView;


    private static final int GET_RESERVATIONS = 0;
    private static final int REMOVE_RESERVATION = 1;
    private static final int ADD_RESERVATION = 2;

    private static final int REQUEST_PERMISSIONS_CALENDAR = 111;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_reservations_overview, container, false);

        mLoaderLayout = view.findViewById(R.id.loadingPanel);
        mLoaderLayout.setVisibility(View.GONE);
        mNoBookingLayout = view.findViewById(R.id.noBooking);
        inflater.inflate(R.layout.no_bookings, mNoBookingLayout);
        mNoBookingLayout.setVisibility(View.GONE);
        buildRecyclerView();
        requestPermissions();
        return view;
    }

    private void requestPermissions(){
        if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()),new String[]{Manifest.permission.WRITE_CALENDAR},REQUEST_PERMISSIONS_CALENDAR);
        }else if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.GET_ACCOUNTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()),new String[]{Manifest.permission.GET_ACCOUNTS},REQUEST_PERMISSIONS_CALENDAR);
        }else{
            new CalendarAsyncTask(GET_RESERVATIONS).execute();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if(requestCode == REQUEST_PERMISSIONS_CALENDAR && grantResults[0] == 0){
            requestPermissions();
        }
    }

    public void buildRecyclerView() {
        mRecyclerView = view.findViewById(R.id.recyclerView_reservations);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                          @NonNull RecyclerView.ViewHolder viewHolder,
                                          @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    //Called when a user swipes left or right on a ViewHolder
                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                        //Get the index corresponding to the selected position
                        final int position = (viewHolder.getAdapterPosition());
                        Snackbar snackbar = Snackbar.make(view,
                                mReservationList.get(position).getReservationRoom().getName()
                                        + " reservation has been deleted.", Snackbar.LENGTH_LONG);
                        new CalendarAsyncTask(REMOVE_RESERVATION).execute(mReservationList.get(position));
                        snackbar.show();
                    }
                };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }
    private class CalendarAsyncTask extends AsyncTask<Reservation, Void, List> {
        private int task;
        private CalendarAsyncTask(int task) {
            this.task = task;
            mLoaderLayout.setVisibility(View.VISIBLE);
            mNoBookingLayout.setVisibility(View.GONE);
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
            } catch (IOException | ParseException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List list) {
            super.onPostExecute(list);

            mLoaderLayout.setVisibility(View.GONE);
            if(list != null) {
                mReservationList = list;
                mAdapter = new ReservationsOverviewAdapter(mReservationList);
                //mAdapter.setReservationList(list);
                mAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(mAdapter);
                if(list.isEmpty()){
                    mNoBookingLayout.setVisibility(View.VISIBLE);
                }
            }
        }
    }
}
