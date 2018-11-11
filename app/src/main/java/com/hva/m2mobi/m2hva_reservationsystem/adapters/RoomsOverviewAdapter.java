package com.hva.m2mobi.m2hva_reservationsystem.adapters;


import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hva.m2mobi.m2hva_reservationsystem.R;
import com.hva.m2mobi.m2hva_reservationsystem.models.Room;

import java.util.ArrayList;


public class RoomsOverviewAdapter extends RecyclerView.Adapter<RoomsOverviewAdapter.RoomsOverviewViewHolder> {
    private ArrayList<Room> mListRooms;
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
        private ImageView imageRoom;
        private TextView time;
        private TextView capacity;

        public RoomsOverviewViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            capacity = itemView.findViewById(R.id.room_capacity);
            name = itemView.findViewById(R.id.room_name);
            availability = itemView.findViewById(R.id.room_availability);
            imageRoom = itemView.findViewById(R.id.room_image);
            description = itemView.findViewById(R.id.room_description);
            time = itemView.findViewById(R.id.room_time);

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

    public RoomsOverviewAdapter(ArrayList<Room> mListRooms) {
        this.mListRooms = mListRooms;
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
        holder.description.setText(room.getDescription());
        holder.imageRoom.setImageResource(room.getImgResource());
        holder.capacity.setText(String.valueOf(room.getCapacity()));

        if(room.isAvailable()){
            holder.availability.setTextColor(Color.rgb(0, 150, 0));
            holder.availability.setText(R.string.status_green);
            holder.time.setText(String.format("%s %s", holder.time.getText(), room.getTime()));

        } else{
            holder.availability.setTextColor(Color.rgb(150, 0, 0));
            holder.availability.setText(R.string.status_red);
            holder.time.setText("");
        }

        Typeface custom_font;
        custom_font = ResourcesCompat.getFont(holder.time.getContext(), R.font.fa_solid_900);
        holder.time.setTypeface(custom_font);
        holder.availability.setTypeface(custom_font);
    }

    @Override
    public int getItemCount() {
        return mListRooms.size();
    }

}
