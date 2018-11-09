package com.hva.m2mobi.m2hva_reservationsystem.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.hva.m2mobi.m2hva_reservationsystem.R;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;

public class ReserveRoomActivity extends AppCompatActivity {
    @BindView(R.id.reserve_room_capacity)
    Spinner spinnerCapacity;
    @BindView(R.id.reserve_room_name)
    Spinner spinnerRoom;
    @BindView(R.id.reserve_room_date)
    TextView datePicker;
    @BindView(R.id.reserve_room_timepicker)
    TextView timePicker;
    @BindView(R.id.reserve_room_duration)
    Spinner spinnerDuration;
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
        loadCapacityData();
        loadRoomNames();
        loadDurationData();
    }

    @OnClick(R.id.reserve_room_button)
   void submitReservation() {
        // TODO call server...
    }

    @OnClick(R.id.reserve_room_date)
    void showDate() {
    datePickerDialog();
    }

    @OnClick(R.id.reserve_room_timepicker)
     void chooseTime() {
        timePickerDialog();
    }

    @OnItemSelected(R.id.reserve_room_capacity)
    void roomCapacitySelected(int position) {
       spinnerCapacity.getItemAtPosition(position);
    }

    @OnItemSelected(R.id.reserve_room_name)
    void roomNameSelected( int position) {
        spinnerRoom.getItemAtPosition(position);
    }


    @OnItemSelected(R.id.reserve_room_duration)
    void roomDurationSelected( int position) {
        spinnerDuration.getItemAtPosition(position);
    }

    private void loadCapacityData() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.capacity_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCapacity.setAdapter(adapter);
    }

    private void loadRoomNames() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.roomnames_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRoom.setAdapter(adapter);
    }

    private void loadDurationData() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.duration_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDuration.setAdapter(adapter);
    }


    private void datePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            datePicker.setText(dayOfMonth + "/" + monthOfYear + "/" + year);
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    private void timePickerDialog() {
        final Calendar myCalender = Calendar.getInstance();
        int hour = myCalender.get(Calendar.HOUR_OF_DAY);
        int minute = myCalender.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        timePicker.setText(hourOfDay + ":" + minute);
                    }
                }, hour, minute, false);
        timePickerDialog.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}



