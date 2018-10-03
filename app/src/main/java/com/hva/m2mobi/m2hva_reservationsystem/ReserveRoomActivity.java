package com.hva.m2mobi.m2hva_reservationsystem;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Spinner;
import android.widget.TimePicker;

/**
 * Created by khaled on 02-10-18.
 */

public class ReserveRoomActivity extends AppCompatActivity {
    private Spinner firstSpinner;
    private Spinner secondSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_room);
        firstSpinner = findViewById(R.id.firstTimePicker);
        secondSpinner = findViewById(R.id.secondTimePicker);
    }
}
