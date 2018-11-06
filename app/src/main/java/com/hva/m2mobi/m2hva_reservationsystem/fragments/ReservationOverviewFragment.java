package com.hva.m2mobi.m2hva_reservationsystem.fragments;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.helper.ItemTouchHelper;
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
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder
                            target) {
                        return false;
                    }

                    //Called when a user swipes left or right on a ViewHolder
                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                        //Get the index corresponding to the selected position
                        int position = (viewHolder.getAdapterPosition());
                        exampleList.remove(position);
                        mAdapter.notifyItemRemoved(position);
                    }
                };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);




        mAdapter.setOnItemClickListener(new ReservationsOverviewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Snackbar snackbar = Snackbar
                        .make(view, exampleList.get(position).getDescription() + " has been clicked.", Snackbar.LENGTH_LONG).setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Snackbar snackbar1 = Snackbar.make(view, "Message is restored!", Snackbar.LENGTH_SHORT);
                                snackbar1.show();
                            }
                        });;

                snackbar.show();
            }
        });

        return view;
    }

    public void createExampleList() {
        exampleList = new ArrayList<>();
        exampleList.add(new Reservation("Jungle Room", "10-01-2019", "09:00-10:00"));
        exampleList.add(new Reservation("Hunting Room", "4-02-2019", "08:30-09:30"));
        exampleList.add(new Reservation("Elephant", "15-03-2019", "11:00-12:30"));
        exampleList.add(new Reservation("Mammoth", "26-04-2019", "14:30-16:00"));
        exampleList.add(new Reservation("Beach house", "19-05-2019", "15:30-17:00"));
        exampleList.add(new Reservation("Auditorium", "23-05-2019", "10:30-13:00"));
    }

    public void buildRecylerView() {
        mRecyclerView = view.findViewById(R.id.recyclerView_reservations);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new ReservationsOverviewAdapter(exampleList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

    }
}
