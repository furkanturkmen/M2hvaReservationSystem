package com.hva.m2mobi.m2hva_reservationsystem.adapters;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hva.m2mobi.m2hva_reservationsystem.R;
import com.hva.m2mobi.m2hva_reservationsystem.models.Reservation;

import java.util.ArrayList;

public class ReservationsOverviewAdapter extends RecyclerView.Adapter<ReservationsOverviewAdapter.ReservationsViewHolder> {
    public ArrayList<Reservation> mResevationsList;
    private OnItemClickListener mListener;


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    public static class ReservationsViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView date;
        private TextView time;
        private TextView attendees;
        private TextView like;


        public ReservationsViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            title = itemView.findViewById(R.id.reservation_title);
            date = itemView.findViewById(R.id.reservation_date);
            time = itemView.findViewById(R.id.reservation_time);
            attendees = itemView.findViewById(R.id.reservation_attendees);

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

    public ReservationsOverviewAdapter(ArrayList<Reservation> reservationsList) {
        mResevationsList = reservationsList;
    }

    @NonNull
    @Override
    public ReservationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_cell_reservationsoverview, parent, false);
        ReservationsViewHolder rvh = new ReservationsViewHolder(view, mListener);
        return rvh;
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationsViewHolder holder, int position) {
        Reservation currentItem = mResevationsList.get(position);
        holder.title.setText(currentItem.getTitle());
        holder.date.setText(holder.date.getText() + " " + currentItem.getReservationDate());
        holder.time.setText(holder.time.getText() + " " + currentItem.getStartTime() + " - " + currentItem.getEndTime());
        holder.attendees.setText(holder.attendees.getText() + " " + currentItem.getAttendees().size());

        Typeface custom_font;
        custom_font = ResourcesCompat.getFont(holder.attendees.getContext(), R.font.fa_solid_900);
        holder.date.setTypeface(custom_font);
        holder.time.setTypeface(custom_font);
        holder.attendees.setTypeface(custom_font);
    }

    @Override
    public int getItemCount() {
        return mResevationsList.size();
    }


}
