package com.hva.m2mobi.m2hva_reservationsystem.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hva.m2mobi.m2hva_reservationsystem.R;
import com.hva.m2mobi.m2hva_reservationsystem.activities.RoomDetailActivity;
import com.hva.m2mobi.m2hva_reservationsystem.models.Room;
import com.hva.m2mobi.m2hva_reservationsystem.adapters.RoomsOverviewAdapter;

import java.util.ArrayList;

public class RoomsOverviewFragment extends Fragment {
    View view;
    private RecyclerView mRecyclerView;
    private RoomsOverviewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    ArrayList<Room> exampleList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_rooms_overview, container, false);
        createExampleList();
        buildRecylerView();


        return view;
    }

    public void createExampleList() {
        exampleList = new ArrayList<>();
        exampleList.add(new Room("Room 1", "Description sample", "Available"));
        exampleList.add(new Room("Room 2", "Description sample", "Available"));
        exampleList.add(new Room("Room 3", "Description sample", "Available"));
        exampleList.add(new Room("Room 4", "Description sample", "Available"));
        exampleList.add(new Room("Room 5", "Description sample", "Available"));
    }

    public void buildRecylerView() {
        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new RoomsOverviewAdapter(exampleList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new RoomsOverviewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.d("tots", "" + position);
                Intent intent = new Intent(getContext(),RoomDetailActivity.class);
                startActivity(intent);
            }
        });
    }

}
