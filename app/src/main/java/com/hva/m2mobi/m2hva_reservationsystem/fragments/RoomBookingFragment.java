package com.hva.m2mobi.m2hva_reservationsystem.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hva.m2mobi.m2hva_reservationsystem.R;

public class RoomBookingFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_room_booking, container, false);
    }
}
