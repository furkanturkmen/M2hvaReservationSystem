package com.hva.m2mobi.m2hva_reservationsystem;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RoomsOverviewActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms_overview);

        List<RoomsOverviewPlaceholder> roomsOverviewPlaceholders = new ArrayList<>();
        for (int i = 0; i < RoomsOverviewPlaceholder.PLACEHOLDER_ROOMNAME.length; i++) {
            roomsOverviewPlaceholders.add(new RoomsOverviewPlaceholder(RoomsOverviewPlaceholder.PLACEHOLDER_ROOMNAME[i],
                    RoomsOverviewPlaceholder.PLACEHOLDER_ROOMAVAILABILITY[i]));
        }

        mRecyclerView = findViewById(R.id.recyclerView);

        // changes in content do not change layout of recyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter
        RoomsOverviewAdapter mAdapter = new RoomsOverviewAdapter(this, roomsOverviewPlaceholders);
        mRecyclerView.setAdapter(mAdapter);
    }
}
