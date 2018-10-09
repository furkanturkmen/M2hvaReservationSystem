package com.hva.m2mobi.m2hva_reservationsystem.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hva.m2mobi.m2hva_reservationsystem.R;

import java.util.List;

public class RoomsOverviewAdapter extends RecyclerView.Adapter<RoomsOverviewAdapter.RoomsOverviewViewHolder> {
    private Context context;
    public List<Room> mListRooms;

    public class RoomsOverviewViewHolder extends RecyclerView.ViewHolder {
        private TextView roomName;
        private TextView roomAvailability;
        public View view;

        public RoomsOverviewViewHolder(@NonNull View itemView) {
            super(itemView);
            roomName = itemView.findViewById(R.id.room_name);
            roomAvailability = itemView.findViewById(R.id.room_availability);
            view = itemView;
        }
    }

    @NonNull
    @Override
    public RoomsOverviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_cell_roomsoverview, parent, false);
        return new RoomsOverviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomsOverviewViewHolder holder, int position) {
        Room room = mListRooms.get(position);
        holder.roomName.setText(room.getName());
        holder.roomAvailability.setText(room.getAvailability());
    }

    @Override
    public int getItemCount() {
        return mListRooms.size();
    }

    public RoomsOverviewAdapter(Context context, List<Room> MListRooms) {
        this.context = context;
        this.mListRooms = MListRooms;
    }
}
