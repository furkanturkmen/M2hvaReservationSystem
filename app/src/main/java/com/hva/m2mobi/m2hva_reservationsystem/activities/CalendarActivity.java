package com.hva.m2mobi.m2hva_reservationsystem.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.hva.m2mobi.m2hva_reservationsystem.R;
import com.hva.m2mobi.m2hva_reservationsystem.adapters.Day;
import com.hva.m2mobi.m2hva_reservationsystem.adapters.DayAdapter;
import com.hva.m2mobi.m2hva_reservationsystem.adapters.Room;
import com.hva.m2mobi.m2hva_reservationsystem.adapters.RoomsOverviewAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;

public class CalendarActivity extends AppCompatActivity {

    private List<Day> mDayList;
    private DayAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        createExampleList();
        buildRecylerView();
    }


    public void createExampleList() {
        mDayList = new ArrayList<>();
        for(int i = 0; i < 32; i++){

            mDayList.add(new Day("Mon",i,i%9));
        }
    }

    public void buildRecylerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView_calendar);
       RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,7);
        mAdapter = new DayAdapter(mDayList);

       recyclerView.setLayoutManager(layoutManager);
       recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new DayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //**launch day screen for day selected**//
                //Intent intent = new Intent(CalendarActivity.this,RoomDetailActivity.class);
                //startActivity(intent);
            }
        });
    }
}
