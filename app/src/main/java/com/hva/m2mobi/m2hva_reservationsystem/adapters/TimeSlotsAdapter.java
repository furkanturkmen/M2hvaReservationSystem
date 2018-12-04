package com.hva.m2mobi.m2hva_reservationsystem.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hva.m2mobi.m2hva_reservationsystem.R;
import com.hva.m2mobi.m2hva_reservationsystem.models.Reservation;
import com.hva.m2mobi.m2hva_reservationsystem.models.TimeSlot;
import com.hva.m2mobi.m2hva_reservationsystem.utils.CalendarConnection;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.List;

public class TimeSlotsAdapter extends RecyclerView.Adapter<TimeSlotsAdapter.TimeSlotsViewHolder> {
    private List<TimeSlot> mTimeSlots;
    private OnItemClickListener mListener;

    public void getTimeSlots(List<TimeSlot> list){
        mTimeSlots = list;
    }
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public class TimeSlotsViewHolder extends RecyclerView.ViewHolder{
        private TextView from;
        private TextView fromTime;
        private TextView until;
        private TextView untilTime;

        public TimeSlotsViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
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

    public TimeSlotsAdapter(List<TimeSlot> timeSlots){
        mTimeSlots = timeSlots;
    }

    @NonNull
    @Override
    public TimeSlotsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grid_cell_reservationslots, viewGroup, false);
        return new TimeSlotsViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeSlotsViewHolder holder, int position) {
        TimeSlot currentItem = mTimeSlots.get(position);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(CalendarConnection.TIME_FORMAT);
        String startTime = simpleDateFormat.format(currentItem.getStartTime());
        String endTime = simpleDateFormat.format(currentItem.getEndTime());
        holder.fromTime.setText(startTime);
        holder.untilTime.setText(endTime);
    }

    @Override
    public int getItemCount() {
        return mTimeSlots.size();
    }

}
