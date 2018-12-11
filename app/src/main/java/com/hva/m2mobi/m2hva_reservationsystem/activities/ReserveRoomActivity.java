package com.hva.m2mobi.m2hva_reservationsystem.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hva.m2mobi.m2hva_reservationsystem.R;
import com.hva.m2mobi.m2hva_reservationsystem.adapters.TimeSlotsAdapter;
import com.hva.m2mobi.m2hva_reservationsystem.fragments.RoomsOverviewFragment;
import com.hva.m2mobi.m2hva_reservationsystem.models.Reservation;
import com.hva.m2mobi.m2hva_reservationsystem.models.Room;
import com.hva.m2mobi.m2hva_reservationsystem.models.TimeSlot;
import com.hva.m2mobi.m2hva_reservationsystem.utils.CalendarConnection;
import com.hva.m2mobi.m2hva_reservationsystem.utils.CustomTimePickerDialog;
import com.hva.m2mobi.m2hva_reservationsystem.utils.DatabaseConnection;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import timber.log.Timber;

public class ReserveRoomActivity extends AppCompatActivity {
    @BindView(R.id.reserve_room_capacity)
    Spinner spinnerCapacity;
    @BindView(R.id.reserve_room_name)
    Spinner spinnerRoom;
    @BindView(R.id.reserve_room_date)
    TextView datePicker;
    @BindView(R.id.reserve_room_starttimepicker)
    TextView timePicker;
    @BindView(R.id.reserve_room_endtimepicker)
    TextView endTimePicker;
    @BindView(R.id.reserve_room_toolbar)
    Toolbar toolbar;
    @BindView(R.id.invalid_time)
    TextView invalidTime;
    @BindView(R.id.meeting_times)
    RecyclerView meetingTimes;
    @BindView(R.id.reserve_room_button)
    Button reserveRoomButton;

    private static final int ADD_RESERVATION = 0;
    private static final int GET_RESERVATION = 1;

    private ArrayAdapter<String> adapter;
    private ArrayList<String> roomArray;
    private List<TimeSlot> mTimeSlotList = new ArrayList<>();
    private TimeSlotsAdapter mAdapter;

    private Room testRoom;
    private FirebaseDatabase dbCon = FirebaseDatabase.getInstance();

    private DatabaseReference dbRef = dbCon.getReference();


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
        loadDateData();

