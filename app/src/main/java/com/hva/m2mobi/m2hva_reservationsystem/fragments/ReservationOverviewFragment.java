package com.hva.m2mobi.m2hva_reservationsystem.fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import timber.log.Timber;


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
    int indexId;
    String chosenDateRes;
    TextView dateOfFirstElement;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_reservations_overview, container, false);

        setHasOptionsMenu(true);

        mLoaderLayout = view.findViewById(R.id.loadingPanel);
        mLoaderLayout.setVisibility(View.GONE);
        mNoBookingLayout = view.findViewById(R.id.noBooking);
        inflater.inflate(R.layout.no_bookings, mNoBookingLayout);
        mNoBookingLayout.setVisibility(View.GONE);
        mNoPermissionLayout = view.findViewById(R.id.noPermission);
        inflater.inflate(R.layout.no_permission, mNoPermissionLayout);
        mNoPermissionLayout.setVisibility(View.GONE);

        dateOfFirstElement = getActivity().findViewById(R.id.dateText);
        dateOfFirstElement.setVisibility(view.VISIBLE);

        //requestData();
        buildRecyclerView();
        mySwipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.setColorSchemeResources(
                R.color.colorOrange,
                R.color.colorBlue,
                R.color.colorLightGrey);
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        getRealReservations();
                    }
                }
        );

        getRealReservations();
        updateUI();
        getFirstElementInRecyclerView();

        return view;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_ACCOUNT_CALENDAR) {
            new CalendarAsyncTask(GET_RESERVATIONS).execute();
        } else {
            mNoPermissionLayout.setVisibility(View.VISIBLE);
        }
    }

    public void buildRecyclerView() {
        mRecyclerView = view.findViewById(R.id.recyclerView_reservations);
        layoutManager = new LinearLayoutManager(getContext());
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
                            }
                        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                getRealReservations();
                            }
                        }).show();
            }
        };

        itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private class CalendarAsyncTask extends AsyncTask<Reservation, Void, Void> {
        private int task;

        private CalendarAsyncTask(int task) {
            mNoPermissionLayout.setVisibility(View.GONE);
            this.task = task;
            mLoaderLayout.setVisibility(View.VISIBLE);
            mNoBookingLayout.setVisibility(View.GONE);
            updateUI();
        }

        @Override
        protected Void doInBackground(Reservation... reservations) {
            try {
                final CalendarConnection con = CalendarConnection.getInstance(getContext());
                switch(task){
                    case REMOVE_RESERVATION:
                        con.removeEvent(reservations[0]);
                        DatabaseConnection.deleteReservation(reservations[0].getID());
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private void updateUI() {
        if (dbAdapter == null) {
            dbAdapter = new ReservationsOverviewAdapter(dbReservationList, getContext());
            mRecyclerView.setAdapter(dbAdapter);
        } else {
            //Refresh list
            dbAdapter.swapList(dbReservationList);
        }
    }

    public void getFirstElementInRecyclerView() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                indexId = layoutManager.findFirstVisibleItemPosition();
                if (!dbReservationList.isEmpty()) {
                    System.out.println("datum - Datum van element is: " + dbReservationList.get(indexId).getDate());
                    System.out.println("datum - Index van element is: " + indexId);
//                    dateOfFirstElement.setText(dbReservationList.get(indexId).getDate());
                }
            }
        });
    }

    public void setFirstElementInRecylcerView(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(CalendarConnection.DATE_FORMAT);
        for (int i = 0; i < dbReservationList.size(); i++) {
            try {
                Date resDate = sdf.parse(dbReservationList.get(i).getDate());
                if(resDate.equals(date) || resDate.after(date)){
                    layoutManager.setSmoothScrollbarEnabled(true);
                    layoutManager.scrollToPositionWithOffset(i, layoutManager.getDecoratedMeasuredHeight(layoutManager.getChildAt(0)));
                    i = dbReservationList.size();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private void datePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                SimpleDateFormat sdf = new SimpleDateFormat(CalendarConnection.DATE_FORMAT);

                calendar.set(year, monthOfYear, dayOfMonth);

                chosenDateRes = sdf.format(calendar.getTime());
                setFirstElementInRecylcerView(calendar.getTime());

            }
        }, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.reservation_toolbar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                datePickerDialog();
                return false;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getRealReservations(){

        final CalendarConnection con = CalendarConnection.getInstance(getContext());

        DatabaseConnection.getReservations(new DatabaseConnection.ReservationListReturner() {

            public void onReturnList(List<Reservation> resList) {

                resList = DatabaseConnection.filterReservations(resList);
                resList = con.filterEventsByOwner(resList, accountName);
                try {
                    resList = con.orderListByDate(resList);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                mySwipeRefreshLayout.setRefreshing(false);
                mLoaderLayout.setVisibility(View.GONE);
                if (resList != null) {
                    dbReservationList.clear();
                    dbReservationList = resList;
                    if (resList.isEmpty()) {
                        mNoBookingLayout.setVisibility(View.VISIBLE);
                    }
                }
                updateUI();
            }
        });
    }
}
