package com.hva.m2mobi.m2hva_reservationsystem.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.hva.m2mobi.m2hva_reservationsystem.R;

public class CalendarScreenActivity extends AppCompatActivity {
    private RecyclerView calendarView;
    private Button closeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_screen);
        calendarView = findViewById(R.id.calendarRecyclerView);
        closeButton = findViewById(R.id.closeCalendar);
        calendarView.setLayoutManager(new GridLayoutManager(this, 7, GridLayoutManager.VERTICAL, false));
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Go back to ...
            }
        });
    }
}
