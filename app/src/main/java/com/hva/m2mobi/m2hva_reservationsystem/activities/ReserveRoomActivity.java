package com.hva.m2mobi.m2hva_reservationsystem.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.hva.m2mobi.m2hva_reservationsystem.R;
import com.hva.m2mobi.m2hva_reservationsystem.fragments.RoomsOverviewFragment;
import com.hva.m2mobi.m2hva_reservationsystem.models.Reservation;
import com.hva.m2mobi.m2hva_reservationsystem.models.Room;
import com.hva.m2mobi.m2hva_reservationsystem.utils.CalendarConnection;
import com.hva.m2mobi.m2hva_reservationsystem.utils.DatabaseConnection;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    @BindView(R.id.reserve_room_toolbar)
    Toolbar toolbar;

    private ArrayAdapter<String> adapter;
    private ArrayList<String> roomArray;

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
        loadDateData();
    }

    @OnClick(R.id.reserve_room_button)
    void submitReservation() {
        String cap = spinnerCapacity.getSelectedItem().toString();
        String startTime = timePicker.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat(CalendarConnection.TIME_FORMAT);
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(sdf.parse(startTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String duration = spinnerDuration.getSelectedItem().toString().substring(0, 1);
        cal.add(Calendar.HOUR, Integer.parseInt(duration));
        String endTime = sdf.format(cal.getTime());
        String accountName = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String roomName = spinnerRoom.getItemAtPosition(spinnerRoom.getSelectedItemPosition()).toString();
        Log.d("Room", roomName);
        Room room = null;
        try {
            room = DatabaseConnection.getRooms().get(0);

            for (Room r : DatabaseConnection.getRooms()) {
                if (r.getName().equals(roomName))
                    room = r;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Reservation res = new Reservation(Integer.parseInt(cap), startTime, endTime, room, accountName,
                datePicker.getText().toString(), "");
        new CalendarAsyncTask().execute(res);
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
        loadRoomNames();
    }

    @OnItemSelected(R.id.reserve_room_name)
    void roomNameSelected(int position) {
        spinnerRoom.getItemAtPosition(position);
    }

    @OnItemSelected(R.id.reserve_room_duration)
    void roomDurationSelected(int position) {
        spinnerDuration.getItemAtPosition(position);
    }

    private void loadCapacityData() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.capacity_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCapacity.setAdapter(adapter);
    }

    private void loadRoomNames() {
        int roomIntent = getIntent().getIntExtra(RoomsOverviewFragment.ROOM_EXTRA, 0);
        if (adapter == null) {
            roomArray = new ArrayList<>();
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roomArray);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerRoom.setAdapter(adapter);
        }
        roomArray.clear();
        try {
            for (Room room : DatabaseConnection.getRooms()) {
                if (room.getCapacity() >= Integer.parseInt(spinnerCapacity.getSelectedItem().toString()))
                    roomArray.add(room.getName());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();
        spinnerRoom.setSelection(roomIntent);
    }

    private void loadDateData() {
        SimpleDateFormat sdf = new SimpleDateFormat(CalendarConnection.DATE_FORMAT);
        SimpleDateFormat stf = new SimpleDateFormat(CalendarConnection.TIME_FORMAT);
        Calendar calendar = Calendar.getInstance();

        datePicker.setText(sdf.format(calendar.getTime()));
        timePicker.setText(stf.format(calendar.getTime()));
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

                SimpleDateFormat sdf = new SimpleDateFormat(CalendarConnection.DATE_FORMAT);
                calendar.set(year, monthOfYear, dayOfMonth);

                datePicker.setText(sdf.format(calendar.getTime()));
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
                SimpleDateFormat sdf = new SimpleDateFormat(CalendarConnection.TIME_FORMAT);
                myCalender.set(0, 0, 0, hourOfDay, minute);

                timePicker.setText(sdf.format(myCalender.getTime()));
            }
        }, hour, minute, false);
        timePickerDialog.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private Dialog onCreateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ReserveRoomActivity.this);
        // Get the layout inflater
        LayoutInflater inflater = ReserveRoomActivity.this.getLayoutInflater();
        TextView confirmButton = findViewById(R.id.btn_ok_button_reserve);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_confirm_reserve_room, null));
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        return builder.create();
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
            onCreateDialog().show();
        }
    }
}