        buildRecyclerView();
    }

    @OnClick(R.id.reserve_room_button)
    void submitReservation() {
        System.out.println("dbCon.toString: " + dbRef.toString() + "\n" + "dbCon: " + dbRef);
        String cap = spinnerCapacity.getSelectedItem().toString();
        String startTime = timePicker.getText().toString();
        String endTime = endTimePicker.getText().toString();

        String accountName = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String roomName = spinnerRoom.getItemAtPosition(spinnerRoom.getSelectedItemPosition()).toString();
        Timber.tag("Room").d(roomName);
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
        new CalendarAsyncTask(ADD_RESERVATION).execute(res);
    }

    @OnClick(R.id.reserve_room_date)
    void showDate() {
        datePickerDialog();
    }

    @OnClick(R.id.reserve_room_starttimepicker)
    void chooseStartTime() {
        timePickerDialog(timePicker, false);
    }

    @OnClick(R.id.reserve_room_endtimepicker)
    void chooseEndTime() {
        timePickerDialog(endTimePicker, true);
    }

    @OnItemSelected(R.id.reserve_room_capacity)
    void roomCapacitySelected(int position) {
        spinnerCapacity.getItemAtPosition(position);
        loadRoomNames();
    }

    @OnItemSelected(R.id.reserve_room_name)
    void roomNameSelected(int position) {
        spinnerRoom.getItemAtPosition(position);
        String roomName = spinnerRoom.getItemAtPosition(spinnerRoom.getSelectedItemPosition()).toString();
        Log.d("Room", roomName);
        Room room = null;
        try {
            // room = DatabaseConnection.getRooms().get(0);

            for (Room r : DatabaseConnection.getRooms()) {
                if (r.getName().equals(roomName))
                    room = r;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Reservation reservation = new Reservation(0, "", "", room, "", datePicker.getText().toString(), "");
        new CalendarAsyncTask(GET_RESERVATION).execute(reservation);
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
        calendar.add(Calendar.HOUR, 1);
        endTimePicker.setText(stf.format(calendar.getTime()));
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

                roomNameSelected(spinnerRoom.getSelectedItemPosition());
            }
        }, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void timePickerDialog(final TextView textView, boolean isEndTime) {
        final Calendar myCalender = Calendar.getInstance();
        int hour = myCalender.get(Calendar.HOUR_OF_DAY);
        int minute = myCalender.get(Calendar.MINUTE);
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay,
                                  int minute) {
                SimpleDateFormat sdf = new SimpleDateFormat( CalendarConnection.TIME_FORMAT);
                myCalender.set(0, 0, 0, hourOfDay, minute);

                textView.setText(sdf.format(myCalender.getTime()));
                isValidTimeslot(timePicker.getText().toString() + datePicker.getText().toString(), endTimePicker.getText().toString() + datePicker.getText().toString());
            }
        };
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(CalendarConnection.DATE_FORMAT);
        try {

            Date date = simpleDateFormat.parse(datePicker.getText().toString());
            SimpleDateFormat simpleTimeFormat = new SimpleDateFormat(CalendarConnection.TIME_FORMAT);
            int maxHour = 23;
            int maxMinute = 59;
            int minHour = hour;
            int minMinute = minute;
            if (isEndTime){
                Date today = new Date();
                Date startTime = simpleTimeFormat.parse(timePicker.getText().toString());
                myCalender.setTime(startTime);
                minHour = myCalender.get(Calendar.HOUR_OF_DAY);
                minMinute = myCalender.get(Calendar.MINUTE);
                if (!date.after(today)){
                    String nowTime = simpleTimeFormat.format(today);
                    today = simpleTimeFormat.parse(nowTime);
                    if (today.after(startTime)){
                        minHour = hour;
                        minMinute = minute;
                    }
                }
            } else {
                Date endTime = simpleTimeFormat.parse(endTimePicker.getText().toString());
                myCalender.setTime(endTime);
                maxHour = myCalender.get(Calendar.HOUR_OF_DAY);
                maxMinute = myCalender.get(Calendar.MINUTE);
            }
            TimePickerDialog timePickerDialog = new CustomTimePickerDialog(this, onTimeSetListener, minHour, minMinute, maxHour, maxMinute);
            timePickerDialog.show();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void buildRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mAdapter = new TimeSlotsAdapter(mTimeSlotList);

        meetingTimes.setLayoutManager(layoutManager);
        meetingTimes.setAdapter(mAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void onCreateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ReserveRoomActivity.this);
        // Get the layout inflater
        LayoutInflater inflater = ReserveRoomActivity.this.getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View dialogView = inflater.inflate(R.layout.dialog_confirm_reserve_room, null);
        builder.setView(dialogView);
        TextView confirmButton = dialogView.findViewById(R.id.btn_ok_button_reserve);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        // create alert dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public class CalendarAsyncTask extends AsyncTask<Reservation, Void, List<TimeSlot>> {

        private int task;

        public CalendarAsyncTask(int task) {
            this.task = task;
        }

        @Override
        protected List<TimeSlot> doInBackground(Reservation... reservations) {
            try {
                CalendarConnection con = CalendarConnection.getInstance(ReserveRoomActivity.this);

                switch (task) {
                    case ADD_RESERVATION:
                        String id = con.addEvent(reservations[0]);
                        reservations[0].setID(id);
                        dbRef.child("reservations").child(id).setValue(reservations[0]);
                        return null;
                    case GET_RESERVATION:
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(CalendarConnection.DATE_FORMAT);
                        Date selectedDate = simpleDateFormat.parse(reservations[0].getDate());
                        Date now = new Date();
                        if(now.after(selectedDate))
                            selectedDate = now;
                        List<Reservation> reservationList =  con.getDateEvents(reservations[0].getReservationRoom(), selectedDate);
                        List<TimeSlot> timeSlots = con.getAvailableTimeSlots(reservationList, 0, selectedDate, new ArrayList<TimeSlot>());
                        return timeSlots;
                }
            } catch (IOException | ParseException e) {
                e.printStackTrace();
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<TimeSlot> v) {
            super.onPostExecute(v);
            if (v == null) {
                onCreateDialog();
            } else {
                mTimeSlotList.clear();
                mTimeSlotList.addAll(v);
                mAdapter.notifyDataSetChanged();
                isValidTimeslot(timePicker.getText().toString() + datePicker.getText().toString(), endTimePicker.getText().toString() + datePicker.getText().toString());
            }
        }
    }

    public void isValidTimeslot(String startTime, String endTime){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(CalendarConnection.TIME_FORMAT + CalendarConnection.DATE_FORMAT);
        boolean result = false;
        try {
            Date start = simpleDateFormat.parse(startTime);
            Date end = simpleDateFormat.parse(endTime);
            for (TimeSlot timeSlot:mTimeSlotList) {
                Log.d("timeslot", timeSlot.toString());
                if ((start.after(timeSlot.getStartTime()) || start.equals(timeSlot.getStartTime())) && (end.before(timeSlot.getEndTime()) || end.equals(timeSlot.getEndTime()))){
                    result = true;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (!result){
            invalidTime.setVisibility(View.VISIBLE);
            reserveRoomButton.setEnabled(false);
        } else {
            invalidTime.setVisibility(View.GONE);
            reserveRoomButton.setEnabled(true);
        }
    }
}




