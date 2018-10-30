package com.hva.m2mobi.m2hva_reservationsystem.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.hva.m2mobi.m2hva_reservationsystem.R;
import com.hva.m2mobi.m2hva_reservationsystem.adapters.Day;
import com.hva.m2mobi.m2hva_reservationsystem.adapters.DayAdapter;

import java.util.ArrayList;
import java.util.List;

public class CalendarActivity extends AppCompatActivity {
    private RecyclerView calendarView;
    private Button closeButton;
    private DayAdapter mAdapter;
    private List<Day> mDayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        calendarView = findViewById(R.id.calendarRecyclerView);
        calendarView.setLayoutManager(new GridLayoutManager(this, 7, GridLayoutManager.VERTICAL, false));

        createExampleList();
        mAdapter = new DayAdapter(mDayList);
        calendarView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new DayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //**launch day screen for day selected**//
                //Intent intent = new Intent(CalendarActivity.this,RoomDetailActivity.class);
                //startActivity(intent);
            }
        });

        closeButton = findViewById(R.id.closeCalendar);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Go back to ...
            }
        });
    }


    public void createExampleList() {
        mDayList = new ArrayList<>();
        for(int i = 0; i < 32; i++){
            mDayList.add(new Day("Mon",i,i%9));
        }
    }

}
