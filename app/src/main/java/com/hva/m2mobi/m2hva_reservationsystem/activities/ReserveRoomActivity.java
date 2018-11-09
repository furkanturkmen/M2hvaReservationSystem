package com.hva.m2mobi.m2hva_reservationsystem.activities;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.hva.m2mobi.m2hva_reservationsystem.R;
import com.hva.m2mobi.m2hva_reservationsystem.models.Reservation;
import com.hva.m2mobi.m2hva_reservationsystem.utils.CalendarConnection;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReserveRoomActivity extends AppCompatActivity {
    @BindView(R.id.reserve_room_capacity)
    Spinner spinnerCapacity;
    @BindView(R.id.reserve_room_name)
    Spinner spinnerRoom;
    @BindView(R.id.reserve_room_date)
    TextView datePicker;
    @BindView(R.id.reserve_room_duration)
    TextView durationSpinner;
    @BindView(R.id.reserve_room_button)
    Button reservationButton;
    @BindView(R.id.reserve_room_toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_room);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


    }

    @OnClick(R.id.reserve_room_button)
    void submitReservation() {
        Reservation res = new Reservation(3,"18:00","19:00",
                CalendarConnection.ROOMS[0],"kylewatson98@gmail.com","09-11-2018");
        new CalendarAsyncTask().execute(res);
    }

    @OnClick(R.id.reserve_room_date)
    void showDate() {
        datePickerDialog().show();
    }

    @OnClick(R.id.reserve_room_timepicker)
    void chooseTime() {
        // TODO go to next activity
    }

    private void loadCapacityData() {
//        ArrayList<Integer> stringArrayList = new ArrayList<>();
//        stringArrayList.add(4);
//        stringArrayList.add(6);
//        stringArrayList.add(8);
//        stringArrayList.add(10);
//        stringArrayList.add(12);
//
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
//                stringArrayList, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerCapacity.setAdapter(adapter);

    }

    private void loadRoomNames() {

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    private DatePickerDialog datePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int startYear = calendar.get(Calendar.YEAR);
        int starthMonth = calendar.get(Calendar.MONTH);
        int startDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                Calendar pickDate = Calendar.getInstance();
                pickDate.set(year, monthOfYear, dayOfMonth);
//                datePicker.setText(dateFormatter.format(newDate.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        return datePickerDialog;
    }

    public class CalendarAsyncTask extends AsyncTask<Reservation, Void, Void> {

        @Override
        protected Void doInBackground(Reservation... reservations) {
            try {
                new CalendarConnection(ReserveRoomActivity.this).addEvent(reservations[0]);
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            finish();
        }
    }

}

