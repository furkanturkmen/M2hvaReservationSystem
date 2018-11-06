package com.hva.m2mobi.m2hva_reservationsystem.adapters;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hva.m2mobi.m2hva_reservationsystem.R;
import com.hva.m2mobi.m2hva_reservationsystem.models.Room;

import java.util.ArrayList;


public class RoomsOverviewAdapter extends RecyclerView.Adapter<RoomsOverviewAdapter.RoomsOverviewViewHolder> {
    public ArrayList<Room> mListRooms;
    private OnItemClickListener mListener;


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class RoomsOverviewViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView description;
        private TextView availability;

        public RoomsOverviewViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            name = itemView.findViewById(R.id.room_name);
            availability = itemView.findViewById(R.id.room_availability);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });

        }
    }

    public RoomsOverviewAdapter(ArrayList<Room> MListRooms) {
        this.mListRooms = MListRooms;
    }

    @NonNull
    @Override
    public RoomsOverviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_cell_roomsoverview, parent, false);
        return new RoomsOverviewViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomsOverviewViewHolder holder, int position) {
        Room room = mListRooms.get(position);
        holder.name.setText(room.getName());
        holder.availability.setText(room.getAvailability());
    }

    @Override
    public int getItemCount() {
        return mListRooms.size();
    }

}
