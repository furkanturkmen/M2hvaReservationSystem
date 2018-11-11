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
import com.hva.m2mobi.m2hva_reservationsystem.activities.ReserveRoomActivity;
import com.hva.m2mobi.m2hva_reservationsystem.activities.StateActivity;
import com.hva.m2mobi.m2hva_reservationsystem.adapters.RoomsOverviewAdapter;
import com.hva.m2mobi.m2hva_reservationsystem.models.Room;
import com.hva.m2mobi.m2hva_reservationsystem.utils.CalendarConnection;

import java.util.ArrayList;
import java.util.Arrays;

public class RoomsOverviewFragment extends Fragment {
    View view;
    private RecyclerView mRecyclerView;
    private RoomsOverviewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_rooms_overview, container, false);

        buildRecylerView();

        return view;
    }

    public void buildRecylerView() {
        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        final ArrayList<Room> rooms = new ArrayList<>();
        rooms.addAll(Arrays.asList(CalendarConnection.ROOMS));
        mAdapter = new RoomsOverviewAdapter(rooms);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new RoomsOverviewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.d("tots", "" + position);
                Intent intent = new Intent(getContext(),ReserveRoomActivity.class);
                intent.putExtra("room",position);
                startActivity(intent);
            }
        });
    }
}
