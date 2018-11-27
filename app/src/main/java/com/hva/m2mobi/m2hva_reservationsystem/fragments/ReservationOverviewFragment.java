package com.hva.m2mobi.m2hva_reservationsystem.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.firebase.auth.FirebaseAuth;
import com.hva.m2mobi.m2hva_reservationsystem.R;
import com.hva.m2mobi.m2hva_reservationsystem.activities.MainActivity;
import com.hva.m2mobi.m2hva_reservationsystem.models.Reservation;
import com.hva.m2mobi.m2hva_reservationsystem.adapters.ReservationsOverviewAdapter;
import com.hva.m2mobi.m2hva_reservationsystem.models.Reservation;
import com.hva.m2mobi.m2hva_reservationsystem.utils.CalendarConnection;
import com.hva.m2mobi.m2hva_reservationsystem.utils.DatabaseConnection;
import com.hva.m2mobi.m2hva_reservationsystem.utils.DatabaseService;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class ReservationOverviewFragment extends Fragment {
    View view;

    private List<Reservation> dbReservationList = new ArrayList<>();
    private ReservationsOverviewAdapter dbAdapter;

    private RelativeLayout mLoaderLayout;
    private RelativeLayout mNoBookingLayout;
    private RecyclerView mRecyclerView;
    private RelativeLayout mNoPermissionLayout;
    private SwipeRefreshLayout mySwipeRefreshLayout;

    LinearLayoutManager layoutManager;
    ItemTouchHelper.SimpleCallback simpleItemTouchCallback;
    ItemTouchHelper itemTouchHelper;

    private static final int GET_RESERVATIONS = 0;
    private static final int REMOVE_RESERVATION = 1;
    private static final int ADD_RESERVATION = 2;
    private static final int REFRESH_RESERVATIONS = 3;

    private static final int REQUEST_PERMISSIONS_CALENDAR = 111;
    private static final int REQUEST_ACCOUNT_CALENDAR = 222;
    String accountName = FirebaseAuth.getInstance().getCurrentUser().getEmail();


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_reservations_overview, container, false);

        mLoaderLayout = view.findViewById(R.id.loadingPanel);
        mLoaderLayout.setVisibility(View.GONE);
        mNoBookingLayout = view.findViewById(R.id.noBooking);
        inflater.inflate(R.layout.no_bookings, mNoBookingLayout);
        mNoBookingLayout.setVisibility(View.GONE);
        mNoPermissionLayout = view.findViewById(R.id.noPermission);
        inflater.inflate(R.layout.no_permission, mNoPermissionLayout);
        mNoPermissionLayout.setVisibility(View.GONE);

//        requestData();
        buildRecyclerView();
        requestPermissions();
        mySwipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary,
                R.color.colorAccentBlue,
                R.color.m2mobiLightGrey);
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        new CalendarAsyncTask(REFRESH_RESERVATIONS).execute();
                    }
                }
        );
        updateUI();

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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_ACCOUNT_CALENDAR){
            new CalendarAsyncTask(GET_RESERVATIONS).execute();
        }else{
            mNoPermissionLayout.setVisibility(View.VISIBLE);
        }
    }

    public void buildRecyclerView() {
        mRecyclerView = view.findViewById(R.id.recyclerView_reservations);
        layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        updateUI();

        simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                          @NonNull RecyclerView.ViewHolder viewHolder,
                                          @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    //Called when a user swipes left or right on a ViewHolder
                    @Override
                    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                        //Get the index corresponding to the selected position
                        new AlertDialog.Builder(getContext())
                                .setTitle(R.string.remove_title)
                                .setMessage(R.string.remove_description)
                                .setIcon(R.drawable.ic_warning)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        final int position = (viewHolder.getAdapterPosition());
                                        Snackbar snackbar = Snackbar.make(view,
                                                dbReservationList.get(position).getReservationRoom().getName()
                                                        + " reservation has been deleted.", Snackbar.LENGTH_LONG);
                                        new CalendarAsyncTask(REMOVE_RESERVATION).execute(dbReservationList.get(position));
                                        snackbar.show();
                                    }})
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        new CalendarAsyncTask(GET_RESERVATIONS).execute();
                                    }
                                }).show();

                    }
                };

        itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }
    private class CalendarAsyncTask extends AsyncTask<Reservation, Void, List> {
        private int task;
        private CalendarAsyncTask(int task) {
            mNoPermissionLayout.setVisibility(View.GONE);
            this.task = task;
                mLoaderLayout.setVisibility(View.VISIBLE);
                mNoBookingLayout.setVisibility(View.GONE);
//                dbAdapter = new ReservationsOverviewAdapter(new ArrayList<Reservation>());
                //mAdapter.setReservationList(list);
//                dbAdapter.notifyDataSetChanged();
//                mRecyclerView.setAdapter(dbAdapter);
            updateUI();
        }

        @Override
        protected List doInBackground(Reservation... reservations) {
            List<Reservation> resList = new ArrayList<>();
            try {

                CalendarConnection con = new CalendarConnection(getContext());
                    switch(task){
                    case REMOVE_RESERVATION:
                        //con.removeEvent(reservations[0]);
                        DatabaseConnection.deleteReservation(reservations[0].getID());
                        break;
                }
                resList = DatabaseConnection.getReservations();
                resList = con.filterEventsByOwner(resList, accountName);
            }catch (UserRecoverableAuthIOException e) {
                    startActivityForResult(e.getIntent(), REQUEST_ACCOUNT_CALENDAR);
                    return null;
            } catch (InterruptedException e) {
                    e.printStackTrace();
                    return null;      
            } catch (IOException | ParseException e ) {
                e.printStackTrace();
            }
            return resList;
        }

        @Override
        protected void onPostExecute(List list) {
            super.onPostExecute(list);
            mySwipeRefreshLayout.setRefreshing(false);
            mLoaderLayout.setVisibility(View.GONE);
            if(list != null) {
                dbReservationList.clear();
                dbReservationList = list;
//                dbAdapter = new ReservationsOverviewAdapter(dbReservationList);
//                //mAdapter.setReservationList(list);
//                dbAdapter.setReservationList(list);
//                dbAdapter.notifyDataSetChanged();
//                mRecyclerView.setAdapter(dbAdapter);
                if(list.isEmpty()){
                    mNoBookingLayout.setVisibility(View.VISIBLE);
                }
            }
            updateUI();
        }
    }

    private void updateUI() {
        if (dbAdapter == null) {
            dbAdapter = new ReservationsOverviewAdapter(dbReservationList);
            mRecyclerView.setAdapter(dbAdapter);
        } else {
            //Refresh list
            dbAdapter.swapList(dbReservationList);
        }
    }
}
