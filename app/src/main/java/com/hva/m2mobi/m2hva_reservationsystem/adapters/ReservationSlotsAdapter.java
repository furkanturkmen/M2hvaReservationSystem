package com.hva.m2mobi.m2hva_reservationsystem.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hva.m2mobi.m2hva_reservationsystem.R;
import com.hva.m2mobi.m2hva_reservationsystem.models.Reservation;

import java.util.List;

public class ReservationSlotsAdapter extends RecyclerView.Adapter<ReservationSlotsAdapter.ReservationSlotsViewHolder> {
    private List<Reservation> mReservationSlots;
    private OnItemClickListener mListener;

    public void getReservationSlots(List<Reservation> list){
        mReservationSlots = list;
    }
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public class ReservationSlotsViewHolder extends RecyclerView.ViewHolder{
        private TextView from;
        private TextView fromTime;
        private TextView until;
        private TextView untilTime;

        public ReservationSlotsViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            from = itemView.findViewById(R.id.reserved_from);
            fromTime = itemView.findViewById(R.id.reserved_from_time);
            until = itemView.findViewById(R.id.reserved_until);
            untilTime = itemView.findViewById(R.id.reserved_until_time);

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

    public ReservationSlotsAdapter(List<Reservation> reservationSlots){
        mReservationSlots = reservationSlots;
    }

    @NonNull
    @Override
    public ReservationSlotsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grid_cell_reservationslots, viewGroup, false);
        return new ReservationSlotsViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationSlotsViewHolder holder, int position) {
        Reservation currentItem = mReservationSlots.get(position);
        holder.fromTime.setText(currentItem.getStartTime());
        holder.untilTime.setText(currentItem.getEndTime());
    }

    @Override
    public int getItemCount() {
        return mReservationSlots.size();
    }

}
