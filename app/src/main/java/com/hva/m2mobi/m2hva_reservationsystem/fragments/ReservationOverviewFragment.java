package com.hva.m2mobi.m2hva_reservationsystem.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hva.m2mobi.m2hva_reservationsystem.R;
import com.hva.m2mobi.m2hva_reservationsystem.models.Reservation;
import com.hva.m2mobi.m2hva_reservationsystem.adapters.ReservationsOverviewAdapter;

import java.util.ArrayList;

public class ReservationOverviewFragment extends Fragment {
    View view;
    private RecyclerView mRecyclerView;
    private ReservationsOverviewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    ArrayList<Reservation> exampleList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_reservations_overview, container, false);
        createExampleList();
        buildRecylerView();


        return view;
    }

    public void createExampleList() {
        exampleList = new ArrayList<>();
        exampleList.add(new Reservation("Reservation 1"));
        exampleList.add(new Reservation("Reservation 2"));
        exampleList.add(new Reservation("Reservation 3"));
        exampleList.add(new Reservation("Reservation 4"));
        exampleList.add(new Reservation("Reservation 5"));
    }

    public void buildRecylerView() {
        mRecyclerView = view.findViewById(R.id.recyclerView_reservations);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new ReservationsOverviewAdapter(exampleList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new ReservationsOverviewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(getActivity(), "clicked"+ position,
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onDeleteClick(int position) {
                Toast.makeText(getActivity(), "Deleted",
                        Toast.LENGTH_LONG).show();
                removeItem(position);
            }
        });
    }

    private void removeItem(int position) {
        exampleList.remove(position);
        mAdapter.notifyDataSetChanged();
    }
}
